package com.tchepannou.kiosk.rss.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.kiosk.rss.model.Feed;
import com.tchepannou.kiosk.rss.service.FeedLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FeedLoaderImpl implements FeedLoader {
    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Override public List<Feed> load() throws IOException{
        final InputStream in = getClass().getResourceAsStream("/feeds.json");

        final ObjectMapper mapper = jackson.build();
        return mapper.readValue(in, new TypeReference<List<Feed>>(){});
    }

    public FeedLoaderImpl withJackson2ObjectMapperBuilder(Jackson2ObjectMapperBuilder jackson) {
        this.jackson = jackson;
        return this;
    }

}
