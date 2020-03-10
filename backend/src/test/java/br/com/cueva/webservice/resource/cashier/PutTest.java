package br.com.cueva.webservice.resource.cashier;

import static br.com.cueva.webservice.resource.SecurityAccess.User.GRETCHEN;
import static br.com.cueva.webservice.resource.SecurityAccess.User.JEAN;
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

import br.com.cueva.webservice.resource.Oauth2;
import br.com.housecash.backend.App;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class PutTest extends Oauth2 {

	@Test
	public void guest_update_forbidden_NotOwner_Dashboard() throws Exception {
		
		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		body()
			.add("name", "Change Name")
			.add("started", new BigDecimal("9.88"))
			.add("balance", new BigDecimal("1234.22"));
				
		put("/cashiers/3")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void update_OK_Owner_Dashboard() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "New Rent & Bills")
			.add("started", new BigDecimal("0.50"))
			.add("balance", new BigDecimal("32.99"));
		
		put("/cashiers/3")
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("New Rent & Bills")))
				.andExpect(jsonPath("$.started", is(0.50)))
				.andExpect(jsonPath("$.balance", is(32.99)));
        // @formatter:on

	}

	@Test
	public void update_invalid_id_NotFound() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "New Name")
			.add("started", new BigDecimal("44.32"))
			.add("balance", new BigDecimal("12.42"));
		
		put("/cashiers/999")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void guest_update_invalid_id_InOther_Dashboard() throws Exception {
		
		loginWith(GRETCHEN);

		// @formatter:off
		body()
			.add("name", "New Name")
			.add("started", new BigDecimal("44.32"))
			.add("balance", new BigDecimal("12.42"));
		
		put("/cashiers/3")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

}
