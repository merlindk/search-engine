package com.thebadtouch.searchengine.services.search.impl;

import com.thebadtouch.searchengine.entities.Document;
import com.thebadtouch.searchengine.entities.Post;
import com.thebadtouch.searchengine.entities.Word;
import com.thebadtouch.searchengine.repositories.PostRepository;
import com.thebadtouch.searchengine.repositories.WordRepository;
import com.thebadtouch.searchengine.services.indexing.IndexingService;
import com.thebadtouch.searchengine.services.search.SearchService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private static final int R = 5;

    private final IndexingService indexingService;
    private final WordRepository wordRepository;
    private final PostRepository postRepository;

    public SearchServiceImpl(IndexingService indexingService, WordRepository wordRepository,
                             PostRepository postRepository) {
        this.indexingService = indexingService;
        this.wordRepository = wordRepository;
        this.postRepository = postRepository;
    }

    @Override
    public String searchQuery(String query) {
        List<Post> queryPosts = indexingService.generatePostList(query, null, new HashMap<>());
        Set<Word> wordSet = new TreeSet<>();
        List<Document> documentList = new ArrayList<>();
        for (Post post : queryPosts) {
            String word = post.getWordByWordId().getValue();
            Word foundWord = wordRepository.findByValue(word);
            if(foundWord != null) {
                wordSet.add(foundWord);
            }
        }

        for (Word word : wordSet) {
            List<Post> postsForWord = postRepository.findAllByWordByWordId_WordId(word.getWordId());
            for (int i = 0; i < postsForWord.size(); i++) {
                if(i == R){
                    break;
                }
                documentList.add(postsForWord.get(i).getDocumentByDocId());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Document document: documentList) {
            sb.append(document.getName());
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
