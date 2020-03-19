package br.com.housecash.backend.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter @Getter
@EqualsAndHashCode @ToString
public class Cashier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String name;
	@Column
	@NumberFormat(style=Style.CURRENCY)
	private BigDecimal started;
	@Column
	@NumberFormat(style=Style.CURRENCY)
	private BigDecimal balance;
	
    @OneToOne
    @JoinColumn(name = "OWNER_ID")
	@JsonProperty(access = Access.WRITE_ONLY)
    private Flatmate owner;

	public Cashier() {
		super();
	}

	public Cashier(String name, BigDecimal started, BigDecimal balance) {
		super();
		this.name = name;
		this.started = started;
		this.balance = balance;
	}
	
	public void deposit(BigDecimal value) {
		this.balance = this.balance.add(value);
	}
	
	public void withdraw(BigDecimal value) {
		this.balance = this.balance.subtract(value);
	}

}
