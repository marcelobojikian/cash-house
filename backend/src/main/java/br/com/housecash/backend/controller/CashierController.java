package br.com.housecash.backend.controller;

import java.math.BigDecimal;
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
import br.com.housecash.backend.exception.NoContentException;
import br.com.housecash.backend.handler.annotation.ObjDashboard;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.dto.CreateCashier;
import br.com.housecash.backend.service.CashierService;

@RestController
@RequestMapping("/cashiers")
@PreAuthorize("hasAnyRole('USER')")
public class CashierController {

	@Autowired
	private CashierService cashierService; 

	@GetMapping("")
	public List<Cashier> findAll(@ObjDashboard Dashboard dashboard) {
		return cashierService.findAll(dashboard);
	}

	@GetMapping("/{id}")
	public Cashier findById(@ObjDashboard Dashboard dashboard, @PathVariable Long id) {
		return cashierService.findById(dashboard, id);
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public Cashier create(
			@ObjDashboard Dashboard dashboard, 
			@RequestDTO(CreateCashier.class) @Valid Cashier cashier) {
		
		String name = cashier.getName();
		BigDecimal started = cashier.getStarted();
		BigDecimal balance = cashier.getBalance();
		
		if(started == null) {
			started = balance;
		}
		
		return cashierService.create(dashboard, name, started, balance);
		
	}

	@PutMapping("/{id}")
	public Cashier update(
			@ObjDashboard Dashboard dashboard, 
			@PathVariable Long id, 
			@RequestBody Cashier cashier) {
		return cashierService.update(dashboard, id, cashier);
	}

	@PatchMapping("/{id}")
	public Cashier patch(
			@ObjDashboard Dashboard dashboard, 
			@PathVariable Long id,
			@RequestBody Map<String, String> update) throws Exception {
		
		if(update.isEmpty()) {
			throw new NoContentException();
		}

		String name = update.get("name");
		if (!StringUtils.isEmpty(name)) {
			return cashierService.update(dashboard, id, name);
		} else {
			throw new InvalidFieldException(update.keySet());
		}

	}
	
}
