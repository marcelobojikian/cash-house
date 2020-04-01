package br.com.cashhouse.server.spring;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocaleResolver extends AcceptHeaderLocaleResolver {

	private static final List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("es"), new Locale("pt"));

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
		log.debug("language parameter: " + language);
		if (language == null || language.isEmpty()) {
			log.debug("language(default): " + Locale.getDefault());
			return Locale.getDefault();
		}
		List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
		Locale locale = Locale.lookup(list, LOCALES);
		log.debug("Locale: " + locale);
		return locale;
	}

}
