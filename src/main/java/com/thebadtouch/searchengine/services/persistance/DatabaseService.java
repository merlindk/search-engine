package com.thebadtouch.searchengine.services.persistance;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;

import java.util.List;

public interface DatabaseService {
    void blindInsertWords(List<Word> objects);
    void blindInsertPosts(List<Post> posts);
    void blindInsertDocs(List<Document> documents);
}
