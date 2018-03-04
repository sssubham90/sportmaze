package com.devil.sportmaze;

public class Video {
    private String URL, name, ImageURL;

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public Video(String name, String URL, String ImageURL) {
        this.URL = URL;
        this.name = name;
        this.ImageURL = ImageURL;
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
