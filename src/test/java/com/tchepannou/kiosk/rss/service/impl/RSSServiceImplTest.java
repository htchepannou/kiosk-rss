package com.tchepannou.kiosk.rss.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.kiosk.rss.model.Feed;
import com.tchepannou.kiosk.rss.service.FeedService;
import com.tchepannou.kiosk.rss.service.URLService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import static com.codahale.metrics.MetricRegistry.name;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RSSServiceImplTest {
    @Mock private FeedService publisherService;
    @Mock private AmazonS3 s3;
    @Mock private URLService urlService;
    @Mock private MetricRegistry metrics;
    @Mock private Meter fetchMeter;
    @Mock private Meter errorMeter;
    @Mock private Meter itemMeter;
    @Mock private Timer.Context timerContext;
    @Mock private Jackson2ObjectMapperBuilder jackson;

    @InjectMocks private RSSServiceImpl service = new RSSServiceImpl();


    @Before
    public void setUp (){
        Timer timer = mock(Timer.class);
        when(timer.time()).thenReturn(timerContext);

        when (jackson.build()).thenReturn(new ObjectMapper());

        when(metrics.meter(name(RSSServiceImpl.class, "item"))).thenReturn(itemMeter);
        when(metrics.meter(name(RSSServiceImpl.class, "fetch"))).thenReturn(fetchMeter);
        when(metrics.meter(name(RSSServiceImpl.class, "fetch-error"))).thenReturn(errorMeter);
        when(metrics.timer(name(RSSServiceImpl.class, "fetch-latency"))).thenReturn(timer);
    }

    @Test
    public void fetch() throws Exception {
        // Given
        Feed p1 = createPublisher("rss");
        Feed p2 = createPublisher("rss");
        Feed p3 = createPublisher("tweeter");
        when(publisherService.getFeeds()).thenReturn(Arrays.asList(p1, p2, p3));

        when(urlService.fetch(new URL(p1.getFeed()))).thenReturn(getClass().getResourceAsStream("/rss_1.xml"));
        when(urlService.fetch(new URL(p2.getFeed()))).thenReturn(getClass().getResourceAsStream("/rss_2.xml"));

        AmazonS3Exception ex = mock(AmazonS3Exception.class);
        when(ex.getStatusCode()).thenReturn(404);
        when(s3.getObject(any(GetObjectRequest.class))).thenThrow(ex);

        // When
        service.fetch();

        // Then
        verify(timerContext).stop();
        verify(fetchMeter).mark();
        verify(itemMeter, times(2)).mark();
        verify(errorMeter, never()).mark();

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3, times(2)).putObject(request.capture());
    }

    @Test
    public void fetchWithURLFetchError() throws Exception {
        // Given
        Feed p1 = createPublisher("rss");
        Feed p2 = createPublisher("rss");
        Feed p3 = createPublisher("tweeter");
        when(publisherService.getFeeds()).thenReturn(Arrays.asList(p1, p2, p3));

        when(urlService.fetch(new URL(p1.getFeed()))).thenReturn(getClass().getResourceAsStream("/rss_1.xml"));
        when(urlService.fetch(new URL(p2.getFeed()))).thenThrow(IOException.class);

        AmazonS3Exception ex = mock(AmazonS3Exception.class);
        when(ex.getStatusCode()).thenReturn(404);
        when(s3.getObject(any(GetObjectRequest.class))).thenThrow(ex);

        // When
        service.fetch();

        // Then
        verify(timerContext).stop();
        verify(fetchMeter).mark();
        verify(itemMeter).mark();
        verify(errorMeter).mark();

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(request.capture());
    }

    @Test
    public void fetchWithURLS3Error() throws Exception {
        // Given
        Feed p1 = createPublisher("rss");
        Feed p2 = createPublisher("rss");
        Feed p3 = createPublisher("tweeter");
        when(publisherService.getFeeds()).thenReturn(Arrays.asList(p1, p2, p3));

        when(urlService.fetch(new URL(p1.getFeed()))).thenReturn(getClass().getResourceAsStream("/rss_1.xml"));
        when(urlService.fetch(new URL(p2.getFeed()))).thenReturn(getClass().getResourceAsStream("/rss_2.xml"));

        AmazonS3Exception ex = mock(AmazonS3Exception.class);
        when(ex.getStatusCode()).thenReturn(404);
        when(s3.getObject(any(GetObjectRequest.class))).thenThrow(ex);
        when(s3.putObject(any(PutObjectRequest.class))).thenThrow(AmazonS3Exception.class);

        // When
        service.fetch();

        // Then
        verify(timerContext).stop();
        verify(fetchMeter).mark();
        verify(itemMeter, never()).mark();
        verify(errorMeter, times(2)).mark();
    }

    private Feed createPublisher (String feedType){
        final String id = UUID.randomUUID().toString();
        final Feed p = new Feed();

        p.setName(id);
        p.setFeed("http://foo.bar/feed/" + id);
        p.setFeedType(feedType);
        return p;
    }
}
