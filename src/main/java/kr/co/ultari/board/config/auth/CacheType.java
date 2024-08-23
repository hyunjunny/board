package kr.co.ultari.board.config.auth;

import lombok.Getter;

@Getter
public enum CacheType {
    CONTENT("CONTENT", 24 * 60, 10000);

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;
}
