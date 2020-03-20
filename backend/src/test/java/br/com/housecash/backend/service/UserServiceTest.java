package br.com.housecash.backend.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.repository.FlatmateRepository;
import br.com.housecash.backend.security.service.AuthenticationFacade;
import br.com.housecash.backend.util.annotation.LoginWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest extends ServiceHelper {
	
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
		
		userService.findInvitations(1l);
		
	}
	
	@LoginWith(id = 1l)
	@Test(expected = AccessDeniedException.class)
	public void whenFindInvitations_thenThrowAccessDeniedException() throws Exception {
		userService.findInvitations(13l);
	}

	@LoginWith(id = 1l)
	@Test
	public void whenChangeNickname_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.changeNickname(1l, "new nickname");
		
		assert(flatmateChanged.getId()).equals(1l);
		assert(flatmateChanged.getNickname()).equals("new nickname");
		
	}
	
	@LoginWith(id = 1l)
	@Test(expected = AccessDeniedException.class)
	public void whenChangeNickname_thenThrowAccessDeniedException() throws Exception {
		userService.changeNickname(13l,"new nickname");
	}

	@LoginWith(id = 1l)
	@Test
	public void whenChangePassword_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("password cripted");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.changePassword(1l, "new password");
		
		assert(flatmateChanged.getId()).equals(1l);
		assert(flatmateChanged.getPassword()).equals("password cripted");
		
	}
	
	@LoginWith(id = 1l)
	@Test(expected = AccessDeniedException.class)
	public void whenChangePassword_thenThrowAccessDeniedException() throws Exception {
		userService.changePassword(13l,"new password");
	}

	@LoginWith(id = 1l)
	@Test
	public void whenFinishStepGuest_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.finishStepGuest(1l);
		
		assert(flatmateChanged.getId()).equals(1l);
		assert(flatmateChanged.isGuestStep());
		
	}
	
	@LoginWith(id = 1l)
	@Test(expected = AccessDeniedException.class)
	public void whenFinishStepGuest_thenThrowAccessDeniedException() throws Exception {
		userService.finishStepGuest(13l);
	}

	@LoginWith(id = 1l)
	@Test
	public void whenFinishStepFirst_thenReturnFlatmateObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(flatmate);
		
		Flatmate flatmateChanged = userService.finishStepFirst(1l);
		
		assert(flatmateChanged.getId()).equals(1l);
		assert(flatmateChanged.isFirstStep());
		
	}
	
	@LoginWith(id = 1l)
	@Test(expected = AccessDeniedException.class)
	public void whenFinishStepFirst_thenThrowAccessDeniedException() throws Exception {
		userService.finishStepFirst(13l);
	}

}
