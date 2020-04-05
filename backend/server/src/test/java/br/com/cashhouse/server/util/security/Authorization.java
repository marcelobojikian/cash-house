package br.com.cashhouse.server.util.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Authorization {
	
	private static final String VERSION = "/v1";

	private static final String CLIENT_ID = "cueva";
	private static final String CLIENT_SECRET = "noop";

	public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	private MockMvc mockMvc;
	private SecurityAccess securityAccess;
	
	private MockHttpServletRequestBuilder request;
	
	private Authorization(MockMvc mockMvc, SecurityAccess securityAccess) {
		this.mockMvc = mockMvc;
		this.securityAccess = securityAccess;
	}

	private String obtainAccessToken(SecurityAccess securityAccess) throws Exception {

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", CLIENT_ID);
		params.add("username", securityAccess.getUsername());
		params.add("password", securityAccess.getPassword());

		// @formatter:off
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
                               .params(params)
                               .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                               .accept(CONTENT_TYPE))
                               .andExpect(status().isOk())
                               .andExpect(content().contentType(CONTENT_TYPE));
        
        // @formatter:on

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
		
	}

    public static Authorization setupWithToken(MockMvc mockMvc, SecurityAccess securityAccess) {
    	return new Authorization(mockMvc, securityAccess);
    }

	public Authorization get(String url) {
    	this.request = MockMvcRequestBuilders.get(VERSION+url);
        return this;
    }

    public Authorization post(String url) {
    	this.request = MockMvcRequestBuilders.post(VERSION+url);
        return this;
    }

    public Authorization put(String url) {
    	this.request = MockMvcRequestBuilders.put(VERSION+url);
        return this;
    }

    public Authorization patch(String url) {
    	this.request = MockMvcRequestBuilders.patch(VERSION+url);
        return this;
    }

    public Authorization delete(String url) {
    	this.request = MockMvcRequestBuilders.delete(VERSION+url);
        return this;
    }
    
    public MockHttpServletRequestBuilder response() throws Exception {

		final String accessToken = obtainAccessToken(securityAccess);
    	
    	HttpHeaders httpHeaders = new HttpHeaders();
    	httpHeaders.add("Authorization", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
    	
    	Integer dashboard = securityAccess.getDashboard();
    	if(!isEmpty(dashboard)) {
    		httpHeaders.add("dashboard", dashboard.toString());
    	}
    	
    	request.headers(httpHeaders);
        return request;
        
    }

}
