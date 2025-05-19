package com.playdata.userservice.common.auth;

import com.playdata.userservice.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

// 역할: 토큰을 발급하고, 서명 위조를 검사하는 객체
@Component
public class JwtTokenProvider {

    // yml에 있는 값 땡겨오기 (properties 방식으로 지목)
    // 서명에 사용할 값(512비트 이상의 랜덤 문자열을 권장)
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    @Value("${jwt.expirationRt}")
    private int expirationRt;

    // 토큰 생성 메서드
     /*
            {
                "iss": "서비스 이름(발급자)",
                "exp": "2023-12-27(만료일자)",
                "iat": "2023-11-27(발급일자)",
                "email": "로그인한 사람 이메일",
                "role": "Premium"
                ...
                == 서명
            }
     */
    public String createToken(String email, String role) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email, String role) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                // 토큰 생성 로직은 비슷하지만 수명과 서명이 다릅니다. (Refresh가 좀 더 길어요)
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, secretKeyRt)
                .compact();
    }

    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조 여부를 확인
     * 토큰을 json으로 파싱해서 클레임(토큰 정보)을 리턴
     *
     * @param token - 필터가 전달해 준 토큰
     * @return - 토큰 안에 있는 인증된 유저 정보를 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token) {

        Claims claims = Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시의 서명을 넣어줌.
                .setSigningKey(secretKey)
                // 토큰 유효성 검사를 해 주는 parser 객체 생성.
                // 생성 과정에서 서명이 위조된 경우에는 예외가 발생합니다.
                // 위조되지 않았다면 payload를 리턴해 줍니다.
                .build()
                .parseClaimsJws(token)
                .getBody();

        System.out.println("claims = " + claims);

        return TokenUserInfo.builder()
                .email(claims.getSubject())
                // 클레임이 Role타입으로 바로 변환을 못 해줍니다.
                // 일단 String으로 데이터를 꺼내고 직접 Role타입으로 포장해서 넣어 줍니다.
                .role(Role.valueOf(claims.get("role", String.class))).build();
    }
}
