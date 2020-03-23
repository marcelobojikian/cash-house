package br.com.housecash.backend.controller.dto;

import javax.validation.constraints.NotEmpty;

import br.com.housecash.backend.handler.annotation.DTO;
import lombok.Getter;
import lombok.Setter;

@DTO
@Getter @Setter
public class UpdateCashier {

    @NotEmpty
	String name;

}
