package br.com.housecash.backend.exception;

import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.model.Transaction.Status;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class InvalidOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Transaction transaction;
	private Status status;
	private Action action;
	
	public InvalidOperationException(Transaction transaction, Status status) {
        super(String.format("Invalid operation, Transaction %s with status equal to %s", transaction.getId(), status));
        this.transaction = transaction;
        this.status = status;
	}
	
	public InvalidOperationException(Transaction transaction, Action action) {
        super(String.format("Invalid operation, Transaction %s with action equal to %s", transaction.getId(), action));
        this.transaction = transaction;
        this.action = action;
	}

}
