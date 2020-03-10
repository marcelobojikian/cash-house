package br.com.housecash.backend.controller;

import java.util.List;
import java.util.Map;

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
import br.com.housecash.backend.handler.annotation.ObjDashboard;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.dto.CreateFlatmate;
import br.com.housecash.backend.service.FlatmateService;

@RestController
@RequestMapping("/flatmates")
@PreAuthorize("hasAnyRole('USER')")
public class FlatmateController {

	@Autowired
	private FlatmateService flatmateService;

	@GetMapping("")
	public List<Flatmate> findAll(@ObjDashboard Dashboard dashboard) {
		return flatmateService.findAll(dashboard);
	}

	@GetMapping("/{id}")
	public Flatmate findById(@ObjDashboard Dashboard dashboard, @PathVariable Long id) {
		return flatmateService.findById(dashboard, id);
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public Flatmate create(
			@ObjDashboard Dashboard dashboard,
			@RequestDTO(CreateFlatmate.class) @Valid Flatmate flatmate) {
		
		String email = flatmate.getEmail();
		String nickname = flatmate.getNickname();
		String password = flatmate.getPassword();
		
		if(StringUtils.isEmpty(nickname)) {
			nickname = email;
		}
		
		return flatmateService.create(dashboard, email, nickname, password);

	}

	@PutMapping("/{id}")
	public Flatmate update(@PathVariable Long id, @RequestBody Flatmate flatmate) {
		return flatmateService.update(id, flatmate);
	}

	@PatchMapping("/{id}")
	public Flatmate patch(
			@ObjDashboard Dashboard dashboard,
			@PathVariable Long id,
			@RequestBody Map<String, String> update) {

		String nickname = update.get("nickname");
		if (!StringUtils.isEmpty(nickname)) {

			String password = update.get("password");
			
			if(StringUtils.isEmpty(password)) {
				return flatmateService.update(id, nickname);
			}else {
				return flatmateService.update(id, nickname, password);
			}
			
		} else {
			throw new InvalidFieldException(update.keySet());
		}

	}

	@PatchMapping("/{id}/step/guest")
	public Flatmate setpGuestCompleted(
			@ObjDashboard Dashboard dashboard,
			@PathVariable Long id,
			@RequestBody Map<String, String> update) {

		String nickname = update.get("nickname");
		String password = update.get("password");
		if (!StringUtils.isEmpty(nickname) && !StringUtils.isEmpty(nickname)) {
			return flatmateService.updateGuest(id, nickname, password);
		} else {
			throw new InvalidFieldException(update.keySet());
		}

	}

}
