package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findAllByWordByWordId_WordId(Long wordId);

    @Modifying
    @Transactional
    @Query(value = "truncate table post restart identity cascade", nativeQuery = true)
    void truncateTable();
}
