package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.repository.FlatmateRepository;
import br.com.cashhouse.server.exception.AccessDeniedException;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.util.security.LoginWithAdmin;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlatmateServiceTest {

	@TestConfiguration
	static class FlatmateServiceImplTestContextConfiguration {
		@Bean
		public FlatmateService flatmateService() {
			return new FlatmateServiceImpl();
		}
	}

	@Autowired
	private FlatmateService flatmateService;

	@MockBean
	private AuthenticationFacade authenticationFacade;

	@MockBean
	private FlatmateRepository flatmateRepository;

	@MockBean
	private DashboardService dashboardService;

	@MockBean
	private TransactionService transactionService;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	public void whenFindByEmail_thenReturnObject() throws Exception {
		
		Flatmate joao = createFlatmate(1l, "joao@mail.com", "Joao A. M.");
		
		when(flatmateRepository.findByEmail("joao@mail.com")).thenReturn(Optional.of(joao));
		
		Flatmate flatmate = flatmateService.findByEmail("joao@mail.com");
		
		assert(flatmate.getEmail()).equals("joao@mail.com");
		assert(flatmate.getNickname()).equals("Joao A. M.");
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenFindByEmail_thenThrowException() throws Exception {
		
		when(flatmateRepository.findByEmail("other@mail.com")).thenReturn(Optional.empty());
		
		flatmateService.findByEmail("other@mail.com");
		
	}

	@Test
	public void whenFindById_thenReturnGuestObject() throws Exception {
		
		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");
		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.");

        Dashboard dashboard = admin.getDashboard();
        dashboard.setGuests(Arrays.asList(joao));
        
		when(flatmateRepository.findByDashboardAndId(dashboard, 2L)).thenReturn(Optional.of(joao));
		
		Flatmate flatmate = flatmateService.findById(dashboard, 2L).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, 2L));
		
		assert(flatmate.getEmail()).equals("joao@mail.com");
		assert(flatmate.getNickname()).equals("Joao A. M.");
		
	}

	@Test
	public void whenFindById_thenReturnAdminObject() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");

        Dashboard dashboard = admin.getDashboard();
		
		Flatmate flatmate = flatmateService.findById(dashboard, 1L).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, 1L));
		
		assert(flatmate.getEmail()).equals("admin@mail.com");
		assert(flatmate.getNickname()).equals("Administrator");
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowException() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");

        Dashboard dashboard = admin.getDashboard();
		
		when(flatmateRepository.findById(3L)).thenReturn(Optional.empty());
		
		flatmateService.findById(dashboard, 3L).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, 3L));
		
	}

	@Test
	public void whenFindAll_thenReturnEmpty() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");

        Dashboard dashboard = admin.getDashboard();
		
		List<Flatmate> flatmates = flatmateService.findAll(dashboard);
		
		assert(flatmates).isEmpty();
		
	}

	@Test
	public void whenFindAll_thenReturnObjectArray() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");
		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.");

        Dashboard dashboard = admin.getDashboard();
        dashboard.setGuests(Arrays.asList(joao));
		
        List<Flatmate> flatmates = flatmateService.findAll(dashboard);
		
		assert(flatmates).contains(joao);
		
	}

	@Test
	public void whenCreate_thenReturnObject() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");
		Flatmate newFlatmate = createFlatmate(2l, "new@mail.com", "new", "password", "USER");

        Dashboard dashboard = admin.getDashboard();

		when(authenticationFacade.getFlatmateLogged()).thenReturn(admin);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("password");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(newFlatmate);
        
        Flatmate flatmate = flatmateService.create(dashboard, "new@mail.com", "new", "password");

		assert(flatmate.getEmail()).equals("new@mail.com");
		assert(flatmate.getNickname()).equals("new");
		assert(flatmate.getPassword()).equals("password");
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenCreate_thenThrowAccessDeniedException() throws Exception {

		Flatmate admin = createFlatmate(1l, "admin@mail.com", "Administrator");
		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.");

        Dashboard dashboard = admin.getDashboard();
        dashboard.setGuests(Arrays.asList(joao));

		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
        
        flatmateService.create(dashboard, "mail@mail.com", "invalid", "invalid");
		
	}

	@Test
	@LoginWithAdmin
	public void whenUpdate_thenReturnObject() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "old-password");
		Flatmate newFlatmate = createFlatmate(null, "new@mail.com", "update", "new-password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("new-password");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(newFlatmate);
        
        Flatmate flatmate = flatmateService.update(2L, newFlatmate);

		assert(flatmate.getEmail()).equals("new@mail.com");
		assert(flatmate.getNickname()).equals("update");
		assert(flatmate.getPassword()).equals("new-password");
		
	}

	@Test(expected = EntityNotFoundException.class)
	@LoginWithAdmin
	public void whenUpdate_thenThrowEntityNotFoundException() throws Exception {

		Flatmate newFlatmate = createFlatmate(null, "new@mail.com", "update", "new-password");

		when(flatmateRepository.findById(3L)).thenReturn(Optional.empty());
        
        flatmateService.update(3L, newFlatmate);
		
	}

	@Test
	public void whenUpdateNickname_thenReturnObject() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		Flatmate update = createFlatmate(2l, "joao@mail.com", "update", "password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(update);
        
        Flatmate flatmate = flatmateService.update(2L, "update");

		assert(flatmate.getEmail()).equals("joao@mail.com");
		assert(flatmate.getNickname()).equals("update");
		assert(flatmate.getPassword()).equals("password");
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenUpdateNickname_thenThrowAccessDeniedException() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
        
        flatmateService.update(3L, "update");
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateNickname_thenThrowEntityNotFoundException() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.empty());
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
        
        flatmateService.update(2L, "update");
		
	}
	
	@Test
	public void whenUpdateNicknamePassword_thenReturnObject() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "old-password");
		Flatmate update = createFlatmate(2l, "joao@mail.com", "update", "new-password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("new-password");
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(update);
        
        Flatmate flatmate = flatmateService.update(2L, "update", "new-password");

		assert(flatmate.getEmail()).equals("joao@mail.com");
		assert(flatmate.getNickname()).equals("update");
		assert(flatmate.getPassword()).equals("new-password");
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenUpdateNicknamePassword_thenThrowAccessDeniedException() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
        
        flatmateService.update(3L, "update", "new-password");
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateNicknamePassword_thenThrowEntityNotFoundException() throws Exception {

		Flatmate joao = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.empty());
		when(authenticationFacade.getFlatmateLogged()).thenReturn(joao);
        
        flatmateService.update(2L, "update", "new-password");
		
	}
	
	@Test
	public void whenDelete_thenReturnVoid() throws Exception {

		Flatmate flatmate = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		Dashboard dashboard = flatmate.getDashboard();

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.of(flatmate));
		when(transactionService.findByFlatmateReferences(eq(dashboard), any(Flatmate.class), any(Flatmate.class))).thenReturn(new ArrayList<Transaction>());
		doNothing().when(dashboardService).removeGuest(eq(dashboard), any(Flatmate.class));
		doNothing().when(dashboardService).removeTransactions(eq(dashboard), anyCollection());
		
		flatmateService.delete(dashboard, 1l);
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenDelete_thenThrowEntityNotFoundException() throws Exception {

		Flatmate flatmate = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		Dashboard dashboard = flatmate.getDashboard();

		when(authenticationFacade.getFlatmateLogged()).thenReturn(flatmate);
		when(flatmateRepository.findByDashboardAndId(dashboard, 1l)).thenReturn(Optional.empty());
		
		flatmateService.delete(dashboard, 1l);
		
	}

	@Test(expected = AccessDeniedException.class)
	public void whenDelete_thenThrowAccessDeniedException() throws Exception {

		Flatmate flatmate = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		Dashboard dashboard = flatmate.getDashboard();

		Flatmate notDashboarOwner = createFlatmate(3l, "not owner", "not owner");

		when(authenticationFacade.getFlatmateLogged()).thenReturn(notDashboarOwner);
		
		flatmateService.delete(dashboard, 1l);
		
	}

}
