package com.thebadtouch.searchengine.controllers;

import com.thebadtouch.searchengine.dto.Result;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.repositories.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private PostRepository postRepository;

    public SearchController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @RequestMapping(value = {"/search/{id}"}, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> processXml(@PathVariable("id") Long id) {
        LOG.info("Received request with id {}", id);
        Optional<Post> optionalPost = postRepository.findById(id);
        ResponseEntity<Result> resultResponseEntity;
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Result result = Result.builder()
                    .content(post.getContent())
                    .build();
            resultResponseEntity = new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            resultResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return resultResponseEntity;
    }
}
