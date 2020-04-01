package br.com.cashhouse.server.rest.dto;

import static br.com.cashhouse.server.util.EntityFactory.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;

public class ContentTest {

	public void checkContent(Content<?> content) {
		
		LocalDate date = LocalDate.now();
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();

        assertEquals(content.getCreatedDate(), date);
        assertEquals(content.getDay(), day);
        assertEquals(content.getMonth(), month);
        assertEquals(content.getYear(), year);
        
        assertThat(content.getData(), instanceOf(Collection.class));
        assertThat(content.getData(), not(IsEmptyCollection.empty()));
        
	}

	@Test
	public void checkTransactionContent() {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		Content<Transaction> creation = new Content<Transaction>(LocalDate.now(), Arrays.asList(transaction));
		
		checkContent(creation);
		
        assertThat(creation.getData(), hasItems(transaction));
        
	}

	@Test
	public void checkDashboardContent() {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Content<Dashboard> creation = new Content<Dashboard>(LocalDate.now(), Arrays.asList(dashboard));
		
		checkContent(creation);
        
        assertThat(creation.getData(), hasItems(dashboard));
        
	}

	@Test
	public void checkFlatmateContent() {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		
		Content<Flatmate> creation = new Content<Flatmate>(LocalDate.now(), Arrays.asList(flatmate));
		
		checkContent(creation);

        assertThat(creation.getData(), hasItems(flatmate));
        
	}

	@Test
	public void checkCashierContent() {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier cashier = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Content<Cashier> creation = new Content<Cashier>(LocalDate.now(), Arrays.asList(cashier));
		
		checkContent(creation);

        assertThat(creation.getData(), hasItems(cashier));
        
	}

}
