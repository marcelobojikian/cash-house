package br.com.housecash.backend.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;

public interface TransactionService {
	
	public List<Transaction> findAll(Dashboard dashboard);
	
	public Collection<Transaction> findAll(Dashboard dashboard, Map<String,String> parameters);
	
	public Page<Transaction> findAll(Dashboard dashboard, Pageable pageable);

	public Transaction findById(Dashboard dashboard, Long id);
	
	public List<Transaction> findByDate(Integer pageNo, Integer pageSize);
	
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
