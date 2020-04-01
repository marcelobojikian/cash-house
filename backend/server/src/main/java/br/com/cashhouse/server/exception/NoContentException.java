package br.com.cashhouse.server.exception;

public class NoContentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoContentException() {
		super("Body of request is invalid.");
	}

}
