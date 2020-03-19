package br.com.housecash.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter
@EqualsAndHashCode @ToString
public class Dashboard implements Serializable {
 
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @OneToOne(cascade = CascadeType.PERSIST)
    private Flatmate owner;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "DASHBOARD_GUEST", joinColumns = @JoinColumn(name = "ID_DASHBOARD"), inverseJoinColumns = @JoinColumn(name = "ID_FLATMATE"))
	private List<Flatmate> guests = new ArrayList<Flatmate>();
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "DASHBOARD_TRANSACTION", joinColumns = @JoinColumn(name = "ID_DASHBOARD"), inverseJoinColumns = @JoinColumn(name = "ID_TRANSACTION"))
	private List<Transaction> transactions = new ArrayList<Transaction>();

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "DASHBOARD_CASHIER", joinColumns = @JoinColumn(name = "ID_DASHBOARD"), inverseJoinColumns = @JoinColumn(name = "ID_CASHIER"))
	private List<Cashier> cashiers = new ArrayList<Cashier>();
	
	public boolean isOwner(Flatmate flatmate) {
		return this.owner.equals(flatmate);
	}
	
	public boolean isOwner(Long idFlatmate) {
		return this.owner.getId().equals(idFlatmate);
	}

}
