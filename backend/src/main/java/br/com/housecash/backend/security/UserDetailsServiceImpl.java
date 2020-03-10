package br.com.housecash.backend.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.repository.FlatmateRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private FlatmateRepository flatmateRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Flatmate userInfo = flatmateRepository.findByEmailAndEnabled(username, true);
		
		String[] roles = userInfo.getRoles().split(",");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		for (int i = 0; i < roles.length; i++) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roles[i]));
		}
		
		return new CurrentUser(userInfo, grantedAuthorities);
		
	}

}
