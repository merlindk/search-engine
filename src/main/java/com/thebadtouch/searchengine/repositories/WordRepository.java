package com.thebadtouch.searchengine.repositories;

import com.thebadtouch.searchengine.entities.Word;
import org.springframework.data.repository.CrudRepository;

public interface WordRepository extends CrudRepository<Word, Long> {
    Word findByValue(String value);
}
