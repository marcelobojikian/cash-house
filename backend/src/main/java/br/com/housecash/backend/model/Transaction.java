package br.com.housecash.backend.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@EqualsAndHashCode @ToString
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
	private Long id;

	@OneToOne
    @Setter @Getter
	private Flatmate createBy;

	@OneToOne
    @Setter @Getter
	private Flatmate assigned;

	@OneToOne
    @Setter @Getter
	private Cashier cashier;

	@Column
    @Enumerated(EnumType.STRING)
    @Setter @Getter
	private Status status;
	public static enum Status {
		CREATED, SENDED, FINISHED, CANCELED, DELETED;
	}

	@Column
    @Enumerated(EnumType.STRING)
    @Setter @Getter
	private Action action;
	public static enum Action {
		DEPOSIT, WITHDRAW ;
	}

	@Column
    @Setter @Getter
	private BigDecimal value;

    @Getter
    @Column(name = "created_at")
    private LocalDateTime createdDate;

    @Getter
    @Column(name = "updated_at")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
    }

	@JsonIgnore
    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

	@JsonIgnore
    public boolean isAvailableToChange() {
    	return status == null || status.equals(Status.CREATED);
    }

	@JsonIgnore
    public boolean isCreateBy(Flatmate flatmate) {
    	return createBy.equals(flatmate);
    }

	@JsonIgnore
    public boolean isAssignedTo(Flatmate flatmate) {
    	return assigned.equals(flatmate);
    }

	@JsonIgnore
    public boolean isCreated() {
    	return status != null && status.equals(Status.CREATED);
    }

	@JsonIgnore
    public boolean isSended() {
    	return status != null && status.equals(Status.SENDED);
    }

	@JsonIgnore
    public boolean isFinished() {
    	return status != null && status.equals(Status.FINISHED);
    }

	@JsonIgnore
    public boolean isCanceled() {
    	return status != null && status.equals(Status.CANCELED);
    }

	@JsonIgnore
    public boolean isDeleted() {
    	return status != null && status.equals(Status.DELETED);
    }

}