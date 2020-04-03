package br.com.cashhouse.server.converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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

		assertThat(conversionService.convert("created", Status.class), is(Status.CREATED));
		assertThat(conversionService.convert("sended", Status.class), is(Status.SENDED));
		assertThat(conversionService.convert("finished", Status.class), is(Status.FINISHED));
		assertThat(conversionService.convert("canceled", Status.class), is(Status.CANCELED));
		assertThat(conversionService.convert("deleted", Status.class), is(Status.DELETED));
		
	}

	@Test
	public void whenConvertStringToAction_thenSuccess() {

		assertThat(conversionService.convert("deposit", Action.class), is(Action.DEPOSIT));
		assertThat(conversionService.convert("withdraw", Action.class), is(Action.WITHDRAW));
		
	}

}