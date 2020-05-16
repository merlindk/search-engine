package com.thebadtouch.searchengine.services.persistance.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.repositories.PostRepository;
import com.thebadtouch.searchengine.repositories.WordRepository;
import com.thebadtouch.searchengine.services.persistance.DatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.partition;
import static java.util.stream.Collectors.joining;

@Service
@Transactional
public class DatabaseServiceImpl implements DatabaseService {

    private final EntityManager entityManager;
    private final WordRepository wordRepository;
    private final PostRepository postRepository;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int insertBatchSize;
    private Map<String, Word> wordMap;

    public DatabaseServiceImpl(EntityManager entityManager, WordRepository wordRepository, PostRepository postRepository) {
        this.entityManager = entityManager;
        this.wordRepository = wordRepository;
        this.postRepository = postRepository;
    }

    @Cacheable(value = "posts")
    public List<Post> getAllPostsForWordId(Long wordId) {
        return postRepository.findAllByWordByWordId_WordId(wordId);
    }

    public Word getWordByName(String name) {
        if (wordMap == null) {
            List<Word> allWords = wordRepository.getAllWords();

            wordMap = new HashMap<>();
            for (Word word : allWords) {
                wordMap.put(word.getValue(), word);
            }
        }
        return wordMap.get(name);
    }

    public void blindInsertPosts(List<Post> posts) {
        partition(posts, insertBatchSize).forEach(this::insertAllPosts);
    }

    private void insertAllPosts(List<Post> posts) {
        String values = posts.stream().map(this::renderSqlForPost).collect(joining(","));
        String insertSQL = "INSERT INTO post (post_id, word_id, doc_id, term_frequency) VALUES ";
        entityManager.createNativeQuery(insertSQL + values).executeUpdate();
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
        String insertSQL = "INSERT INTO document (doc_id, name) VALUES ";
        entityManager.createNativeQuery(insertSQL + values).executeUpdate();
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
        String insertSQL = "INSERT INTO word (word_id, value, word_frequency, max_term_frequency) VALUES ";
        entityManager.createNativeQuery(insertSQL + values).executeUpdate();
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
