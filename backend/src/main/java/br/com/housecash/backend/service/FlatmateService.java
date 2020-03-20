package br.com.housecash.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;

//@PreAuthorize("hasAnyRole('ADMIN')")
public interface FlatmateService {

	public Optional<Flatmate> findById(Dashboard dashboard, long id);
	
	public Flatmate findByEmail(String email);
	
	public List<Flatmate> findAll(Dashboard dashboard);
	
	public Flatmate create(Dashboard dashboard, String email, String nickname, String password);
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	public Flatmate update(long id, Flatmate flatmate);
	
	public Flatmate update(long id, String nickname);
	
	public Flatmate update(long id, String nickname, String password);

	public void delete(Dashboard dashboard, long id);

}
