package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.assertEquals;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import br.com.housecash.backend.model.Cashier;

public class UpdateCashierTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		UpdateCashier creation = new UpdateCashier();
        creation.setName("Name");

        Cashier result = modelMapper.map(creation, Cashier.class);

        assertEquals(creation.getName(), result.getName());
        
	}

}
