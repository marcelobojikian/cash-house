package br.com.housecash.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Class<?> clazz;
	private String field;
	private Object id;
	
	public EntityNotFoundException(Class<?> clazz, Object id) {
        super(String.format("%s %s not found", clazz.getSimpleName(), id));
        this.clazz = clazz;
        this.id = id;
	}
	
	public EntityNotFoundException(Class<?> clazz, String field, Long id) {
        super(String.format("%s field %s id %s not found", clazz.getSimpleName(), field, id));
        this.clazz = clazz;
        this.field = field;
        this.id = id;
	}

}
