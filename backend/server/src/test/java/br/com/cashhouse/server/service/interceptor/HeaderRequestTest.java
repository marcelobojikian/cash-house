package br.com.cashhouse.server.service.interceptor;

import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.ServletWebRequest;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.AuthenticationFacade;
import br.com.cashhouse.server.service.DashboardService;
import br.com.cashhouse.server.service.LocaleService;

public class HeaderRequestTest {
	
	private static final String DASHBOARD_ID = "dashboard";

	private HeaderRequest resolver;

	@Mock
	private HttpServletRequest request;
	
	private ServletWebRequest webRequest;

	private AuthenticationFacade authenticationFacade; 
	
	private DashboardService dashboardService;
	
	private LocaleService localeService;

	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		webRequest = Mockito.mock(ServletWebRequest.class);
		authenticationFacade = Mockito.mock(AuthenticationFacade.class);
		dashboardService = Mockito.mock(DashboardService.class);
		localeService = Mockito.mock(LocaleService.class);
		
		resolver = new HeaderRequest(localeService, authenticationFacade, dashboardService);

		when(webRequest.getNativeRequest()).thenReturn(request);
		
	}

	@Test
	public void should_return_true_when_no_header() throws Exception {
		when(request.getHeader(DASHBOARD_ID)).thenReturn(null);
		assertTrue(resolver.preHandle(request, null, null));
	}

	@Test
	public void should_return_true_when_invalid_header() throws Exception {
		when(request.getHeader(DASHBOARD_ID)).thenReturn("21e");
		assertTrue(resolver.preHandle(request, null, null));
	}

	@Test
	public void should_return_true_when_header() throws Exception {
		when(request.getHeader(DASHBOARD_ID)).thenReturn("1");
		assertTrue(resolver.preHandle(request, null, null));
	}

	@Test
	public void should_return_new_Dashboard_when_flatame_dont_have_dashboard() throws Exception {
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(null);
		resolver.preHandle(request, null, null);
		
		Flatmate flatmate = new Flatmate();
		
		Dashboard expected = new Dashboard();
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(dashboardService.findByOwner(flatmate)).thenReturn(null);
		when(dashboardService.createDashboard(flatmate)).thenReturn(expected);
		
		Dashboard dashboard = resolver.getDashboard();
		
		assertThat(dashboard).isEqualTo(expected);
		
	}

	@Test
	public void should_return_Dashboard_when_header_dashboard_null() throws Exception {
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn(null);
		resolver.preHandle(request, null, null);

		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(dashboardService.findByOwner(flatmate)).thenReturn(dashboard);
		
		Dashboard expected = resolver.getDashboard();
		
		assertThat(dashboard).isEqualTo(expected);
		
	}

	@Test
	public void should_return_Dashboard_when_header_flatmate_owner() throws Exception {
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn("1");
		resolver.preHandle(request, null, null);

		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(dashboardService.findById(1l)).thenReturn(dashboard);
		
		Dashboard expected = resolver.getDashboard();
		
		assertThat(dashboard).isEqualTo(expected);
		
	}

	@Test
	public void should_return_Dashboard_when_header_flatmate_guest() throws Exception {
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn("1");
		resolver.preHandle(request, null, null);

		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate guest = createFlatmate(2l, "none", "none");
		dashboard.getGuests().add(guest);
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(guest);
		when(dashboardService.findById(1l)).thenReturn(dashboard);
		
		Dashboard expected = resolver.getDashboard();
		
		assertThat(dashboard).isEqualTo(expected);
		assertThat(dashboard.getGuests()).contains(guest);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void should_throw_AccessDeniedException() throws Exception {
		
		when(request.getHeader(DASHBOARD_ID)).thenReturn("1");
		resolver.preHandle(request, null, null);

		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate guest = createFlatmate(2l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(guest);
		when(dashboardService.findById(1l)).thenReturn(dashboard);
		
		resolver.getDashboard();
		
	}

}
