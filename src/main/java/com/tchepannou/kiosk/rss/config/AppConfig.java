package com.tchepannou.kiosk.rss.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.tchepannou.kiosk.rss.service.PublisherLoader;
import com.tchepannou.kiosk.rss.service.PublisherService;
import com.tchepannou.kiosk.rss.service.impl.PublisherLoaderImpl;
import com.tchepannou.kiosk.rss.service.impl.PublisherServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    //--- Attributes
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

    @Bean PublisherService publisherService (){
        return new PublisherServiceImpl(publisherLoader());
    }

    @Bean PublisherLoader publisherLoader (){
        return new PublisherLoaderImpl();
    }
}
