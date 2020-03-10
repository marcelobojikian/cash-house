package br.com.housecash.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;

public interface CashierRepository extends JpaRepository<Cashier, Long> {
	
	@Query("SELECT c FROM Cashier c WHERE c.id = :id AND c IN :#{#dashboard.cashiers}")
	public Optional<Cashier> findByDashboardAndId(@Param("dashboard") Dashboard dashboard, @Param("id") long id);

}
