package com.thebadtouch.searchengine.services.indexing;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IndexingService {

    List<Post> indexResources(Set<Resource> resourceSet, double stopWordsPercentage);

    List<Post> generatePostList(String original, Document document, Map<String, Word> wordMap);
}
