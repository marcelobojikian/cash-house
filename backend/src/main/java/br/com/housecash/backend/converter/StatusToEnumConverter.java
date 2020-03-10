package br.com.housecash.backend.converter;

import org.springframework.core.convert.converter.Converter;

import br.com.housecash.backend.model.Transaction.Status;

public class StatusToEnumConverter implements Converter<String, Status> {
	
    @Override
    public Status convert(String source) {
        return Status.valueOf(source.toUpperCase());
    }

}
