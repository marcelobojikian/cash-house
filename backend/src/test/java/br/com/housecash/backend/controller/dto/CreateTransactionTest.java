package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

public class CreateTransactionTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		CreateTransaction creation = new CreateTransaction();
        creation.setAssigned(1l);
        creation.setCashier(1l);
        creation.setValue(BigDecimal.valueOf(10));

        CreateTransaction result = modelMapper.map(creation, CreateTransaction.class);

        assertEquals(creation.getAssigned(), result.getAssigned());
        assertEquals(creation.getCashier(), result.getCashier());
        assertEquals(creation.getValue(), result.getValue());
        
	}

}
