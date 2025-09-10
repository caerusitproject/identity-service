package com.caerus.identity.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    private long expiration;

    private long refreshTokenExpiration;

    public long getAccessTokenExpiration() {
        return expiration;
    }

}
