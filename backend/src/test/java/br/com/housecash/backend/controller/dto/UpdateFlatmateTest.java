package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.*;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import br.com.housecash.backend.model.Flatmate;

public class UpdateFlatmateTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		UpdateFlatmate creation = new UpdateFlatmate();
        creation.setNickname("nickname");
        creation.setPassword("password");

        Flatmate result = modelMapper.map(creation, Flatmate.class);

        assertEquals(creation.getNickname(), result.getNickname());
        assertEquals(creation.getPassword(), result.getPassword());
        
	}

}
