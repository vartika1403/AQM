package com.example.admin.aqm;


import java.util.Objects;

public class AQMFeature {
    public Object image;
    public String quality;
    public String percentage;

    public AQMFeature(Object image, String quality, String percentage) {
        this.image = image;
        this.quality = quality;
        this.percentage = percentage;
    }

    public String getQuality() {
        return quality;
    }

    public String getPercentage() {
        return percentage;
    }

    public Object getImage() {
        return image;
    }
}
