package br.com.housecash.backend.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
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

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.exception.InvalidOperationException;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.repository.TransactionRepository;
import br.com.housecash.backend.security.LoginWithAdmin;
import br.com.housecash.backend.security.service.AuthenticationFacade;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest extends ServiceHelper {

	@Autowired
	private TransactionService transactionService;

	@MockBean
	private DashboardService dashboardService;
	
	@MockBean
	private FlatmateService flatmateService;

	@MockBean
	private CashierService cashierService;
	
	@MockBean
    private TransactionRepository transactionRepository;

	@MockBean
	private AuthenticationFacade authenticationFacade;

	@TestConfiguration
	static class TransactionServiceImplTestContextConfiguration {
		@Bean
		public TransactionService transactionService() {
			return new TransactionServiceImpl();
		}
	}

	@Test
	public void whenFindById_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		Transaction transactionFound = transactionService.findById(dashboard, 1L);
		
		assert(transactionFound.getId()).equals(1l);
		assert(transactionFound.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionFound.getStatus()).equals(Status.CREATED);
		assert(transactionFound.getAction()).equals(Action.WITHDRAW);
		assert(transactionFound.getCreateBy()).equals(flatmate);
		assert(transactionFound.getAssigned()).equals(flatmate);
		assert(transactionFound.getCashier()).equals(energy);
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.findById(dashboard, 1L);
		
	}
	
	@Test
	public void whenFindAll_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
        List<Transaction> cashiers = transactionService.findAll(dashboard);
		
		assert(cashiers).contains(transaction);
		
	}
	
	@Test
	public void whenCreateDeposit_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createDeposit(dashboard, energy, BigDecimal.valueOf(2.33));

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.DEPOSIT);
		assert(transactionCreated.getCreateBy()).equals(flatmate);
		assert(transactionCreated.getAssigned()).equals(flatmate);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}
	
	@Test
	public void whenCreateDepositWithFlatmateAssign_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createDeposit(dashboard, energy, flatmateAssign, BigDecimal.valueOf(2.33));

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.DEPOSIT);
		assert(transactionCreated.getCreateBy()).equals(flatmate);
		assert(transactionCreated.getAssigned()).equals(flatmateAssign);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenCreateDepositWithFlatmateAssign_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);

        transactionService.createDeposit(dashboard, energy, flatmateAssign, BigDecimal.valueOf(2.33));
		
	}
	
	@Test
	public void whenCreateWithdraw_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createwithdraw(dashboard, energy, BigDecimal.valueOf(2.33));

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.WITHDRAW);
		assert(transactionCreated.getCreateBy()).equals(flatmate);
		assert(transactionCreated.getAssigned()).equals(flatmate);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}
	
	@Test
	public void whenCreateWithdrawWithFlatmateAssign_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createwithdraw(dashboard, energy, flatmateAssign, BigDecimal.valueOf(2.33));

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.WITHDRAW);
		assert(transactionCreated.getCreateBy()).equals(flatmate);
		assert(transactionCreated.getAssigned()).equals(flatmateAssign);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenCreateWithdrawWithFlatmateAssign_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);

        transactionService.createwithdraw(dashboard, energy, flatmateAssign, BigDecimal.valueOf(2.33));
		
	}
	
	@Test
	@LoginWithAdmin
	public void whenUpdateDashboardOwner_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.of(transaction));
		when(cashierService.findById(eq(dashboard), anyLong())).thenReturn(energy);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.update(dashboard, 1l, transaction);

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);

		assert(transactionCreated.getId()).equals(1l);
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.WITHDRAW);
		assert(transactionCreated.getCreateBy()).equals(flatmate);
		assert(transactionCreated.getAssigned()).equals(flatmate);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}
	
	@Test
	@LoginWithAdmin
	public void whenUpdate_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);
        
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.of(transaction));
		when(cashierService.findById(eq(dashboard), anyLong())).thenReturn(energy);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		when(flatmateService.findById(any(Dashboard.class), anyLong())).thenReturn(Optional.of(flatmateAssign));

        transactionService.update(dashboard, 1l, transaction);

		assertEquals(dashboard.getTransactions().size(), 1);
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);

		assert(transactionCreated.getId()).equals(1l);
		assert(transactionCreated.getValue()).equals(BigDecimal.valueOf(2.33));
		assert(transactionCreated.getStatus()).equals(Status.CREATED);
		assert(transactionCreated.getAction()).equals(Action.WITHDRAW);
		assert(transactionCreated.getCreateBy()).equals(flatmateAssign);
		assert(transactionCreated.getAssigned()).equals(flatmateAssign);
		assert(transactionCreated.getCashier()).equals(energy);
		
	}

	@LoginWithAdmin
	@Test(expected = AccessDeniedException.class)
	public void whenUpdate_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);

        transactionService.update(dashboard, 1l, new Transaction());
		
	}

	@LoginWithAdmin
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowFlatmateAssignedEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		when(flatmateService.findById(any(Dashboard.class), anyLong())).thenReturn(Optional.empty());

        transactionService.update(dashboard, 1l, new Transaction());
		
	}

	@LoginWithAdmin
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowFlatmateCreatedByEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		when(flatmateService.findById(any(Dashboard.class), anyLong())).thenReturn(Optional.empty());

        transactionService.update(dashboard, 1l, new Transaction());
		
	}

	@LoginWithAdmin
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowTransactionEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);

        transactionService.update(dashboard, 1l, new Transaction());
		
	}

	@Test
	public void whenUpdateValue_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.updateValue(dashboard, 1L, BigDecimal.valueOf(5.71));
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getValue()).equals(BigDecimal.valueOf(5.71));
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateValue_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
        
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateValue(dashboard, 1L, BigDecimal.valueOf(5.71));
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenUpdateValue_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateValue(dashboard, 1L, BigDecimal.valueOf(5.71));
		
	}

	@Test
	public void whenUpdateCashier_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.updateCashier(dashboard, 1L, energy);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getCashier()).equals(energy);
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateCashier_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
        
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateCashier(dashboard, 1L, new Cashier());
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenUpdateCashier_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
        
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateCashier(dashboard, 1L, energy);
		
	}

	@Test
	public void whenUpdateFlatmateAssigned_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.updateFlatmateAssigned(dashboard, 1L, flatmateAssign);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getAssigned()).equals(flatmateAssign);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenUpdateFlatmateAssigned_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);
		
		transactionService.updateFlatmateAssigned(dashboard, 1L, new Flatmate());
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateFlatmateAssigned_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateFlatmateAssigned(dashboard, 1L, new Flatmate());
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenUpdateFlatmateAssigned_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateFlatmateAssigned(dashboard, 1L, flatmate);
		
	}

	@Test
	public void whenActionSend_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.send(dashboard, transaction);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getStatus()).equals(Status.SENDED);
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenActionSend_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		
		transactionService.send(dashboard, transaction);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenActionSend_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		
		transactionService.send(dashboard, transaction);
		
	}

	@Test
	public void whenActionFinish_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		doNothing().when(cashierService).applyTransaction(transaction);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.finish(dashboard, transaction);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getStatus()).equals(Status.FINISHED);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenActionFinish_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);
		
		transactionService.finish(dashboard, transaction);
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenActionFinish_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		
		transactionService.finish(dashboard, transaction);
		
	}

	@Test
	public void whenActionCancel_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.cancel(dashboard, transaction);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getStatus()).equals(Status.CANCELED);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenActionCancel_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);
		
		transactionService.cancel(dashboard, transaction);
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenActionCancel_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		
		transactionService.cancel(dashboard, transaction);
		
	}
	
	@Test
	public void whenActionDelete_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.delete(dashboard, transaction);
		
		assert(transaction.getId()).equals(1l);
		assert(transaction.getStatus()).equals(Status.DELETED);
		
	}

	@Test(expected = InvalidOperationException.class)
	public void whenActionDelete_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		
		transactionService.delete(dashboard, transaction);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenActionDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);
		
		transactionService.delete(dashboard, transaction);
		
	}
	
	@Test
	public void whenFindBylatmateReferences_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		when(transactionRepository.findByDashboardAndFlatmateRef(any(Dashboard.class), any(Flatmate.class), any(Flatmate.class))).thenReturn(Collections.emptyList());
		
        transactionService.findByFlatmateReferences(dashboard, flatmate, flatmate);
		
	}
	
	@Test
	public void whenFindByCashierReferences_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		when(transactionRepository.findByDashboardAndCashier(any(Dashboard.class), any(Cashier.class))).thenReturn(Collections.emptyList());
		
        transactionService.findByCashierReferences(dashboard, energy);
		
	}
	
	@Test
	public void whenDelete_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		doNothing().when(dashboardService).removeTransaction(dashboard, transaction);
		
		transactionService.delete(dashboard, 1l);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmateAssign);
		
		transactionService.delete(dashboard, 1l);
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenDelete_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.empty());
		
		transactionService.delete(dashboard, 1l);
		
	}
	
	@Test
	public void whenFindAllPageable_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
        List<Transaction> transactions = transactionService.findAll(dashboard);
		
		assert(transactions).contains(transaction);
		
	}

}
