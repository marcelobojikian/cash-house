package br.com.housecash.backend.model.dto;

import javax.validation.constraints.NotEmpty;

import br.com.housecash.backend.handler.annotation.DTO;
import lombok.Getter;
import lombok.Setter;

@DTO
@Getter @Setter
public class Propertie {
	
    @NotEmpty
	String value;

}
