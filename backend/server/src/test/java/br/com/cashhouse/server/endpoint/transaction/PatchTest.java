package br.com.cashhouse.server.endpoint.transaction;

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
public class PatchTest extends Oauth2 {

	@Test
	public void patch_NoContent_Fail() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		patch("/transactions/4", "{}")
				.andExpect(status().isNoContent());
        // @formatter:on

	}

	@Test
	public void patch_Transaction_notFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/9")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void patch_All_parameter_userCreator_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/11")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(11)))
				.andExpect(jsonPath("$.createBy.id", is(1)))
				.andExpect(jsonPath("$.assigned.id", is(3)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(0.32)));
        // @formatter:on

	}

	@Test
	public void patch_All_parameter_userAssigned_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/22")//.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(22)))
				.andExpect(jsonPath("$.createBy.id", is(5)))
				.andExpect(jsonPath("$.assigned.id", is(3)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(0.32)));
        // @formatter:on

	}
	
	@Test
	public void patch_Transaction_unavailable_Status_MethodNotAllowed() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("value", new BigDecimal("0.00"));
		
		patch("/transactions/2")
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

	@Test
	public void patch_invalid_Cashier_Fail() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 999l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/4")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void patch_invalid_Assigner_Fail() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 999l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/4")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void patch_invalid_Value_Fail() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("12345678901.32"));
		
		patch("/transactions/4")
				.andExpect(status().isBadRequest());
		
		get("/transactions/4")
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(4)))
				.andExpect(jsonPath("$.createBy.id", is(5)))
				.andExpect(jsonPath("$.assigned.id", is(5)))
				.andExpect(jsonPath("$.cashier.id", is(2)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(12.03)));
        // @formatter:on

	}

	@Test
	public void patch_Forbidden_Not_Owner_Dashboard() throws Exception {
		
		loginWith(BIRO).dashboard(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/4")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void patch_Forbidden_Owner_Dashboard_but_notAssignedAndCreator_fail() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body()
			.add("assigned", 3l)
			.add("cashier", 1l)
			.add("value", new BigDecimal("0.32"));
		
		patch("/transactions/4")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void patch_Only_assigned_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("assigned", 3l);
		
		patch("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(1)))
				.andExpect(jsonPath("$.assigned.id", is(3)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(1.99)));
        // @formatter:on

	}

	@Test
	public void patch_Only_cashier_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("cashier", 2l);
		
		patch("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(1)))
				.andExpect(jsonPath("$.assigned.id", is(1)))
				.andExpect(jsonPath("$.cashier.id", is(2)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(1.99)));
        // @formatter:on

	}

	@Test
	public void patch_Only_value_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("value", new BigDecimal("0.32"));
		
		patch("/transactions/1")//.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.createBy.id", is(1)))
				.andExpect(jsonPath("$.assigned.id", is(1)))
				.andExpect(jsonPath("$.cashier.id", is(1)))
				.andExpect(jsonPath("$.status", is("CREATED")))
				.andExpect(jsonPath("$.action", is("DEPOSIT")))
				.andExpect(jsonPath("$.value",is(0.32)));
        // @formatter:on

	}
	
	@Test
	public void patch_Transaction_unavailable_Cashier_NotFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("cashier", 3l);
		
		patch("/transactions/1")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void patch_Transaction_unavailable_Assigner_NotFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		body().add("assigned", 9l);
		
		patch("/transactions/1")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

}
