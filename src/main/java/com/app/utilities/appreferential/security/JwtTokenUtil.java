package com.app.utilities.appreferential.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

	@Value("${jwt.token.validity}")
	private long validity;

	@Value("${jwt.secret}")
	private String secret;

	public String generateToken(UserDetails userDetails) {
		return generateToken(userDetails, validity);
	}

	public String generateToken(UserDetails userDetails, long validity) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername(), validity);
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		if (isTokenExpired(token) || !userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
			return false;
		}
		String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject, long validity) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + validity * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	private boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date());
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

}
