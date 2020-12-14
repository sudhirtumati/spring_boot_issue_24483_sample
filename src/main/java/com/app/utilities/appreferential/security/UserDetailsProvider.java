package com.app.utilities.appreferential.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsProvider implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) {
		return org.springframework.security.core.userdetails.User.builder().username("sample").password("sample")
				.disabled(false).accountLocked(false).roles("USER", "APP_OWNER", "ADMIN").build();
	}

}
