package br.com.cashhouse.server.rest.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCashier {

    @NotEmpty
	String name;

}
