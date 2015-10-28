package com.tchepannou.kiosk.rss.service;

import com.tchepannou.kiosk.rss.model.Feed;

import java.io.IOException;
import java.util.List;

public interface FeedLoader {
    List<Feed> load () throws IOException;
}
