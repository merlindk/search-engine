package com.thebadtouch.searchengine.services.indexing.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
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

    private static final String SPACE_EQUIVALENT_REGEX = "[.,\\-;*/" + System.lineSeparator() + "]";
    private static final String REMOVABLE_CHARACTERS_REGEX = "[^\\w\\s]";
    private static final String MULTIPLE_SPACE = " +";
    private static final String SPACE = " ";
    private static final String EMPTY = "";


    private final ResourceReader resourceReader;

    public IndexingServiceImpl(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    public Set<Post> indexResources(Set<Resource> resourceSet) {
        Set<Post> postList = new TreeSet<>();

        Map<String, Word> wordMap = new HashMap<>();
        int resourceCount = resourceSet.size();
        int currentResourceCount = 1;
        for (Resource resource : resourceSet) {
            LOG.info("Processing resource {} out of {}", currentResourceCount, resourceCount);
            currentResourceCount++;
            String docName = resource.getFilename();
            Document document = Document.builder().name(docName).build();

            String original = resourceReader.asString(resource).toLowerCase();

            postList.addAll(generatePostList(original, document, wordMap));
        }
        return postList;
    }

    @Override
    public List<Post> generatePostList(String original, Document document, Map<String, Word> wordMap) {
        Map<String, Post> subPostMap = new HashMap<>();

        String purged = purgeText(original);

        String[] words = purged.split(SPACE);

        for (String word : words) {
            Word currentWord = getCurrentWord(wordMap, word);

            Post existingPost = getPost(subPostMap, document, currentWord);
            existingPost.incrementTermFrequency();

            long vocabularyMaxFrequency = currentWord.getMaxTermFrequency();
            long existingPostFrequency = existingPost.getTermFrequency();

            if (existingPostFrequency > vocabularyMaxFrequency) {
                currentWord.setMaxTermFrequency(existingPostFrequency);
            }
        }

        return new ArrayList<>(subPostMap.values());
    }

    private String purgeText(String content) {
        content = content.replaceAll(SPACE_EQUIVALENT_REGEX, SPACE);

        content = content.replaceAll(REMOVABLE_CHARACTERS_REGEX, EMPTY);

        content = content.trim().replaceAll(MULTIPLE_SPACE, SPACE);

        return content;
    }

    private Post getPost(Map<String, Post> subPostMap, Document document, Word currentWord) {
        String key = currentWord.getValue() + document.getName();
        Post existingPost = subPostMap.get(key);
        if (existingPost == null) {
            currentWord.addToFrequency();
            existingPost = Post.builder()
                    .documentByDocId(document)
                    .termFrequency(0L)
                    .wordByWordId(currentWord)
                    .build();
            subPostMap.put(key, existingPost);
        }
        return existingPost;
    }

    private Word getCurrentWord(Map<String, Word> wordMap, String word) {
        Word currentWord = wordMap.get(word);
        if (currentWord == null) {
            currentWord = Word.builder()
                    .value(word)
                    .maxTermFrequency(1L)
                    .wordFrequency(0L)
                    .build();
            wordMap.put(word, currentWord);
        }
        return currentWord;
    }
}
