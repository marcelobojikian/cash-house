package br.com.housecash.backend.security;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import br.com.housecash.backend.model.Flatmate;

public class CurrentUser extends User {

	private static final long serialVersionUID = 1L;

    private final Flatmate flatmate;

	public CurrentUser(Flatmate flatmate, Set<GrantedAuthority> grantedAuthorities) {
		super(flatmate.getEmail(), flatmate.getPassword(), grantedAuthorities);
		this.flatmate = flatmate;
	}

	public Flatmate getFlatmate() {
		return flatmate;
	}

}
