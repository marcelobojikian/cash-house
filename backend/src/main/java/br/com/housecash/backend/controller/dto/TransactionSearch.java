package br.com.housecash.backend.controller.dto;

import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import br.com.housecash.backend.model.QTransaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.model.Transaction.Status;

public class TransactionSearch implements QuerydslBinderCustomizer<QTransaction>{

	Action action;

	Status status;

	Long cashier;

	@Override
	public void customize(QuerydslBindings bindings, QTransaction transaction) {
		bindings.bind(transaction.action).first((path, value) -> path.eq(value));
		bindings.bind(transaction.status).first((path, value) -> path.eq(value));
		bindings.bind(transaction.cashier.id).first((path, value) -> path.eq(value));
	}

}
