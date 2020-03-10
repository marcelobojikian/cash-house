package br.com.housecash.backend.repository.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.security.service.AuthenticationFacade;

public class TransactionRepositoryFilterImpl implements TransactionRepositoryFilter {

	@Autowired
	private AuthenticationFacade authenticationFacade;
 
    @PersistenceContext
    private EntityManager entityManager;

	@Override
	public Collection<Transaction> findByParameters(Dashboard dashboard, Map<String, String> parameters) {
		
        CriteriaBuilder bulder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = bulder.createQuery(Transaction.class);
        
        Root<Transaction> transactions = query.from(Transaction.class);
        Join<Transaction, Cashier> cashier = transactions.join("cashier");
        
        Subquery<Long> transactionsAllowed = transactionsAllowed(query, bulder, dashboard);
        
        if(parameters.isEmpty()) {
        	query.select(transactions).where(bulder.in(transactions.get("id")).value(transactionsAllowed));
            return entityManager.createQuery(query).getResultList();
        }
        
        Set<Predicate> params = new HashSet<Predicate>();
    	Predicate paramJoin = bulder.in(transactions.get("id")).value(transactionsAllowed);
    	params.add(paramJoin);
    	
    	Predicate transactionByUserLogged = transactionByUserLogged(bulder, transactions);
    	params.add(transactionByUserLogged);
    	
        if(parameters.containsKey("action")) {
        	Action action = Action.valueOf(parameters.get("action").toUpperCase());
        	Predicate p = bulder.equal(transactions.get("action"), action);
        	params.add(p);
        }
        
        if(parameters.containsKey("status")) {
        	Status status = Status.valueOf(parameters.get("status").toUpperCase());
        	Predicate p = bulder.equal(transactions.get("status"), status);
        	params.add(p);
        }
        
        if(parameters.containsKey("cashier")) {
        	Predicate p = bulder.equal(cashier.get("id"), parameters.get("cashier"));
        	params.add(p);
        }
        
    	query.select(transactions).where(params.toArray(new Predicate[0]));
 
        return entityManager.createQuery(query).getResultList();
        
	}
	 
	private Subquery<Long> transactionsAllowed(CriteriaQuery<Transaction> query, CriteriaBuilder bulder, Dashboard dashboard) {

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<Dashboard> subRoot = subQuery.from(Dashboard.class);
		Join<Dashboard, Transaction> join = subRoot.join("transactions");
		
		subQuery.select(join.get("id")).where(bulder.equal(subRoot.get("id"), dashboard.getId()));
		
		return subQuery;
		
	}
	
	private Predicate transactionByUserLogged(CriteriaBuilder bulder, Root<Transaction> transactions ) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

        Join<Transaction, Flatmate> createBy = transactions.join("createBy");
		
		Predicate statusExclusive = transactions.get("status").in(Status.CREATED, Status.DELETED).not();
		Predicate statusExclusiveUserLogged = bulder.equal(createBy.get("id"), flatmateLogged.getId());
		
		Predicate transactionByUserLogged = bulder.or(statusExclusive, statusExclusiveUserLogged);
		
		return transactionByUserLogged;
	}

}
