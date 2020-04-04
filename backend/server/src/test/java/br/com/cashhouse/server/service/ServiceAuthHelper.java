package br.com.cashhouse.server.service;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import br.com.cashhouse.server.spring.UserDetailsImpl;

public class ServiceAuthHelper {

	@MockBean
	private AuthenticationFacade authenticationFacade;
	
	@MockBean
	private HeaderRequest headerRequest;
	
	@Before
	public void init() {
		when(authenticationFacade.getFlatmateLogged()).thenReturn(getFlatmateLogged());
		when(headerRequest.getDashboard()).thenReturn(getFlatmateLogged().getDashboard());
	}
	
	public void userDashboard(Flatmate flatmate) {
		when(headerRequest.getDashboard()).thenReturn(flatmate.getDashboard());
	}
	
	public void addGuest(Flatmate guest) {
		Dashboard dashboard = getFlatmateLogged().getDashboard();
		dashboard.getGuests().add(guest);
	}
	
	public Flatmate getFlatmateLogged() {
		return getCurrentUser().getFlatmate();
	}

	private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
	}

    private UserDetailsImpl getCurrentUser(){
        return (UserDetailsImpl) getAuthentication().getPrincipal();
    }

}
