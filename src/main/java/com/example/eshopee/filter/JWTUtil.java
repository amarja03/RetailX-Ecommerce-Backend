package com.example.eshopee.filter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import com.example.eshopee.config.AppConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.crypto.SecretKey;

@Component
public class JWTUtil {

	public String generateToken() throws IllegalArgumentException, JWTCreationException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		String authority = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
		String secret = AppConstants.JWT_SECRET_DEFAULT;
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder().issuer("RetailX").subject("JWT Token")
				.claim("username", email)
				.claim("authorities", authority)
				.issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + AppConstants.JWT_TOKEN_VALIDITY))
				.signWith(secretKey).compact();
	}
}