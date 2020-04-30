package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
}
