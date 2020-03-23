package br.com.housecash.backend.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.controller.dto.Propertie;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.security.annotation.UserLogged;
import br.com.housecash.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/users/self")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

	@Autowired
	private UserService userService;

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
		return userService.findInvitations(flatmate.getId());
	}

	@PutMapping("/nickname")
	@ApiOperation(value = "Return a flatmate entity with update nickname", response = Flatmate.class)
	public Flatmate updateNickname(@ApiIgnore @UserLogged Flatmate flatmate,
			@RequestDTO(Propertie.class) @Valid Propertie propertie) {
		return userService.changeNickname(flatmate.getId(), propertie.getValue());
	}

	@PutMapping("/password")
	@ApiOperation(value = "Return a flatmate entity with update password", response = Flatmate.class)
	public Flatmate updatePassword(@ApiIgnore @UserLogged Flatmate flatmate,
			@RequestDTO(Propertie.class) @Valid Propertie propertie) {
		return userService.changePassword(flatmate.getId(), propertie.getValue());
	}

	@PostMapping("/step/guest/finish")
	@ApiOperation(value = "Return a flatmate entity with guest step completed", response = Flatmate.class)
	public Flatmate finishStepGuest(@ApiIgnore @UserLogged Flatmate flatmate) {
		return userService.finishStepGuest(flatmate.getId());
	}

	@PostMapping("/step/first/finish")
	@ApiOperation(value = "Return a flatmate entity with first step completed", response = Flatmate.class)
	public Flatmate finishStepFirst(@ApiIgnore @UserLogged Flatmate flatmate) {
		return userService.finishStepFirst(flatmate.getId());
	}

}
