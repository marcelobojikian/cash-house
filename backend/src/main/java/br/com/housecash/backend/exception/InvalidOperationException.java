package br.com.housecash.backend.exception;

import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Status;
import lombok.Getter;

@Getter
public class InvalidOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Transaction transaction;
	private Status status;
	
	public InvalidOperationException(Transaction transaction, Status status) {
        super(String.format("Invalid operation, Transaction %s with status equal to %s", transaction.getId(), status));
        this.transaction = transaction;
        this.status = status;
	}

}
