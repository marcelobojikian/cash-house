package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.repository.FlatmateRepository;
import br.com.cashhouse.server.exception.EntityNotFoundException;
import br.com.cashhouse.server.util.annotation.LoginWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlatmateServiceTest extends ServiceAuthHelper {

	@Autowired
	private FlatmateService flatmateService;

	@MockBean
	private FlatmateRepository flatmateRepository;

	@MockBean
	private DashboardService dashboardService;

	@MockBean
	private TransactionService transactionService;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@TestConfiguration
	static class FlatmateServiceImplTestContextConfiguration {
		@Bean
		public FlatmateService flatmateService() {
			return new FlatmateServiceImpl();
		}
	}

	@LoginWith(id = 1, email = "joao@mail.com", nickname = "Joao A. M.")
	@Test
	public void whenFindByEmail_thenReturnObject() throws Exception {
		
		Flatmate joao = getFlatmateLogged();
		
		when(flatmateRepository.findByEmail("joao@mail.com")).thenReturn(Optional.of(joao));
		
		Flatmate flatmate = flatmateService.findByEmail("joao@mail.com");

		assertThat(flatmate.getEmail(), is("joao@mail.com"));
		assertThat(flatmate.getNickname(), is("Joao A. M."));
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenFindByEmail_thenThrowException() throws Exception {
		
		when(flatmateRepository.findByEmail("other@mail.com")).thenReturn(Optional.empty());
		
		flatmateService.findByEmail("other@mail.com");
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindById_thenReturnGuestObject() throws Exception {
		
		Flatmate logged = getFlatmateLogged();
        Dashboard dashboard = logged.getDashboard();
        
		Flatmate guest = createFlatmate(2l, "joao@mail.com", "Joao A. M.");
		addGuest(guest);

		when(flatmateRepository.findByDashboardAndId(dashboard, 2L)).thenReturn(Optional.of(guest));
		
		Flatmate flatmate = flatmateService.findById(2L).get();

		assertThat(flatmate.getEmail(), is("joao@mail.com"));
		assertThat(flatmate.getNickname(), is("Joao A. M."));
		
	}

	@LoginWith(id = 1, email = "admin@mail.com", nickname = "Administrator")
	@Test
	public void whenFindById_thenReturnAdminObject() throws Exception {
		
		Flatmate logged = getFlatmateLogged();
		
		when(flatmateRepository.findById(1L)).thenReturn(Optional.of(logged));

		Flatmate flatmate = flatmateService.findById(1L).get();

		assertThat(flatmate.getEmail(), is("admin@mail.com"));
		assertThat(flatmate.getNickname(), is("Administrator"));
		
	}

	@LoginWith(id = 1)
	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowException() throws Exception {

		when(flatmateRepository.findById(3L)).thenReturn(Optional.empty());
		
		flatmateService.findById(3L).orElseThrow(() -> new EntityNotFoundException(Flatmate.class, 3L));
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindAll_thenReturnEmpty() throws Exception {

		List<Flatmate> flatmates = flatmateService.findAll();
		assertThat(flatmates, empty());
		
	}

	@LoginWith(id = 1)
	@Test
	public void whenFindAll_thenReturnObjectArray() throws Exception {

		Flatmate guest = createFlatmate(2l, "joao@mail.com", "Joao A. M.");
		addGuest(guest);
		
        List<Flatmate> flatmates = flatmateService.findAll();

        assertThat(flatmates, contains(guest));
		
	}

	@LoginWith(id = 1, email = "lucas@mail.com", nickname = "Lucas")
	@Test
	public void whenCreate_thenReturnObject() throws Exception {

		Flatmate flatmateOwner = getFlatmateLogged();
        Dashboard dashboard = flatmateOwner.getDashboard();
        
		Flatmate newFlatmate = createFlatmate(2l, "new@mail.com", "new", "password", "USER");

		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("password");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(newFlatmate);
        
        Flatmate flatmate = flatmateService.createGuest("new@mail.com", "new", "password");

		assertThat(flatmate.getEmail(), is("new@mail.com"));
		assertThat(flatmate.getNickname(), is("new"));
		assertThat(flatmate.getPassword(), is("password"));
		assertThat(dashboard.getGuests(), hasSize(1));
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenCreate_thenThrowAccessDeniedException() throws Exception {

		Flatmate flatmate = createFlatmate(1l, "admin@mail.com", "Administrator");
		userDashboard(flatmate);
        
        flatmateService.createGuest("mail@mail.com", "invalid", "invalid");
		
	}

	@LoginWith(roles = "ADMIN", id = 2, email = "joao@mail.com", nickname = "Joao A. M.", password = "old-password")
	@Test
	public void whenUpdate_thenReturnObject() throws Exception {

		Flatmate joao = getFlatmateLogged();
		Flatmate newFlatmate = createFlatmate(null, "new@mail.com", "update", "new-password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("new-password");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(newFlatmate);
        
        Flatmate flatmate = flatmateService.update(2L, newFlatmate);

		assertThat(flatmate.getEmail(), is("new@mail.com"));
		assertThat(flatmate.getNickname(), is("update"));
		assertThat(flatmate.getPassword(), is("new-password"));
		
	}

	@LoginWith(roles = "ADMIN")
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdate_thenThrowEntityNotFoundException() throws Exception {

		when(flatmateRepository.findById(3L)).thenReturn(Optional.empty());
        flatmateService.update(3L, new Flatmate());
		
	}

	@LoginWith(id = 2, email = "joao@mail.com", nickname = "Joao A. M.", password = "password")
	@Test
	public void whenUpdateNickname_thenReturnObject() throws Exception {

		Flatmate joao = getFlatmateLogged();
		Flatmate update = createFlatmate(2l, "joao@mail.com", "update", "password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(update);
        
        Flatmate flatmate = flatmateService.update(2L, "update");

		assertThat(flatmate.getEmail(), is("joao@mail.com"));
		assertThat(flatmate.getNickname(), is("update"));
		assertThat(flatmate.getPassword(), is("password"));
		
	}

	@LoginWith(id = 2l)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateNickname_thenThrowAccessDeniedException() throws Exception {
        
        flatmateService.update(3L, "update");
		
	}

	@LoginWith(id = 2l)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateNickname_thenThrowEntityNotFoundException() throws Exception {

		when(flatmateRepository.findById(2L)).thenReturn(Optional.empty());
        
        flatmateService.update(2L, "update");
		
	}

	@LoginWith(id = 2, email = "joao@mail.com", nickname = "Joao A. M.", password = "old-password")
	@Test
	public void whenUpdateNicknamePassword_thenReturnObject() throws Exception {

		Flatmate joao = getFlatmateLogged();
		Flatmate update = createFlatmate(2l, "joao@mail.com", "update", "new-password");

		when(flatmateRepository.findById(2L)).thenReturn(Optional.of(joao));
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("new-password");
		when(flatmateRepository.save(any(Flatmate.class))).thenReturn(update);
        
        Flatmate flatmate = flatmateService.update(2L, "update", "new-password");

		assertThat(flatmate.getEmail(), is("joao@mail.com"));
		assertThat(flatmate.getNickname(), is("update"));
		assertThat(flatmate.getPassword(), is("new-password"));
		
	}

	@LoginWith(id = 2)
	@Test(expected = AccessDeniedException.class)
	public void whenUpdateNicknamePassword_thenThrowAccessDeniedException() throws Exception {
        
        flatmateService.update(3L, "update", "new-password");
		
	}

	@LoginWith(id = 2)
	@Test(expected = EntityNotFoundException.class)
	public void whenUpdateNicknamePassword_thenThrowEntityNotFoundException() throws Exception {

		when(flatmateRepository.findById(2L)).thenReturn(Optional.empty());
        
        flatmateService.update(2L, "update", "new-password");
		
	}

	@LoginWith(id = 2)
	@Test
	public void whenDelete_thenReturnVoid() throws Exception {

		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest = createFlatmate(3l, "guest@mail.com", "Guest", "password");
		addGuest(guest);

		when(flatmateRepository.findByDashboardAndId(dashboard, 3l)).thenReturn(Optional.of(guest));
		when(transactionService.findByFlatmateReferences(any(Flatmate.class), any(Flatmate.class))).thenReturn(new ArrayList<Transaction>());
		doNothing().when(dashboardService).removeGuest(eq(dashboard), any(Flatmate.class));
		doNothing().when(dashboardService).removeTransactions(eq(dashboard), anyCollection());
		
		flatmateService.deleteGuest(3l);
		
		verify(dashboardService, times(1)).removeGuest(eq(dashboard), any(Flatmate.class));
		verify(dashboardService, times(1)).removeTransactions(eq(dashboard), anyCollection());
		
	}

	@LoginWith(id = 2)
	@Test(expected = EntityNotFoundException.class)
	public void whenDelete_thenThrowEntityNotFoundException() throws Exception {
		
		Flatmate flatmate = getFlatmateLogged();
		Dashboard dashboard = flatmate.getDashboard();
		
		when(flatmateRepository.findByDashboardAndId(dashboard, 3l)).thenReturn(Optional.empty());
		
		flatmateService.deleteGuest(3l);
		
	}

	@LoginWith(id = 1)
	@Test(expected = AccessDeniedException.class)
	public void whenDelete_thenThrowAccessDeniedException() throws Exception {
		
		Flatmate flatmate = createFlatmate(2l, "joao@mail.com", "Joao A. M.", "password");
		userDashboard(flatmate);
		
		flatmateService.deleteGuest(1l);
		
	}

}
