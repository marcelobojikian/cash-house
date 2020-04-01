package br.com.cashhouse.server.service;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;

public interface UserService {

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Collection<Dashboard> findInvitations(long id);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate changeNickname(long id, String nickname);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate changePassword(long id, String password);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate finishStepGuest(long id);

	@PreAuthorize("#id == authentication.principal.flatmate.id")
	public Flatmate finishStepFirst(long id);

}
