package com.example.admin.aqm;


public class AQMFeature {
    public int image;
    public String quality;
    public String percentage;

    public AQMFeature(int image, String quality, String percentage) {
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

    public int getImage() {
        return image;
    }
}
