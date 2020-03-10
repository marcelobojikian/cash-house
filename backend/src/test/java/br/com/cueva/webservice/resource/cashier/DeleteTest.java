package br.com.cueva.webservice.resource.cashier;

import static br.com.cueva.webservice.resource.SecurityAccess.User.JEAN;
import static br.com.cueva.webservice.resource.SecurityAccess.User.MARCELO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import br.com.cueva.webservice.resource.Oauth2;
import br.com.housecash.backend.App;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class DeleteTest extends Oauth2 {

	@Test
	public void delete_Forbidden_RoleType_USER() throws Exception {

		loginWith(JEAN);

		// @formatter:off
		delete("/cashiers/3")
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

	@Test
	public void delete_OK_RoleType_ADMIN() throws Exception {

		loginWith(MARCELO);

		// @formatter:off
		delete("/cashiers/2")
				.andExpect(status().isMethodNotAllowed());
        // @formatter:on

	}

}
