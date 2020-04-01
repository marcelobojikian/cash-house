package br.com.cashhouse.server.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import br.com.cashhouse.server.spring.LocaleResolver;

@Service
public class LocaleServiceImpl implements LocaleService {

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getMessage(String code) {
		return messageSource.getMessage(code, null, code, localeResolver.resolveLocale(request));
	}

	@Override
	public String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, code, localeResolver.resolveLocale(request));
	}

}
