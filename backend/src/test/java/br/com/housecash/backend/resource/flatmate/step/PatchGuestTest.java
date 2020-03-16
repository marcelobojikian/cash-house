package br.com.housecash.backend.resource.flatmate.step;

import static br.com.housecash.backend.resource.SecurityAccess.User.JEAN;
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

import br.com.housecash.backend.App;
import br.com.housecash.backend.resource.Oauth2;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class PatchGuestTest extends Oauth2 {

	@Test
	public void patch_nickname_NOT_password_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body().add("nickname", "Jean (test UPDATE)");

		patch("/flatmates/8/step/guest")
				.andExpect(status().isBadRequest());
        // @formatter:on

	}

	@Test
	public void patch_nickname_password_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("nickname", "Jean (test UPDATE)")
			.add("password", "test123");

		patch("/flatmates/8/step/guest")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test UPDATE)")))
				.andExpect(jsonPath("$.password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void patch_other_user_Forbidden() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("nickname", "Jean (test UPDATE)")
			.add("password", "test123");

		patch("/flatmates/3/step/guest")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

}
