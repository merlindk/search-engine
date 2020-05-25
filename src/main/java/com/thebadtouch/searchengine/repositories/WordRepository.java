package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface WordRepository extends CrudRepository<Word, Long> {

    Word findTopByOrderByWordIdDesc();

    @Query(value = "select * from word", nativeQuery = true)
    List<Word> getAllWords();

    @Modifying
    @Transactional
    @Query(value = "truncate table word restart identity cascade", nativeQuery = true)
    void truncateTable();

}
