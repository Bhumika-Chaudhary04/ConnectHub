package com.connecthub.auth.security;

import com.connecthub.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	private static final String SECRET_KEY =
		    "connecthubsupersecretkeyconnecthubsupersecretkey1234567890abcdefghijklmnopqrstuvwxyz";
	public String generateToken(UUID userId, String email) {
		return Jwts.builder().setSubject(email).claim("userId", userId.toString()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractEmail(String token) {
		try {
			return getClaims(token).getSubject();
		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException("JWT token has expired");
		} catch (JwtException e) {
			throw new InvalidTokenException("Invalid JWT token");
		}
	}

	public UUID extractUserId(String token) {
		try {
			String userId = getClaims(token).get("userId", String.class);
			return UUID.fromString(userId);
		} catch (Exception e) {
			throw new InvalidTokenException("Could not extract userId from token");
		}
	}

	public boolean isTokenValid(String token) {
		try {
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = getClaims(token).getExpiration();
			return expiration.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (JwtException e) {
			return true;
		}
	}

	private Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
}