package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import br.com.housecash.backend.model.Cashier;

public class CreateCashierTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		CreateCashier creation = new CreateCashier();
        creation.setName("Name");
        creation.setBalance(BigDecimal.valueOf(50d));
        creation.setStarted(BigDecimal.valueOf(10d));

        Cashier result = modelMapper.map(creation, Cashier.class);

        assertEquals(creation.getName(), result.getName());
        assertEquals(creation.getBalance(), result.getBalance());
        assertEquals(creation.getStarted(), result.getStarted());
        
	}

}
