package br.com.cashhouse.server.endpoint.user;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
public class GetTest extends Oauth2 {

	@Test
	public void view_DETAIL_MARCELO() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		get("/users/self/detail")
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.email", is("marcelo@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Marcelo (test)")))
				.andExpect(jsonPath("$.enabled", is(true)))
				.andExpect(jsonPath("$.firstStep", is(false)))
				.andExpect(jsonPath("$.guestStep", is(false)));
        // @formatter:on

	}

	@Test
	public void view_DETAIL_JEAN() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		get("/users/self/detail")
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test)")))
				.andExpect(jsonPath("$.enabled", is(true)))
				.andExpect(jsonPath("$.firstStep", is(false)))
				.andExpect(jsonPath("$.guestStep", is(false)));
        // @formatter:on

	}

	@Test
	public void view_INVITATIONS_MARCELO() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		get("/users/self/invitations")
				.andExpect(jsonPath("$[0].id", is(2)));
		
        // @formatter:on

	}

	@Test
	public void view_DASHBOARD_MARCELO() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		get("/users/self/dashboard")
				.andExpect(jsonPath("$.id", is(1)))
				
				.andExpect(jsonPath("$.owner.id", is(1)))
				
				.andExpect(jsonPath("$.guests[0].id", is(2)))
				.andExpect(jsonPath("$.guests[1].id", is(3)))
				.andExpect(jsonPath("$.guests[2].id", is(4)))
				.andExpect(jsonPath("$.guests[3].id", is(5)))
				.andExpect(jsonPath("$.guests[4].id", is(6)))
				.andExpect(jsonPath("$.guests[5].id", is(7)))

				.andExpect(jsonPath("$.transactions[0].id", is(1)))
				.andExpect(jsonPath("$.transactions[1].id", is(2)))
				.andExpect(jsonPath("$.transactions[2].id", is(3)))
				.andExpect(jsonPath("$.transactions[3].id", is(4)))
				.andExpect(jsonPath("$.transactions[4].id", is(5)))
				.andExpect(jsonPath("$.transactions[5].id", is(6)))

				.andExpect(jsonPath("$.cashiers[0].id", is(1)))
				.andExpect(jsonPath("$.cashiers[1].id", is(2)));
		
        // @formatter:on

	}
	
}
