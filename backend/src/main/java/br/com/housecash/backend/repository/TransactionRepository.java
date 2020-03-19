package br.com.housecash.backend.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.repository.custom.TransactionRepositoryFilter;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, TransactionRepositoryFilter {
	
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

}
