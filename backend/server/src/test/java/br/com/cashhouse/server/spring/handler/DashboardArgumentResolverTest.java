package br.com.cashhouse.server.spring.handler;

import static br.com.cashhouse.server.util.EntityFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.context.request.ServletWebRequest;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.DashboardService;
import br.com.cashhouse.server.service.FlatmateService;
import br.com.cashhouse.server.service.LocaleService;
import br.com.cashhouse.server.spring.UserDetailsImpl;

// https://github.com/spring-projects/spring-framework/blob/master/spring-webmvc/src/test/java/org/springframework/web/servlet/mvc/method/annotation/RequestResponseBodyMethodProcessorTests.java
public class DashboardArgumentResolverTest {
	
	private static final String DASHBOARD_ID = "dashboard";

	private DashboardArgumentResolver resolver;

	@Mock
	private HttpServletRequest request;
	
	private ServletWebRequest webRequest;
	
	private LocaleService localeService;
	private FlatmateService flatmateService;
	private DashboardService dashboardService;
	
	private Principal principal;
	private Flatmate flatmate;

	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		webRequest = Mockito.mock(ServletWebRequest.class);
		flatmateService = Mockito.mock(FlatmateService.class);
		dashboardService = Mockito.mock(DashboardService.class);
		localeService = Mockito.mock(LocaleService.class);
		
		resolver = new DashboardArgumentResolver(localeService, flatmateService, dashboardService);
		
		flatmate = createFlatmate(1l, "none", "none");

		UserDetailsImpl user = new UserDetailsImpl(flatmate, AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.principal = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		
		when(webRequest.getNativeRequest()).thenReturn(request);
		
	}

	@Test
	public void should_invalid_when_not_dashboard() throws Exception {
		assertFalse(resolver.supportsParameter(new MethodParameter(Dashboard.class.getConstructor(), -1)));
	}

	@Test
	public void should_null_when_not_found_dashboard() throws Exception {
		
		Dashboard dashboard = flatmate.getDashboard();
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(dashboard.getId().toString());
		when(request.getUserPrincipal()).thenReturn(null);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNull();
		
	}

	@Test
	public void should_return_dashboard_when_header_is_null() throws Exception {
		
		Dashboard dashboard = flatmate.getDashboard();
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(null);
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(flatmate);
		when(dashboardService.findByOwner(flatmate)).thenReturn(dashboard);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNotNull();
		
	}

	@Test
	public void should_return_dashboard_when_header_is_invalid() throws Exception {
		
		Dashboard dashboard = flatmate.getDashboard();
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn("AD21");
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(flatmate);
		when(dashboardService.findByOwner(flatmate)).thenReturn(dashboard);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNotNull();
		
	}

	@Test
	public void should_create_dashboard_when_dashboard_is_null() throws Exception {

		Dashboard dashboard = new Dashboard();
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(null);
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(flatmate);
		when(dashboardService.findByOwner(flatmate)).thenReturn(null);
		when(dashboardService.createDashboard(flatmate)).thenReturn(dashboard);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNotNull();
		
	}

	@Test
	public void should_return_dashboard_when_flatmate_is_owner_dashboard() throws Exception {

		Dashboard dashboard = flatmate.getDashboard();
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(dashboard.getId().toString());
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(flatmate);
		when(dashboardService.findById(dashboard.getId())).thenReturn(dashboard);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNotNull();
		
	}

	@Test
	public void should_return_dashboard_when_flatmate_is_invited_dashboard() throws Exception {

		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate invited = createFlatmate(2l, "Invited", "invited");
		dashboard.getGuests().add(invited);

		UserDetailsImpl user = new UserDetailsImpl(invited, AuthorityUtils.createAuthorityList("ROLE_USER"));
		Principal principal = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(dashboard.getId().toString());
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(invited);
		when(dashboardService.findById(dashboard.getId())).thenReturn(dashboard);

		Object result = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
		assertThat(result).isNotNull();
		
	}

	@Test(expected = AccessDeniedException.class)
	public void should_throws_accessDenied_when_flatmate_access_other_dashboard() throws Exception {

		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate invited = createFlatmate(2l, "Invited", "invited");

		UserDetailsImpl user = new UserDetailsImpl(invited, AuthorityUtils.createAuthorityList("ROLE_USER"));
		Principal principal = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(dashboard.getId().toString());
		when(request.getUserPrincipal()).thenReturn(principal);
		when(flatmateService.findByEmail(principal.getName())).thenReturn(invited);
		when(dashboardService.findById(dashboard.getId())).thenReturn(dashboard);

		resolver.resolveArgument(null, null, new ServletWebRequest(request), null);
		
	}

}
