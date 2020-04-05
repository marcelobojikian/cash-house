package br.com.cashhouse.server.endpoint.transaction;

import static br.com.cashhouse.server.util.security.SecurityAccess.User.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
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
public class GetGroupTest extends Oauth2 {

	@Test
	public void should_get_all_transactions_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-28")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(28)))
			.andExpect(jsonPath("$.content[0].data", hasSize(greaterThanOrEqualTo(3))))
	        .andExpect(jsonPath("$.content[0].data[*].id", hasItems(1,3,6)))
			.andExpect(jsonPath("$.content[1].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[1].year", is(2020)))
			.andExpect(jsonPath("$.content[1].month", is(1)))
			.andExpect(jsonPath("$.content[1].day", is(27)))
			.andExpect(jsonPath("$.content[1].data", hasSize(greaterThanOrEqualTo(2))))
	        .andExpect(jsonPath("$.content[1].data[*].id", hasItems(2,5)));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Action_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&action=WITHDRAW")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-28")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(28)))
			.andExpect(jsonPath("$.content[0].data", hasSize(1)))
			.andExpect(jsonPath("$.content[0].data[0].id", is(3)))
			.andExpect(jsonPath("$.content[0].data[0].action", is("WITHDRAW")))
			.andExpect(jsonPath("$.content[1].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[1].year", is(2020)))
			.andExpect(jsonPath("$.content[1].month", is(1)))
			.andExpect(jsonPath("$.content[1].day", is(27)))
			.andExpect(jsonPath("$.content[1].data", hasSize(1)))
			.andExpect(jsonPath("$.content[1].data[0].id", is(2)))
			.andExpect(jsonPath("$.content[1].data[0].action", is("WITHDRAW")));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Status_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&status=FINISHED")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(27)))
			.andExpect(jsonPath("$.content[0].data", hasSize(1)))
			.andExpect(jsonPath("$.content[0].data[0].id", is(2)))
			.andExpect(jsonPath("$.content[0].data[0].status", is("FINISHED")));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Cashier_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&cashier=1")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-28")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(28)))
			.andExpect(jsonPath("$.content[0].data", hasSize(2)))
	        .andExpect(jsonPath("$.content[0].data[*].id", contains(1,6)))
	        .andExpect(jsonPath("$.content[0].data[*].cashier.id", contains(1,1)))
			.andExpect(jsonPath("$.content[1].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[1].year", is(2020)))
			.andExpect(jsonPath("$.content[1].month", is(1)))
			.andExpect(jsonPath("$.content[1].day", is(27)))
			.andExpect(jsonPath("$.content[1].data", hasSize(1)))
			.andExpect(jsonPath("$.content[1].data[0].id", is(2)))
			.andExpect(jsonPath("$.content[1].data[0].cashier.id", is(1)));
        // @formatter:on

	}

	@Test
	public void should_get_all_transactions_filtered_by_Action_and_Cashier_with_ok_status() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&action=WITHDRAW&cashier=1")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(27)))
			.andExpect(jsonPath("$.content[0].data", hasSize(1)))
			.andExpect(jsonPath("$.content[0].data[0].id", is(2)))
			.andExpect(jsonPath("$.content[0].data[0].action", is("WITHDRAW")))
			.andExpect(jsonPath("$.content[0].data[0].cashier.id", is(1)));
        // @formatter:on

	}

	@Test
	public void should_not_get_transactions_for_bad_pagination() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&page=999")
			.andExpect(status().isNoContent());
        // @formatter:on

	}

	@Test
	public void should_get_first_page_paginated_transactions() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&page=0&size=1")
			.andExpect(status().isPartialContent())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-28")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(28)))
			.andExpect(jsonPath("$.content[0].data", hasSize(1)));
        // @formatter:on

	}

	@Test
	public void should_get_second_page_paginated_transactions() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		get("/transactions?group=createdDate&page=1&size=2")
			.andExpect(status().isPartialContent())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].createdDate", is("2020-01-28")))
			.andExpect(jsonPath("$.content[0].year", is(2020)))
			.andExpect(jsonPath("$.content[0].month", is(1)))
			.andExpect(jsonPath("$.content[0].day", is(28)))
			.andExpect(jsonPath("$.content[0].data", hasSize(1)))
			.andExpect(jsonPath("$.content[1].createdDate", is("2020-01-27")))
			.andExpect(jsonPath("$.content[1].year", is(2020)))
			.andExpect(jsonPath("$.content[1].month", is(1)))
			.andExpect(jsonPath("$.content[1].day", is(27)))
			.andExpect(jsonPath("$.content[1].data", hasSize(1)));
        // @formatter:on

	}

}
