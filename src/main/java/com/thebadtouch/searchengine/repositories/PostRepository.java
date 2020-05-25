package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PostRepository extends CrudRepository<Post, Long> {

    Post findTopByOrderByPostIdDesc();

    List<Post> findAllByWordByWordId_WordIdOrderByTermFrequencyDesc(Long wordId);

    @Modifying
    @Transactional
    @Query(value = "truncate table post restart identity cascade", nativeQuery = true)
    void truncateTable();

    void deleteAllByWordByWordId(Word wordId);
}
