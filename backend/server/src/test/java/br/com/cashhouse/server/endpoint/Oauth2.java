package br.com.cashhouse.server.endpoint;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.cashhouse.server.util.ContentHelper;
import br.com.cashhouse.server.util.security.Authorization;
import br.com.cashhouse.server.util.security.SecurityAccess;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Oauth2 {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	private Authorization authUser;
	private SecurityAccess securityAccess;
	private ContentHelper content;

	private MockMvc mockMvc;

	@Before
	public void init() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	public SecurityAccess loginWith(SecurityAccess.User user) {
		this.securityAccess = SecurityAccess.login().with(user);
		this.authUser = Authorization.setupWithToken(mockMvc, this.securityAccess);
		return this.securityAccess;
	}
	
	public ContentHelper body() {
		this.content = ContentHelper.generate();
		return content;
	}
	
	public ResultActions post(String url) throws Exception {
		if(content == null) {
			throw new IllegalArgumentException("Undeclared content. Use first ContentHelper.generate()");
		}
		String content = this.content.toJson();
        log.debug(String.format("Url %s with body: %s", url, content));
		this.content = null;
		return post(url, content);
	}
	
	public ResultActions put(String url) throws Exception {
		if(content == null) {
			throw new IllegalArgumentException("Undeclared content. Use first ContentHelper.generate()");
		}
		String content = this.content.toJson();
        log.debug(String.format("Url %s with body: %s", url, content));
		this.content = null;
		return put(url, content);
	}
	
	public ResultActions patch(String url) throws Exception {
		if(content == null) {
			throw new IllegalArgumentException("Undeclared content. Use first ContentHelper.generate()");
		}
		String content = this.content.toJson();
        log.debug(String.format("Url %s with body: %s", url, content));
		this.content = null;
		return patch(url, content);
	}
	
	public ResultActions get(String url) throws Exception {
		return mockMvc.perform(authUser.get(url).response());
	}
	
	public ResultActions post(String url, String content) throws Exception {
		return mockMvc.perform(authUser.post(url).response().content(content).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}
	
	public ResultActions put(String url, String content) throws Exception {
		return mockMvc.perform(authUser.put(url).response().content(content).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}
	
	public ResultActions patch(String url, String content) throws Exception {
		return mockMvc.perform(authUser.patch(url).response().content(content).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}
	
	public ResultActions delete(String url) throws Exception {
		return mockMvc.perform(authUser.delete(url).response());
	}

}
