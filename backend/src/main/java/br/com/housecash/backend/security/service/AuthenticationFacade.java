package br.com.housecash.backend.security.service;

import org.springframework.security.core.Authentication;

import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.security.CurrentUser;

public interface AuthenticationFacade {
	
    public Authentication getAuthentication();
    
    public CurrentUser getLoggedUser();
    
    public Flatmate getFlatmateLogged();

}
