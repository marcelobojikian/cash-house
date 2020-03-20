package br.com.housecash.backend.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import br.com.housecash.backend.util.LoginSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = LoginSecurityContextFactory.class)
public @interface LoginWith {

	long id() default -1l;
	
	String email() default "none@mail.com";
	
	String nickname() default "none@mail.com";
	
	String password() default "none";
	
	String[] roles() default {}; 
	
}
