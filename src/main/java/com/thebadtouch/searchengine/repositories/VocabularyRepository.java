package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Vocabulary;
import org.springframework.data.repository.CrudRepository;

public interface VocabularyRepository extends CrudRepository<Vocabulary, Integer> {
}
