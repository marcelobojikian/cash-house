package br.com.cashhouse.server.endpoint.cashier;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
	public void update_OK_ADMIN_Dashboard() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("name", "New Rent & Bills")
			.add("started", new BigDecimal("0.50"))
			.add("balance", new BigDecimal("32.99"))
			.add("owner", "2");
		
		put("/cashiers/1")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("New Rent & Bills")))
				.andExpect(jsonPath("$.started", is(0.50)))
				.andExpect(jsonPath("$.balance", is(32.99)))
				.andExpect(jsonPath("$.owner.id", is(2)));
        // @formatter:on

	}

	@Test
	public void update_invalid_id_NotFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("name", "New Name")
			.add("started", new BigDecimal("44.32"))
			.add("balance", new BigDecimal("12.42"))
			.add("owner", "2");
		
		put("/cashiers/999")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void guest_update_invalid_id_InOther_Dashboard() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("name", "New Name")
			.add("started", new BigDecimal("44.32"))
			.add("balance", new BigDecimal("12.42"))
			.add("owner", "2");
		
		put("/cashiers/3")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

}
