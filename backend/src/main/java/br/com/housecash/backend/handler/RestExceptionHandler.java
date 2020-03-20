package br.com.housecash.backend.handler;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.exception.InvalidFieldException;
import br.com.housecash.backend.exception.InvalidOperationException;
import br.com.housecash.backend.exception.NoContentException;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.service.LocaleService;
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

	@ExceptionHandler(InvalidFieldException.class)
	public ResponseEntity<ErrorResponse> invalidFieldException(InvalidFieldException ex) {
		String message = localeService.getMessage("field.invalid", ex.getValue());
		return buildResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoContentException.class)
	public ResponseEntity<ErrorResponse> noContentException(NoContentException ex) {
		String message = localeService.getMessage("body.no.content");
		return buildResponse(message, HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(InvalidOperationException.class)
	public ResponseEntity<ErrorResponse> invalidOperationException(InvalidOperationException ex) {

		Transaction transaction = ex.getTransaction();
		Status status = ex.getStatus();
		Action action = ex.getAction();
		String message = null;

		if (status != null) {
			message = localeService.getMessage("Transaction.action.invalid.operation", transaction.getId(), status);
		} else if (action != null) {
			message = localeService.getMessage("Transaction.status.invalid.operation", transaction.getId(), action);
		} else {
			message = localeService.getMessage("Transaction.invalid.operation", transaction.getId());
		}

		return buildResponse(message, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException ex) {

		String flatmateName = ex.getFlatmate().getNickname();
		String fieldName = ex.getField();
		String message = null;

		if (fieldName == null) {
			message = localeService.getMessage("flatmate.access.denied", flatmateName);
		} else {
			message = localeService.getMessage("flatmate.access.field.denied", flatmateName, fieldName);
		}

		return buildResponse(message, HttpStatus.FORBIDDEN);
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

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<ErrorResponse> entityNotFoundException(EmptyResultDataAccessException ex) {
		return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
		// @formatter:off
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                		.timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(request.getServletPath())
                        .build()
                );
		// @formatter:on
	}

}
