package br.com.cashhouse.server.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Class<?> clazz;
	private final String field;
	private final transient Object id;
	
	public EntityNotFoundException(Class<?> clazz, Object id) {
        super(String.format("%s %s not found", clazz.getSimpleName(), id));
        this.clazz = clazz;
        this.field = null;
        this.id = id;
	}
	
	public EntityNotFoundException(Class<?> clazz, String field, Long id) {
        super(String.format("%s field %s id %s not found", clazz.getSimpleName(), field, id));
        this.clazz = clazz;
        this.field = field;
        this.id = id;
	}

}
