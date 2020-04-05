package br.com.cashhouse.server.rest.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Flatmate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntityCashier {

    @NotEmpty
	String name;

    @NotNull
	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
	BigDecimal started;
    
    @NotNull
	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
	BigDecimal balance;
    
    @NotNull
	private Long owner;
    
    public Cashier toEntity(){
    	
    	Flatmate ownerEntity = new Flatmate();
    	ownerEntity.setId(owner);
    	
    	Cashier entity = new Cashier(name, started, balance);
    	entity.setOwner(ownerEntity);
    	
    	return entity;
    	
    }
    
}
