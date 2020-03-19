package br.com.housecash.backend.resource.transaction;

import static br.com.housecash.backend.resource.SecurityAccess.User.JEAN;
import static br.com.housecash.backend.resource.SecurityAccess.User.MARCELO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import br.com.housecash.backend.App;
import br.com.housecash.backend.resource.Oauth2;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class DeleteTest extends Oauth2 {

	@Test
	public void delete_OK() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		delete("/transactions/2")
				.andExpect(status().isOk());
		
		get("/transactions/2")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

	@Test
	public void delete_Forbidden() throws Exception {

		loginWith(MARCELO).dashboard(JEAN);

		// @formatter:off
		delete("/transactions/9")
				.andExpect(status().isForbidden());
        // @formatter:on

	}

	@Test
	public void delete_NotFound() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		delete("/transactions/99")
				.andExpect(status().isNotFound());
        // @formatter:on

	}

}
