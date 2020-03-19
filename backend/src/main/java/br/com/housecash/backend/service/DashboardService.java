package br.com.housecash.backend.service;

import java.util.Collection;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;

public interface DashboardService {
	
	public Dashboard findById(Long id);
	
	public Dashboard findByOwner(Flatmate flatmate);
	
	public Collection<Dashboard> findMyInvitations(Flatmate flatmate);
	
	public Dashboard createDashboard(Flatmate flatmate);
	
	public void removeGuest(Dashboard dashboard, Flatmate guest);
	
	public void removeCashier(Dashboard dashboard, Cashier cashier);
	
	public void removeTransaction(Dashboard dashboard, Transaction transaction);
	
	public void removeTransactions(Dashboard dashboard, Collection<Transaction> transactions);

}
