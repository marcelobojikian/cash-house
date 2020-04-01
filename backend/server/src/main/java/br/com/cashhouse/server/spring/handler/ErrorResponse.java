package br.com.cashhouse.server.spring.handler;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {
	
    private LocalDateTime timestamp;

    private String path;
    private Integer status;
    private String error;
    private String message;
    
	public ErrorResponse(HttpServletRequest request, HttpStatus status, String message) {
		super();
		this.path = request.getRequestURI().substring(request.getContextPath().length());
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
    
    

}
