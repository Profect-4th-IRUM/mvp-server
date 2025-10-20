package com.irum.come2us.global.security.jwt;

import static com.irum.come2us.global.constants.SecurityConstants.TOKEN_ROLE_NAME;

import com.irum.come2us.domain.auth.presentation.dto.request.AccessTokenDto;
import com.irum.come2us.domain.auth.presentation.dto.request.RefreshTokenDto;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.global.infrastructure.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public AccessTokenDto generateAccessTokenDto(Long memberId, Role authority) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.accessTokenExpirationMilliTime());
        String accessTokenValue = buildAccessToken(memberId, authority, issuedAt, expiredAt);
        return AccessTokenDto.of(memberId, authority, accessTokenValue);
    }

    public String generateAccessToken(Long memberId, Role authority) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.accessTokenExpirationMilliTime());
        return buildAccessToken(memberId, authority, issuedAt, expiredAt);
    }

    public RefreshTokenDto generateRefreshTokenDto(Long memberId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.refreshTokenExpirationMilliTime());
        String refreshTokenValue = buildRefreshToken(memberId, issuedAt, expiredAt);
        return RefreshTokenDto.of(
                memberId, refreshTokenValue, jwtProperties.refreshTokenExpirationTime());
    }

    public String generateRefreshToken(Long memberId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.refreshTokenExpirationMilliTime());
        return buildRefreshToken(memberId, issuedAt, expiredAt);
    }

    public AccessTokenDto parseAccessToken(String accessTokenValue) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = getClaims(accessTokenValue, getAccessTokenKey());

            return AccessTokenDto.of(
                    Long.parseLong(claims.getBody().getSubject()),
                    Role.valueOf(claims.getBody().get(TOKEN_ROLE_NAME, String.class)),
                    accessTokenValue);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    public String resolveToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }
        return null;
    }

    public long getRemainingExpirationMillis(String tokenValue) {
        Jws<Claims> claims = getClaims(tokenValue, getAccessTokenKey());
        Date exp = claims.getBody().getExpiration();
        return Math.max(exp.getTime() - System.currentTimeMillis(), 0);
    }

    public RefreshTokenDto parseRefreshToken(String refreshTokenValue) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = getClaims(refreshTokenValue, getRefreshTokenKey());

            return RefreshTokenDto.of(
                    Long.parseLong(claims.getBody().getSubject()),
                    refreshTokenValue,
                    jwtProperties.refreshTokenExpirationTime());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    private Jws<Claims> getClaims(String token, Key key) {
        return Jwts.parser()
                .requireIssuer(jwtProperties.issuer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public long getRefreshTokenExpirationTime() {
        return jwtProperties.refreshTokenExpirationTime();
    }

    private Key getAccessTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret().getBytes());
    }

    private Key getRefreshTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret().getBytes());
    }

    private String buildAccessToken(Long memberId, Role authority, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(memberId.toString())
                .claim(TOKEN_ROLE_NAME, authority.name())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getAccessTokenKey())
                .compact();
    }

    private String buildRefreshToken(Long memberId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(memberId.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getRefreshTokenKey())
                .compact();
    }
}
