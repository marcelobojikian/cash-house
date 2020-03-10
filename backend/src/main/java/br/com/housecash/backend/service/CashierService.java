package br.com.housecash.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Transaction;

public interface CashierService {

	public Cashier findById(Dashboard dashboard, long id);
	
	public List<Cashier> findAll(Dashboard dashboard);
	
	public Cashier create(Dashboard dashboard, String name, BigDecimal startedValue, BigDecimal balance);

	public Cashier update(Dashboard dashboard, long id, Cashier cashier);
	
	public Cashier update(Dashboard dashboard, long id, String name);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public void deleteCashierById(Dashboard dashboard, long id);
	
	public void applyTransaction(Transaction transaction);

}
