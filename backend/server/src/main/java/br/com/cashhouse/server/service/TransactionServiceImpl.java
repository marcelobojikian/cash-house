package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Status;
import br.com.cashhouse.core.repository.TransactionRepository;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.InvalidOperationException;
import br.com.cashhouse.server.rest.dto.UpdateTransaction;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	private static final String LOCALE_KEY_ACCESS_DENIED = "flatmate.access.denied"; 

	@Autowired
	private HeaderRequest headerRequest;

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

	@Autowired
	private LocaleService localeService;

	@Override
	public Transaction findById(Long id) {
		Dashboard dashboard = headerRequest.getDashboard();
		return transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
	}

	@Override
	public List<Transaction> findAll() {
		Dashboard dashboard = headerRequest.getDashboard();
		return dashboard.getTransactions();
	}

	@Override
	public Page<Transaction> findAll(Predicate parameters, Pageable pageable) {
		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		return transactionRepository.findAll(flatmateLogged, dashboard, parameters, pageable);
	}

	@Override
	public Transaction createDeposit(Cashier cashier, BigDecimal value) {

		Dashboard dashboard = headerRequest.getDashboard();
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
	public Transaction createDeposit(Cashier cashier, Flatmate flatmateAssign, BigDecimal value) {

		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

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
	public Transaction createwithdraw(Cashier cashier, BigDecimal value) {

		Dashboard dashboard = headerRequest.getDashboard();
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
	public Transaction createwithdraw(Cashier cashier, Flatmate flatmateAssign, BigDecimal value) {

		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

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
	public Transaction update(Long id, Transaction newTransaction) {

		Dashboard dashboard = headerRequest.getDashboard();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		Long cashierId = newTransaction.getCashier().getId();
		Long assignedId = newTransaction.getAssigned().getId();
		Long createById = newTransaction.getCreateBy().getId();

		if (dashboard.isOwner(assignedId)) {
			entity.setAssigned(dashboard.getOwner());
		} else {
			Flatmate assigned = flatmateService.findById(assignedId)
					.orElseThrow(() -> new EntityNotFoundException(Transaction.class, "assigned", assignedId));
			entity.setAssigned(assigned);
		}

		if (dashboard.isOwner(createById)) {
			entity.setCreateBy(dashboard.getOwner());
		} else {
			Flatmate createBy = flatmateService.findById(createById)
					.orElseThrow(() -> new EntityNotFoundException(Transaction.class, "createBy", createById));
			entity.setCreateBy(createBy);
		}

		Cashier cashier = cashierService.findById(cashierId);
		entity.setCashier(cashier);

		entity.setAction(newTransaction.getAction());
		entity.setStatus(newTransaction.getStatus());
		entity.setValue(newTransaction.getValue());

		return transactionRepository.save(entity);

	}

	@Override
	public Transaction update(Long id, UpdateTransaction content) {
		
		Dashboard dashboard = headerRequest.getDashboard();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
		
		if(!content.haveChanges()) {
			return entity;
		}
		
		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		}
		
		if(content.haveValue()) {
			entity.setValue(content.getValue());
		}
		
		if(content.haveCashier()) {
			Cashier cashier = cashierService.findById(content.getCashier());
			entity.setCashier(cashier);
		}
		
		if(content.haveAssigned()) {
			Long idAssigned = content.getAssigned();
			Flatmate flatmateAssigned = flatmateService.findById(idAssigned)
					.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, idAssigned));
			
			Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

			if (!dashboard.isOwner(flatmateLogged)) {
				throw new AccessDeniedException(localeService.getMessage("flatmate.access.field.denied", flatmateLogged.getNickname(), "assigned"));
			}

			if (!entity.isCreateBy(flatmateLogged) && !entity.isAssignedTo(flatmateLogged)) {
				throw new AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname()));
			}
			
			entity.setAssigned(flatmateAssigned);
		}

		return transactionRepository.save(entity);
	}

	@Override
	public Transaction updateValue(Long id, BigDecimal value) {
		
		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		} else if (!entity.isCreateBy(flatmateLogged) && !entity.isAssignedTo(flatmateLogged)) {
			throw new org.springframework.security.access.AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname() ));
		}

		entity.setValue(value);

		return transactionRepository.save(entity);

	}

	@Override
	public Transaction updateCashier(Long id, Cashier cashier) {
		
		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		} else if (!entity.isCreateBy(flatmateLogged) && !entity.isAssignedTo(flatmateLogged)) {
			throw new org.springframework.security.access.AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname() ));
		}

		entity.setCashier(cashier);

		return transactionRepository.save(entity);

	}

	@Override
	public Transaction updateFlatmateAssigned(Long id, Flatmate flatmateAssigned) {
		
		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		if (!entity.isAvailableToChange()) {
			throw new InvalidOperationException(entity, entity.getStatus());
		} else if (!entity.isCreateBy(flatmateLogged) && !entity.isAssignedTo(flatmateLogged)) {
			throw new org.springframework.security.access.AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname() ));
		}

		entity.setAssigned(flatmateAssigned);

		return transactionRepository.save(entity);

	}

	@Override
	public void delete(Long id) {

		Dashboard dashboard = headerRequest.getDashboard();

		Transaction entity = transactionRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));

		dashboardService.removeTransaction(dashboard, entity);

	}

	@Override
	public Collection<Transaction> findByFlatmateReferences(Flatmate createBy, Flatmate assigned) {
		Dashboard dashboard = headerRequest.getDashboard();
		return transactionRepository.findByDashboardAndFlatmateRef(dashboard, createBy, assigned);
	}

	@Override
	public Collection<Transaction> findByCashierReferences(Cashier cashier) {
		Dashboard dashboard = headerRequest.getDashboard();
		return transactionRepository.findByDashboardAndCashier(dashboard, cashier);
	}

	@Override
	public Transaction send(Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

		if (transaction.isAssignedTo(flatmateLogged)) {

			transaction.setStatus(Status.SENDED);

			return transactionRepository.save(transaction);

		} else {
			throw new AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname()));
		}

	}

	@Override
	public Transaction finish(Transaction transaction) {

		if (transaction.isSended()) {

			cashierService.applyTransaction(transaction);

			transaction.setStatus(Status.FINISHED);

			return transactionRepository.save(transaction);
		} else {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}
		
	}

	@Override
	public Transaction cancel(Transaction transaction) {

		if (transaction.isSended()) {

			transaction.setStatus(Status.CANCELED);

			return transactionRepository.save(transaction);

		} else {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

	}

	@Override
	public Transaction delete(Transaction transaction) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

		if (transaction.isAssignedTo(flatmateLogged)) {

			transaction.setStatus(Status.DELETED);

			return transactionRepository.save(transaction);

		} else {
			throw new AccessDeniedException(localeService.getMessage(LOCALE_KEY_ACCESS_DENIED, flatmateLogged.getNickname()));
		}

	}

}
