package br.com.housecash.backend.service;

import java.util.Collection;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;

public interface DashboardService {
	
	public Dashboard findById(Long id);
	
	public Dashboard findByOwner(Flatmate flatmate);
	
	public Collection<Dashboard> findMyInvitations(Flatmate flatmate);
	
	public Dashboard createDashboard(Flatmate flatmate);

}
