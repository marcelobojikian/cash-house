package br.com.cashhouse.server.spring;

import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.repository.FlatmateRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsServiceImplTest {

	@Autowired
	private UserDetailsService userDetailsService;

	@MockBean
	private FlatmateRepository flatmateRepository;

	@TestConfiguration
	static class UserDetailsServiceImplTestContextConfiguration {
		@Bean
		public UserDetailsService userDetailsService() {
			return new UserDetailsServiceImpl();
		}
	}

	@Test
	public void whenFindByUsername_thenReturnUserDetailsObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "user@mail.com", "none", "password", "USER");

		when(flatmateRepository.findByEmailAndEnabled(eq("user@mail.com"), anyBoolean())).thenReturn(flatmate);
		
		UserDetails userDetails = userDetailsService.loadUserByUsername("user@mail.com");
		
		assertNotNull(userDetails);
		assertThat(userDetails, instanceOf(UserDetailsImpl.class));
		
	}

	@Test(expected = UsernameNotFoundException.class)
	public void whenFindByUsernameInvalid_thenThrowUsernameNotFoundException() throws Exception {

		when(flatmateRepository.findByEmailAndEnabled(anyString(), anyBoolean())).thenReturn(null);
		
		userDetailsService.loadUserByUsername("invalid@mail.com");
		
	}

}
