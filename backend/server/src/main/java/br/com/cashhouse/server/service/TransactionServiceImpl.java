package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.core.repository.TransactionRepository;
import br.com.cashhouse.server.exception.AccessDeniedException;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.InvalidOperationException;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private AuthenticationFacade authenticationFacade;

	@Autowired
	private CashierService cashierService;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private FlatmateService flatmateService;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public Transaction findById(Dashboard dashboard, Long id) {
		return transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
	}

	@Override
	public List<Transaction> findAll(Dashboard dashboard) {
		return dashboard.getTransactions();
	}

	@Override
	public Page<Transaction> findAll(Dashboard dashboard, Predicate parameters, Pageable pageable) {
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		return transactionRepository.findAll(flatmateLogged, dashboard, parameters, pageable);
	}

	@Override
	public Transaction createDeposit(Dashboard dashboard, Cashier cashier, BigDecimal value) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		Transaction transaction = new Transaction();

		transaction.setAction(Transaction.Action.DEPOSIT);
		transaction.setStatus(Status.CREATED);
		transaction.setAssigned(flatmateLogged);
		transaction.setCashier(cashier);
		transaction.setCreateBy(flatmateLogged);
		transaction.setValue(value);

		dashboard.getTransactions().add(transaction);

		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction createDeposit(Dashboard dashboard, Cashier cashier, Flatmate flatmateAssign, BigDecimal value) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged, "assigned");
		}

		Transaction transaction = new Transaction();

		transaction.setAction(Transaction.Action.DEPOSIT);
		transaction.setStatus(Status.CREATED);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(cashier);
		transaction.setCreateBy(flatmateLogged);
		transaction.setValue(value);

		dashboard.getTransactions().add(transaction);

		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction createwithdraw(Dashboard dashboard, Cashier cashier, BigDecimal value) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		Transaction transaction = new Transaction();

		transaction.setAction(Transaction.Action.WITHDRAW);
		transaction.setStatus(Status.CREATED);
		transaction.setAssigned(flatmateLogged);
		transaction.setCashier(cashier);
		transaction.setCreateBy(flatmateLogged);
		transaction.setValue(value);

		dashboard.getTransactions().add(transaction);

		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction createwithdraw(Dashboard dashboard, Cashier cashier, Flatmate flatmateAssign, BigDecimal value) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged, "assigned");
		}

		Transaction transaction = new Transaction();

		transaction.setAction(Transaction.Action.WITHDRAW);
		transaction.setStatus(Status.CREATED);
		transaction.setAssigned(flatmateAssign);
		transaction.setCashier(cashier);
		transaction.setCreateBy(flatmateLogged);
		transaction.setValue(value);

		dashboard.getTransactions().add(transaction);

		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction update(Dashboard dashboard, Long id, Transaction newTransaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		Long cashierId = newTransaction.getCashier().getId();
		Long assignedId = newTransaction.getAssigned().getId();
		Long createById = newTransaction.getCreateBy().getId();

		if (dashboard.isOwner(assignedId)) {
			entity.setAssigned(dashboard.getOwner());
		} else {
			Flatmate assigned = flatmateService.findById(dashboard, assignedId)
					.orElseThrow(() -> new EntityNotFoundException(Transaction.class, "assigned", assignedId));
			entity.setAssigned(assigned);
		}

		if (dashboard.isOwner(createById)) {
			entity.setCreateBy(dashboard.getOwner());
		} else {
			Flatmate createBy = flatmateService.findById(dashboard, createById)
					.orElseThrow(() -> new EntityNotFoundException(Transaction.class, "createBy", createById));
			entity.setCreateBy(createBy);
		}

		Cashier cashier = cashierService.findById(dashboard, cashierId);
		entity.setCashier(cashier);

		entity.setAction(newTransaction.getAction());
		entity.setStatus(newTransaction.getStatus());
		entity.setValue(newTransaction.getValue());

		return transactionRepository.save(entity);

	}

	@Override
	public void updateValue(Dashboard dashboard, Long id, BigDecimal value) {

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		}

		entity.setValue(value);

		transactionRepository.save(entity);

	}

	@Override
	public void updateCashier(Dashboard dashboard, Long id, Cashier cashier) {

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		}

		entity.setCashier(cashier);

		transactionRepository.save(entity);

	}

	@Override
	public void updateFlatmateAssigned(Dashboard dashboard, Long id, Flatmate flatmateAssigned) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		}

		entity.setAssigned(flatmateAssigned);

		transactionRepository.save(entity);

	}

	@Override
	public Transaction send(Dashboard dashboard, Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

		if (transaction.isAssignedTo(flatmateLogged)) {

			transaction.setStatus(Status.SENDED);

			return transactionRepository.save(transaction);

		} else {
			throw new AccessDeniedException(flatmateLogged);
		}

	}

	@Override
	public Transaction finish(Dashboard dashboard, Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		if (transaction.isSended()) {

			cashierService.applyTransaction(transaction);

			transaction.setStatus(Status.FINISHED);

			return transactionRepository.save(transaction);
		} else {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}
	}

	@Override
	public Transaction cancel(Dashboard dashboard, Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		if (transaction.isSended()) {

			transaction.setStatus(Status.CANCELED);

			return transactionRepository.save(transaction);

		} else {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

	}

	@Override
	public Transaction delete(Dashboard dashboard, Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

		if (transaction.isAssignedTo(flatmateLogged)) {

			transaction.setStatus(Status.DELETED);

			return transactionRepository.save(transaction);

		} else {
			throw new AccessDeniedException(flatmateLogged);
		}

	}

	@Override
	public Collection<Transaction> findByFlatmateReferences(Dashboard dashboard, Flatmate createBy, Flatmate assigned) {
		return transactionRepository.findByDashboardAndFlatmateRef(dashboard, createBy, assigned);
	}

	@Override
	public Collection<Transaction> findByCashierReferences(Dashboard dashboard, Cashier cashier) {
		return transactionRepository.findByDashboardAndCashier(dashboard, cashier);
	}

	@Override
	public void delete(Dashboard dashboard, Long id) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		dashboardService.removeTransaction(dashboard, entity);

	}

}
