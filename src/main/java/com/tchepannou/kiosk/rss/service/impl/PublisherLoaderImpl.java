package com.tchepannou.kiosk.rss.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.kiosk.rss.model.Publisher;
import com.tchepannou.kiosk.rss.service.PublisherLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PublisherLoaderImpl implements PublisherLoader {
    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Override public List<Publisher> load() throws IOException{
        final InputStream in = getClass().getResourceAsStream("/publishers.json");

        final ObjectMapper mapper = jackson.build();
        return mapper.readValue(in, new TypeReference<List<Publisher>>(){});
    }

    public PublisherLoaderImpl withJackson2ObjectMapperBuilder(Jackson2ObjectMapperBuilder jackson) {
        this.jackson = jackson;
        return this;
    }

}
