package br.com.cashhouse.server.endpoint.flatmate;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
	public void update_forbidden_RoleType_USER() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("email", "update@mail.com")
			.add("nickname", "Flatmate updated")
			.add("password", "test")
			.add("roles", "OTHER")
			.add("enabled", "false")
			.add("firstStep", "true")
			.add("guestStep", "true")
			.add("dashboard", "2");

		put("/flatmates/8")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void update_OK_RoleType_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("email", "carol2@mail.com")
			.add("nickname", "Carol (test UPDATE)")
			.add("password", "test")
			.add("roles", "OTHER")
			.add("enabled", "false")
			.add("firstStep", "true")
			.add("guestStep", "true")
			.add("dashboard", "2");
		
		put("/flatmates/6")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(6)))
				.andExpect(jsonPath("$.email", is("carol2@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Carol (test UPDATE)")))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.roles").doesNotExist())
				.andExpect(jsonPath("$.enabled", is(false)))
				.andExpect(jsonPath("$.firstStep", is(true)))
				.andExpect(jsonPath("$.guestStep", is(true)))
				.andExpect(jsonPath("$.dashboard").doesNotExist());
        // @formatter:on

	}

	@Test
	public void update_invalid_id_NotFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("email", "carol2@mail.com")
			.add("nickname", "Carol (test UPDATE)")
			.add("password", "test")
			.add("roles", "USER")
			.add("enabled", "true")
			.add("firstStep", "true")
			.add("guestStep", "true")
			.add("dashboard", "2");
		
		put("/flatmates/999")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

}
