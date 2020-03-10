package br.com.housecash.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class NoContentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoContentException() {
		super("Body of request is invalid.");
	}

}
