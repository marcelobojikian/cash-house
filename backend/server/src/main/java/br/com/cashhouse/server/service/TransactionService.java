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
	
	public Page<Transaction> findAll(Dashboard dashboard, Predicate parameters, Pageable pageable);

	public Transaction findById(Dashboard dashboard, Long id);
	
	public Collection<Transaction> findByCashierReferences(Dashboard dashboard, Cashier cashier);
	
	public Collection<Transaction> findByFlatmateReferences(Dashboard dashboard, Flatmate createBy, Flatmate assigned);

	@PreAuthorize("hasAnyRole('ADMIN')")
	public Transaction update(Dashboard dashboard, Long id, Transaction newTransaction);

	public void updateFlatmateAssigned(Dashboard dashboard, Long id, Flatmate flatmateAssigned);

	public void updateCashier(Dashboard dashboard, Long id, Cashier cashier);

	public void updateValue(Dashboard dashboard, Long id, BigDecimal value);
	
	public void delete(Dashboard dashboard, Long id);
	
	/* Actions */

	public Transaction createDeposit(Dashboard dashboard, Cashier cashier, BigDecimal value);

	public Transaction createDeposit(Dashboard dashboard, Cashier cashier, Flatmate flatmateAssign, BigDecimal value);

	public Transaction createwithdraw(Dashboard dashboard, Cashier cashier, BigDecimal value);

	public Transaction createwithdraw(Dashboard dashboard, Cashier cashier, Flatmate flatmateAssign, BigDecimal value);
	
	public Transaction send(Dashboard dashboard, Transaction transaction);
	
	public Transaction finish(Dashboard dashboard, Transaction transaction);
	
	public Transaction cancel(Dashboard dashboard, Transaction transaction);
	
	public Transaction delete(Dashboard dashboard, Transaction transaction);

}