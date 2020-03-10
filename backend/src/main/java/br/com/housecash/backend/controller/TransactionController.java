package br.com.housecash.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.exception.NoContentException;
import br.com.housecash.backend.handler.annotation.ObjDashboard;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.dto.Content;
import br.com.housecash.backend.model.dto.CreateTransaction;
import br.com.housecash.backend.model.dto.UpdateTransaction;
import br.com.housecash.backend.repository.TransactionRepository;
import br.com.housecash.backend.security.annotation.UserLogged;
import br.com.housecash.backend.service.CashierService;
import br.com.housecash.backend.service.FlatmateService;
import br.com.housecash.backend.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@PreAuthorize("hasAnyRole('USER')")
public class TransactionController {

	@Autowired
	private CashierService cashierService; 

	@Autowired
	private FlatmateService flatmateService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionRepository repository;

	@GetMapping("")
	public List<Transaction> findAll(@ObjDashboard Dashboard dashboard) {
		return transactionService.findAll(dashboard);
	}

	@GetMapping("/group")
	public List<Content<Transaction>> findAllGroupByDate(
			@ObjDashboard Dashboard dashboard,
			@RequestParam Map<String,String> parameters) {
		
		Collection<Transaction> transactions = transactionService.findAll(dashboard, parameters);
		
		Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
				.collect(Collectors.groupingBy(item -> item.getCreatedDate().toLocalDate()));
		
		List<Content<Transaction>> list = new ArrayList<Content<Transaction>>();
		for (Map.Entry<LocalDate,List<Transaction>> entry : groupedByDate.entrySet()) {  
			list.add(new Content<Transaction>(entry.getKey(),entry.getValue()));
		}
		
		return list;
	}

	@GetMapping("/group/paged")
	public List<Content<Transaction>> findAllGroupByDateAndPaged(
			@ObjDashboard Dashboard dashboard, Pageable pageable) {
		
		Page<Transaction> pages = transactionService.findAll(dashboard, pageable);
		
		Map<LocalDate, List<Transaction>> groupedByDate = pages.stream()
				.collect(Collectors.groupingBy(item -> item.getCreatedDate().toLocalDate()));
		
		List<Content<Transaction>> list = new ArrayList<Content<Transaction>>();
		for (Map.Entry<LocalDate,List<Transaction>> entry : groupedByDate.entrySet()) {  
			list.add(new Content<Transaction>(entry.getKey(),entry.getValue()));
		}
		
		return list;
	}

	@GetMapping("/group/paged/assembler")
	public Object findAllGroupDate(
			@UserLogged Flatmate flatmateLogged,
			@ObjDashboard Dashboard dashboard, 
			Pageable pageable, 
			PagedResourcesAssembler<Content<Transaction>> assembler) {
		
		Page<Transaction> pages = transactionService.findAll(dashboard, pageable);
		
		Map<LocalDate, List<Transaction>> groupedByDate = pages.stream()
				.collect(Collectors.groupingBy(item -> item.getCreatedDate().toLocalDate()));
		
		List<Content<Transaction>> list = new ArrayList<Content<Transaction>>();
		for (Map.Entry<LocalDate,List<Transaction>> entry : groupedByDate.entrySet()) {  
			list.add(new Content<Transaction>(entry.getKey(),entry.getValue()));
		}

		Page<Content<Transaction>> result = new PageImpl<Content<Transaction>>(list, pageable, Long.valueOf(list.size()));

		return assembler.toResource(result);

	}

	@GetMapping("/{id}")
	public Transaction findOne(@ObjDashboard Dashboard dashboard, @PathVariable Long id) {
		return transactionService.findById(dashboard, id);
	}

	@PostMapping("/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction createDepoist(
			@ObjDashboard Dashboard dashboard,
			@RequestDTO(CreateTransaction.class) @Valid CreateTransaction content) {
		
		BigDecimal value = content.getValue();
		Long cashierId = content.getCashier();
		Cashier cashier = cashierService.findById(dashboard, cashierId);

		Long flatmateId = content.getAssigned();
		if (StringUtils.isEmpty(flatmateId)) {
			return transactionService.createDeposit(dashboard, cashier, value);
		} else {
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId);
			return transactionService.createDeposit(dashboard, cashier, flatmateAssigned, value);
		}
		
	}

	@PostMapping("/withdraw")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction createWithdraw(
			@ObjDashboard Dashboard dashboard, 
			@RequestDTO(CreateTransaction.class) @Valid CreateTransaction content) {
		
		BigDecimal value = content.getValue();
		Long cashierId = content.getCashier();
		Cashier cashier = cashierService.findById(dashboard, cashierId);

		Long flatmateId = content.getAssigned();
		if (StringUtils.isEmpty(flatmateId)) {
			return transactionService.createwithdraw(dashboard, cashier, value);
		} else {
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId);
			return transactionService.createwithdraw(dashboard, cashier, flatmateAssigned, value);
		}
		
	}

	@PutMapping("/{id}")
	public Transaction update(
			@ObjDashboard Dashboard dashboard,
			@PathVariable Long id, 
			@RequestBody Transaction transaction) {
		return transactionService.update(dashboard, id, transaction);
	}

	@PatchMapping("/{id}")
//	@Transactional // Not necesary, spring.jpa.open-in-view is TRUE by default
	public Transaction patch(
			@ObjDashboard Dashboard dashboard,
			@PathVariable @NotNull Long id,
			@RequestDTO(UpdateTransaction.class) @Valid UpdateTransaction content) {
		
		if(!content.haveChanges()) {
			throw new NoContentException();
		}
		
		if(content.haveFlatmateAssigned()) {
			Long flatmateId = content.getAssigned();
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId);
			transactionService.updateFlatmateAssigned(dashboard, id, flatmateAssigned);
		}
		
		if(content.changeCashier()) {
			Long cashierId = content.getCashier();
			Cashier cashier = cashierService.findById(dashboard, cashierId);
			transactionService.updateCashier(dashboard, id, cashier);
		}
		
		if(content.changeValue()) {
			transactionService.updateValue(dashboard, id, content.getValue());
		}
		
		return transactionService.findById(dashboard, id);

	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
