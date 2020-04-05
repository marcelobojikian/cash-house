package br.com.cashhouse.server.exception;

import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Status;
import lombok.Getter;

@Getter
public class InvalidOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final Transaction transaction;
	private final Status status;
	
	public InvalidOperationException(Transaction transaction, Status status) {
        super(String.format("Invalid operation, Transaction %s with status equal to %s", transaction.getId(), status));
        this.transaction = transaction;
        this.status = status;
	}

}
