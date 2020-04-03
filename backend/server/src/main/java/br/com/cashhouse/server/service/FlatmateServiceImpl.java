package br.com.cashhouse.server.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.repository.FlatmateRepository;
import br.com.cashhouse.server.exception.AccessDeniedException;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;

@Service
public class FlatmateServiceImpl implements FlatmateService {

	@Autowired
	private HeaderRequest headerRequest;

	@Autowired
	private AuthenticationFacade authenticationFacade;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private FlatmateRepository flatmateRepository;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private TransactionService transactionService;

	@Override
	public Flatmate findByEmail(String email) {
		return flatmateRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, email));
	}

	@Override
	public Optional<Flatmate> findById(long id) {

		Dashboard dashboard = headerRequest.getDashboard();

		if (dashboard.getOwner().getId().equals(id)) {
			return Optional.of(dashboard.getOwner());
		}

		return flatmateRepository.findByDashboardAndId(dashboard, id);

	}

	@Override
	public List<Flatmate> findAll() {
		Dashboard dashboard = headerRequest.getDashboard();
		return dashboard.getGuests();
	}

	@Override
	public Flatmate create(String email, String nickname, String password) {

		Dashboard dashboard = headerRequest.getDashboard();

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.getOwner().equals(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate flatmate = new Flatmate(email, nickname, cryptPassword);
		dashboard.getGuests().add(flatmate);
		flatmate.setRoles("USER");

		return flatmateRepository.save(flatmate);

	}

	@Override
	public Flatmate update(long id, Flatmate flatmate) {

		Flatmate entity = flatmateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		String cryptPassword = bCryptPasswordEncoder.encode(flatmate.getPassword());

		entity.setEmail(flatmate.getEmail());
		entity.setNickname(flatmate.getNickname());
		entity.setPassword(cryptPassword);
		entity.setRoles(flatmate.getRoles());
		entity.setEnabled(flatmate.isEnabled());
		entity.setFirstStep(flatmate.isFirstStep());
		entity.setGuestStep(flatmate.isGuestStep());

		return flatmateRepository.save(entity);

	}

	@Override
	public Flatmate update(long id, String nickname) {

		Flatmate entity = flatmateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		entity.setNickname(nickname);

		return flatmateRepository.save(entity);

	}

	@Override
	public Flatmate update(long id, String nickname, String password) {

		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate entity = flatmateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		entity.setNickname(nickname);
		entity.setPassword(cryptPassword);

		return flatmateRepository.save(entity);

	}

	@Override
	@Transactional
	public void delete(long id) {

		Dashboard dashboard = headerRequest.getDashboard();

		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if (!dashboard.isOwner(flatmateLogged)) {
			throw new AccessDeniedException(flatmateLogged);
		}

		Flatmate entity = flatmateRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		Collection<Transaction> transactions = transactionService.findByFlatmateReferences(dashboard, entity, entity);

		dashboardService.removeGuest(dashboard, entity);
		dashboardService.removeTransactions(dashboard, transactions);

	}

}
