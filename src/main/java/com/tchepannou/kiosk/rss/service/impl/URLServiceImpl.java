package com.tchepannou.kiosk.rss.service.impl;

import com.tchepannou.kiosk.rss.service.URLService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLServiceImpl implements URLService {
    @Override public InputStream fetch(URL url) throws IOException {
        return url.openStream();
    }
}
