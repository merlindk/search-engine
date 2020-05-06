package com.thebadtouch.searchengine.services.indexing;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.core.io.Resource;

import java.util.Set;

public interface IndexingService {

    Set<Post> generatePostList(Set<Resource> resourceSet);
}
