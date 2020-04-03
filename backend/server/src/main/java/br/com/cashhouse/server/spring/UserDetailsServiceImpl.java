package br.com.cashhouse.server.spring;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.repository.FlatmateRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private FlatmateRepository flatmateRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		
		Flatmate userInfo = flatmateRepository.findByEmailAndEnabled(username, true);
		
		if (userInfo == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));	
		}
		
		String[] roles = userInfo.getRoles().split(",");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (int i = 0; i < roles.length; i++) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roles[i]));
		}
		
		return new UserDetailsImpl(userInfo, grantedAuthorities);
		
	}

}
