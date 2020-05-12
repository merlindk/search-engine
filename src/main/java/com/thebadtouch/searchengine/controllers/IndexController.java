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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public IndexController(Properties properties,
                           IndexingService indexingService,
                           PostRepository postRepository,
                           WordRepository wordRepository,
                           DocumentRepository documentRepository) {
        this.properties = properties;
        this.indexingService = indexingService;
        this.postRepository = postRepository;
        this.wordRepository = wordRepository;
        this.documentRepository = documentRepository;
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> startIndexing() {
        postRepository.deleteAll();
        wordRepository.deleteAll();
        documentRepository.deleteAll();
        String path = properties.getFilesToIndex();
        Set<Resource> resources = Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(FileSystemResource::new)
                .collect(Collectors.toSet());

        List<Post> postList = indexingService.indexResources(resources);
        Set<Document> documentList = new HashSet<>();
        Set<Word> wordList = new HashSet<>();
        for (Post post : postList) {
            documentList.add(post.getDocumentByDocId());
            wordList.add(post.getWordByWordId());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        LOG.info("About to save {} documents", documentList.size());
        LOG.info("About to save {} words", wordList.size());
        LOG.info("About to save {} posts", postList.size());
        documentRepository.saveAll(documentList);
        wordRepository.saveAll(wordList);
        List<Post> storedPostList = (List<Post>) postRepository.saveAll(postList);
        LOG.info("Saved {} Posts", storedPostList.size());
        LOG.info("Finished indexing in {}", stopwatch.elapsed(TimeUnit.SECONDS));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
