package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Word;
import org.springframework.data.repository.CrudRepository;

public interface VocabularyRepository extends CrudRepository<Word, Integer> {
}
