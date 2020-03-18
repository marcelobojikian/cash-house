package br.com.housecash.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/transactions/{id}")
@PreAuthorize("hasAnyRole('USER')")
public class TransactionActionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/send")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a sent transaction entity", response = Transaction.class)
	public Transaction send(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		Transaction transaction = transactionService.findById(dashboard, id);
		return transactionService.send(dashboard, transaction);
	}

	@PostMapping("/finish")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a finished transaction entity", response = Transaction.class)
	public Transaction finish(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		Transaction transaction = transactionService.findById(dashboard, id);
		return transactionService.finish(dashboard, transaction);
	}

	@PostMapping("/cancel")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a canceled transaction entity", response = Transaction.class)
	public Transaction cancel(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		Transaction transaction = transactionService.findById(dashboard, id);
		return transactionService.cancel(dashboard, transaction);
	}

	@PostMapping("/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a deleted transaction entity", response = Transaction.class)
	public Transaction delete(@ApiIgnore Dashboard dashboard, @PathVariable Long id) {
		Transaction transaction = transactionService.findById(dashboard, id);
		return transactionService.delete(dashboard, transaction);
	}

}
