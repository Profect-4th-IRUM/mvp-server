package com.irum.come2us.global.infrastructure.config.security;

import com.irum.come2us.domain.auth.application.service.JwtTokenService;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.security.JwtAuthenticationFilter;
import com.irum.come2us.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final CookieUtil cookieUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");
        config.addAllowedOrigin("http://localhost:8080");
        config.setMaxAge(3600L); // Preflight 캐시 허용 1시간
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(
                auth ->
                        auth // API 개발 완료 후 비즈니스 로직에 맞게 설정 변경 예정
                                .requestMatchers("/members")
                                .hasAnyRole(
                                        Role.CUSTOMER.name(),
                                        Role.OWNER.name(),
                                        Role.MANAGER.name(),
                                        Role.MASTER.name())
                                .requestMatchers(HttpMethod.DELETE, "/members/me")
                                .hasRole(Role.CUSTOMER.name())
                                .requestMatchers("/managers/**")
                                .hasRole(Role.MASTER.name())
                                .anyRequest()
                                .permitAll()); // API 완료 후 허용된 public endpoints 외 모든 경로
        // .authenticated() 옵션으로 변경할 예정

        http.addFilterBefore(
                jwtAuthenticationFilter(memberRepository, jwtTokenService, cookieUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            MemberRepository memberRepository,
            JwtTokenService jwtTokenService,
            CookieUtil cookieUtil) {
        return new JwtAuthenticationFilter(memberRepository, jwtTokenService, cookieUtil);
    }
}
