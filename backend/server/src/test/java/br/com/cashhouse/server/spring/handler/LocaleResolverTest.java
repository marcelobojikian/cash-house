package br.com.cashhouse.server.spring.handler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.cashhouse.server.spring.LocaleResolver;

public class LocaleResolverTest {

	@InjectMocks
	private LocaleResolver resolver = new LocaleResolver();

	@Mock
	private HttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetLocaleDefault() {
		
		Locale result = resolver.resolveLocale(request);
		assertEquals(Locale.getDefault(), result);

		when(request.getHeader("Accept-Language")).thenReturn("");
		result = resolver.resolveLocale(request);
		assertEquals(Locale.getDefault(), result);
		
	}

	@Test
	public void shodldGetEnLocale() {
		when(request.getHeader("Accept-Language")).thenReturn("en-US,en;q=0.8");
		Locale result = resolver.resolveLocale(request);
		assertEquals(new Locale("en"), result);
	}

	@Test
	public void shouldGetEsLocale() {
		when(request.getHeader("Accept-Language")).thenReturn("es-MX,en-US;q=0.7,en;q=0.3");
		Locale result = resolver.resolveLocale(request);
		assertEquals(new Locale("es"), result);
	}

	@Test
	public void shouldGetPtLocale() {
		when(request.getHeader("Accept-Language")).thenReturn("pt-MX,en-US;q=0.7,en;q=0.3");
		Locale result = resolver.resolveLocale(request);
		assertEquals(new Locale("pt"), result);
	}

}
