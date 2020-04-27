package com.thebadtouch.searchengine.controllers;

import com.thebadtouch.searchengine.config.Properties;
import com.thebadtouch.searchengine.constants.Constants;
import com.thebadtouch.searchengine.dto.Result;
import com.thebadtouch.searchengine.services.readers.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private final Properties properties;
    private final ResourceLoader resourceLoader;
    private final ResourceReader resourceReader;


    public SearchController(Properties properties, ResourceLoader resourceLoader, ResourceReader resourceReader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.resourceReader = resourceReader;
    }

    @RequestMapping(value = {"/file/{id}"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> retrieveFile(@PathVariable("id") String id) {
        String path = "file:" + properties.getFilesToIndex() + id;

        LOG.info("About to load file {}", path);

        if (!path.endsWith(Constants.TXT_EXTENSION)) {
            path += Constants.TXT_EXTENSION;
        }

        Resource resource = resourceLoader.getResource(path);

        ResponseEntity<Result> resultResponseEntity;
        if (resource.exists()) {
            String content = resourceReader.asString(resource);
            LOG.debug("Reading {}", content);
            Result result = Result.builder()
                    .content(content)
                    .build();
            resultResponseEntity = new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            resultResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return resultResponseEntity;
    }
}
