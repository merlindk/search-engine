package com.thebadtouch.searchengine.services.persistance;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabaseService {
    void blindInsertWords(List<Word> objects);

    void blindInsertPosts(List<Post> posts);

    void blindInsertDocs(List<Document> documents);

    Word getWordByName(String name);

    List<Post> getAllPostsForWordId(Long wordId);

    Map<String, Word> getWordMap();

    void truncateTables();

    void saveAndUpdateAllWords(Set<Word> wordList);

    long getLastPostId();

    long getLastWordId();

    long getLastDocId();

    long getTotalDocuments();

    void deleteWords(List<Long> wordsToBeDeleted);

    void evictAllCacheValues();
}
