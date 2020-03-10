package br.com.housecash.backend.model.dto;

import java.time.LocalDate;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Content<T> {
	
	private LocalDate createdDate;
	
	@JsonProperty("data")
	private Collection<T> transactions;
	
	public int getDay() {
		return createdDate.getDayOfMonth();
	}
	
	public int getMonth() {
		return createdDate.getMonthValue();
	}
	
	public int getYear() {
		return createdDate.getYear();
	}

}
