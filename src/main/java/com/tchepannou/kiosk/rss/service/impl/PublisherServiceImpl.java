package com.tchepannou.kiosk.rss.service.impl;

import com.tchepannou.kiosk.rss.model.Publisher;
import com.tchepannou.kiosk.rss.service.PublisherLoader;
import com.tchepannou.kiosk.rss.service.PublisherService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PublisherServiceImpl implements PublisherService {
    //-- Attributes
    private PublisherLoader loader;
    private List<Publisher> publishers;

    //-- Constructor
    public PublisherServiceImpl (PublisherLoader loader){
        this.loader = loader;
    }

    //-- PublisherService
    @Override public List<Publisher> getPublishers() {
        if (publishers == null){
            try {
                publishers = loader.load();
            } catch (IOException e){
                throw new IllegalStateException("Unable to load publishers", e);
            }
        }
        return Collections.unmodifiableList(publishers);
    }
}
