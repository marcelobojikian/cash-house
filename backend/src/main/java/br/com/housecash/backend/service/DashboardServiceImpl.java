package br.com.housecash.backend.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.repository.DashboardRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;

	@Override
	public Dashboard findById(Long id) {
		return dashboardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Dashboard.class, id));
	}

	@Override
	public Dashboard findByOwner(Flatmate flatmate) {
		return dashboardRepository.findByOwner(flatmate);
	}

	@Override
	public Collection<Dashboard> findMyInvitations(Flatmate flatmate) {
		return dashboardRepository.findByMyInvitations(flatmate);
	}

	@Override
	public synchronized Dashboard createDashboard(Flatmate flatmate) {
		
		Dashboard dashboard = new Dashboard();
		dashboard.setOwner(flatmate);
		return dashboardRepository.save(dashboard);
		
	}

	@Override
	public void removeGuest(Dashboard dashboard, Flatmate guest) {
		dashboard.getGuests().remove(guest);
		dashboardRepository.save(dashboard);
	}

	@Override
	public void removeCashier(Dashboard dashboard, Cashier cashier) {
		dashboard.getCashiers().remove(cashier);
		dashboardRepository.save(dashboard);
	}

	@Override
	public void removeTransaction(Dashboard dashboard, Transaction transaction) {
		dashboard.getTransactions().remove(transaction);
		dashboardRepository.save(dashboard);
	}

	@Override
	public void removeTransactions(Dashboard dashboard, Collection<Transaction> transactions) {
		dashboard.getTransactions().removeAll(transactions);
		dashboardRepository.save(dashboard);
	}

}
