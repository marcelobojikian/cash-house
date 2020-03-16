package br.com.housecash.backend.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.security.annotation.UserLogged;
import br.com.housecash.backend.service.DashboardService;

@RestController
@RequestMapping("/users/self")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/detail")
	public Flatmate getDetails(@UserLogged Flatmate flatmate) {
		return flatmate;
	}

	@GetMapping("/dashboard")
	public Dashboard getDashboard(Dashboard dashboard) {
		return dashboard;
	}

	@GetMapping("/invitations")
	public Collection<Dashboard> getDashboardInvited(@UserLogged Flatmate flatmate) {
		return dashboardService.findMyInvitations(flatmate);
	}

}
