package com.tchepannou.kiosk.rss.service;

import com.tchepannou.kiosk.rss.model.Publisher;

import java.io.IOException;
import java.util.List;

public interface PublisherLoader {
    List<Publisher> load () throws IOException;
}
