package br.com.housecash.backend.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.housecash.backend.model.Transaction.Status;
import br.com.housecash.backend.validator.annotation.TransactionStatus;

public class StatusValidator implements ConstraintValidator<TransactionStatus, Status> {
	
    private Status[] subset;
 
    @Override
    public void initialize(TransactionStatus constraint) {
        this.subset = constraint.anyOf();
    }
 
    @Override
    public boolean isValid(Status value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }

}
