package com.thebadtouch.searchengine.services.readers;

import org.springframework.core.io.Resource;

public interface ResourceReader {

    String asString(Resource resource);
}
