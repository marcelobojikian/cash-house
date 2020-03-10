package br.com.housecash.backend.converter;

import org.springframework.core.convert.converter.Converter;

import br.com.housecash.backend.model.Transaction.Action;

public class ActionToEnumConverter implements Converter<String, Action> {
	
    @Override
    public Action convert(String source) {
        return Action.valueOf(source.toUpperCase());
    }

}
