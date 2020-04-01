package br.com.cashhouse.server.util.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.App;
import br.com.cashhouse.server.service.AuthenticationFacade;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AuthenticationFacadeTest {
	
	@Autowired
	private AuthenticationFacade authenticationFacade;
 
    @Test
    @LoginWith(username = "marcelo@mail.com")
    public void mockApplicationUser() {

    	Flatmate flatmate = authenticationFacade.getFlatmateLogged();

		assertEquals(flatmate.getId(), new Long(1));

    }

}
