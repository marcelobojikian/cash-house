package br.com.housecash.backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Profile("developer")
@Configuration
@EnableResourceServer
public class H2ConfigConsole extends ResourceServerConfigurerAdapter {
	
    private static final String RESOURCE_ID = "h2-console";
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
      	.headers().disable()
      	.authorizeRequests()
        .antMatchers("/h2/**").permitAll();
    }

}
