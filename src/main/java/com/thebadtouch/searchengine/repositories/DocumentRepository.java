package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
}
