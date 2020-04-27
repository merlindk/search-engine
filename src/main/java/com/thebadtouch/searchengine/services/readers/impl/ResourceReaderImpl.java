package com.thebadtouch.searchengine.services.readers.impl;

import com.thebadtouch.searchengine.config.Properties;
import com.thebadtouch.searchengine.services.readers.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


@Service
public class ResourceReaderImpl implements ResourceReader {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceReaderImpl.class);
    private final Properties properties;

    public ResourceReaderImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String asString(Resource resource) {
        if (resource == null) {
            LOG.error("File cannot be null");
            return null;
        }
        try (Reader reader = new InputStreamReader(resource.getInputStream(), properties.getEncoding())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            LOG.error("Unable to read file {}", resource.getFilename(), e);
            return null;
        }
    }
}
