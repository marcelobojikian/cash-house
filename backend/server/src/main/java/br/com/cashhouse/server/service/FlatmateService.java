package br.com.cashhouse.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.cashhouse.core.model.Flatmate;

//@PreAuthorize("hasAnyRole('ADMIN')")
public interface FlatmateService {

	public Optional<Flatmate> findById(long id);
	
	public Flatmate findByEmail(String email);
	
	public List<Flatmate> findAll();
	
	public Flatmate create(String email, String nickname, String password);
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	public Flatmate update(long id, Flatmate flatmate);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate update(long id, String nickname);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate update(long id, String nickname, String password);

	public void delete(long id);

}
