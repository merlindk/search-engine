package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    @Override
    void deleteAll();
    List<Post> findAllByWordByWordId_WordId(Long wordId);
}
