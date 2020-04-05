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
		
		Cashier cashier = new Cashier(name, started, balance);
		cashier.setOwner(flatmateLogged);
		dashboard.getCashiers().add(cashier);

		log.info(String.format("Flatmate %s creating Cashier %s", flatmateLogged.getNickname(), name));
		
		return cashierRepository.save(cashier);
		
	}

	@Override
	public Cashier update(long id, Cashier cashier) {

		Dashboard dashboard = headerRequest.getDashboard();

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
		
		log.info(String.format("Cashier %s changing ... ", entity.getName()));
		log.info(String.format("Name[%s], Started[%s], Balance[%s]", cashier.getName(), cashier.getStarted(), cashier.getBalance()));

		entity.setName(cashier.getName());
		entity.setStarted(cashier.getStarted());
		entity.setBalance(cashier.getBalance());
		entity.setOwner(cashier.getOwner());

		return cashierRepository.save(entity);

	}

	@Override
	public Cashier update(long id, String name) {

		Dashboard dashboard = headerRequest.getDashboard();

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
		
		log.info(String.format("Cashier %s change name to %s", entity.getName(), name));

		entity.setName(name);

		return cashierRepository.save(entity);

	}

	@Override
	@Transactional
	public void delete(long id) {

		Dashboard dashboard = headerRequest.getDashboard();

		Cashier entity = cashierRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
		
		log.info(String.format("Deleting Cashier %s", entity.getName()));

		Collection<Transaction> transactions = transactionService.findByCashierReferences(entity);

		dashboardService.removeCashier(dashboard, entity);
		dashboardService.removeTransactions(dashboard, transactions);
		
		log.info("Delete sucess");

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
		
		log.info(String.format("Apply %s. Changed balance to %s", transaction.getValue(), cashier.getBalance()));
		
		cashierRepository.save(cashier);
		
	}

}
