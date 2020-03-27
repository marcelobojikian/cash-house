package br.com.housecash.backend.controller.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCashier {

    @NotEmpty
	String name;

}
