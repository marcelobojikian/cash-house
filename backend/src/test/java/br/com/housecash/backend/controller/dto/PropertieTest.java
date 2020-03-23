package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.assertEquals;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

public class PropertieTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		Propertie creation = new Propertie();
        creation.setValue("value");

        Propertie result = modelMapper.map(creation, Propertie.class);

        assertEquals(creation.getValue(), result.getValue());
        
	}

}
