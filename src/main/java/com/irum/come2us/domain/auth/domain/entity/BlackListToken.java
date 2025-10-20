package com.irum.come2us.domain.auth.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("blacklistToken")
public class BlackListToken {

    @Id private final String token;

    @TimeToLive private final long ttl;

    @Builder(access = AccessLevel.PRIVATE)
    private BlackListToken(String token, long ttl) {
        this.token = token;
        this.ttl = ttl;
    }

    public static BlackListToken createBlackListToken(String token, Long ttl) {
        return builder().token(token).ttl(ttl).build();
    }
}
