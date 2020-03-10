package br.com.housecash.backend.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import br.com.housecash.backend.handler.annotation.DTO;
import lombok.Data;

@DTO
@Data
public class CreateCashier {

    @NotEmpty
	String name;

	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
	BigDecimal started;
    
    @NotNull
	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
	BigDecimal balance;
    
}