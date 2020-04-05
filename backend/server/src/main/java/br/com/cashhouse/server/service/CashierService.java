package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Transaction;

@PreAuthorize("isAuthenticated()")
public interface CashierService {
	
	public List<Cashier> findAll();

	public Cashier findById(long id);

	@PreAuthorize("isDashboardOwner()")
	public Cashier create(String name, BigDecimal startedValue, BigDecimal balance);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public Cashier update(long id, Cashier cashier);

	@PreAuthorize("isDashboardOwner()")
	public Cashier update(long id, String name);

	@PreAuthorize("isDashboardOwner()")
	public void delete(long id);

	public void applyTransaction(Transaction transaction);

}
