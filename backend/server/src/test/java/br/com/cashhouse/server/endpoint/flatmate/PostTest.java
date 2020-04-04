package br.com.cashhouse.server.endpoint.flatmate;

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
	public void save_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("email", "newFlatmate@mail.com")
			.add("nickname", "New Flatmate")
			.add("password", "test");

		post("/flatmates")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1001)))
				.andExpect(jsonPath("$.email", is("newFlatmate@mail.com")))
				.andExpect(jsonPath("$.nickname", is("New Flatmate")))
				.andExpect(jsonPath("$.password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void save_without_email_Fail() throws Exception {

		loginWith(JEAN);

		// @formatter:off
		body()
			.add("nickname", "New Gretchen Flatmate")
			.add("password", "test");

		post("/flatmates")
				.andExpect(status().isBadRequest());
        // @formatter:on

	}

	@Test
	public void save_without_nickname_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("email", "newFlatmate@mail.com")
			.add("password", "test");

		post("/flatmates")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1001)))
				.andExpect(jsonPath("$.email", is("newFlatmate@mail.com")))
				.andExpect(jsonPath("$.nickname", is("newFlatmate@mail.com")))
				.andExpect(jsonPath("$.password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void save_without_password_Fail() throws Exception {

		loginWith(JEAN);

		// @formatter:off
		body()
			.add("email", "newFlatmate@mail.com")
			.add("nickname", "New Gretchen Flatmate");

		post("/flatmates")
				.andExpect(status().isBadRequest());
        // @formatter:on

	}

	@Test
	public void guest_save_Forbidden() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		body()
			.add("email", "gretchen@mail.com")
			.add("nickname", "New Gretchen Flatmate")
			.add("password", "test");

		post("/flatmates")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

}
