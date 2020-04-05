package br.com.cashhouse.server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.server.service.TransactionService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/transactions/{id}")
@PreAuthorize("hasAnyRole('USER')")
public class TransactionActionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/send")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a sent transaction entity", response = Transaction.class)
	public Transaction send(@PathVariable Long id) {
		Transaction transaction = transactionService.findById(id);
		return transactionService.send(transaction);
	}

	@PostMapping("/finish")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a finished transaction entity", response = Transaction.class)
	public Transaction finish(@PathVariable Long id) {
		Transaction transaction = transactionService.findById(id);
		return transactionService.finish(transaction);
	}

	@PostMapping("/cancel")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a canceled transaction entity", response = Transaction.class)
	public Transaction cancel(@PathVariable Long id) {
		Transaction transaction = transactionService.findById(id);
		return transactionService.cancel(transaction);
	}

	@PostMapping("/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(value = "Returns a deleted transaction entity", response = Transaction.class)
	public Transaction delete(@PathVariable Long id) {
		Transaction transaction = transactionService.findById(id);
		return transactionService.delete(transaction);
	}

}
