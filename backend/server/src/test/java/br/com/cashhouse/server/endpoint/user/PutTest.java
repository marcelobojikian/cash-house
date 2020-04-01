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
public class PutTest extends Oauth2 {

	@Test
	public void whenUpdateNickname__thenStatus_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("value", "New nickname");

		put("/users/self/nickname")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("New nickname")))
				.andExpect(jsonPath("$.enabled",is(true)))
				.andExpect(jsonPath("$.firstStep",is(false)))
				.andExpect(jsonPath("$.guestStep",is(false)));
        // @formatter:on

	}

	@Test
	public void whenUpdatePassword__thenStatus_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("value", "New password");

		put("/users/self/password")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test)")))
				.andExpect(jsonPath("$.enabled",is(true)))
				.andExpect(jsonPath("$.firstStep",is(false)))
				.andExpect(jsonPath("$.guestStep",is(false)));
        // @formatter:on

	}

}
