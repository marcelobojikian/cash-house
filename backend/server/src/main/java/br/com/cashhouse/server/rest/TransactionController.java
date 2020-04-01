package br.com.cashhouse.server.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.exception.NoContentException;
import br.com.cashhouse.server.rest.dto.Content;
import br.com.cashhouse.server.rest.dto.CreateTransaction;
import br.com.cashhouse.server.rest.dto.UpdateTransaction;
import br.com.cashhouse.server.service.CashierService;
import br.com.cashhouse.server.service.FlatmateService;
import br.com.cashhouse.server.service.TransactionService;
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
	public ResponseEntity<?> findAll(
			@ApiIgnore Dashboard dashboard,
			@ApiIgnore @QuerydslPredicate(root = Transaction.class) Predicate predicate, 
			@ApiIgnore Pageable pageable,
			@RequestParam(required = false, defaultValue = "none") String group) {
		
		Page<Transaction> result = transactionService.findAll(dashboard, predicate, pageable);
    	
        long totalElements = result.getTotalElements();
        int numberOfElements = result.getNumberOfElements();
        
        if (result.getContent().isEmpty()) {
	        return new ResponseEntity<Page<Content<Transaction>>>(HttpStatus.NO_CONTENT);
	    }
        
        HttpStatus httpStatus = numberOfElements < totalElements ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
		
		if(group.equals("createdDate")) {
		        
			List<Content<Transaction>> list = groupByCreatedDate(result);
			Page<Content<Transaction>> pageFormated = new PageImpl<Content<Transaction>>(list, pageable, Long.valueOf(list.size()));

            return new ResponseEntity<Page<Content<Transaction>>>(pageFormated, httpStatus);
			
		}else {
			return new ResponseEntity<Page<Transaction>>(result, httpStatus);
		}
		
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
			@RequestBody @Valid CreateTransaction content) {
		
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
			@RequestBody @Valid CreateTransaction content) {
		
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
			@RequestBody @Valid UpdateTransaction content) {
		
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
	
	private List<Content<Transaction>> groupByCreatedDate(Page<Transaction> transaction) {
		Map<LocalDate, List<Transaction>> groupedByDate = transaction.stream()
				.collect(Collectors.groupingBy(item -> item.getCreatedDate().toLocalDate()));
		return apply(groupedByDate);
	}
	
	private <T> List<Content<T>> apply(Map<LocalDate, List<T>> data) {
		List<Content<T>> list = new ArrayList<Content<T>>();
		for (Map.Entry<LocalDate, List<T>> entry : data.entrySet()) {
			list.add(new Content<T>(entry.getKey(), entry.getValue()));
		}
		return list;
	}

}
