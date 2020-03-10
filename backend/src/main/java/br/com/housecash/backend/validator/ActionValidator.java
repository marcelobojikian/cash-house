package br.com.housecash.backend.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.housecash.backend.model.Transaction.Action;
import br.com.housecash.backend.validator.annotation.TransactionAction;

public class ActionValidator implements ConstraintValidator<TransactionAction, Action> {
	
    private Action[] subset;
 
    @Override
    public void initialize(TransactionAction constraint) {
        this.subset = constraint.anyOf();
    }
 
    @Override
    public boolean isValid(Action value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }

}
