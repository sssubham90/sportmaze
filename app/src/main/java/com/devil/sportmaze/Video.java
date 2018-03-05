package com.devil.sportmaze;

public class Video {
    private String URL, name, ImageURL ,key;

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Video(String name, String URL, String ImageURL, String key) {
        this.URL = URL;
        this.name = name;
        this.ImageURL = ImageURL;
        this.key = key;

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
