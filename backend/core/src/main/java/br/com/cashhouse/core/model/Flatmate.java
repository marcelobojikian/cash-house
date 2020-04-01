package br.com.cashhouse.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter @RequiredArgsConstructor
public class Flatmate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(unique = true)
	private String email;

	@Column
	private String nickname;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column
	private String password;

	@JsonIgnore
	@Column(name = "roles", length = 50)
	private String roles;

	@Column(name = "enabled")
	private boolean enabled;
	@Column
	private boolean firstStep;
	@Column
	private boolean guestStep;

	@JsonIgnore
	@OneToOne(mappedBy = "owner")
	private Dashboard dashboard;

	public Flatmate(String email, String nickname, String password) {
		super();
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.enabled = true;
		this.firstStep = false;
		this.guestStep = false;
	}

}
