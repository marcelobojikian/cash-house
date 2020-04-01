package br.com.cashhouse.core.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.QTransaction;
import br.com.cashhouse.core.model.Transaction.Status;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, 
											   QuerydslPredicateExecutor<Transaction>,
											   QuerydslBinderCustomizer<QTransaction> {
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions}")
	public Collection<Transaction> findByDashboard(@Param("dashboard") Dashboard dashboard);
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions} AND t.cashier = :cashier")
	public Collection<Transaction> findByDashboardAndCashier(@Param("dashboard") Dashboard dashboard, @Param("cashier") Cashier cashier);
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions} AND ( t.createBy = :createBy OR t.assigned = :assigned )")
	public Collection<Transaction> findByDashboardAndFlatmateRef(@Param("dashboard") Dashboard dashboard, @Param("createBy") Flatmate createBy, @Param("assigned") Flatmate assigned);
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions}")
	public Page<Transaction> findByDashboard(@Param("dashboard") Dashboard dashboard, Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE t.id = :id AND t IN :#{#dashboard.transactions}")
	public Optional<Transaction> findByDashboardAndId(@Param("dashboard") Dashboard dashboard, @Param("id") long id);
	
	default Page<Transaction> findAll(Flatmate flatmateLogged, Dashboard dashboard, Predicate parameters, Pageable pageable) {
		QTransaction transaction = QTransaction.transaction;
		BooleanExpression inDashboard = transaction.in(dashboard.getTransactions());
		
		BooleanExpression loggedUserCreated = transaction.createBy.eq(flatmateLogged);
		BooleanExpression assignedToLoggedUser = transaction.assigned.eq(flatmateLogged)
				.and(transaction.status.eq(Status.CREATED));
		BooleanExpression allSendedOrFinishedOrCanceled = transaction.createBy.ne(flatmateLogged)
				.and(transaction.status.notIn(Status.CREATED, Status.DELETED));
		
		return findAll(
					inDashboard
					.and(
						parameters
					)
					.and(
						loggedUserCreated
						.or(
							allSendedOrFinishedOrCanceled
						)
						.or(
							assignedToLoggedUser
						)
					)
				, pageable);
	}
	
	@Override
	default void customize(QuerydslBindings bindings, QTransaction transaction) {
		bindings.excluding(transaction.id);
	}

}
