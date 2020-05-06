package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findAllByWordByWordId_WordId(Long wordId);
}
