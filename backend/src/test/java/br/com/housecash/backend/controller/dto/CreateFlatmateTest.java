package br.com.housecash.backend.controller.dto;

import static org.junit.Assert.assertEquals;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import br.com.housecash.backend.model.Flatmate;

public class CreateFlatmateTest {

    private static final DozerBeanMapper modelMapper = new DozerBeanMapper();

	@Test
	public void checkMapping() {
		
		CreateFlatmate creation = new CreateFlatmate();
        creation.setEmail("name@mail.com");
        creation.setNickname("nickname");
        creation.setPassword("password");

        Flatmate result = modelMapper.map(creation, Flatmate.class);

        assertEquals(creation.getEmail(), result.getEmail());
        assertEquals(creation.getNickname(), result.getNickname());
        assertEquals(creation.getPassword(), result.getPassword());
        
	}

}
