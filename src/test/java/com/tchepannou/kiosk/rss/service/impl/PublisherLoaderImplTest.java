package com.tchepannou.kiosk.rss.service.impl;

import com.tchepannou.kiosk.rss.model.Publisher;
import com.tchepannou.kiosk.rss.service.PublisherLoader;
import junit.framework.TestCase;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PublisherLoaderImplTest extends TestCase {
    private PublisherLoader loader = new PublisherLoaderImpl()
            .withJackson2ObjectMapperBuilder(new Jackson2ObjectMapperBuilder());

    public void testLoad() throws Exception {
        List<Publisher> publishers = loader.load();

        assertThat(publishers).hasSize(2);

        Publisher p1 = publishers.get(0);
        assertThat(p1.getId()).isEqualTo(1);
        assertThat(p1.getName()).isEqualTo("Mboa Football");
        assertThat(p1.getWebsite()).isEqualTo("http://mboafootball.com");
        assertThat(p1.getFeed()).isEqualTo("http://mboafootball.com/rss");
        assertThat(p1.getFeedType()).isEqualTo("rss");
        assertThat(p1.getCategories()).containsExactly("sport", "football");
        assertThat(p1.getCountry()).isEqualTo("CM");
    }
}
