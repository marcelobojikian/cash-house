package br.com.housecash.backend.resource.transaction.action;

import static br.com.housecash.backend.resource.SecurityAccess.User.MARCELO;
import static br.com.housecash.backend.resource.SecurityAccess.User.WILL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import br.com.housecash.backend.App;
import br.com.housecash.backend.resource.Oauth2;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class PostCancel extends Oauth2 {
	
	private ResultActions cancelTransaction(int id) throws Exception {
		body(); // Body empty
		return post(String.format("/transactions/%s/cancel", id));
	}

	@Test
	public void send_fail_STATUS_FINISHED() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		cancelTransaction(2)
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

//	@Test
//	public void send_fail_STATUS_SENDED() throws Exception {
//		
//		loginWith(MARCELO);
//
//		// @formatter:off
//		sendTransaction(3)
//				.andExpect(status().isMethodNotAllowed());
//        // @formatter:on
//
//	}

	@Test
	public void send_fail_STATUS_CANCELED() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		cancelTransaction(5)
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

	@Test
	public void send_fail_STATUS_DELETED() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		cancelTransaction(6)
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

	@Test
	public void send_fail_NotFound() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		cancelTransaction(999)
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void send_fail_NotDashboardOwner() throws Exception {
		
		loginWith(WILL).dashboard(MARCELO);

		// @formatter:off
		cancelTransaction(3)
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void send_OK() throws Exception {
		
		loginWith(MARCELO);

		// @formatter:off
		cancelTransaction(3)
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.status", is("CANCELED")));
        // @formatter:on

	}

}
