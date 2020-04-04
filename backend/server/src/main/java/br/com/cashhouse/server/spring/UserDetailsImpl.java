package br.com.cashhouse.server.spring;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import br.com.cashhouse.core.model.Flatmate;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UserDetailsImpl extends User {

	private static final long serialVersionUID = 1L;

    private final Flatmate flatmate;

	public UserDetailsImpl(Flatmate flatmate, Collection<GrantedAuthority> grantedAuthorities) {
		super(flatmate.getEmail(), flatmate.getPassword(), grantedAuthorities);
		this.flatmate = flatmate;
	}

	public Flatmate getFlatmate() {
		return flatmate;
	}

}
