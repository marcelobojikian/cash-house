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

import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.exception.NoContentException;
import br.com.housecash.backend.handler.annotation.RequestDTO;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.dto.Content;
import br.com.housecash.backend.model.dto.CreateTransaction;
import br.com.housecash.backend.model.dto.UpdateTransaction;
import br.com.housecash.backend.security.annotation.UserLogged;
import br.com.housecash.backend.service.CashierService;
import br.com.housecash.backend.service.FlatmateService;
import br.com.housecash.backend.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

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

	@GetMapping("")
	@ApiOperation(value = "Return a list with all transactions", response = Transaction[].class)
	public List<Transaction> findAll(@ApiIgnore Dashboard dashboard) {
		return transactionService.findAll(dashboard);
	}

	@GetMapping("/group")
	public List<Content<Transaction>> findAllGroupByDate(
			@ApiIgnore Dashboard dashboard,
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
			@ApiIgnore Dashboard dashboard, Pageable pageable) {
		
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
			@ApiIgnore Dashboard dashboard, 
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
	@ApiOperation(value = "Return a transaction entity by id", response = Transaction.class)
	public Transaction findOne(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		return transactionService.findById(dashboard, id);
	}

	@PostMapping("/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Returns a created deposit transaction entity", response = Transaction.class)
	public Transaction createDepoist(
			@ApiIgnore Dashboard dashboard,
			@RequestDTO(CreateTransaction.class) @Valid CreateTransaction content) {
		
		BigDecimal value = content.getValue();
		Long cashierId = content.getCashier();
		Cashier cashier = cashierService.findById(dashboard, cashierId);

		Long flatmateId = content.getAssigned();
		if (StringUtils.isEmpty(flatmateId)) {
			return transactionService.createDeposit(dashboard, cashier, value);
		} else {
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, flatmateId));
			return transactionService.createDeposit(dashboard, cashier, flatmateAssigned, value);
		}
		
	}

	@PostMapping("/withdraw")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Returns a created withdraw transaction entity", response = Transaction.class)
	public Transaction createWithdraw(
			@ApiIgnore Dashboard dashboard, 
			@RequestDTO(CreateTransaction.class) @Valid CreateTransaction content) {
		
		BigDecimal value = content.getValue();
		Long cashierId = content.getCashier();
		Cashier cashier = cashierService.findById(dashboard, cashierId);

		Long flatmateId = content.getAssigned();
		if (StringUtils.isEmpty(flatmateId)) {
			return transactionService.createwithdraw(dashboard, cashier, value);
		} else {
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, flatmateId));
			return transactionService.createwithdraw(dashboard, cashier, flatmateAssigned, value);
		}
		
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "Return a transaction entity updated", response = Transaction.class)
	public Transaction update(
			@ApiIgnore Dashboard dashboard,
			@PathVariable Long id, 
			@RequestBody Transaction transaction) {
		return transactionService.update(dashboard, id, transaction);
	}

	@PatchMapping("/{id}")
	@ApiOperation(value = "Return a transaction entity partial updated", response = Transaction.class)
	public Transaction patch(
			@ApiIgnore Dashboard dashboard,
			@PathVariable @NotNull Long id,
			@RequestDTO(UpdateTransaction.class) @Valid UpdateTransaction content) {
		
		if(!content.haveChanges()) {
			throw new NoContentException();
		}
		
		if(content.haveFlatmateAssigned()) {
			Long flatmateId = content.getAssigned();
			Flatmate flatmateAssigned = flatmateService.findById(dashboard, flatmateId).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, flatmateId));
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
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Return status OK when deleted", response = Transaction.class)
	public void detele(
			@ApiIgnore Dashboard dashboard, 
			@PathVariable Long id){
		transactionService.delete(dashboard, id);
	}

}
