package br.com.housecash.backend.repository.custom;

import java.util.Collection;
import java.util.Map;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Transaction;

public interface TransactionRepositoryFilter {
	
	public Collection<Transaction> findByParameters(Dashboard dashboard, Map<String,String> parameters);

}
