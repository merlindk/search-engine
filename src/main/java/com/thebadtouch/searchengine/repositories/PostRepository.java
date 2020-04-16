package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface PostRepository extends CrudRepository<Post, Long> {
}
