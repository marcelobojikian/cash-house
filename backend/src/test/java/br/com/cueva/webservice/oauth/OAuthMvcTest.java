package br.com.cueva.webservice.oauth;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.cueva.webservice.util.ManualAuthorization;
import br.com.housecash.backend.App;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
//@ActiveProfiles("mvc")
public class OAuthMvcTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}

	@Test
	public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/server/info")).andExpect(status().isUnauthorized());
	}

	@Test
	public void givenInvalidRole_whenGetSecureRequest_thenForbidden() throws Exception {
		
		ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
		
		mockMvc.perform(jean.get("/server/info").response())
				.andExpect(status().isForbidden());
		
	}

	@Test
	public void givenToken_whenPostGetSecureRequest_thenOk() throws Exception {
		
		ManualAuthorization marcelo = ManualAuthorization.setupWithToken(mockMvc, "marcelo@mail.com", "test");

		// @formatter:off
        mockMvc.perform(marcelo.get("/server/info").response()
                .accept(ManualAuthorization.CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(ManualAuthorization.CONTENT_TYPE))
                .andExpect(jsonPath("$.version", is("0.0.1")));
	      
        // @formatter:on
		
//		String employeeString = "{\"version\":\"" + EMAIL + "\",\"name\":\"" + NAME + "\",\"age\":30}";
//
//        mockMvc.perform(post("/server/info")
//                .header("Authorization", "Bearer " + accessToken)
//                .contentType(CONTENT_TYPE)
//                .content(employeeString)
//                .accept(CONTENT_TYPE))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(get("/employee")
//                .param("email", EMAIL)
//                .header("Authorization", "Bearer " + accessToken)
//                .accept(CONTENT_TYPE))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(CONTENT_TYPE))
//                .andExpect(jsonPath("$.name", is(NAME)));
        
	}

}
