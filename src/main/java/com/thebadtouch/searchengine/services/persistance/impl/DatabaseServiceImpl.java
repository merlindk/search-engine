package com.thebadtouch.searchengine.services.persistance.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.repositories.DocumentRepository;
import com.thebadtouch.searchengine.repositories.PostRepository;
import com.thebadtouch.searchengine.repositories.WordRepository;
import com.thebadtouch.searchengine.services.persistance.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.partition;
import static java.util.stream.Collectors.joining;

@Service
@Transactional
public class DatabaseServiceImpl implements DatabaseService {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    private final EntityManager entityManager;
    private final WordRepository wordRepository;
    private final PostRepository postRepository;
    private final DocumentRepository documentRepository;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size_insert}")
    private int insertBatchSize;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size_delete}")
    private int deleteBatchSize;
    private Map<String, Word> wordMap;

    public DatabaseServiceImpl(EntityManager entityManager, WordRepository wordRepository, PostRepository postRepository, DocumentRepository documentRepository) {
        this.entityManager = entityManager;
        this.wordRepository = wordRepository;
        this.postRepository = postRepository;
        this.documentRepository = documentRepository;
    }

    public long getTotalDocuments() {
        return documentRepository.countAll();
    }

    @Override
    @Async
    public void deleteWords(List<Long> wordsToBeDeleted) {
        partition(wordsToBeDeleted, deleteBatchSize).forEach(this::deletePostsById);
        partition(wordsToBeDeleted, deleteBatchSize).forEach(this::deleteWordsById);
    }

    private void deleteWordsById(List<Long> wordsToBeDeleted) {
        String values = wordsToBeDeleted.stream().map(Object::toString).collect(joining(", "));
        String deleteSQL = "DELETE FROM word WHERE word_id IN (" + values + ")";
        LOG.debug("About to execute query: {}", deleteSQL);
        entityManager.createNativeQuery(deleteSQL).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        LOG.info("Finished execute query");
    }

    private void deletePostsById(List<Long> wordsToBeDeleted) {
        String values = wordsToBeDeleted.stream().map(Object::toString).collect(joining(", "));
        String deleteSQL = "DELETE FROM post WHERE word_id IN (" + values + ")";
        LOG.debug("About to execute query: {}", deleteSQL);
        entityManager.createNativeQuery(deleteSQL).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        LOG.info("Finished execute query");
    }

    public void saveAndUpdateAllWords(Set<Word> wordList) {
        wordRepository.saveAll(wordList);
    }

    @Override
    public long getLastDocId() {
        Document top = documentRepository.findTopByOrderByDocIdDesc();
        if (top == null) {
            return 0;
        } else {
            return top.getDocId();
        }
    }

    @Override
    public long getLastPostId() {
        Post top = postRepository.findTopByOrderByPostIdDesc();
        if (top == null) {
            return 0;
        } else {
            return top.getPostId();
        }
    }

    @Override
    public long getLastWordId() {
        Word top = wordRepository.findTopByOrderByWordIdDesc();
        if (top == null) {
            return 0;
        } else {
            return top.getWordId();
        }
    }

    public void truncateTables() {
        postRepository.truncateTable();
        wordRepository.truncateTable();
        documentRepository.truncateTable();
    }

    @Cacheable(value = "posts")
    @Override
    public List<Post> getAllPostsForWordId(Long wordId) {
        return postRepository.findAllByWordByWordId_WordIdOrderByTermFrequencyDesc(wordId);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Override
    public void evictAllCacheValues() {
        wordMap = null;
    }

    public Word getWordByName(String name) {
        populateWordMap();
        return wordMap.get(name);
    }

    public Map<String, Word> getWordMap() {
        populateWordMap();
        return wordMap;
    }

    private void populateWordMap() {
        LOG.info("Retrieving words from database");
        if (wordMap == null) {
            List<Word> allWords = wordRepository.getAllWords();
            wordMap = new HashMap<>();
            for (Word word : allWords) {
                wordMap.put(word.getValue(), word);
            }
        }
        LOG.info("Finished retrieving words from database");
    }

    public void blindInsertPosts(List<Post> posts) {
        partition(posts, insertBatchSize).forEach(this::insertAllPosts);
    }

    private void insertAllPosts(List<Post> posts) {
        String values = posts.stream().map(this::renderSqlForPost).collect(joining(","));
        String insertSQL = "INSERT INTO post (post_id, word_id, doc_id, term_frequency) VALUES " + values;
        LOG.debug("About to execute query: {}", insertSQL);
        entityManager.createNativeQuery(insertSQL).executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    private String renderSqlForPost(Post post) {
        return "('" + post.getPostId() + "','" +
                post.getWordByWordId().getWordId() + "','" +
                post.getDocumentByDocId().getDocId() + "'," +
                post.getTermFrequency() + ")";
    }

    public void blindInsertDocs(List<Document> documents) {
        partition(documents, insertBatchSize).forEach(this::insertAllDocuments);
    }


    private void insertAllDocuments(List<Document> objects) {
        String values = objects.stream().map(this::renderSqlForDocument).collect(joining(","));
        String insertSQL = "INSERT INTO document (doc_id, name) VALUES " + values;
        LOG.debug("About to execute query: {}", insertSQL);
        entityManager.createNativeQuery(insertSQL).executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    private String renderSqlForDocument(Document doc) {
        return "('" + doc.getDocId() + "','" +
                doc.getName() + "')";
    }

    public void blindInsertWords(List<Word> words) {
        partition(words, insertBatchSize).forEach(this::insertAllWords);
    }


    private void insertAllWords(List<Word> words) {
        String values = words.stream().map(this::renderSqlForWord).collect(joining(","));
        String insertSQL = "INSERT INTO word (word_id, value, word_frequency, max_term_frequency) VALUES " + values;
        LOG.debug("About to execute query: {}", insertSQL);
        entityManager.createNativeQuery(insertSQL).executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    private String renderSqlForWord(Word word) {
        return "('" + word.getWordId() + "','" +
                word.getValue() + "','" +
                word.getWordFrequency() + "'," +
                word.getMaxTermFrequency() + ")";
    }
}
