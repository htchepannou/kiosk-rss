package com.tchepannou.kiosk.rss.util;

import com.tchepannou.kiosk.rss.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RSSFeedSAXHandler extends DefaultHandler {
    //-- Attributes
    private static final Logger LOGGER = LoggerFactory.getLogger(RSSFeedSAXHandler.class);

    private List<Item> items = new ArrayList<>();
    private Item item = null;
    private StringBuilder text = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    //-- Public
    public List<Item> getItems (){
        return items;
    }

    //-- DefaultHandler overrides
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if(qName.equalsIgnoreCase("item")) {
            item = new Item();
        }

        text = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if(text != null) {
            text.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("item")) {
            items.add(item);
            item = null;
        }

        if (qName.equalsIgnoreCase("title")) {
            item.setTitle(text.toString());
        } else if (qName.equalsIgnoreCase("description")) {
            item.setDescription(text.toString());
        } else if (qName.equalsIgnoreCase("content:encoded")) {
            item.setContent(text.toString());
        } else if (qName.equalsIgnoreCase("link")) {
            item.setLink(text.toString());
        } else if (qName.equalsIgnoreCase("pubDate")) {
            try {
                item.setPublishedDate(dateFormat.parse(text.toString()));
            } catch (ParseException e) {
                LOGGER.warn("Invalid publish date", e);
            }
        }
    }
    
}
