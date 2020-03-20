package br.com.housecash.backend.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.repository.FlatmateRepository;
import br.com.housecash.backend.security.service.AuthenticationFacade;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private AuthenticationFacade authenticationFacade;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private FlatmateRepository flatmateRepository;

	@Override
	public Collection<Dashboard> findInvitations(long id) {
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		return dashboardService.findMyInvitations(flatmateLogged);
	}

	@Override
	public Flatmate changeNickname(long id, String nickname) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setNickname(nickname);

		return flatmateRepository.save(flatmateLogged);

	}

	@Override
	public Flatmate changePassword(long id, String password) {
		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setPassword(cryptPassword);

		return flatmateRepository.save(flatmateLogged);

	}

	@Override
	public Flatmate finishStepGuest(long id) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setGuestStep(true);

		return flatmateRepository.save(flatmateLogged);
		
	}

	@Override
	public Flatmate finishStepFirst(long id) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setFirstStep(true);

		return flatmateRepository.save(flatmateLogged);
		
	}

}
