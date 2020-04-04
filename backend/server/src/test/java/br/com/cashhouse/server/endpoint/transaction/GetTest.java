package br.com.cashhouse.server.endpoint.transaction;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
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
public class GetTest extends Oauth2 {

	@Test
	public void should_get_all_transactions_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(5))))
	        .andExpect(jsonPath("$.content[*].id", hasItems(1,2,3,5,6)));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Action_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?action=WITHDRAW")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
	        .andExpect(jsonPath("$.content[*].id", contains(2,3)))
        	.andExpect(jsonPath("$.content[*].action", contains("WITHDRAW","WITHDRAW")));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Status_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?status=FINISHED")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].id", is(2)))
			.andExpect(jsonPath("$.content[0].status", is("FINISHED")));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Cashier_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?cashier=1")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(3)))
	        .andExpect(jsonPath("$.content[*].id", contains(1,2,6)))
	        .andExpect(jsonPath("$.content[*].cashier.id", contains(1,1,1)));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Action_and_Cashier_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?action=WITHDRAW&cashier=1")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].id", is(2)))
			.andExpect(jsonPath("$.content[0].action", is("WITHDRAW")))
			.andExpect(jsonPath("$.content[0].cashier.id", is(1)));
        // @formatter:on

	}

	@Test
	public void should_not_get_transactions_for_bad_pagination() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?page=999")
			.andExpect(status().isNoContent());
        // @formatter:on

	}

	@Test
	public void should_get_first_page_paginated_transactions() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?page=0&size=1")
			.andExpect(status().isPartialContent())
			.andExpect(jsonPath("$.content", hasSize(1)));
        // @formatter:on

	}

	@Test
	public void should_get_second_page_paginated_transactions() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?page=1&size=2")
			.andExpect(status().isPartialContent())
			.andExpect(jsonPath("$.content", hasSize(2)));
        // @formatter:on

	}

}
