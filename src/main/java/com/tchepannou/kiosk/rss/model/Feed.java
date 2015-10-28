package com.tchepannou.kiosk.rss.model;

import java.util.Collections;
import java.util.List;

public class Feed {
    //-- Attributes
    private long id;
    private String name;
    private String website;
    private String feed;
    private String feedType;
    private List<String> categories = Collections.emptyList();
    private String country;

    //-- Getter/Setter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
