package br.com.cashhouse.server.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContentHelper {

	private static final ObjectMapper om = new ObjectMapper();
	
	private Map<String, Object> content;
	
	private ContentHelper() {
		content = new HashMap<String, Object>();
	}
	
	public static ContentHelper generate() {
		return new ContentHelper();
	}
	
	public ContentHelper add(String fieldName, BigDecimal value) {
		content.put(fieldName, value);
		return this;
	}
	
	public ContentHelper add(String fieldName, Integer value) {
		content.put(fieldName, value);
		return this;
	}
	
	public ContentHelper add(String fieldName, Long value) {
		content.put(fieldName, value);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public ContentHelper addParent(String parent, String fieldName, Long value) {
		if(!content.containsKey(parent)) {
			content.put(parent, new HashMap<String, Object>());
		}
		((Map<String, Object>) content.get(parent)).put(fieldName, value);
		return this;
	}
	
	public ContentHelper add(String fieldName, String value) {
		content.put(fieldName, value);
		return this;
	}
	
	public String toJson() throws Exception {
		return om.writeValueAsString(content);
	}

}
