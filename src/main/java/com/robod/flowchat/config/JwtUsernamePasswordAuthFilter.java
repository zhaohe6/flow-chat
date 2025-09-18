package com.robod.flowchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robod.flowchat.common.ResponseBody;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

//@Component
public class JwtUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtil;

    public JwtUsernamePasswordAuthFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login"); // 前端请求的登录地址
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password); // 生成一个待验证的对象
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        String token = jwtUtil.generateToken(authResult.getName());

        // 设置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1天
        response.addCookie(cookie);

        // 构造返回体
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        ResponseBody<HashMap<String, String>> body = ResponseBody.<HashMap<String, String>>builder()
                        .code(200)
                        .message("Login successful")
                        .data(hashMap)
                        .build();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              org.springframework.security.core.AuthenticationException failed) throws IOException {
        ResponseBody<Object> body = ResponseBody.builder()
                        .code(401)
                        .message("Invalid username or password")
                        .build();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
