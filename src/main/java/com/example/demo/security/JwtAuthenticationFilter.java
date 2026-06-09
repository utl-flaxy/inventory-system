package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("PATH = " + request.getServletPath());

        // auth系はJWTチェックしない
        if (request.getServletPath().startsWith("/auth")) {

            System.out.println("SKIP AUTH");

            filterChain.doFilter(request, response);
            return;
        }

        // Authorizationヘッダ取得
        String authHeader = request.getHeader("Authorization");

        // Bearerトークンが無ければ次へ
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("NO TOKEN");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            System.out.println("JWT FILTER START");

            // Bearer を除去
            String token = authHeader.substring(7);

            // username取得
            String username = jwtUtil.extractUsername(token);

            System.out.println("USERNAME = " + username);

            // 未認証なら認証情報セット
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                System.out.println("LOAD USER");

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(token)) {

                    System.out.println("TOKEN VALID");

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    System.out.println("AUTH SUCCESS");
                }
            }

        } catch (Exception e) {

            System.out.println("JWT ERROR");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}