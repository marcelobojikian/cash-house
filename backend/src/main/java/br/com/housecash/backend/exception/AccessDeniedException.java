package br.com.housecash.backend.exception;

import br.com.housecash.backend.model.Flatmate;
import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Flatmate flatmate;
	private String field;

	public AccessDeniedException(Flatmate flatmate) {
        super(String.format("Flatmate %s does not have permissions for the resource", flatmate.getId()));
        this.flatmate = flatmate;
    }

	public AccessDeniedException(Flatmate flatmate, String field) {
	    super(String.format("Flatmate %s does not have permissions for field %s", flatmate.getId(), field));
        this.flatmate = flatmate;
        this.field = field;
    }

}
