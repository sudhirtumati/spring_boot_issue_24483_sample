package com.app.utilities.appreferential.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	private final JwtTokenUtil jwtTokenUtil;

	private final UserDetailsService userDetailsService;

	public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
			@NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String token = getTokenFromHeader(httpServletRequest);
		UserDetails userDetails = getUserDetails(token);
		if (userDetails != null && jwtTokenUtil.validateToken(token, userDetails)) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			log.debug("Authentication success - User: {}", userDetails.getUsername());
		}
		else {
			if (userDetails != null) {
				log.info("Authentication failure - User: {} enabled: {}, locked: {} ", userDetails.getUsername(),
						userDetails.isEnabled(), !userDetails.isAccountNonLocked());
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private UserDetails getUserDetails(String token) {
		if (token == null) {
			return null;
		}
		String username = null;
		try {
			username = jwtTokenUtil.getUsernameFromToken(token);
		}
		catch (ExpiredJwtException ex) {
			log.error("Token {} expired", token, ex);
		}
		if (username != null) {
			return userDetailsService.loadUserByUsername(username);
		}
		return null;
	}

	private String getTokenFromHeader(HttpServletRequest httpServletRequest) {
		final String tokenHeader = httpServletRequest.getHeader("Authorization");
		String token;
		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			token = tokenHeader.substring(7);
		}
		else {
			logger.warn("Token does not begin with 'Bearer '");
			token = tokenHeader;
		}
		return token;
	}

}
