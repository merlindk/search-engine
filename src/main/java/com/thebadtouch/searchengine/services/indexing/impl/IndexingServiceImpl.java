package com.thebadtouch.searchengine.services.indexing.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Vocabulary;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.readers.ResourceReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final ResourceReader resourceReader;

    public IndexingServiceImpl(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    public List<Post> generatePostList(Set<Resource> resourceSet) {
        List<Post> postList = new ArrayList<>();
        for (Resource resource: resourceSet) {
            Map<String, Vocabulary> vocabulary = new HashMap<>();
            String fileName = resource.getFilename();
            Document document = new Document();
            document.setName(fileName);
            String[] content = resourceReader.asString(resource).split(" ");

            for (String word: content) {
                Vocabulary current = vocabulary.get(word);
                if(current == null){
                    current = new Vocabulary();
                    current.setWord(word);
                    current.setMaxTermFrequency(1L);
                    vocabulary.put(word, current);
                } else {
                    current.setMaxTermFrequency(1L);
                }
            }
        }
        return null;
    }
}
