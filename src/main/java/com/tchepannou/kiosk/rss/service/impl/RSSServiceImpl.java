package com.tchepannou.kiosk.rss.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.kiosk.rss.model.Item;
import com.tchepannou.kiosk.rss.model.Feed;
import com.tchepannou.kiosk.rss.service.FeedService;
import com.tchepannou.kiosk.rss.service.RSSService;
import com.tchepannou.kiosk.rss.service.URLService;
import com.tchepannou.kiosk.rss.util.RSSFeedSAXHandler;
import com.tchepannou.kiosk.rss.util.S3Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class RSSServiceImpl implements RSSService {
    //-- Attributes
    private static final Logger LOGGER = LoggerFactory.getLogger(RSSServiceImpl.class);

    @Value("${amazon.s3.bucket}")
    private String bucket;

    @Value("${amazon.s3.folder.items}")
    private String itemFolder;

    @Autowired
    private FeedService feedService;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Autowired
    private AmazonS3 s3;

    @Autowired
    private URLService urlService;

    @Autowired
    private MetricRegistry metrics;


    //-- RSSService overrides
    @Override public void fetch() {
        Timer.Context timer = metrics.timer(name(getClass(), "fetch-latency")).time();
        try {
            metrics.meter(name(getClass(), "fetch")).mark();

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser sax = factory.newSAXParser();

            final ObjectMapper mapper = jackson.build();
            final List<Feed> feeds = feedService.getFeeds();

            for (Feed feed : feeds) {
                if (!"rss".equals(feed.getFeedType())) {
                    continue;
                }

                try {

                    List<Item> items = fetch(feed, sax);
                    LOGGER.info("{} items fetched from <{}>", items.size(), feed.getName());

                    int saved = store(feed, items, mapper);
                    LOGGER.info("{} items saved from <{}>", saved, feed.getName());

                } catch (Exception e){

                    LOGGER.warn("Unexpected error when processing feed <{}>", feed.getName(), e);
                    metrics.meter(name(getClass(), "fetch-error")).mark();

                }
            }

        } catch (SAXException | ParserConfigurationException e) {

            metrics.meter(name(getClass(), "fetch-error")).mark();
            throw new IllegalStateException("Unable create XML parser", e);

        } finally {

            timer.stop();

        }
    }


    //-- Private
    private List<Item> fetch (Feed feed, SAXParser sax) throws SAXException, IOException {
        final URL url = new URL(feed.getFeed());
        try (final InputStream in = urlService.fetch(url)) {
            final RSSFeedSAXHandler handler = new RSSFeedSAXHandler();

            sax.parse(in, handler);
            return handler.getItems();
        }
    }

    private int store (Feed feed, List<Item> items, ObjectMapper mapper) throws IOException{
        final S3Helper s3Helper = new S3Helper(s3);
        int count = 0;
        for (Item item : items) {
            final String key = String.format("%s/%d/%s.json", itemFolder, feed.getId(), item.getId());
            final String json = mapper.writeValueAsString(item);

            if (!s3Helper.exist(bucket, key)) {
                s3Helper.write(
                        bucket,
                        key,
                        new ByteArrayInputStream(json.getBytes("utf-8")),
                        "application/json"
                );
                count++;
                metrics.meter(name(getClass(), "item")).mark();
            }
        }
        return count;
    }
}
