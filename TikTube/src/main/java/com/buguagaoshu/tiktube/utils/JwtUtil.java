package com.buguagaoshu.tiktube.utils;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.exception.UserNotLoginException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;


import java.util.Date;

@Slf4j
public class JwtUtil {
    // 使用更安全的密钥处理方式
    //private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(WebConstant.SECRET_KEY.getBytes());
    // private static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();
    // SECRET_KEY 从 WebConstant 类统一获取


    public static String createJwt(String email, String userId, String role, long expirationTime, Long stopTime) {
        if (stopTime == null) {
            stopTime = -1L;
        }

        return Jwts.builder()
                .subject(email)
                .id(userId)
                .claim(WebConstant.ROLE_KEY, role)
                .claim(WebConstant.VIP_STOP_TIME_KEY, stopTime)
                .expiration(new Date(expirationTime))
                .signWith(WebConstant.SECRET_KEY, Jwts.SIG.HS512)
                .compact();
    }

    public static Claims getUser(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, WebConstant.COOKIE_TOKEN);
        String token = cookie != null ? cookie.getValue() : null;

        if (token != null) {
            try {
                return Jwts.parser()
                        .verifyWith(WebConstant.SECRET_KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            } catch (Exception e) {
                //log.warn("来自 IP： {} 的用户 JWT TOKEN解析失败！", ipUtil.getIpAddr(request));
                return null;
            }
        }
        return null;
    }

    public static long getUserId(HttpServletRequest request) {
        Object userId = request.getSession().getAttribute(WebConstant.USER_ID);
        if (userId == null) {
            Claims user = JwtUtil.getUser(request);
            if (user != null) {
                return Long.parseLong(user.getId());
            }
            throw new UserNotLoginException("当前用户未登录！");
        }
        return (long) userId;
    }

    public static String getRole(HttpServletRequest request) {
        String userRole = (String) request.getSession().getAttribute(WebConstant.ROLE_KEY);
        if (userRole == null) {
            Claims user = JwtUtil.getUser(request);
            if (user != null) {
                return user.get(WebConstant.ROLE_KEY, String.class);
            }
            throw new UserNotLoginException("当前用户未登录！");
        }
        return userRole;
    }
}