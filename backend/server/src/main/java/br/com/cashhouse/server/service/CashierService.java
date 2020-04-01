package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Transaction;

public interface CashierService {
	
	public List<Cashier> findAll(Dashboard dashboard);

	public Cashier findById(Dashboard dashboard, long id);
	
	public Cashier create(Dashboard dashboard, String name, BigDecimal startedValue, BigDecimal balance);

	public Cashier update(Dashboard dashboard, long id, Cashier cashier);
	
	public Cashier update(Dashboard dashboard, long id, String name);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public void delete(Dashboard dashboard, long id);
	
	public void applyTransaction(Transaction transaction);

}
