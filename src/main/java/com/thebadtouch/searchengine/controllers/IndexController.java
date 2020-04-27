package com.thebadtouch.searchengine.controllers;

import com.thebadtouch.searchengine.config.Properties;
import com.thebadtouch.searchengine.dto.Result;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.readers.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
    private final Properties properties;
    private final ResourceLoader resourceLoader;

    private final IndexingService indexingService;

    public IndexController(Properties properties, ResourceLoader resourceLoader, ResourceReader resourceReader,
                           IndexingService indexingService) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.resourceReader = resourceReader;
        this.indexingService = indexingService;
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> startIndexing() {
        String path = properties.getFilesToIndex();
        Set<Resource> resources = Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(FileSystemResource::new)
                .collect(Collectors.toSet());

        indexingService.generatePostList(resources);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
