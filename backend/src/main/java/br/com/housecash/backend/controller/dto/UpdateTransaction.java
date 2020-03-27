package br.com.housecash.backend.controller.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateTransaction {

	private Long assigned;
	
    private Long cashier;

	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
    private BigDecimal value;
	
	public boolean haveFlatmateAssigned() {
		return assigned != null;
	}
	
	public boolean changeCashier() {
		return cashier != null;
	}
	
	public boolean changeValue() {
		return value != null;
	}
	
	public boolean haveChanges() {
		return assigned != null || cashier != null || value != null;
	}

}
