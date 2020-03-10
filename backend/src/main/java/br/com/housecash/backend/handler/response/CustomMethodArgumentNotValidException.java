package br.com.housecash.backend.handler.response;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import br.com.housecash.backend.handler.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
//https://github.com/adrianaden/spring-boot-starter
public class CustomMethodArgumentNotValidException {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
    	
        log.debug(e.getMessage(), e);

        BindingResult result = e.getBindingResult();
        FieldError fieldError = result.getFieldErrors().iterator().next();

		// @formatter:off
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                		.timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Field '" + fieldError.getField() + "' " + fieldError.getDefaultMessage())
                        .path(request.getServletPath())
                        .build()
                );
		// @formatter:on
        
    }

}
