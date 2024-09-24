package com.example.eshopee.filter;

import com.example.eshopee.config.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String jwt = request.getHeader(AppConstants.JWT_HEADER);
//        System.out.println(jwt);
//        System.out.println("Hello");
//        try{
//            String secret = AppConstants.JWT_SECRET_DEFAULT;
//            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//            Claims claims = Jwts
//                    .parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(jwt)
//                    .getPayload();
////            System.out.println(claims);
//            String username = String.valueOf(claims.get("username"));
//            String authorities = String.valueOf(claims.get("authorities"));
////            System.out.println(jwt);
//            System.out.println(authorities + " " + username);
//            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
//
//            SecurityContextHolder.clearContext();
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } catch (Exception e){
//            throw new BadCredentialsException("Invalid Token");
//        }
//
//        filterChain.doFilter(request, response);
//
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(AppConstants.JWT_HEADER);
        if (jwt != null) {
            try {
                String secret = AppConstants.JWT_SECRET_DEFAULT;
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                String username = claims.getSubject();
                String authorities = (String) claims.get("authorities");

                if (username != null) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Add detailed logging
                throw new BadCredentialsException("Invalid Token", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //do not filter if the request is for public urls which is stored in AppConstants
        for (String url : AppConstants.PUBLIC_URLS) {
            if (request.getServletPath().contains(url)) {
                return true;
            }
        }

        return false;
    }


}
