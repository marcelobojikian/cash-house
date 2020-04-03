package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.createCashier;
import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static br.com.cashhouse.server.util.EntityFactory.createTransaction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.repository.CashierRepository;
import br.com.cashhouse.server.exception.AccessDeniedException;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import br.com.cashhouse.server.util.security.LoginWithAdmin;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CashierServiceTest {

	@Autowired
	private CashierService cashierService;

	@MockBean
	private CashierRepository cashierRepository;

	@MockBean
	private DashboardService dashboardService;

	@MockBean
	private TransactionService transactionService;

	@MockBean
	private AuthenticationFacade authenticationFacade;
	
	@MockBean
	private HeaderRequest headerRequest;

	@TestConfiguration
	static class CashierServiceImplTestContextConfiguration {
		@Bean
		public CashierService cashierService() {
			return new CashierServiceImpl();
		}
	}

	@Test
	public void whenFindById_thenReturnCashierObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(energy));
		
		Cashier cashierExpect = cashierService.findById(1L);
		
		assertThat(cashierExpect.getId(), is(1l));
		assertThat(cashierExpect.getName(), is("Energy"));
		assertThat(cashierExpect.getStarted(), is(BigDecimal.valueOf(12.3)));
		assertThat(cashierExpect.getBalance(), is(BigDecimal.valueOf(12.3)));
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.empty());
		
		cashierService.findById(1L);
		
	}
	
	@Test
	public void whenFindAll_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		Cashier garbage = createCashier(dashboard, 2l, "Garbage", 4.2, 56.6);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
        List<Cashier> cashiers = cashierService.findAll();

        assertThat(cashiers, contains(energy, garbage));
		
	}
	
	@Test
	public void whenCreate_thenReturnCashierObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		Cashier cashier = cashierService.create("Energy", BigDecimal.valueOf(12.3), BigDecimal.valueOf(12.3));
		
		assertThat(cashier.getId(), is(1l));
		assertThat(cashier.getName(), is("Energy"));
		assertThat(cashier.getStarted(), is(BigDecimal.valueOf(12.3)));
		assertThat(cashier.getBalance(), is(BigDecimal.valueOf(12.3)));
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenCreate_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate notDashboarOwner = createFlatmate(2l, "not owner", "not owner");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(notDashboarOwner);
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.create("Energy", BigDecimal.valueOf(12.3), BigDecimal.valueOf(12.3));
		
	}
	
	@Test
	public void whenUpdate_thenReturnCashierObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		Cashier energyNew = createCashier(dashboard, 1l, "Energy UP", 3.1, 3.2);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(energy));
		when(cashierRepository.save(energy)).thenReturn(energy);
		
		Cashier cashier = cashierService.update(1l, energyNew);
		
		assertThat(cashier.getId(), is(1l));
		assertThat(cashier.getName(), is("Energy UP"));
		assertThat(cashier.getStarted(), is(BigDecimal.valueOf(3.1)));
		assertThat(cashier.getBalance(), is(BigDecimal.valueOf(3.2)));
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 99l)).thenReturn(Optional.empty());
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.update(99l, energy);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenUpdate_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate notDashboarOwner = createFlatmate(2l, "not owner", "not owner");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(notDashboarOwner);
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.update(1l, energy);
		
	}
	
	@Test
	public void whenUpdateName_thenReturnCashierObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(energy));
		when(cashierRepository.save(energy)).thenReturn(energy);
		
		Cashier cashier = cashierService.update(1l, "Energy UP");
		
		assertThat(cashier.getId(), is(1l));
		assertThat(cashier.getName(), is("Energy UP"));
		assertThat(cashier.getStarted(), is(BigDecimal.valueOf(12.3)));
		assertThat(cashier.getBalance(), is(BigDecimal.valueOf(12.3)));
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateName_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 99l)).thenReturn(Optional.empty());
		
		cashierService.update(99l, "Energy UP");
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenUpdateName_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate notDashboarOwner = createFlatmate(2l, "not owner", "not owner");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(notDashboarOwner);
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.update(1l, "Energy UP");
		
	}
	
	@Test
	@LoginWithAdmin
	public void whenDelete_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(energy));
		when(transactionService.findByCashierReferences(eq(dashboard), any(Cashier.class))).thenReturn(new ArrayList<Transaction>());
		doNothing().when(dashboardService).removeCashier(eq(dashboard), any(Cashier.class));
		doNothing().when(dashboardService).removeTransactions(eq(dashboard), anyCollection());
		
		cashierService.delete(1l);
		
		verify(dashboardService, times(1)).removeCashier(dashboard, energy);
		verify(dashboardService, times(1)).removeTransactions(eq(dashboard), anyCollection());
		
	}

	@Test(expected = EntityNotFoundException.class)
	@LoginWithAdmin
	public void whenDelete_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(cashierRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.empty());
		
		cashierService.delete(1l);
		
	}

	@Test(expected = AccessDeniedException.class)
	@LoginWithAdmin
	public void whenDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate notDashboarOwner = createFlatmate(2l, "not owner", "not owner");

		when(headerRequest.getDashboard()).thenReturn(dashboard);
		when(authenticationFacade.getFlatmateLogged()).thenReturn(notDashboarOwner);
		
		cashierService.delete(1l);
		
	}
	
	@Test
	public void whenApplyTransactionDeposit_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, null, Action.DEPOSIT);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(cashierRepository.findById(1l)).thenReturn(Optional.of(energy));
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.applyTransaction(transaction);
		
		verify(cashierRepository, times(1)).save(any(Cashier.class));
		
	}
	
	@Test
	public void whenApplyTransactionWithdraw_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, null, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(cashierRepository.findById(1l)).thenReturn(Optional.of(energy));
		when(cashierRepository.save(any(Cashier.class))).thenReturn(energy);
		
		cashierService.applyTransaction(transaction);
		
		verify(cashierRepository, times(1)).save(any(Cashier.class));
		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void whenApplyTransaction_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, null, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(cashierRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		cashierService.applyTransaction(transaction);
		
	}

}
