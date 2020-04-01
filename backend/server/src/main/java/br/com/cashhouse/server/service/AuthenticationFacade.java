package br.com.cashhouse.server.service;

import org.springframework.security.core.Authentication;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.spring.UserDetailsImpl;

public interface AuthenticationFacade {
	
    public Authentication getAuthentication();
    
    public UserDetailsImpl getLoggedUser();
    
    public Flatmate getFlatmateLogged();

}
