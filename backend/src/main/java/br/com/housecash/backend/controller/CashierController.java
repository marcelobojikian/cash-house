package br.com.housecash.backend.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.dto.CreateCashier;
import br.com.housecash.backend.model.dto.UpdateCashier;
import br.com.housecash.backend.service.CashierService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/cashiers")
@PreAuthorize("hasAnyRole('USER')")
public class CashierController {

	@Autowired
	private CashierService cashierService; 

	@GetMapping("")
	@ApiOperation(value = "Return a list with all cashiers", response = Cashier[].class)
	public List<Cashier> findAll(@ApiIgnore Dashboard dashboard) {
		return cashierService.findAll(dashboard);
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Return a cashier entity by id", response = Cashier.class)
	public Cashier findById(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		return cashierService.findById(dashboard, id);
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Return a cashier entity created", response = Cashier.class)
	public Cashier create(
			@ApiIgnore Dashboard dashboard, 
			@RequestDTO(CreateCashier.class) @Valid CreateCashier cashier) {
		
		String name = cashier.getName();
		BigDecimal started = cashier.getStarted();
		BigDecimal balance = cashier.getBalance();
		
		if(started == null) {
			started = balance;
		}
		
		return cashierService.create(dashboard, name, started, balance);
		
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "Return a cashier entity updated", response = Cashier.class)
	public Cashier update(
			@ApiIgnore Dashboard dashboard, 
			@PathVariable Long id, 
			@RequestBody Cashier cashier) {
		return cashierService.update(dashboard, id, cashier);
	}

	@PatchMapping("/{id}")
	@ApiOperation(value = "Return a cashier entity partial updated", response = Cashier.class)
	public Cashier patch(
			@ApiIgnore Dashboard dashboard, 
			@PathVariable Long id,
			@RequestDTO(UpdateCashier.class) @Valid UpdateCashier cashier) throws Exception {

		String name = cashier.getName();
		return cashierService.update(dashboard, id, name);

	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Return a cashier entity partial updated", response = Cashier.class)
	public void detele(
			@ApiIgnore Dashboard dashboard, 
			@PathVariable Long id){
		cashierService.deleteCashierById(dashboard, id);
	}
	
}
