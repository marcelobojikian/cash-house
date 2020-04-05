package br.com.cashhouse.server.spring.handler;

import static br.com.cashhouse.server.util.EntityFactory.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.InvalidOperationException;
import br.com.cashhouse.server.service.LocaleService;
import br.com.cashhouse.server.util.ContentHelper;
import br.com.cashhouse.server.util.annotation.LoginWith;
import lombok.Getter;
import lombok.Setter;

@RunWith(SpringRunner.class)
@WebMvcTest(RestExceptionHandlerTest.SampleController.class)
@ContextConfiguration(classes={
		RestExceptionHandlerTest.SampleController.class,
		RestExceptionHandler.class,
		HttpServletRequest.class
})
public class RestExceptionHandlerTest {
	
	DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    @Autowired
    private MockMvc mockMvc;

	@MockBean
	private LocaleService localeService;

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_MethodArgumentNotValidException() throws Exception {
		
		ContentHelper content = ContentHelper.generate();
		content.add("value", "");
		
		MockHttpServletRequestBuilder request = get("/exception/bean/validator")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content.toJson())
				.characterEncoding("utf-8");
		
		when(localeService.getMessage(eq("dto.field.invalid"),eq("value"),eq("must not be empty"))).thenReturn("Field 'value' must not be empty");
		
		mockMvc.perform(request)
			      	.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
					.andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
					.andExpect(jsonPath("$.message", is("Field 'value' must not be empty")))
					.andExpect(jsonPath("$.path", is("/exception/bean/validator")))
					.andExpect(jsonPath("$.timestamp", notNullValue()));
		
	}

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_NoContentException() throws Exception {
		
		when(localeService.getMessage("body.no.content")).thenReturn("Body of request is invalid");
		
		mockMvc.perform(get("/exception/no_content"))
			      	.andExpect(status().isNoContent());
		
	}

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_InvalidOperationException() throws Exception {

		when(localeService.getMessage("Transaction.status.invalid.operation", 1l, Status.SENDED)).thenReturn("Invalid operation, Transaction 1 with status equal to SENDED");
		
		mockMvc.perform(get("/exception/transaction_invalid/status"))
			      	.andExpect(status().isMethodNotAllowed())
					.andExpect(jsonPath("$.status", is(HttpStatus.METHOD_NOT_ALLOWED.value())))
					.andExpect(jsonPath("$.error", is(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())))
					.andExpect(jsonPath("$.message", is("Invalid operation, Transaction 1 with status equal to SENDED")))
					.andExpect(jsonPath("$.path", is("/exception/transaction_invalid/status")))
					.andExpect(jsonPath("$.timestamp", notNullValue()));
		
	}

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_EntityNotFoundException_Class() throws Exception {

		when(localeService.getMessage(eq("Flatmate.not.found"), eq(1l))).thenReturn("Flatmate 1 not found");
		
		mockMvc.perform(get("/exception/not_found/class"))
			      	.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
					.andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
					.andExpect(jsonPath("$.message", is("Flatmate 1 not found")))
					.andExpect(jsonPath("$.path", is("/exception/not_found/class")))
					.andExpect(jsonPath("$.timestamp", notNullValue()));
		
	}

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_EntityNotFoundException_Class_Field() throws Exception {

		when(localeService.getMessage(eq("Transaction.cashier.not.found"), eq(1l))).thenReturn("Transaction cashier 1 not found");
		
		mockMvc.perform(get("/exception/not_found/class_field"))
			      	.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
					.andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
					.andExpect(jsonPath("$.message", is("Transaction cashier 1 not found")))
					.andExpect(jsonPath("$.path", is("/exception/not_found/class_field")))
					.andExpect(jsonPath("$.timestamp", notNullValue()));
		
	}

	@LoginWith(roles = "ADMIN")
	@Test
	public void should_throw_SpringAccessDeniedException() throws Exception {

		mockMvc.perform(get("/exception/spring/access_denied"))
			      	.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
					.andExpect(jsonPath("$.error", is(HttpStatus.FORBIDDEN.getReasonPhrase())))
					.andExpect(jsonPath("$.message", is("Spring AccessDeniedException")))
					.andExpect(jsonPath("$.path", is("/exception/spring/access_denied")))
					.andExpect(jsonPath("$.timestamp", notNullValue()));
		
	}

	@RestController
	@RequestMapping("/exception")
	public static class SampleController{
	
		@GetMapping("/bean/validator")
		public void throwMethodArgumentNotValidException(@RequestBody @Valid SimpleBean propertie) throws Exception {}
	
		@GetMapping("/no_content")
		public ResponseEntity<Object> throwNoContentException() throws Exception {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	
		@GetMapping("/transaction_invalid/status")
		public ResponseEntity<?> throwInvalidOperationException() throws Exception {
			Transaction transaction = createTransaction(1l, 2.33, Status.SENDED, Action.WITHDRAW);
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}
	
		@GetMapping("/not_found/class")
		public ResponseEntity<?> throwEntityNotFoundException_Class() throws Exception {
			throw new EntityNotFoundException(Flatmate.class, 1l);
		}
	
		@GetMapping("/not_found/class_field")
		public ResponseEntity<?> throwEntityNotFoundException_Class_Field() throws Exception {
			throw new EntityNotFoundException(Transaction.class, "cashier", 1l);
		}
	
		@GetMapping("/spring/access_denied")
		public ResponseEntity<?> throwSpringAccessDeniedException() throws Exception {
			throw new AccessDeniedException("Spring AccessDeniedException");
		}
		
	}
	
	@Getter @Setter
	public static class SimpleBean {
		@NotEmpty
		String value;
	}

}
