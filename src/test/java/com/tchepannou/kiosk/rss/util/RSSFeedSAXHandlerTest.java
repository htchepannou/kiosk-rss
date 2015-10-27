package com.tchepannou.kiosk.rss.util;

import com.tchepannou.kiosk.rss.model.Item;
import org.junit.Test;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class RSSFeedSAXHandlerTest {

    @Test
    public void load () throws Exception{
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();
        final RSSFeedSAXHandler handler = new RSSFeedSAXHandler();
        final DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        InputStream in = getClass().getResourceAsStream("/rss.xml");
        saxParser.parse(in, handler);

        List<Item> items = handler.getItems();

        assertThat(items).hasSize(2);

        Item item = items.get(0);
        assertThat(item.getPublishedDate()).isEqualTo(df.parse("Sun, 06 Sep 2009 16:20:00 +0000"));
        assertThat(item.getCategories()).containsExactly("sport", "football");
        assertThat(item.getContent()).isEqualTo("This is a <a href=\"http://example.com/\">link</a>.");
        assertThat(item.getCountry()).isNull();
        assertThat(item.getDescription()).isEqualTo("Here is some text containing an interesting description.");
        assertThat(item.getId()).isNotNull();
        assertThat(item.getLanguage()).isEqualTo("fr_CA");
        assertThat(item.getLink()).isEqualTo("http://www.example.com/blog/post/1");
        assertThat(item.getTitle()).isEqualTo("Example entry");

        item = items.get(1);
        assertThat(item.getPublishedDate()).isEqualTo(df.parse("Sun, 07 Sep 2009 16:20:00 +0000"));
        assertThat(item.getContent()).isEqualTo("Hello world.");
        assertThat(item.getCountry()).isNull();
        assertThat(item.getDescription()).isEqualTo("Here is some text containing an interesting description #2");
        assertThat(item.getLanguage()).isEqualTo("en_US");
        assertThat(item.getLink()).isEqualTo("http://www.example.com/blog/post/2");
        assertThat(item.getId()).isNotNull();
        assertThat(item.getCategories()).isEmpty();
        assertThat(item.getTitle()).isEqualTo("Example entry #2");

    }
}
