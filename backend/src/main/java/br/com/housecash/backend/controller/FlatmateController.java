package br.com.housecash.backend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.exception.InvalidFieldException;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.dto.CreateFlatmate;
import br.com.housecash.backend.model.dto.UpdateFlatmate;
import br.com.housecash.backend.service.FlatmateService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/flatmates")
@PreAuthorize("hasAnyRole('USER')")
public class FlatmateController {

	@Autowired
	private FlatmateService flatmateService;

	@GetMapping("")
	@ApiOperation(value = "Return a list with all flatmates", response = Flatmate[].class)
	public List<Flatmate> findAll(@ApiIgnore Dashboard dashboard) {
		return flatmateService.findAll(dashboard);
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Return a flatmate entity by id", response = Flatmate.class)
	public Flatmate findById(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		return flatmateService.findById(dashboard, id);
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Return a flatmate entity created", response = Flatmate.class)
	public Flatmate create(
			@ApiIgnore Dashboard dashboard,
			@RequestDTO(CreateFlatmate.class) @Valid CreateFlatmate flatmate) {
		
		String email = flatmate.getEmail();
		String nickname = flatmate.getNickname();
		String password = flatmate.getPassword();
		
		if(StringUtils.isEmpty(nickname)) {
			nickname = email;
		}
		
		return flatmateService.create(dashboard, email, nickname, password);

	}

	@PutMapping("/{id}")
	@ApiOperation(value = "Return a flatmate entity updated", response = Flatmate.class)
	public Flatmate update(@PathVariable Long id, @RequestBody Flatmate flatmate) {
		return flatmateService.update(id, flatmate);
	}

	@PatchMapping("/{id}")
	@ApiOperation(value = "Return a flatmate entity partial updated", response = Cashier.class)
	public Flatmate patch(
			@ApiIgnore Dashboard dashboard,
			@PathVariable Long id,
			@RequestDTO(UpdateFlatmate.class) @Valid UpdateFlatmate flatmate) {

		String nickname = flatmate.getNickname();
		String password = flatmate.getPassword();
		
		if(StringUtils.isEmpty(password)) {
			return flatmateService.update(id, nickname);
		}else {
			return flatmateService.update(id, nickname, password);
		}

	}
	
	// TODO Mover para o controller de usuario Method: "setpGuestCompleted".
	@PatchMapping("/{id}/step/guest")
	@ApiOperation(value = "Return a flatmate entity with guest step completed", response = Cashier.class)
	public Flatmate setpGuestCompleted(
			@ApiIgnore Dashboard dashboard,
			@PathVariable Long id,
			@RequestDTO(UpdateFlatmate.class) @Valid UpdateFlatmate flatmate) {

		String nickname = flatmate.getNickname();
		String password = flatmate.getPassword();
		
		if (!StringUtils.isEmpty(password)) {
			return flatmateService.updateGuest(id, nickname, password);
		} else {
			throw new InvalidFieldException(password);
		}

	}

}
