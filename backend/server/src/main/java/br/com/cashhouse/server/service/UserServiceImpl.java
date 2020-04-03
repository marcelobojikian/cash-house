package br.com.cashhouse.server.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.repository.FlatmateRepository;

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
	public Collection<Dashboard> findInvitations() {
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		return dashboardService.findMyInvitations(flatmateLogged);
	}

	@Override
	public Flatmate changeNickname(String nickname) {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setNickname(nickname);

		return flatmateRepository.save(flatmateLogged);

	}

	@Override
	public Flatmate changePassword(String password) {
		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setPassword(cryptPassword);

		return flatmateRepository.save(flatmateLogged);

	}

	@Override
	public Flatmate finishStepGuest() {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setGuestStep(true);

		return flatmateRepository.save(flatmateLogged);
		
	}

	@Override
	public Flatmate finishStepFirst() {

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();
		flatmateLogged.setFirstStep(true);

		return flatmateRepository.save(flatmateLogged);
		
	}

}
