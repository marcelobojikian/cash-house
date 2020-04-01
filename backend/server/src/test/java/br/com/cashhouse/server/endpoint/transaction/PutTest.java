package br.com.cashhouse.server.endpoint.transaction;

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
public class PutTest extends Oauth2 {

	@Test
	public void update_OK_Owner_Dashboard_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(2)))
				.andExpect(jsonPath("$.assigned.id", is(2)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("WITHDRAW")))
				.andExpect(jsonPath("$.value",is(2.82)));
        // @formatter:on

	}

	@Test
	public void update_OK_assigned_User_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 1l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(2)))
				.andExpect(jsonPath("$.assigned.id", is(1)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("WITHDRAW")))
				.andExpect(jsonPath("$.value",is(2.82)));
        // @formatter:on

	}

	@Test
	public void update_OK_createBy_User_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 1l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(1)))
				.andExpect(jsonPath("$.assigned.id", is(2)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("WITHDRAW")))
				.andExpect(jsonPath("$.value",is(2.82)));
        // @formatter:on

	}
	
	@Test
	public void update_invalid_id_NotFound_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/999")
				.andExpect(status().isNotFound());
        // @formatter:on

	}
	
	@Test
	public void update_invalid_cashier_NotFound_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 3l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")
				.andExpect(status().isNotFound());
        // @formatter:on

	}
	
	@Test
	public void update_invalid_assigned_NotFound_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 9l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")
				.andExpect(status().isNotFound());
        // @formatter:on

	}
	
	@Test
	public void update_invalid_createBy_NotFound_ADMIN() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.addParent("createBy", "id", 9l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void update_NotOwner_Dashboard_Forbidden_ADMIN() throws Exception {
		
		loginWith(MARCELO).dashboard(JEAN);

		// @formatter:off
		body()
			.addParent("createBy", "id", 2l)
			.addParent("assigned", "id", 2l)
			.addParent("cashier", "id", 1l)
			.add("status", "CREATED")
			.add("action", "WITHDRAW")
			.add("value", new BigDecimal("2.82"));
		
		put("/transactions/1")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

}
