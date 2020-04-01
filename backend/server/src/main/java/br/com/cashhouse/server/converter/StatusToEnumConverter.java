package br.com.cashhouse.server.converter;

import org.springframework.core.convert.converter.Converter;

import br.com.cashhouse.core.model.Transaction.Status;

public class StatusToEnumConverter implements Converter<String, Status> {
	
    @Override
    public Status convert(String source) {
        return Status.valueOf(source.toUpperCase());
    }

}
