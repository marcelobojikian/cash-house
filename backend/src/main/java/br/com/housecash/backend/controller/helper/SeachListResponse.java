package br.com.housecash.backend.controller.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import br.com.housecash.backend.controller.dto.Content;
import br.com.housecash.backend.model.Transaction;

public class SeachListResponse {
	
	public static List<Content<Transaction>> groupByCreatedDate(Page<Transaction> transaction) {
		Map<LocalDate, List<Transaction>> groupedByDate = transaction.stream()
				.collect(Collectors.groupingBy(item -> item.getCreatedDate().toLocalDate()));
		return apply(groupedByDate);
	}
	
	private static <T> List<Content<T>> apply(Map<LocalDate, List<T>> data) {
		List<Content<T>> list = new ArrayList<Content<T>>();
		for (Map.Entry<LocalDate, List<T>> entry : data.entrySet()) {
			list.add(new Content<T>(entry.getKey(), entry.getValue()));
		}
		return list;
	}

//	private static <T> Stream<T> toStream(Iterable<T> data) {
//		return StreamSupport.stream(data.spliterator(), false);
//	}

}
