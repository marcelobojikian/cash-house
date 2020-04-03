package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.repository.FlatmateRepository;
import br.com.cashhouse.server.util.LoginWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
	
	@Autowired
	private UserService userService;

	@MockBean
	private AuthenticationFacade authenticationFacade;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@MockBean
	private DashboardService dashboardService;

	@MockBean
	private FlatmateRepository flatmateRepository;

	@TestConfiguration
	static class UserServiceImplTestContextConfiguration {
		@Bean
		public UserService userService() {
			return new UserServiceImpl();
		}
	}

	@LoginWith(id = 1l)
	@Test
	public void whenFindInvitations_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(dashboardService.findMyInvitations(any(Flatmate.class))).thenReturn(Collections.emptyList());
		
		Collection<Dashboard> dashboards = userService.findInvitations();
		
		assertThat(dashboards, empty());
		
	}

	@LoginWith(id = 1l)
	@Test
	public void whenChangeNickname_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.changeNickname("new nickname");

		assertThat(flatmateChanged.getId(), is(1l));
		assertThat(flatmateChanged.getNickname(), is("new nickname"));
		
	}

	@LoginWith(id = 1l)
	@Test
	public void whenChangePassword_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("password cripted");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.changePassword("new password");

		assertThat(flatmateChanged.getId(), is(1l));
		assertThat(flatmateChanged.getPassword(), is("password cripted"));
		
	}

	@LoginWith(id = 1l)
	@Test
	public void whenFinishStepGuest_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.finishStepGuest();

		assertThat(flatmateChanged.getId(), is(1l));
		assertTrue(flatmateChanged.isGuestStep());
		
	}

	@LoginWith(id = 1l)
	@Test
	public void whenFinishStepFirst_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.finishStepFirst();

		assertThat(flatmateChanged.getId(), is(1l));
		assertTrue(flatmateChanged.isFirstStep());
		
	}

}
