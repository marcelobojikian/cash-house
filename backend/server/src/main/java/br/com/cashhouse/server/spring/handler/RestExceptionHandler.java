package br.com.cashhouse.server.spring.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.InvalidOperationException;
import br.com.cashhouse.server.service.LocaleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
//https://auth0.com/blog/exception-handling-and-i18n-on-spring-boots-apis/#Next-Steps--Integration-Testing-on-Spring-Boot-APIs
public class RestExceptionHandler {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LocaleService localeService;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> methodArgumentNotValidException(HttpServletRequest request,
			MethodArgumentNotValidException e) {

		log.debug(e.getMessage(), e);

		BindingResult result = e.getBindingResult();
		FieldError fieldError = result.getFieldErrors().iterator().next();

		String message = localeService.getMessage("dto.field.invalid", fieldError.getField(), fieldError.getDefaultMessage());
		
		return buildResponse(message, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(InvalidOperationException.class)
	public ResponseEntity<ErrorResponse> invalidOperationException(InvalidOperationException ex) {

		Transaction transaction = ex.getTransaction();
		Status status = ex.getStatus();
		String message = localeService.getMessage("Transaction.status.invalid.operation", transaction.getId(), status);

		return buildResponse(message, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> entityNotFoundException(EntityNotFoundException ex) {

		String className = ex.getClazz().getSimpleName();
		String fieldName = ex.getField();
		String codeMessage = null;

		if (fieldName == null) {
			codeMessage = String.format("%s.not.found", className);
		} else {
			codeMessage = String.format("%s.%s.not.found", className, fieldName);
		}

		String message = localeService.getMessage(codeMessage, ex.getId());

		return buildResponse(message, HttpStatus.NOT_FOUND);

	}
	
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> entityAccessDeniedHandler(org.springframework.security.access.AccessDeniedException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
		ErrorResponse error = new ErrorResponse(request, status, message);
		return new ResponseEntity<>(error, status);
	}

}
