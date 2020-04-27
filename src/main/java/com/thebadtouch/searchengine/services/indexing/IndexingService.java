package com.thebadtouch.searchengine.services.indexing;

import com.thebadtouch.searchengine.entities.Post;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Set;

public interface IndexingService {

    List<Post> generatePostList(Set<Resource> resourceSet);
}
