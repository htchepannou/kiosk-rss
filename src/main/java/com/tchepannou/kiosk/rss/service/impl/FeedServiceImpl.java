package com.tchepannou.kiosk.rss.service.impl;

import com.tchepannou.kiosk.rss.model.Feed;
import com.tchepannou.kiosk.rss.service.FeedLoader;
import com.tchepannou.kiosk.rss.service.FeedService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FeedServiceImpl implements FeedService {
    //-- Attributes
    private FeedLoader loader;
    private List<Feed> feeds;

    //-- Constructor
    public FeedServiceImpl(FeedLoader loader){
        this.loader = loader;
    }

    //-- PublisherService
    @Override public List<Feed> getFeeds() {
        if (feeds == null){
            try {
                feeds = loader.load();
            } catch (IOException e){
                throw new IllegalStateException("Unable to load feeds", e);
            }
        }
        return Collections.unmodifiableList(feeds);
    }
}
