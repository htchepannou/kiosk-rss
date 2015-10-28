package com.tchepannou.kiosk.rss.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.tchepannou.kiosk.rss.service.FeedLoader;
import com.tchepannou.kiosk.rss.service.FeedService;
import com.tchepannou.kiosk.rss.service.RSSService;
import com.tchepannou.kiosk.rss.service.URLService;
import com.tchepannou.kiosk.rss.service.impl.FeedLoaderImpl;
import com.tchepannou.kiosk.rss.service.impl.FeedServiceImpl;
import com.tchepannou.kiosk.rss.service.impl.RSSServiceImpl;
import com.tchepannou.kiosk.rss.service.impl.URLServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    //--- Attributes
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Value("${amazon.access_key}")
    private String accessKey;

    @Value("${amazon.secret_key}")
    private String secretKey;


    //-- Beans
    @Bean AWSCredentials awsCredentials (){
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean AmazonS3 s3 (){
        return new AmazonS3Client(awsCredentials());
    }

    @Bean FeedService feedService (){
        return new FeedServiceImpl(feedLoader());
    }

    @Bean FeedLoader feedLoader (){
        return new FeedLoaderImpl();
    }

    @Bean RSSService rssService (){
        return new RSSServiceImpl();
    }

    @Bean URLService urlService (){
        return new URLServiceImpl();
    }

    //-- Cron
    @Scheduled(cron = "${rss.fetcher.cron}")
    public void fetch (){
        try {
            rssService().fetch();
        } catch (Exception e){
            LOGGER.warn("Unable to fetch", e);
        }
    }
}
