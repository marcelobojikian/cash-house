package br.com.housecash.backend.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
	
	@Query("SELECT d FROM Dashboard d WHERE d.owner = ?1")
	public Dashboard findByOwner(Flatmate flatmate);
	
	@Query("SELECT d FROM Dashboard d WHERE :flatmate MEMBER OF d.guests")
	public Collection<Dashboard> findByMyInvitations(@Param("flatmate") Flatmate flatmate);

}
