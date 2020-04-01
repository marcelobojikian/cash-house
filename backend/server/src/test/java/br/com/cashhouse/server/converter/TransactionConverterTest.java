package br.com.cashhouse.server.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.server.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TransactionConverterTest {

	@Autowired
	ConversionService conversionService;

	@Test
	public void whenConvertStringToStatus_thenSuccess() {

		assertEquals(conversionService.convert("created", Status.class),Status.CREATED);
		assertEquals(conversionService.convert("sended", Status.class),Status.SENDED);
		assertEquals(conversionService.convert("finished", Status.class),Status.FINISHED);
		assertEquals(conversionService.convert("canceled", Status.class),Status.CANCELED);
		assertEquals(conversionService.convert("deleted", Status.class),Status.DELETED);
		
	}

	@Test
	public void whenConvertStringToAction_thenSuccess() {

		assertEquals(conversionService.convert("deposit", Action.class),Action.DEPOSIT);
		assertEquals(conversionService.convert("withdraw", Action.class),Action.WITHDRAW);
		
	}

}