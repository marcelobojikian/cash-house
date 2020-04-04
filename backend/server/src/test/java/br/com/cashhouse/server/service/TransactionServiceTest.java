package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.createCashier;
import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static br.com.cashhouse.server.util.EntityFactory.createTransaction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.core.repository.TransactionRepository;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.InvalidOperationException;
import br.com.cashhouse.server.util.annotation.LoginWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest extends ServiceAuthHelper {

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

	@TestConfiguration
	static class TransactionServiceImplTestContextConfiguration {
		@Bean
		public TransactionService transactionService() {
			return new TransactionServiceImpl();
		}
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindById_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		Transaction transactionFound = transactionService.findById(1L);
		
		assertThat(transactionFound.getId(), is(1l));
		assertThat(transactionFound.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionFound.getStatus(), is(Status.CREATED));
		assertThat(transactionFound.getAction(), is(Action.WITHDRAW));
		assertThat(transactionFound.getCreateBy(), is(flatmate));
		assertThat(transactionFound.getAssigned(), is(flatmate));
		assertThat(transactionFound.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowEntityNotFoundException() throws Exception {

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.findById(1L);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindAll_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
        List<Transaction> transactions = transactionService.findAll();

		assertThat(transactions, hasSize(1));
        assertThat(transactions, contains(transaction));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenCreateDeposit_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createDeposit(energy, BigDecimal.valueOf(2.33));

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.DEPOSIT));
		assertThat(transactionCreated.getCreateBy(), is(flatmate));
		assertThat(transactionCreated.getAssigned(), is(flatmate));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenCreateDepositWithFlatmateAssign_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createDeposit(energy, flatmateAssign, BigDecimal.valueOf(2.33));

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.DEPOSIT));
		assertThat(transactionCreated.getCreateBy(), is(flatmate));
		assertThat(transactionCreated.getAssigned(), is(flatmateAssign));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(id = 3)
	@Test(expected = AccessDeniedException.class)
	public void whenCreateDepositWithFlatmateAssign_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

        transactionService.createDeposit(energy, flatmateAssign, BigDecimal.valueOf(2.33));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenCreateWithdraw_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createwithdraw(energy, BigDecimal.valueOf(2.33));

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.WITHDRAW));
		assertThat(transactionCreated.getCreateBy(), is(flatmate));
		assertThat(transactionCreated.getAssigned(), is(flatmate));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenCreateWithdrawWithFlatmateAssign_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		
        transactionService.createwithdraw(energy, flatmateAssign, BigDecimal.valueOf(2.33));

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);
		
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.WITHDRAW));
		assertThat(transactionCreated.getCreateBy(), is(flatmate));
		assertThat(transactionCreated.getAssigned(), is(flatmateAssign));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(id = 3)
	@Test(expected = AccessDeniedException.class)
	public void whenCreateWithdrawWithFlatmateAssign_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

        transactionService.createwithdraw(energy, flatmateAssign, BigDecimal.valueOf(2.33));
		
	}

	@LoginWith(roles = "ADMIN", id = 1)
	@Test
	public void whenUpdateDashboardOwner_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.of(transaction));
		when(cashierService.findById(anyLong())).thenReturn(energy);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.update(1l, transaction);

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);

		assertThat(transactionCreated.getId(), is(1l));
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.WITHDRAW));
		assertThat(transactionCreated.getCreateBy(), is(flatmate));
		assertThat(transactionCreated.getAssigned(), is(flatmate));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(roles = "ADMIN", id = 1)
	@Test
	public void whenUpdate_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.of(transaction));
		when(cashierService.findById(anyLong())).thenReturn(energy);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		when(flatmateService.findById(anyLong())).thenReturn(Optional.of(flatmateAssign));

        transactionService.update(1l, transaction);

		assertThat(dashboard.getTransactions(), hasSize(1));
		
		Transaction transactionCreated = dashboard.getTransactions().get(0);

		assertThat(transactionCreated.getId(), is(1l));
		assertThat(transactionCreated.getValue(), is(BigDecimal.valueOf(2.33)));
		assertThat(transactionCreated.getStatus(), is(Status.CREATED));
		assertThat(transactionCreated.getAction(), is(Action.WITHDRAW));
		assertThat(transactionCreated.getCreateBy(), is(flatmateAssign));
		assertThat(transactionCreated.getAssigned(), is(flatmateAssign));
		assertThat(transactionCreated.getCashier(), is(energy));
		
	}

	@LoginWith(roles = "USER", id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdate_thenThrowAccessDeniedException() throws Exception {
		
        transactionService.update(1l, new Transaction());
		
	}

	@LoginWith(roles = "ADMIN", id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowFlatmateAssignedEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		when(flatmateService.findById(anyLong())).thenReturn(Optional.empty());

        transactionService.update(1l, new Transaction());
		
	}

	@LoginWith(roles = "ADMIN", id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowFlatmateCreatedByEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		when(flatmateService.findById(anyLong())).thenReturn(Optional.empty());

        transactionService.update(1l, new Transaction());
		
	}

	@LoginWith(roles = "ADMIN", id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowTransactionEntityNotFoundException() throws Exception {

		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());

        transactionService.update(1l, new Transaction());
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateValue_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateValue(1L, BigDecimal.valueOf(5.71));

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getValue(), is(BigDecimal.valueOf(5.71)));
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateValue_thenThrowEntityNotFoundException() throws Exception {
		
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateValue(1L, BigDecimal.valueOf(5.71));
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenUpdateValue_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateValue(1L, BigDecimal.valueOf(5.71));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateValue_isCreator_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(guest);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateValue(1L, BigDecimal.valueOf(5.71));

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getValue(), is(BigDecimal.valueOf(5.71)));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateValue_isAssigned_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(guest);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateValue(1L, BigDecimal.valueOf(5.71));

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getValue(), is(BigDecimal.valueOf(5.71)));
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateValue_isNotCreatorAndAssigned_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(guest);
		transaction.setAssigned(guest);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateValue(1L, BigDecimal.valueOf(5.71));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateCashier_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateCashier(1L, energy);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateCashier_thenThrowEntityNotFoundException() throws Exception {
		
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateCashier(1L, new Cashier());
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenUpdateCashier_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateCashier(1L, energy);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateCashier_isCreator_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(guest);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateCashier(1L, energy);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateCashier_isAssigned_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(guest);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateCashier(1L, energy);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getCashier(), is(energy));
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateCashier_isNotCreatorAndAssigned_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(2l, "none", "none");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(guest);
		transaction.setAssigned(guest);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.updateCashier(1L, energy);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateFlatmateAssigned_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateFlatmateAssigned(1L, flatmateAssign);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getAssigned(), is(flatmateAssign));
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateFlatmateAssigned_thenThrowAccessDeniedException() throws Exception {

		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		transactionService.updateFlatmateAssigned(1L, new Flatmate());
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateFlatmateAssigned_thenThrowEntityNotFoundException() throws Exception {
		
		when(transactionRepository.findByDashboardAndId(any(Dashboard.class), eq(1l))).thenReturn(Optional.empty());
		
		transactionService.updateFlatmateAssigned(1L, new Flatmate());
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenUpdateFlatmateAssigned_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		
		transactionService.updateFlatmateAssigned(1L, flatmate);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateFlatmateAssigned_isCreator_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateFlatmateAssigned(1L, flatmateAssign);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getAssigned(), is(flatmateAssign));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenUpdateFlatmateAssigned_isAssigned_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		Transaction expected = transactionService.updateFlatmateAssigned(1L, flatmateAssign);

		assertThat(expected.getId(), is(1l));
		assertThat(expected.getAssigned(), is(flatmateAssign));
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateFlatmateAssigned_isNotCreatorAndAssigned_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmateAssign);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.updateFlatmateAssigned(1L, flatmateAssign);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenDelete_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(transaction));
		doNothing().when(dashboardService).removeTransaction(dashboard, transaction);
		
		transactionService.delete(1l);
		
		verify(dashboardService, times(1)).removeTransaction(dashboard, transaction);
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		transactionService.delete(1l);
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenDelete_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		when(transactionRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.empty());
		
		transactionService.delete(1l);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindBylatmateReferences_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();

		when(transactionRepository.findByDashboardAndFlatmateRef(any(Dashboard.class), any(Flatmate.class), any(Flatmate.class))).thenReturn(Collections.emptyList());
		
        Collection<Transaction> transactions = transactionService.findByFlatmateReferences(flatmate, flatmate);

		assertThat(transactions, empty());
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindByCashierReferences_thenReturnObjectArray() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);

		when(transactionRepository.findByDashboardAndCashier(any(Dashboard.class), any(Cashier.class))).thenReturn(Collections.emptyList());
		
		Collection<Transaction> transactions = transactionService.findByCashierReferences(energy);

		assertThat(transactions, empty());
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenActionSend_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.send(transaction);

		assertThat(transaction.getId(), is(1l));
		assertThat(transaction.getStatus(), is(Status.SENDED));
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenActionSend_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.send(transaction);
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenActionSend_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);
		
		transactionService.send(transaction);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenActionFinish_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		doNothing().when(cashierService).applyTransaction(transaction);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.finish(transaction);

		assertThat(transaction.getId(), is(1l));
		assertThat(transaction.getStatus(), is(Status.FINISHED));
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenActionFinish_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.finish(transaction);
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenActionFinish_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.finish(transaction);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenActionCancel_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.cancel(transaction);

		assertThat(transaction.getId(), is(1l));
		assertThat(transaction.getStatus(), is(Status.CANCELED));
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenActionCancel_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		userDashboard(flatmate);
		
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.SENDED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.cancel(transaction);
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenActionCancel_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.cancel(transaction);
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenActionDelete_thenReturnTransactionObject() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
		
		transactionService.delete(transaction);

		assertThat(transaction.getId(), is(1l));
		assertThat(transaction.getStatus(), is(Status.DELETED));
		
	}

	@LoginWith(id = 1)
	@Test(expected = InvalidOperationException.class)
	public void whenActionDelete_thenThrowInvalidOperationException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.FINISHED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmate);
		transaction.setCashier(energy);
		
		transactionService.delete(transaction);
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenActionDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate flatmateAssign = createFlatmate(2l, "Assign", "Assign");

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		
		Transaction transaction = createTransaction(dashboard, 1l, 2.33, Status.CREATED, Action.WITHDRAW);
		transaction.setCreateBy(flatmate);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(energy);
		
		transactionService.delete(transaction);
		
	}

}
