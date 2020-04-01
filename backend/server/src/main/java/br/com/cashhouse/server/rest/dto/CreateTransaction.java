package br.com.cashhouse.server.rest.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTransaction {
	
    @NotNull
    private Long cashier;
	
    private Long assigned;

    @NotNull
	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
    private BigDecimal value;

}
