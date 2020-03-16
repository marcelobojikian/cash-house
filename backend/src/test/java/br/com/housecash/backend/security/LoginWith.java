package br.com.housecash.backend.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = LoginWithSecurityContextFactory.class)
public @interface LoginWith {

	String username() default "";
	
}
