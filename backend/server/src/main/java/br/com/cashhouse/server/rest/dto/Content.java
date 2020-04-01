package br.com.cashhouse.server.rest.dto;

import java.time.LocalDate;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Content<T> {
	
	private LocalDate createdDate;
	
	@JsonProperty("data")
	private Collection<T> data;
	
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
