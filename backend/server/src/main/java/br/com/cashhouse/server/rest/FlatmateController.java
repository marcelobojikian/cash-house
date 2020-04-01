package br.com.cashhouse.server.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.rest.dto.CreateFlatmate;
import br.com.cashhouse.server.rest.dto.UpdateFlatmate;
import br.com.cashhouse.server.service.FlatmateService;
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
		return flatmateService.findById(dashboard, id).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Return a flatmate entity created", response = Flatmate.class)
	public Flatmate create(
			@ApiIgnore Dashboard dashboard,
			@RequestBody @Valid CreateFlatmate flatmate) {
		
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
	@ApiOperation(value = "Return a flatmate entity partial updated", response = Flatmate.class)
	public Flatmate patch(
			@ApiIgnore Dashboard dashboard,
			@PathVariable Long id,
			@RequestBody @Valid UpdateFlatmate flatmate) {

		String nickname = flatmate.getNickname();
		String password = flatmate.getPassword();
		
		if(StringUtils.isEmpty(password)) {
			return flatmateService.update(id, nickname);
		}else {
			return flatmateService.update(id, nickname, password);
		}

	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Return status OK when deleted", response = Flatmate.class)
	public void detele(
			@ApiIgnore Dashboard dashboard, 
			@PathVariable Long id){
		flatmateService.delete(dashboard, id);
	}

}
