package br.com.cashhouse.server.endpoint.cashier;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.BIRO;
import static br.com.cashhouse.server.util.security.SecurityAccess.User.GRETCHEN;
import static br.com.cashhouse.server.util.security.SecurityAccess.User.JEAN;
import static br.com.cashhouse.server.util.security.SecurityAccess.User.MARCELO;
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
		get("/cashiers/3")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("Rent & Clean")))
				.andExpect(jsonPath("$.started", is(12.45)))
				.andExpect(jsonPath("$.balance", is(3.11)));
        // @formatter:on

	}

	@Test
	public void guest_find_Id_OK() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);
		
		// @formatter:off
		get("/cashiers/3")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("Rent & Clean")))
				.andExpect(jsonPath("$.started", is(12.45)))
				.andExpect(jsonPath("$.balance", is(3.11)));
        // @formatter:on

	}

	@Test
	public void find_all_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		get("/cashiers")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Energy & bin")))
				.andExpect(jsonPath("$[0].started", is(0.0)))
				.andExpect(jsonPath("$[0].balance",is(32.54)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Geral")))
				.andExpect(jsonPath("$[1].started", is(23.0)))
				.andExpect(jsonPath("$[1].balance", is(120.0)));
        // @formatter:on

	}

	@Test
	public void firstAccess_find_all_OK() throws Exception {
		
		loginWith(GRETCHEN);

		// @formatter:off
		get("/cashiers")
				.andExpect(status().isNoContent());
        // @formatter:on

	}

	@Test
	public void guest_find_all_OK() throws Exception {
		
		loginWith(BIRO).dashboard(MARCELO);

		// @formatter:off
		get("/cashiers")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Energy & bin")))
				.andExpect(jsonPath("$[0].started", is(0.0)))
				.andExpect(jsonPath("$[0].balance",is(32.54)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Geral")))
				.andExpect(jsonPath("$[1].started", is(23.0)))
				.andExpect(jsonPath("$[1].balance", is(120.0)));
        // @formatter:on

	}

	@Test
	public void find_notFound() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		get("/cashiers/99")
				.andExpect(status().isNotFound());
        // @formatter:on
		
	}

	@Test
	public void guest_find_notFound() throws Exception {

		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		get("/cashiers/999")
				.andExpect(status().isNotFound());
        // @formatter:on
		
	}

}
