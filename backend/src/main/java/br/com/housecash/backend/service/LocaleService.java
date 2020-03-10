package br.com.housecash.backend.service;

public interface LocaleService {

	public String getMessage(String code);

	public String getMessage(String code, Object... args);

}
