package br.com.housecash.backend;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.repository.DashboardRepository;
import br.com.housecash.backend.repository.FlatmateRepository;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableAutoConfiguration
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

    @Profile("developer")
    @Bean
    CommandLineRunner initDatabase(FlatmateRepository repoFlatmate, DashboardRepository repoDashboard) {
        return args -> {
        	
        	Flatmate joao = new Flatmate("joao@mail.com", new BCryptPasswordEncoder().encode("test"));
        	joao.setNickname("Joao A. M.");

            Dashboard dashboard = new Dashboard();

        	Flatmate admin = new Flatmate("admin@mail.com", new BCryptPasswordEncoder().encode("test"));
        	admin.setNickname("Administrator");
        	admin.setEnabled(true);
        	admin.setRoles("ADMIN");
        	
        	Cashier energy = new Cashier();
        	energy.setName("Energy");
        	energy.setStarted(BigDecimal.valueOf(0.0));
        	energy.setBalance(BigDecimal.valueOf(12.3));
        	
        	Cashier garbage = new Cashier();
        	garbage.setName("Garbage");
        	garbage.setStarted(BigDecimal.valueOf(120.0));
        	garbage.setBalance(BigDecimal.valueOf(34.3));
            
            Transaction first = new Transaction();
            first.setCreateBy(admin);
            first.setAssigned(admin);
            first.setCashier(energy);
            first.setAction(Transaction.Action.DEPOSIT);
            first.setStatus(Status.CREATED);
            first.setValue(BigDecimal.valueOf(12.54));
            
            Transaction second = new Transaction();
            second.setCreateBy(admin);
            second.setAssigned(joao);
            second.setCashier(energy);
            second.setAction(Transaction.Action.WITHDRAW);
            second.setStatus(Status.FINISHED);
            second.setValue(BigDecimal.valueOf(7.19));
        	
            dashboard.setOwner(admin);
            dashboard.setGuests(Arrays.asList(joao));
            dashboard.setCashiers(Arrays.asList(energy, garbage));
            dashboard.setTransactions(Arrays.asList(first, second));

            repoDashboard.save(dashboard);
            
        };
    }

}
