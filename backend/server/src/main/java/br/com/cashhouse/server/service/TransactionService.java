package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.server.rest.dto.UpdateTransaction;

@PreAuthorize("isAuthenticated()")
public interface TransactionService {

	public Transaction findById(Long id);
	
	public List<Transaction> findAll();
	
	public Page<Transaction> findAll(Predicate parameters, Pageable pageable);

	@PreAuthorize("isDashboardOwner()")
	public Transaction createDeposit(Cashier cashier, Flatmate flatmateAssign, BigDecimal value);

	public Transaction createDeposit(Cashier cashier, BigDecimal value);

	@PreAuthorize("isDashboardOwner()")
	public Transaction createwithdraw(Cashier cashier, Flatmate flatmateAssign, BigDecimal value);

	public Transaction createwithdraw(Cashier cashier, BigDecimal value);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public Transaction update(Long id, Transaction newTransaction);

	@PreAuthorize("isDashboardOwner()")
	public Transaction updateFlatmateAssigned(Long id, Flatmate flatmateAssigned);

	public Transaction update(Long id, UpdateTransaction content);

	public Transaction updateCashier(Long id, Cashier cashier);

	public Transaction updateValue(Long id, BigDecimal value);

	@PreAuthorize("isDashboardOwner()")
	public void delete(Long id);

	@PreAuthorize("isDashboardOwner()")
	public Collection<Transaction> findByCashierReferences(Cashier cashier);

	@PreAuthorize("isDashboardOwner()")
	public Collection<Transaction> findByFlatmateReferences(Flatmate createBy, Flatmate assigned);
	
	/* Actions */
	
	public Transaction send(Transaction transaction);

	@PreAuthorize("isDashboardOwner()")
	public Transaction finish(Transaction transaction);

	@PreAuthorize("isDashboardOwner()")
	public Transaction cancel(Transaction transaction);
	
	public Transaction delete(Transaction transaction);

}
