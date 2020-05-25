package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Document;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DocumentRepository extends CrudRepository<Document, Long> {


    Document findTopByOrderByDocIdDesc();

    @Query(value = "select count(*) from document", nativeQuery = true)
    Long countAll();

    @Modifying
    @Query(value = "truncate table document restart identity cascade", nativeQuery = true)
    void truncateTable();
}
