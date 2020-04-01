package br.com.cashhouse.core.model;

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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter @RequiredArgsConstructor
public class Cashier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column
	private String name;
	@Column
	@NumberFormat(style = Style.CURRENCY)
	private BigDecimal started;
	@Column
	@NumberFormat(style = Style.CURRENCY)
	private BigDecimal balance;

	@OneToOne
	@JoinColumn(name = "OWNER_ID")
	private Flatmate owner;

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
