package br.com.housecash.backend.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.security.CurrentUser;

@Service
public class AuthenticationFacadeBean implements AuthenticationFacade {

	@Override
	public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public CurrentUser getLoggedUser() {
		return getCurrentUser();
	}

	@Override
	public Flatmate getFlatmateLogged() {
		return getLoggedUser().getFlatmate();
	}
 
    private CurrentUser getCurrentUser(){
        return (CurrentUser) getAuthentication().getPrincipal();
    }

}
