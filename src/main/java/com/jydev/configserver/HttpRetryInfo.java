package com.jydev.configserver;

public record HttpRetryInfo(
        int retryCount,
        String url
) {

    public static HttpRetryInfo fromUrl(String url) {
        return new HttpRetryInfo(0, url);
    }
}
