package br.com.housecash.backend.security.context;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.housecash.backend.App;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.security.LoginWith;
import br.com.housecash.backend.security.service.AuthenticationFacade;

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
