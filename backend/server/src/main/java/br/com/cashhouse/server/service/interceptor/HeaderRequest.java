package br.com.cashhouse.server.service.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.AuthenticationFacade;
import br.com.cashhouse.server.service.DashboardService;
import br.com.cashhouse.server.service.LocaleService;

@Service
public class HeaderRequest extends HandlerInterceptorAdapter {

	private static final String DASHBOARD_ID = "dashboard";
	private Long dashboardId;
	
	private LocaleService localeService;

	private DashboardService dashboardService;

	private AuthenticationFacade authenticationFacade; 
	
	@Autowired
	public HeaderRequest(@Lazy LocaleService localeService, @Lazy AuthenticationFacade authenticationFacade, @Lazy DashboardService dashboardService) {
		this.localeService = localeService;
		this.dashboardService = dashboardService;
		this.authenticationFacade = authenticationFacade;
	}
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	String headerId = request.getHeader(DASHBOARD_ID);
    	
    	if(headerId == null || !headerId.matches("([0-9])")) {
    		dashboardId = null;
    	} else {
        	dashboardId = Long.parseLong(headerId);
    	}
    	
        return true;
        
    }

	public Dashboard getDashboard() {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
    	
    	if(dashboardId == null) {
    		
			Dashboard dashboardLogged = dashboardService.findByOwner(flatmateLogged);
			
			if(dashboardLogged == null) {
				return dashboardService.createDashboard(flatmateLogged);
			}
			
			return dashboardLogged;
			
    	} else {
    		
    		Dashboard dashboardRequested = dashboardService.findById(dashboardId);
    		
    		if(dashboardRequested.isOwner(flatmateLogged) 
    				|| dashboardRequested.isGuest(flatmateLogged)) {
    			
    			return dashboardRequested;
    	        
    		} else {
    			throw new AccessDeniedException(localeService.getMessage("flatmate.access.denied", flatmateLogged.getNickname()));
    		}
    		
    	}

	}

}
