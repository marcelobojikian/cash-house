package br.com.housecash.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.resource.ContentHelper;
import br.com.housecash.backend.util.FlatmateBuilder;
import br.com.housecash.backend.util.FlatmateBuilder.FlatmateBuilderBuilder;

public class ServiceHelper {
	
	public static Flatmate createFlatmate(Long id, String email, String nickname, String password) {
		Flatmate flatmate = new Flatmate();
		flatmate.setId(id);
		flatmate.setEmail(email);
		flatmate.setNickname(nickname);
		flatmate.setPassword(password);

        Dashboard dashboard = new Dashboard();
        flatmate.setDashboard(dashboard);

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
