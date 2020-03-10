package br.com.housecash.backend.exception;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class InvalidFieldException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String value;

	public InvalidFieldException(Set<String> keys) {
		super("Field " + keys.toString() + " invalid.");
		this.value = keys.toString();
	}

	public InvalidFieldException(String field) {
		super("Field " + field + " invalid.");
		this.value = field;
	}

}
