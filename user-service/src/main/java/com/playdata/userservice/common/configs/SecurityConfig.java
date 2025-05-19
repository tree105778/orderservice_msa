package com.playdata.userservice.common.configs;

import com.playdata.userservice.common.auth.JwtAuthFilter;
import com.playdata.userservice.common.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 권한 검사를 컨트롤러의 메서드에서 전역적으로 수행하기 위한 설정.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean // 이 메서드가 리턴하는 시큐리티 설정을 빈으로 등록하겠다.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 스프링 시큐리티에서 기본으로 제공하는 CSRF 토큰 공격을 방지하기 위한 장치 해제.
        // CSRF(Cross Site Request Forgery) 사이트 간 요청 위조
        http.csrf(AbstractHttpConfigurer::disable);

//        http.cors(Customizer.withDefaults()); // 직접 커스텀한 CORS 설정을 적용

        // 세션 관리 상태를 사용하지 않고
        // STATELESS한 토큰을 사용하겠다.
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 요청 권한 설정(어떤 url이냐에 따라 검사를 할 지 말지를 결정)
        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers("/user/create", "/user/doLogin", "/user/refresh", "/user/findByEmail",
                            "/user/health-check", "/actuator/**").permitAll()
                    .anyRequest().authenticated();
        });

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 설정한 HttpSecurity 객체를 기반으로 시큐리티 설정 구축 및 반환.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
