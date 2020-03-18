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
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/users/self")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/detail")
	@ApiOperation(value = "Return a user details entity", response = Flatmate.class)
	public Flatmate getDetails(@ApiIgnore @UserLogged Flatmate flatmate) {
		return flatmate;
	}

	@GetMapping("/dashboard")
	@ApiOperation(value = "Return a dashboard entity", response = Dashboard.class)
	public Dashboard getDashboard(@ApiIgnore Dashboard dashboard) {
		return dashboard;
	}

	@GetMapping("/invitations")
	@ApiOperation(value = "Return a list of guest dashboard entities", response = Dashboard[].class)
	public Collection<Dashboard> getDashboardInvited(@ApiIgnore @UserLogged Flatmate flatmate) {
		return dashboardService.findMyInvitations(flatmate);
	}

}
