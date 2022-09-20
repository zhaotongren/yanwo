package com.yanwo.redis;

public interface KeyPrefix {

    public int expireSeconds() ;

    public String getPrefix() ;

}
