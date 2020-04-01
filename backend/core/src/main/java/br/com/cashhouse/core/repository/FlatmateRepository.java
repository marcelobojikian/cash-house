package br.com.cashhouse.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;

public interface FlatmateRepository extends JpaRepository<Flatmate, Long> {
	
	@Query("SELECT f FROM Flatmate f WHERE f.email = ?1")
	public Optional<Flatmate> findByEmail(String email);
	
	@Query("SELECT f FROM Flatmate f WHERE f.email = ?1 AND f.enabled = ?2")
	public Flatmate findByEmailAndEnabled(String email, boolean enabled);
	
	@Query("SELECT f FROM Flatmate f WHERE f.id = :id AND f IN :#{#dashboard.guests}")
	public Optional<Flatmate> findByDashboardAndId(@Param("dashboard") Dashboard dashboard, @Param("id") long id);

}
