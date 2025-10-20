package com.irum.come2us.global.security;

import static com.irum.come2us.global.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME;
import static com.irum.come2us.global.constants.SecurityConstants.TOKEN_PREFIX;

import com.irum.come2us.domain.auth.application.service.JwtTokenService;
import com.irum.come2us.domain.auth.presentation.dto.request.AccessTokenDto;
import com.irum.come2us.domain.auth.presentation.dto.request.RefreshTokenDto;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessTokenHeaderValue = extractAccessTokenFromHeader(request);
        String refreshTokenCookieValue = extractRefreshTokenFromCookie(request);

        // 헤더에 AT가 있으면 우선적으로 검증
        if (accessTokenHeaderValue != null) {
            AccessTokenDto accessTokenDto =
                    jwtTokenService.retrieveAccessToken(accessTokenHeaderValue);

            // AT가 유효하면 통과
            if (accessTokenDto != null) {
                setAuthenticationToken(accessTokenDto.memberId(), accessTokenDto.role());

                filterChain.doFilter(request, response);
                return;
            }
        }

        // AT가 유효하지 않다면 RT 파싱
        if (refreshTokenCookieValue != null) {
            RefreshTokenDto refreshTokenDto =
                    jwtTokenService.retrieveRefreshToken(refreshTokenCookieValue);

            // RT가 유효하면 AT, RT 재발급
            if (refreshTokenDto != null) {
                Member member =
                        memberRepository
                                .findById(refreshTokenDto.memberId())
                                .orElseThrow(
                                        () ->
                                                new CommonException(
                                                        MemberErrorCode.MEMBER_NOT_FOUND));

                AccessTokenDto reissueAccessTokenDto =
                        jwtTokenService.createAccessTokenDto(
                                member.getMemberId(), member.getRole());
                RefreshTokenDto reissueRefreshTokenDto =
                        jwtTokenService.createRefreshTokenDto(member.getMemberId());

                HttpHeaders headers =
                        cookieUtil.generateRefreshTokenCookie(
                                reissueRefreshTokenDto.refreshTokenValue());

                response.addHeader(
                        HttpHeaders.AUTHORIZATION,
                        TOKEN_PREFIX + reissueAccessTokenDto.accessTokenValue());
                response.addHeader(
                        HttpHeaders.SET_COOKIE, headers.getFirst(HttpHeaders.SET_COOKIE));
            }
        }

        // AT, RT가 모두 만료된 경우 실패
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationToken(Long memberId, Role authority) {
        UserDetails userDetails = new MemberDetails(memberId, authority);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.replace(TOKEN_PREFIX, "");
        }

        return null;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);

        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }
}
