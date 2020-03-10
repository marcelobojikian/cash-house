package br.com.cueva.webservice.util;

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

public class ManualAuthorization {

	private static final String CLIENT_ID = "cueva";
	private static final String CLIENT_SECRET = "noop";

	public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	private MockMvc mockMvc;
	private String username;
	private String password;
	private Integer dashboard;
	
	private MockHttpServletRequestBuilder request;
	
	private ManualAuthorization(MockMvc mockMvc, String username, String password) {
		this.mockMvc = mockMvc;
		this.username = username;
		this.password = password;
	}

	private String obtainAccessToken(String username, String password) throws Exception {

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", CLIENT_ID);
		params.add("username", username);
		params.add("password", password);

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

    public static ManualAuthorization setup(MockMvc mockMvc) {
        return setupWithToken(mockMvc, "", "");
    }
    
    public static ManualAuthorization setupWithToken(MockMvc mockMvc, String username, String password) {
    	return new ManualAuthorization(mockMvc, username, password);
    }

	public ManualAuthorization get(String url) {
    	this.request = MockMvcRequestBuilders.get(url);
        return this;
    }

    public ManualAuthorization post(String url) {
    	this.request = MockMvcRequestBuilders.post(url);
        return this;
    }

    public ManualAuthorization put(String url) {
    	this.request = MockMvcRequestBuilders.put(url);
        return this;
    }

    public ManualAuthorization patch(String url) {
    	this.request = MockMvcRequestBuilders.patch(url);
        return this;
    }

    public ManualAuthorization delete(String url) {
    	this.request = MockMvcRequestBuilders.delete(url);
        return this;
    }

	public ManualAuthorization dashboard(Integer id) {
    	this.dashboard = id;
        return this;
    }
    
    public MockHttpServletRequestBuilder response() throws Exception {

		final String accessToken = obtainAccessToken(username, password);
    	
    	HttpHeaders httpHeaders = new HttpHeaders();
    	httpHeaders.add("Authorization", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
    	
    	if(!isEmpty(dashboard)) {
    		httpHeaders.add("dashboard", dashboard.toString());
    	}
    	
    	request.headers(httpHeaders);
        return request;
        
    }

}
