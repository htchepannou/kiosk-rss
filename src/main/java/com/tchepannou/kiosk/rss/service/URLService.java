package com.tchepannou.kiosk.rss.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface URLService {
    InputStream fetch (URL url) throws IOException;
}
