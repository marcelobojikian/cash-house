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
public class PatchTest extends Oauth2 {

	@Test
	public void patch_nickname_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body().add("nickname", "Jean (test UPDATE)");

		patch("/flatmates/8")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test UPDATE)")))
				.andExpect(jsonPath("$.password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void patch_nickname_password_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("nickname", "Jean (test UPDATE)")
			.add("password", "test123");

		patch("/flatmates/8")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test UPDATE)")))
				.andExpect(jsonPath("$.password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void patch_invalid_id_Forbidden() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body().add("nickname", "Jean (test UPDATE)");

		patch("/flatmates/999")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void patch_nickname_Forbidden() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body().add("nickname", "Gretchen (test UPDATE)");

		patch("/flatmates/9")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void patch_email_MethodNotAllowed() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body().add("email", "test@othermail.com");

		patch("/flatmates/8")
				.andExpect(status().isBadRequest());
        // @formatter:on

	}

}
