package br.com.cashhouse.server.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.spring.UserDetailsImpl;

public class LoginSecurityContextFactory implements WithSecurityContextFactory<LoginWith> {
	
	@Override
	public SecurityContext createSecurityContext(LoginWith withUser) {

		Flatmate userInfo = new Flatmate();
		userInfo.setId(withUser.id());
		userInfo.setEmail(withUser.email());
		userInfo.setNickname(withUser.nickname());
		userInfo.setPassword(withUser.password());
		
		String[] roles = withUser.roles();
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		for (int i = 0; i < roles.length; i++) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roles[i]));
		}

		UserDetails principal = new UserDetailsImpl(userInfo, grantedAuthorities);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}

}
