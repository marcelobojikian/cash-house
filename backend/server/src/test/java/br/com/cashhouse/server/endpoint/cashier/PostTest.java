package br.com.cashhouse.server.endpoint.cashier;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
	public void save_WithStarted_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", new BigDecimal("11.23"))
			.add("balance", new BigDecimal("123.23"));

		post("/cashiers")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1001)))
				.andExpect(jsonPath("$.name", is("Post Test Cashier")))
				.andExpect(jsonPath("$.started", is(11.23)))
				.andExpect(jsonPath("$.balance",is(123.23)));
        // @formatter:on

	}

	@Test
	public void save_invalid_balance_Fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", new BigDecimal("11.23"))
			.add("balance", "AA123");

		post("/cashiers")
				.andExpect(status().isBadRequest());;
        // @formatter:on

	}

	@Test
	public void save_invalid_MAX_balance_Fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", new BigDecimal("11.23"))
			.add("balance", "12345678901.22");

		post("/cashiers")
				.andExpect(status().isBadRequest());;
        // @formatter:on

	}

	@Test
	public void save_invalid_MAX_started_Fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", "12345678901.22")
			.add("balance", new BigDecimal("11.23"));

		post("/cashiers")
				.andExpect(status().isBadRequest());;
        // @formatter:on

	}

	@Test
	public void save_invalid_started_Fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", "AA123")
			.add("balance", new BigDecimal("11.23"));

		post("/cashiers")
				.andExpect(status().isBadRequest());;
        // @formatter:on

	}

	@Test
	public void save_WithoutStarted_OK() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("balance", new BigDecimal("198.44"));

		post("/cashiers")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1001)))
				.andExpect(jsonPath("$.name", is("Post Test Cashier")))
				.andExpect(jsonPath("$.started", is(198.44)))
				.andExpect(jsonPath("$.balance",is(198.44)));
        // @formatter:on

	}

	@Test
	public void save_without_balance_fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", new BigDecimal("11.23"));

		post("/cashiers")
				.andExpect(status().isBadRequest());
        // @formatter:on

	}
	
	@Test
	public void save_without_name_fail() throws Exception {
		
		loginWith(JEAN);

		// @formatter:off
		body()
			.add("started", new BigDecimal("11.23"))
			.add("balance", new BigDecimal("123.23"));

		post("/cashiers")
				.andExpect(status().isBadRequest());
        // @formatter:on
		
	}
	
	@Test
	public void guest_save_Forbidden_fail() throws Exception {
		
		loginWith(GRETCHEN).dashboard(JEAN);

		// @formatter:off
		body()
			.add("name", "Post Test Cashier")
			.add("started", new BigDecimal("11.23"))
			.add("balance", new BigDecimal("123.23"));

		post("/cashiers")
				.andExpect(status().isForbidden());
        // @formatter:on
		
	}

}
