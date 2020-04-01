package br.com.cashhouse.server.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.spring.UserDetailsImpl;

@Service
public class AuthenticationFacadeBean implements AuthenticationFacade {

	@Override
	public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public UserDetailsImpl getLoggedUser() {
		return getCurrentUser();
	}

	@Override
	public Flatmate getFlatmateLogged() {
		return getLoggedUser().getFlatmate();
	}
 
    private UserDetailsImpl getCurrentUser(){
        return (UserDetailsImpl) getAuthentication().getPrincipal();
    }

}
