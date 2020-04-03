package br.com.cashhouse.server.service;

import static br.com.cashhouse.server.util.EntityFactory.createCashier;
import static br.com.cashhouse.server.util.EntityFactory.createFlatmate;
import static br.com.cashhouse.server.util.EntityFactory.createTransaction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

import br.com.cashhouse.core.model.Cashier;
import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.Transaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.repository.DashboardRepository;
import br.com.cashhouse.server.exception.EntityNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DashboardServiceTest {
	
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

		assertThat(dashboardExpect.getId(), is(1l));
		assertThat(dashboardExpect.getOwner(), is(flatmate));
		assertThat(dashboardExpect.getCashiers(), empty());
		assertThat(dashboardExpect.getGuests(), empty());
		assertThat(dashboardExpect.getTransactions(), empty());
		
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

		assertThat(dashboardExpect.getId(), is(1l));
		assertThat(dashboardExpect.getOwner(), is(flatmate));
		assertThat(dashboardExpect.getCashiers(), empty());
		assertThat(dashboardExpect.getGuests(), empty());
		assertThat(dashboardExpect.getTransactions(), empty());
		
	}

	@Test
	public void whenFindByMyInvitations_thenReturnDashboardArrayObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
        
		when(dashboardRepository.findByMyInvitations(flatmate)).thenReturn(Collections.emptyList());
		
		Collection<Dashboard> dashboards = dashboardService.findMyInvitations(flatmate);

		assertThat(dashboards, empty());
		
	}
	
	@Test
	public void whenCreate_thenReturnDashboardObject() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		Dashboard dashboardExpect = dashboardService.createDashboard(flatmate);

		assertThat(dashboardExpect.getOwner(), is(flatmate));
		
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

		assertThat(dashboard.getGuests(), hasSize(1));
        assertThat(dashboard.getGuests(), contains(guest2));
		
	}
	
	@Test
	public void whenRemoveCashier_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Cashier energy = createCashier(dashboard, 1l, "Energy", 12.3);
		Cashier garbage = createCashier(dashboard, 2l, "Garbage", 4.2, 56.6);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeCashier(dashboard, garbage);

		assertThat(dashboard.getCashiers(), hasSize(1));
        assertThat(dashboard.getCashiers(), contains(energy));
		
	}
	
	@Test
	public void whenRemoveTransaction_thenReturnVoid() throws Exception {
		
		Flatmate flatmate = createFlatmate(1l, "none", "none");
		Dashboard dashboard = flatmate.getDashboard();

		Transaction withdraw = createTransaction(dashboard, 1l, 2.33, null, Action.WITHDRAW);
		Transaction deposit = createTransaction(dashboard, 2l, 63.3, null, Action.DEPOSIT);

		when(dashboardRepository.save(any(Dashboard.class))).thenReturn(dashboard);
		
		dashboardService.removeTransaction(dashboard, withdraw);

		assertThat(dashboard.getTransactions(), hasSize(1));
        assertThat(dashboard.getTransactions(), contains(deposit));
		
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

		assertThat(dashboard.getTransactions(), hasSize(1));
        assertThat(dashboard.getTransactions(), contains(withdraw));
		
	}

}
