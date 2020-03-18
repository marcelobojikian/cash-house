package br.com.housecash.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.exception.InvalidOperationException;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.repository.CashierRepository;
import br.com.housecash.backend.repository.DashboardRepository;
import br.com.housecash.backend.security.service.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CashierServiceImpl implements CashierService {

	@Autowired
	private AuthenticationFacade authenticationFacade; 

	@Autowired
	private CashierRepository cashierRepository;

	@Autowired
	private DashboardRepository dashboardRepository;

	@Override
	public Cashier findById(Dashboard dashboard, long id) {
		return cashierRepository.findByDashboardAndId(dashboard, id).orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
	}

	@Override
	public List<Cashier> findAll(Dashboard dashboard) {
		return dashboard.getCashiers();
	}

	@Override
	public Cashier create(Dashboard dashboard, String name, BigDecimal started, BigDecimal balance) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Cashier cashier = new Cashier(name, started, balance);
		dashboard.getCashiers().add(cashier);
		
		return cashierRepository.save(cashier);
		
	}

	@Override
	public Cashier update(Dashboard dashboard, long id, Cashier cashier) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		return cashierRepository.findByDashboardAndId(dashboard, id).map(entity -> {
			
			entity.setName(cashier.getName());
			entity.setStarted(cashier.getStarted());
			entity.setBalance(cashier.getBalance());
			
			return cashierRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Cashier.class, id));
		
	}

	@Override
	public Cashier update(Dashboard dashboard, long id, String name) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		
		if(!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		return cashierRepository.findByDashboardAndId(dashboard, id).map(entity -> {
			
			entity.setName(name);
			
			return cashierRepository.save(entity);
			
		}).orElseThrow(() ->  new EntityNotFoundException(Cashier.class, id) );
		
	}

	@Override
	public synchronized void applyTransaction(Transaction transaction) {
		
		Long cashierId = transaction.getCashier().getId();
		Cashier cashier = cashierRepository.findById(cashierId).orElseThrow(() -> new EntityNotFoundException(Cashier.class, "cashier", cashierId));

		log.info(String.format("Action %s in a Cashier %s current balance %s", transaction.getAction(), cashierId, cashier.getBalance()));
		
		if(transaction.getAction().equals(Action.DEPOSIT)) {
			cashier.deposit(transaction.getValue());
		} else if(transaction.getAction().equals(Action.WITHDRAW)) {
			cashier.withdraw(transaction.getValue());
		} else {
			throw new InvalidOperationException(transaction, transaction.getAction());
		}
		
		log.info(String.format("Changed balance by %s", cashier.getBalance()));
		
		cashierRepository.save(cashier);
		
	}

	@Override
	@Transactional
	public void deleteCashierById(Dashboard dashboard, long id) {
		
		cashierRepository.findByDashboardAndId(dashboard, id).map(entity -> {

			System.out.println("dashboard.getCashiers(): " +  dashboard.getCashiers().size());
			dashboard.getCashiers().remove(entity);
			System.out.println("dashboard.getCashiers(): " +  dashboard.getCashiers().size());
			
			dashboardRepository.save(dashboard);
			
			cashierRepository.save(entity);
			
			return entity;
			
		}).orElseThrow(() ->  new EntityNotFoundException(Cashier.class, id) );
		
	}

}
