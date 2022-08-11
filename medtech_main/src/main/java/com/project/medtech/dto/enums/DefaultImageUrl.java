package com.project.medtech.dto.enums;

public enum DefaultImageUrl {

    DEFAULT_IMAGE_ONE("https://res.cloudinary.com/neobisteamfour/image/upload/v1660042292/samples/landscapes/architecture-signs.jpg"),
    DEFAULT_IMAGE_TWO("https://res.cloudinary.com/neobisteamfour/image/upload/v1660042291/samples/people/jazz.jpg"),
    DEFAULT_IMAGE_THREE("https://res.cloudinary.com/testneobis/image/upload/v1660046588/bevveashokdzlmktujaq.jpg");

    private final String url;

    DefaultImageUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
