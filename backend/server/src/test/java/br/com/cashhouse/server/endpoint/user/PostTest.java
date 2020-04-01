package br.com.cashhouse.server.endpoint.user;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import br.com.cashhouse.server.App;
import br.com.cashhouse.server.endpoint.Oauth2;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class PostTest extends Oauth2 {

	@Test
	public void whenFinishGuestStep__thenStatus_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body();

		post("/users/self/step/guest/finish")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test)")))
				.andExpect(jsonPath("$.enabled",is(true)))
				.andExpect(jsonPath("$.firstStep",is(false)))
				.andExpect(jsonPath("$.guestStep",is(true)));
        // @formatter:on

	}

	@Test
	public void whenFinishFirstStep__thenStatus_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body();

		post("/users/self/step/first/finish")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test)")))
				.andExpect(jsonPath("$.enabled",is(true)))
				.andExpect(jsonPath("$.firstStep",is(true)))
				.andExpect(jsonPath("$.guestStep",is(false)));
        // @formatter:on

	}

}
