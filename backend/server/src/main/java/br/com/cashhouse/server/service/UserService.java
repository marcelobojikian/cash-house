package br.com.cashhouse.server.service;

import java.util.Collection;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;

public interface UserService {

	public Collection<Dashboard> findInvitations();

	public Flatmate changeNickname(String nickname);

	public Flatmate changePassword(String password);

	public Flatmate finishStepGuest();

	public Flatmate finishStepFirst();

}
