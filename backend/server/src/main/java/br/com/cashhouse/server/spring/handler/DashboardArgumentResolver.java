package br.com.cashhouse.server.spring.handler;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.DashboardService;
import br.com.cashhouse.server.service.FlatmateService;
import br.com.cashhouse.server.service.LocaleService;

@Component
public class DashboardArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DASHBOARD_ID = "dashboard";

	private DashboardService dashboardService;

	private FlatmateService flatmateService;
	
	private LocaleService localeService;
	
	@Autowired
	public DashboardArgumentResolver(@Lazy LocaleService localeService, @Lazy FlatmateService flatmateService, @Lazy DashboardService dashboardService) {
		this.localeService = localeService;
		this.flatmateService = flatmateService;
		this.dashboardService = dashboardService;
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterType().equals(Dashboard.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Principal principal = webRequest.getUserPrincipal();
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		
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
		
		if(dashboard.getOwner().getEmail().equals(principal.getName()) 
				|| dashboard.getGuests().contains(flatmate)) {
			return dashboard;
		} else {
			throw new AccessDeniedException(localeService.getMessage("flatmate.access.denied", flatmate.getNickname()));
		}
		
	}

}
