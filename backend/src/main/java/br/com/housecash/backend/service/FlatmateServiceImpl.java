package br.com.housecash.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.housecash.backend.exception.AccessDeniedException;
import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.repository.FlatmateRepository;
import br.com.housecash.backend.security.service.AuthenticationFacade;

@Service
public class FlatmateServiceImpl implements FlatmateService {

	@Autowired
	private AuthenticationFacade authenticationFacade; 
	
	@Autowired
	private FlatmateRepository flatmateRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public Flatmate findByEmail(String email) {
		return flatmateRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, email));
	}

	@Override
	public Flatmate findById(Dashboard dashboard, long id) {
		
		if(dashboard.getOwner().getId().equals(id)) {
			return dashboard.getOwner();
		}
		
		return flatmateRepository.findByDashboardAndId(dashboard, id).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id));
		
	}

	@Override
	public List<Flatmate> findAll(Dashboard dashboard) {
		return dashboard.getGuests();
	}

	@Override
	public Flatmate create(Dashboard dashboard, String email, String nickname, String password) {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!dashboard.getOwner().equals(flatmateLogged)) {
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
		
		return flatmateRepository.findById(id).map(entity -> {
			
			String cryptPassword = bCryptPasswordEncoder.encode(flatmate.getPassword());

			entity.setEmail(flatmate.getEmail());
			entity.setNickname(flatmate.getNickname());
			entity.setPassword(cryptPassword);
			entity.setRoles(flatmate.getRoles());
			entity.setEnabled(flatmate.getEnabled());
			entity.setFirstStep(flatmate.isFirstStep());
			entity.setGuestStep(flatmate.isGuestStep());
			
			return flatmateRepository.save(entity);
			
		}).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, id) );
		
	}

	@Override
	public Flatmate update(long id, String nickname)  {
		
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!flatmateLogged.getId().equals(id)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		return flatmateRepository.findById(id).map(entity -> {
			
			entity.setNickname(nickname);
			
			return flatmateRepository.save(entity);
			
		}).orElseThrow(() ->  new EntityNotFoundException(Flatmate.class, id) );
		
	}

	@Override
	public Flatmate update(long id, String nickname, String password)  {
	
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!flatmateLogged.getId().equals(id)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		String cryptPassword = bCryptPasswordEncoder.encode(password);
		
		return flatmateRepository.findById(id).map(entity -> {
			
			entity.setNickname(nickname);
			entity.setPassword(cryptPassword);
			
			return flatmateRepository.save(entity);
			
		}).orElseThrow(() ->  new EntityNotFoundException(Flatmate.class, id) );
		
	}

	@Override
	public Flatmate updateGuest(long id, String nickname, String password)  {
	
		Flatmate flatmateLogged = authenticationFacade.getFlatmateLogged();

		if(!flatmateLogged.getId().equals(id)) {
			throw new AccessDeniedException(flatmateLogged);
		}
		
		String cryptPassword = bCryptPasswordEncoder.encode(password);
		
		return flatmateRepository.findById(id).map(entity -> {
			
			entity.setNickname(nickname);
			entity.setPassword(cryptPassword);
			entity.setGuestStep(false);
			
			return flatmateRepository.save(entity);
			
		}).orElseThrow(() ->  new EntityNotFoundException(Flatmate.class, id) );
		
	}

}
