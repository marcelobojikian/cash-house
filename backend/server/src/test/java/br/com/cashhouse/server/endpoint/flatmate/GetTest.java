package br.com.cashhouse.server.endpoint.flatmate;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.GRETCHEN;
import static br.com.cashhouse.server.util.security.SecurityAccess.User.JEAN;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
public class GetTest extends Oauth2 {

	@Test
	public void find_Id_OK() throws Exception {
		
		loginWith(JEAN);
		
		// @formatter:off
		get("/flatmates/9")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(9)))
				.andExpect(jsonPath("$.email", is("gretchen@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Gretchen (test)")))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.firstStep", is(false)))
				.andExpect(jsonPath("$.guestStep", is(false)));
        // @formatter:on

	}

	@Test
	public void guest_find_Id_OK() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);
		
		// @formatter:off
		get("/flatmates/10")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.email", is("fernando@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Fernando (test)")))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.firstStep", is(false)))
				.andExpect(jsonPath("$.guestStep", is(false)));
        // @formatter:on

	}

	@Test
	public void guest_find_Owner_OK() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);
		
		// @formatter:off
		get("/flatmates/8")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(8)))
				.andExpect(jsonPath("$.email", is("jean@mail.com")))
				.andExpect(jsonPath("$.nickname", is("Jean (test)")))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.firstStep", is(false)))
				.andExpect(jsonPath("$.guestStep", is(false)));
        // @formatter:on

	}

	@Test
	public void find_all_OK() throws Exception {

		loginWith(JEAN);

		// @formatter:off
		get("/flatmates")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(9)))
				.andExpect(jsonPath("$[0].email", is("gretchen@mail.com")))
				.andExpect(jsonPath("$[0].nickname", is("Gretchen (test)")))
				.andExpect(jsonPath("$[0].password").doesNotExist())
				.andExpect(jsonPath("$[1].id", is(10)))
				.andExpect(jsonPath("$[1].email", is("fernando@mail.com")))
				.andExpect(jsonPath("$[1].nickname", is("Fernando (test)")))
				.andExpect(jsonPath("$[1].password").doesNotExist())
				.andExpect(jsonPath("$[2].id", is(1)))
				.andExpect(jsonPath("$[2].email", is("marcelo@mail.com")))
				.andExpect(jsonPath("$[2].nickname", is("Marcelo (test)")))
				.andExpect(jsonPath("$[2].password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void firstAccess_find_all_OK() throws Exception {

		loginWith(GRETCHEN);

		// @formatter:off
		get("/flatmates")
				.andExpect(status().isNoContent());
        // @formatter:on

	}

	@Test
	public void guest_find_all_OK() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		get("/flatmates")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(9)))
				.andExpect(jsonPath("$[0].email", is("gretchen@mail.com")))
				.andExpect(jsonPath("$[0].nickname", is("Gretchen (test)")))
				.andExpect(jsonPath("$[0].password").doesNotExist())
				.andExpect(jsonPath("$[1].id", is(10)))
				.andExpect(jsonPath("$[1].email", is("fernando@mail.com")))
				.andExpect(jsonPath("$[1].nickname", is("Fernando (test)")))
				.andExpect(jsonPath("$[1].password").doesNotExist())
				.andExpect(jsonPath("$[2].id", is(1)))
				.andExpect(jsonPath("$[2].email", is("marcelo@mail.com")))
				.andExpect(jsonPath("$[2].nickname", is("Marcelo (test)")))
				.andExpect(jsonPath("$[2].password").doesNotExist());
        // @formatter:on

	}

	@Test
	public void find_notFound() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		get("/flatmates/5")
				.andExpect(status().isNotFound());
        // @formatter:on
		
	}

	@Test
	public void guest_find_notFound() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		get("/flatmates/5")
				.andExpect(status().isNotFound());
        // @formatter:on
		
	}

}
