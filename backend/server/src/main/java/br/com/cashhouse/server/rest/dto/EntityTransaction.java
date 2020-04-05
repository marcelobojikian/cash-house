package br.com.cashhouse.server.rest.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntityTransaction {

    @NotNull
	private Long createBy;

    @NotNull
    private Long assigned;
	
    @NotNull
    private Long cashier;

    @NotNull
	private Status status;

    @NotNull
    private Action action;

    @NotNull
	@NumberFormat(style=Style.CURRENCY)
	@Digits(integer = 10, fraction = 2)
    private BigDecimal value;
    
    public Transaction toEntity(){
    	
    	Flatmate createByEntity = new Flatmate();
    	createByEntity.setId(createBy);
    	
    	Flatmate assignedEntity = new Flatmate();
    	assignedEntity.setId(assigned);
    	
    	Cashier cashierEntity = new Cashier();
    	cashierEntity.setId(cashier);
    	
    	Transaction entity =  new Transaction();
    	entity.setCreateBy(createByEntity);
    	entity.setAssigned(assignedEntity);
    	entity.setCashier(cashierEntity);
    	entity.setStatus(status);
    	entity.setAction(action);
    	entity.setValue(value);
    	
    	return entity;
    }

}
