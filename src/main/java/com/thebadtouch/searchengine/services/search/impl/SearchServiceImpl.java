package com.thebadtouch.searchengine.services.search.impl;

import com.google.common.base.Stopwatch;
import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.persistance.DatabaseService;
import com.thebadtouch.searchengine.services.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);


    private static final int R = 5;
    private final Long totalCount;

    private final IndexingService indexingService;
    private final DatabaseService databaseService;

    public SearchServiceImpl(IndexingService indexingService,
                             DatabaseService databaseService) {
        this.indexingService = indexingService;
        this.databaseService = databaseService;
        totalCount = databaseService.getTotalDocuments();
    }

    @Override
    public String searchQuery(String query) {
        List<Post> queryPosts = indexingService.generatePostList(query, Document.builder().name("query").build(), new HashMap<>());
        LOG.info("Recovered {} posts for query", queryPosts.size());
        List<Word> wordList = new ArrayList<>();
        Set<Document> queryResultDocumentSet = new HashSet<>();
        for (Post post : queryPosts) {
            String word = post.getWordByWordId().getValue();
            Word foundWord = databaseService.getWordByName(word);
            if (foundWord != null) {
                wordList.add(foundWord);
                LOG.info("Word {} found in db", word);
            } else {
                LOG.info("Word {} not found in db", word);
            }
        }

        Collections.sort(wordList);
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (Word word : wordList) {
            List<Post> postsForWord = databaseService.getAllPostsForWordId(word.getWordId());
            LOG.info("Recovered {} posts for word {}", postsForWord.size(), word.getValue());
            for (int i = 0; i < postsForWord.size(); i++) {
                if (i == R) {
                    break;
                }
                Post currentPost = postsForWord.get(i);
                Document currentDocument = currentPost.getDocumentByDocId();
                double calculatedWeight = calculateWeight(totalCount, currentPost.getTermFrequency(), word.getWordFrequency());
                LOG.info("Calculated weight {} for word {} in document {}", calculatedWeight, word.getValue(), currentDocument.getName());
                currentDocument.addWeight(calculatedWeight);
                queryResultDocumentSet.add(currentDocument);

            }
        }
        LOG.info("Took {}s to retrieve all posts for all words", stopwatch.elapsed(TimeUnit.SECONDS));
        List<Document> documentList = new ArrayList<>(queryResultDocumentSet);
        Collections.sort(documentList);

        StringBuilder sb = new StringBuilder();
        for (Document document : documentList) {
            sb.append(document.getName());
            sb.append(": ");
            sb.append(document.getWeight());
            sb.append(System.lineSeparator());
            //Vuelve el peso a cero de los Docs cacheados
            document.setWeight(0D);
        }

        return sb.toString();
    }

    private double calculateWeight(double totalDocuments, double termFrequency, double wordFrequency) {
        return termFrequency * Math.log(totalDocuments / wordFrequency);
    }
}
