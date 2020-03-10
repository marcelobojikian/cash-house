package br.com.housecash.backend.model;

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

@Entity
public class Flatmate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public Flatmate() {
		super();
	}

	public Flatmate(String email, String password) {
		this(email, email, password);
	}

	public Flatmate(String email, String nickname, String password) {
		this();
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.enabled = true;
		this.firstStep = false;
		this.guestStep = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isFirstStep() {
		return firstStep;
	}

	public void setFirstStep(boolean firstStep) {
		this.firstStep = firstStep;
	}

	public boolean isGuestStep() {
		return guestStep;
	}

	public void setGuestStep(boolean guestStep) {
		this.guestStep = guestStep;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + (firstStep ? 1231 : 1237);
		result = prime * result + (guestStep ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flatmate other = (Flatmate) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (enabled != other.enabled)
			return false;
		if (firstStep != other.firstStep)
			return false;
		if (guestStep != other.guestStep)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("UserInfo [id=%s, nickname=%s, password=%s, roles=%s, enabled=%s]", id, nickname, password, roles, enabled);
	}

}
