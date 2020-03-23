package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

public class UpdateTransactionTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		UpdateTransaction creation = new UpdateTransaction();
        creation.setAssigned(1l);
        creation.setCashier(1l);
        creation.setValue(BigDecimal.valueOf(10));

        UpdateTransaction result = modelMapper.map(creation, UpdateTransaction.class);

        assertEquals(creation.getAssigned(), result.getAssigned());
        assertEquals(creation.getCashier(), result.getCashier());
        assertEquals(creation.getValue(), result.getValue());
        
	}

}
