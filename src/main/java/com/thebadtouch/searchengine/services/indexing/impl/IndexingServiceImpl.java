package com.thebadtouch.searchengine.services.indexing.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Vocabulary;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.readers.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexingServiceImpl implements IndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexingServiceImpl.class);


    private final ResourceReader resourceReader;

    public IndexingServiceImpl(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    public List<Post> generatePostList(Set<Resource> resourceSet) {
        List<Post> postList = new ArrayList<>();
        Map<String, Vocabulary> vocabularyMap = new HashMap<>();
        int resourceCount = resourceSet.size();
        int currentResourceCount = 1;
        for (Resource resource : resourceSet) {
            LOG.info("Processing resource {} out of {}", currentResourceCount, resourceCount);
            currentResourceCount++;
            String docName = resource.getFilename();
            Document document = Document.builder().name(docName).build();

            Map<String, Post> subPostMap = new HashMap<>();

            String content = resourceReader.asString(resource).toLowerCase();
            String regex = "[.,\\-;/*" + System.lineSeparator() + "]";
            content = content.replaceAll(regex, " ");

            //content = content.replaceAll("[\"'()\\[\\]{}\\t:¿?!¡^\\d»]", "");
            content = content.replaceAll("[^\\w\\s]", "");

            content = content.trim().replaceAll(" +", " ");

            String[] words = content.split(" ");

            for (String word : words) {
                Vocabulary currentWord = getVocabulary(vocabularyMap, word);

                Post existingPost = getPost(subPostMap, document, currentWord);
                existingPost.incrementTermFrequency();

                long vocabularyMaxFrequency = currentWord.getMaxTermFrequency();
                long existingPostFrequency = existingPost.getTermFrequency();

                if (existingPostFrequency > vocabularyMaxFrequency) {
                    currentWord.setMaxTermFrequency(existingPostFrequency);
                }
            }
            postList.addAll(subPostMap.values());
        }
        return postList;
    }

    private Post getPost(Map<String, Post> subPostMap, Document document, Vocabulary currentWord) {
        Post existingPost = subPostMap.get(currentWord.getWord());
        if (existingPost == null) {
            existingPost = Post.builder()
                    .documentByDocId(document)
                    .termFrequency(0)
                    .vocabularyByVocabId(currentWord)
                    .build();
            subPostMap.put(currentWord.getWord(), existingPost);
        }
        return existingPost;
    }

    private Vocabulary getVocabulary(Map<String, Vocabulary> vocabularyMap, String word) {
        Vocabulary currentWord = vocabularyMap.get(word);
        if (currentWord == null) {
            currentWord = Vocabulary.builder()
                    .word(word)
                    .maxTermFrequency(1L)
                    .build();
            vocabularyMap.put(word, currentWord);
        }
        return currentWord;
    }
}
