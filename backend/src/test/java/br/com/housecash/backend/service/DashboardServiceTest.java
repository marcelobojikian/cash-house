package br.com.housecash.backend.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.housecash.backend.exception.EntityNotFoundException;
import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.repository.DashboardRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DashboardServiceTest extends ServiceHelper {
	
	@Autowired
	private DashboardService dashboardService;

	@MockBean
	private DashboardRepository dashboardRepository;

	@TestConfiguration
	static class DashboardServiceImplTestContextConfiguration {
		@Bean
		public DashboardService dashboardService() {
			return new DashboardServiceImpl();
		}
	}

	@Test
	public void whenFindById_thenReturnDashboardObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
        
		when(dashboardRepository.findById(1l)).thenReturn(Optional.of(dashboard));
		
		Dashboard dashboardExpect = dashboardService.findById(1L);
		
		assert(dashboardExpect.getId()).equals(1l);
		assert(dashboardExpect.getOwner()).equals(flatmate);
		assert(dashboardExpect.getCashiers()).isEmpty();
		assert(dashboardExpect.getGuests()).isEmpty();
		assert(dashboardExpect.getTransactions()).isEmpty();
		
	}

	@Test(expected = EntityNotFoundException.class)
	public void whenFindById_thenThrowEntityNotFoundException() throws Exception {
		
		when(dashboardRepository.findById(1l)).thenReturn(Optional.empty());
		
		dashboardService.findById(1L);
		
	}

	@Test
	public void whenFindByOwner_thenReturnDashboardObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
        
		when(dashboardRepository.findByOwner(flatmate)).thenReturn(dashboard);
		
		Dashboard dashboardExpect = dashboardService.findByOwner(flatmate);
		
		assert(dashboardExpect.getId()).equals(1l);
		assert(dashboardExpect.getOwner()).equals(flatmate);
		assert(dashboardExpect.getCashiers()).isEmpty();
		assert(dashboardExpect.getGuests()).isEmpty();
		assert(dashboardExpect.getTransactions()).isEmpty();
		
	}

	@Test
	public void whenFindByMyInvitations_thenReturnDashboardArrayObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
        
		when(dashboardRepository.findByMyInvitations(flatmate)).thenReturn(Collections.emptyList());
		
		Collection<Dashboard> dashboards = dashboardService.findMyInvitations(flatmate);
		
		assert(dashboards).isEmpty();
		
	}
	
	@Test
	public void whenCreate_thenReturnDashboardObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		Dashboard dashboardExpect = dashboardService.createDashboard(flatmate);

		assert(dashboardExpect.getOwner()).equals(flatmate);
		
	}
	
	@Test
	public void whenRemoveGuest_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();
		
		Flatmate guest1 = createFlatmate(2l, "guest_01@mail.com", "Guest 1");
		Flatmate guest2 = createFlatmate(2l, "guest_02@mail.com", "Guest 2");

		dashboard.getGuests().add(guest1);
		dashboard.getGuests().add(guest2);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeGuest(dashboard, guest1);

		assertEquals(dashboard.getGuests().size(), 1);
		assert(dashboard.getGuests()).contains(guest2);
		
	}
	
	@Test
	public void whenRemoveCashier_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		Cashier garbage = createCashier(dashboard, 2l, "Garbage", 4.2, 56.6);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeCashier(dashboard, garbage);

		assertEquals(dashboard.getCashiers().size(), 1);
		assert(dashboard.getCashiers()).contains(energy);
		
	}
	
	@Test
	public void whenRemoveTransaction_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Transaction withdraw = createTransaction(dashboard, 1l, 2.33, null, Action.WITHDRAW);
		Transaction deposit = createTransaction(dashboard, 2l, 63.3, null, Action.DEPOSIT);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeTransaction(dashboard, withdraw);

		assertEquals(dashboard.getTransactions().size(), 1);
		assert(dashboard.getTransactions()).contains(deposit);
		
	}
	
	@Test
	public void whenRemoveManyTransactions_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Transaction withdraw = createTransaction(dashboard, 1l, 2.33, null, Action.WITHDRAW);
		Transaction deposit = createTransaction(dashboard, 2l, 63.3, null, Action.DEPOSIT);
		Transaction deposit2 = createTransaction(dashboard, 3l, 1.73, null, Action.DEPOSIT);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeTransactions(dashboard, Arrays.asList(deposit,deposit2));

		assertEquals(dashboard.getTransactions().size(), 1);
		assert(dashboard.getTransactions()).contains(withdraw);
		
	}

}
