package br.com.housecash.backend.handler;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.InvalidFieldException;
import br.com.housecash.backend.handler.annotation.ObjDashboard;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.service.DashboardService;
import br.com.housecash.backend.service.FlatmateService;

@Component
public class DashboardArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DASHBOARD_ID = "dashboard";

	private DashboardService dashboardService;

	private FlatmateService flatmateService;
	
	@Autowired
	public DashboardArgumentResolver(@Lazy FlatmateService flatmateService, @Lazy DashboardService dashboardService) {
		this.flatmateService = flatmateService;
		this.dashboardService = dashboardService;
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(ObjDashboard.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Principal principal = webRequest.getUserPrincipal();
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//		System.out.println("*** Principal ***: " + principal);
//		System.out.println("*** Header Dashboard ***: " + request.getHeader(DASHBOARD_ID));
		
		if(principal == null) {
			return null;
		}
		
		String headerId = request.getHeader(DASHBOARD_ID);
		Flatmate flatmate = flatmateService.findByEmail(principal.getName());
		
		if(headerId == null || !headerId.matches("([0-9])")) {
			
			Dashboard dashboard = dashboardService.findByOwner(flatmate);
			
			if(dashboard == null) {
				return dashboardService.createDashboard(flatmate);
			}
			
			return dashboard;
		}

		Long dashboardId = Long.parseLong(headerId);
		
		Dashboard dashboard = dashboardService.findById(dashboardId);
//		System.out.println("*** id ***: " + dashboardId);
//		System.out.println("*** dashboard ***: " + dashboard);
		
		if(dashboard.getOwner().getEmail().equals(principal.getName()) 
				|| dashboard.getGuests().contains(flatmate)) {
			return dashboard;
		} else {
			throw new AccessDeniedException(flatmate);
//			return dashboardRepository.findByOwner(flatmate);
		}
		
	}

}
