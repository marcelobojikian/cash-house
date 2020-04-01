package br.com.cashhouse.server.service;

public interface LocaleService {

	public String getMessage(String code);

	public String getMessage(String code, Object... args);

}
