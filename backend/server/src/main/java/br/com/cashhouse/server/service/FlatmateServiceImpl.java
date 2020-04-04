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
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlatmateServiceImpl implements FlatmateService {

	@Autowired
	private HeaderRequest headerRequest;

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

		if (dashboard.isOwner(id)) {
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
	public Flatmate createGuest(String email, String nickname, String password) {

		Dashboard dashboard = headerRequest.getDashboard();

		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate flatmate = new Flatmate(email, nickname, cryptPassword);
		dashboard.getGuests().add(flatmate);
		flatmate.setRoles("USER");

		log.info(String.format("Creating Flatmate %s", email));

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

		log.info(String.format("Update Flatmate %s", entity.getEmail()));

		return flatmateRepository.save(entity);

	}

	@Override
	public Flatmate update(Long id, String nickname) {

		Flatmate entity = flatmateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		entity.setNickname(nickname);

		log.info(String.format("Update Flatmate %s to nickname %s", entity.getEmail(), nickname));

		return flatmateRepository.save(entity);

	}

	@Override
	public Flatmate update(Long id, String nickname, String password) {

		String cryptPassword = bCryptPasswordEncoder.encode(password);

		Flatmate entity = flatmateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		entity.setNickname(nickname);
		entity.setPassword(cryptPassword);

		log.info(String.format("Update Flatmate %s to nickname %s and password *** ", entity.getEmail(), nickname));

		return flatmateRepository.save(entity);

	}

	@Override
	@Transactional
	public void deleteGuest(long id) {

		Dashboard dashboard = headerRequest.getDashboard();

		Flatmate entity = flatmateRepository.findByDashboardAndId(dashboard, id)
				.orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));

		Collection<Transaction> transactions = transactionService.findByFlatmateReferences(entity, entity);
		
		log.info(String.format("Deleting Flatmate %s to Dashboard %s ", entity.getEmail(), dashboard.getId()));

		dashboardService.removeGuest(dashboard, entity);
		dashboardService.removeTransactions(dashboard, transactions);
		
		log.info("Delete sucess");

	}

}
