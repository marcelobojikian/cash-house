package br.com.cashhouse.server.rest.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntityFlatmate {

    @NotNull
    @NotEmpty
	private String email;

    @NotNull
    @NotEmpty
	private String nickname;

    @NotNull
    @NotEmpty
	private String password;

    @NotNull
    @NotEmpty
	private String roles;

    @NotNull
	private boolean enabled;
    
    @NotNull
	private boolean firstStep;
    
    @NotNull
	private boolean guestStep;

    @NotNull
	private Long dashboard;
    
    public Flatmate toEntity(){
    	
    	Dashboard dashboardEntity = new Dashboard();
    	dashboardEntity.setId(dashboard);
    	
    	Flatmate entity = new Flatmate(email, nickname, password);
    	entity.setRoles(roles);
    	entity.setEnabled(enabled);
    	entity.setFirstStep(firstStep);
    	entity.setGuestStep(guestStep);
    	entity.setDashboard(dashboardEntity);
    	
    	return entity;
    	
    }

}
