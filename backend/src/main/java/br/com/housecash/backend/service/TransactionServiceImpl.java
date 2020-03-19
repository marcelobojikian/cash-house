package br.com.housecash.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.exception.InvalidOperationException;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.repository.TransactionRepository;
import br.com.housecash.backend.security.service.AuthenticationFacade;

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
	public List<Transaction> findAll(Dashboard dashboard) {
		return dashboard.getTransactions();
	}

	@Override
	public Collection<Transaction> findAll(Dashboard dashboard, Map<String,String> parameters) {
		return transactionRepository.findByParameters(dashboard, parameters);
	}

	@Override
	public Page<Transaction> findAll(Dashboard dashboard, Pageable pageable) {
		return transactionRepository.findByDashboard(dashboard, pageable);
	}

	@Override
	public List<Transaction> findByDate(Integer pageNo, Integer pageSize) {
		
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("cashier"));
 
        Page<Transaction> pagedResult = transactionRepository.findAll(paging);
         
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Transaction>();
        }
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

		if(!dashboard.isOwner(flatmateLogged)) {
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

		if(!dashboard.isOwner(flatmateLogged)) {
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

		if(!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		return transactionRepository.findByDashboardAndId(dashboard, id).map(entity -> {
			
			Long cashierId = newTransaction.getCashier().getId();
			Long assignedId = newTransaction.getAssigned().getId();
			Long createById = newTransaction.getCreateBy().getId();
			
			if(dashboard.isOwner(assignedId)) {
				entity.setAssigned(dashboard.getOwner());
			} else {
				Flatmate assigned = flatmateService.findById(dashboard, assignedId).orElseThrow(() -> new EntityNotFoundException(Transaction.class, "assigned", assignedId));
				entity.setAssigned(assigned);
			}

			if(dashboard.isOwner(createById)) {
				entity.setCreateBy(dashboard.getOwner());
			} else {
				Flatmate createBy = flatmateService.findById(dashboard, createById).orElseThrow(() -> new EntityNotFoundException(Transaction.class, "createBy", createById));
				entity.setCreateBy(createBy);
			}
			
			Cashier cashier = cashierService.findById(dashboard, cashierId);
			entity.setCashier(cashier);

			entity.setAction(newTransaction.getAction());
			entity.setStatus(newTransaction.getStatus());
			entity.setValue(newTransaction.getValue());
			
			return transactionRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
		
	}

	@Override
	public Transaction findById(Dashboard dashboard, Long id) {
		return transactionRepository.findByDashboardAndId(dashboard, id).orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
	}

	@Override
	public void updateValue(Dashboard dashboard, Long id, BigDecimal value) {
		
		transactionRepository.findByDashboardAndId(dashboard, id).map(entity -> {

			if(!entity.isAvailableToChange()) {
				throw new InvalidOperationException(entity, entity.getStatus());
			}
			
			entity.setValue(value);
			
			return transactionRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
		
	}

	@Override
	public void updateCashier(Dashboard dashboard, Long id, Cashier cashier) {
		
		transactionRepository.findByDashboardAndId(dashboard, id).map(entity -> {
			
			if(!entity.isAvailableToChange()) {
				throw new InvalidOperationException(entity, entity.getStatus());
			}
			
			entity.setCashier(cashier);
			
			return transactionRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
		
	}

	@Override
	public void updateFlatmateAssigned(Dashboard dashboard, Long id, Flatmate flatmateAssigned) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		transactionRepository.findByDashboardAndId(dashboard, id).map(entity -> {

			if(!entity.isAvailableToChange()) {
				throw new InvalidOperationException(entity, entity.getStatus());
			}
			
			entity.setAssigned(flatmateAssigned);
			
			return transactionRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
		
	}

	@Override
	public Transaction send(Dashboard dashboard, Transaction transaction) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}
		
		if(transaction.isAssignedTo(flatmateLogged)){
			
			transaction.setStatus(Status.SENDED);
			
			return transactionRepository.save(transaction);
			
		} else {
			throw new AccessDeniedException(flatmateLogged);
		}
		
	}

	@Override
	public Transaction finish(Dashboard dashboard, Transaction transaction) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		if(transaction.isSended()) {

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

		if(!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		if(transaction.isSended()) {
			
			transaction.setStatus(Status.CANCELED);
			
			return transactionRepository.save(transaction);
			
		} else {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}

	}

	@Override
	public Transaction delete(Dashboard dashboard, Transaction transaction) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!transaction.isCreated()) {
			throw new InvalidOperationException(transaction, transaction.getStatus());
		}
		
		if(transaction.isAssignedTo(flatmateLogged)){
			
			transaction.setStatus(Status.DELETED);
			
			return transactionRepository.save(transaction);
			
		} else {
			throw new AccessDeniedException(flatmateLogged);
		}
		
	}

	@Override
	public Collection<Transaction> findByFlatmateReferences(Dashboard dashboard, Flatmate createBy, Flatmate assigned){
		return transactionRepository.findByDashboardAndFlatmateRef(dashboard, createBy, assigned);
	}

	@Override
	public Collection<Transaction> findByCashierReferences(Dashboard dashboard, Cashier cashier){
		return transactionRepository.findByDashboardAndCashier(dashboard, cashier);
	}

	@Override
	public void delete(Dashboard dashboard, Long id) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		
		if(!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		transactionRepository.findByDashboardAndId(dashboard, id).map(entity -> {
			
			dashboardService.removeTransaction(dashboard, entity);
			
			return entity;
			
		}).orElseThrow(() ->  new EntityNotFoundException(Transaction.class, id) );
		
	}

}
