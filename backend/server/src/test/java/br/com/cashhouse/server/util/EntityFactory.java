package br.com.cashhouse.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;

public class EntityFactory {
	
	/*
	 * Transaction Helper 
	 */
	
	public static Transaction createTransaction(Long id, Double value, Status status, Action action) {
    	
		Transaction transaction = new Transaction();
		transaction.setId(id);
		transaction.setValue(BigDecimal.valueOf(value));
		transaction.setStatus(status);
		transaction.setAction(action);
        
		return transaction;
	}
	
	public static Transaction createTransaction(Dashboard dashboard, Long id, Double value, Status status, Action action) {
		Transaction transaction = createTransaction(id, value, status, action);
		dashboard.getTransactions().add(transaction);
		return transaction;
	}
	
	/*
	 * Cashier Helper 
	 */
	
	public static Cashier createCashier(Dashboard dashboard, Long id, String name, Double started) {
		Cashier cashier = createCashier(id, name, started, started);
		dashboard.getCashiers().add(cashier);
		return cashier;
	}
	
	public static Cashier createCashier(Dashboard dashboard, Long id, String name, Double started, Double balance) {
		Cashier cashier = createCashier(id, name, started, balance);
		dashboard.getCashiers().add(cashier);
		return cashier;
	}
	
	public static Cashier createCashier(Long id, String name, Double started) {
		return createCashier(id, name, started, started);
	}
	
	public static Cashier createCashier(Long id, String name, Double started, Double balance) {
    	
    	Cashier cashier = new Cashier();
    	cashier.setId(id);
    	cashier.setName(name);
    	cashier.setStarted(BigDecimal.valueOf(started));
    	cashier.setBalance(BigDecimal.valueOf(balance));
        
		return cashier;
	}
	
	/*
	 * Flatmate Helper 
	 */
	
	public static Flatmate createFlatmate(Long id, String email, String nickname, String password) {
		Flatmate flatmate = new Flatmate();
		flatmate.setId(id);
		flatmate.setEmail(email);
		flatmate.setNickname(nickname);
		flatmate.setPassword(password);

        Dashboard dashboard = new Dashboard();
        flatmate.setDashboard(dashboard);

        dashboard.setId(1l);
        dashboard.setOwner(flatmate);
        dashboard.setGuests(new ArrayList<Flatmate>());
        dashboard.setCashiers(new ArrayList<Cashier>());
        dashboard.setTransactions(new ArrayList<Transaction>());
        
		return flatmate;
	}
	
	public static Flatmate createFlatmate(Long id, String email, String nickname) {
		Flatmate flatmate = createFlatmate(id, email, nickname, "password");
		return flatmate;
	}
	
	public static Flatmate createFlatmate(Long id, String email, String nickname, String password, String roles) {
		Flatmate flatmate = createFlatmate(id, email, nickname, password);
		flatmate.setRoles(roles);
		return flatmate;
	}

}
