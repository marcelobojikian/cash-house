package br.com.cashhouse.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;

public interface TransactionService {
	
	public List<Transaction> findAll(Dashboard dashboard);
	
	public Page<Transaction> findAll(Predicate parameters, Pageable pageable);

	public Transaction findById(Long id);
	
	public Collection<Transaction> findByCashierReferences(Dashboard dashboard, Cashier cashier);
	
	public Collection<Transaction> findByFlatmateReferences(Dashboard dashboard, Flatmate createBy, Flatmate assigned);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public Transaction update(Long id, Transaction newTransaction);

	public void updateFlatmateAssigned(Long id, Flatmate flatmateAssigned);

	public void updateCashier(Long id, Cashier cashier);

	public void updateValue(Long id, BigDecimal value);
	
	public void delete(Long id);
	
	/* Actions */

	public Transaction createDeposit(Cashier cashier, BigDecimal value);

	public Transaction createDeposit(Cashier cashier, Flatmate flatmateAssign, BigDecimal value);

	public Transaction createwithdraw(Cashier cashier, BigDecimal value);

	public Transaction createwithdraw(Cashier cashier, Flatmate flatmateAssign, BigDecimal value);
	
	public Transaction send(Transaction transaction);
	
	public Transaction finish(Transaction transaction);
	
	public Transaction cancel(Transaction transaction);
	
	public Transaction delete(Transaction transaction);

}
