package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.repository.CashierRepository;
import br.com.cashhouse.server.exception.AccessDeniedException;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CashierServiceImpl implements CashierService {

	@Autowired
	private HeaderRequest headerRequest;

	@Autowired
	private AuthenticationFacade authenticationFacade; 

	@Autowired
	private CashierRepository cashierRepository;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private TransactionService transactionService;

	@Override
	public Cashier findById(long id) {
		return cashierRepository.findByDashboardAndId(headerRequest.getDashboard(), id).orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
	}

	@Override
	public List<Cashier> findAll() {
		Dashboard dashboard = headerRequest.getDashboard();
		return dashboard.getCashiers();
	}

	@Override
	public Cashier create(String name, BigDecimal started, BigDecimal balance) {
		
		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Cashier cashier = new Cashier(name, started, balance);
		cashier.setOwner(flatmateLogged);
		dashboard.getCashiers().add(cashier);
		
		return cashierRepository.save(cashier);
		
	}

	@Override
	public Cashier update(long id, Cashier cashier) {

		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));

		entity.setName(cashier.getName());
		entity.setStarted(cashier.getStarted());
		entity.setBalance(cashier.getBalance());

		return cashierRepository.save(entity);

	}

	@Override
	public Cashier update(long id, String name) {

		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));

		entity.setName(name);

		return cashierRepository.save(entity);

	}

	@Override
	@Transactional
	public void delete(long id) {

		Dashboard dashboard = headerRequest.getDashboard();
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));

		Collection<Transaction> transactions = transactionService.findByCashierReferences(dashboard, entity);

		dashboardService.removeCashier(dashboard, entity);
		dashboardService.removeTransactions(dashboard, transactions);

	}

	@Override
	public synchronized void applyTransaction(Transaction transaction) {
		
		Long cashierId = transaction.getCashier().getId();
		Cashier cashier = cashierRepository.findById(cashierId).orElseThrow(() -> new EntityNotFoundException(Cashier.class, "cashier", cashierId));

		log.info(String.format("Action %s in a Cashier %s current balance %s", transaction.getAction(), cashierId, cashier.getBalance()));
		
		if(transaction.getAction().equals(Action.WITHDRAW)) {
			cashier.withdraw(transaction.getValue());
		} else {
			cashier.deposit(transaction.getValue());
		}
		
		log.info(String.format("Changed balance by %s", cashier.getBalance()));
		
		cashierRepository.save(cashier);
		
	}

}
