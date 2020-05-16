package com.thebadtouch.searchengine.controllers;

import com.google.common.base.Stopwatch;
import com.thebadtouch.searchengine.config.Properties;
import com.thebadtouch.searchengine.dto.Result;
import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.repositories.DocumentRepository;
import com.thebadtouch.searchengine.repositories.PostRepository;
import com.thebadtouch.searchengine.repositories.WordRepository;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.indexing.impl.IndexingServiceImpl;
import com.thebadtouch.searchengine.services.persistance.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class IndexController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexingServiceImpl.class);

    private final Properties properties;

    private final IndexingService indexingService;
    private final PostRepository postRepository;
    private final WordRepository wordRepository;
    private final DocumentRepository documentRepository;
    private final DatabaseService databaseService;

    public IndexController(Properties properties,
                           IndexingService indexingService,
                           PostRepository postRepository,
                           WordRepository wordRepository,
                           DocumentRepository documentRepository,
                           DatabaseService databaseService) {
        this.properties = properties;
        this.indexingService = indexingService;
        this.postRepository = postRepository;
        this.wordRepository = wordRepository;
        this.documentRepository = documentRepository;
        this.databaseService = databaseService;
    }

    @RequestMapping(value = {"/index/{stopWordsPercentage}"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> startIndexing(@PathVariable(value = "stopWordsPercentage") double stopWordsPercentage) {

        postRepository.truncateTable();
        wordRepository.truncateTable();
        documentRepository.truncateTable();

        String path = properties.getFilesToIndex();
        Set<Resource> resources = Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(FileSystemResource::new)
                .collect(Collectors.toSet());

        List<Post> postList = indexingService.indexResources(resources, stopWordsPercentage);
        Set<Document> documentList = new HashSet<>();
        Set<Word> wordList = new HashSet<>();
        for (Post post : postList) {
            documentList.add(post.getDocumentByDocId());
            wordList.add(post.getWordByWordId());
        }

        Collections.sort(postList);

        LOG.info("About to save {} documents", documentList.size());
        LOG.info("About to save {} words", wordList.size());
        LOG.info("About to save {} posts", postList.size());

        Stopwatch stopwatch = Stopwatch.createStarted();
        databaseService.blindInsertDocs(new ArrayList<>(documentList));
        LOG.info("Finished saving docs via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch = Stopwatch.createStarted();
        databaseService.blindInsertWords(new ArrayList<>(wordList));
        LOG.info("Finished saving words via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch = Stopwatch.createStarted();
        databaseService.blindInsertPosts(postList);
        LOG.info("Finished saving posts via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> startIndexing() {
        return startIndexing(0.8);
    }
}
