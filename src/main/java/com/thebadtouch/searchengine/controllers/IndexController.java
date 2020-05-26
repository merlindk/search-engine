package com.thebadtouch.searchengine.controllers;

import com.google.common.base.Stopwatch;
import com.thebadtouch.searchengine.config.Properties;
import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.indexing.impl.IndexingServiceImpl;
import com.thebadtouch.searchengine.services.persistance.DatabaseService;
import com.thebadtouch.searchengine.services.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final DatabaseService databaseService;
    private final StorageService storageService;

    public IndexController(Properties properties,
                           IndexingService indexingService,
                           DatabaseService databaseService, StorageService storageService) {
        this.properties = properties;
        this.indexingService = indexingService;
        this.databaseService = databaseService;
        this.storageService = storageService;
    }

    @RequestMapping(value = {"/index/new"}, method = {RequestMethod.POST},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> startIndexingNew(@RequestParam("file") Set<MultipartFile> newFiles) {

        for (MultipartFile newFile : newFiles) {
            storageService.store(newFile);
        }

        String path = properties.getNewFiles();
        Set<Resource> resources = Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .filter(file -> !file.getName().startsWith("."))
                .map(FileSystemResource::new)
                .collect(Collectors.toSet());

        List<Post> postList = indexingService.indexResources(resources, 0.8);

        Set<Document> documentList = new HashSet<>();
        Set<Word> wordList = new HashSet<>();
        for (Post post : postList) {
            documentList.add(post.getDocumentByDocId());
            wordList.add(post.getWordByWordId());
        }

        LOG.info("About to save {} documents", documentList.size());
        LOG.info("About to save {} words", wordList.size());
        LOG.info("About to save {} posts", postList.size());

        Stopwatch stopwatch = Stopwatch.createStarted();
        databaseService.blindInsertDocs(new ArrayList<>(documentList));
        LOG.info("Finished saving docs via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch = Stopwatch.createStarted();
        databaseService.saveAndUpdateAllWords(wordList);
        LOG.info("Finished saving words via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch = Stopwatch.createStarted();
        databaseService.blindInsertPosts(postList);
        LOG.info("Finished saving posts via dbsvc in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        String response = String.format("Finished indexing %s documents, %s words and %s posts", documentList.size(), wordList.size(), postList.size());
        storageService.moveAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = {"/index/{stopWordsPercentage}"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> startIndexing(@PathVariable(value = "stopWordsPercentage") double stopWordsPercentage) {

        databaseService.truncateTables();

        String path = properties.getFilesToIndex();
        Set<Resource> resources = Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .filter(file -> !file.getName().startsWith("."))
                .map(FileSystemResource::new)
                .collect(Collectors.toSet());

        List<Post> postList = indexingService.indexResources(resources, stopWordsPercentage);
        Set<Document> documentList = new HashSet<>();
        Set<Word> wordList = new HashSet<>();
        for (Post post : postList) {
            documentList.add(post.getDocumentByDocId());
            wordList.add(post.getWordByWordId());
        }

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
        String response = String.format("Finished indexing %s documents, %s words and %s posts", documentList.size(), wordList.size(), postList.size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> startIndexing() {
        return startIndexing(0.8);
    }
}
