package br.com.cashhouse.server.spring.expression;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import br.com.cashhouse.server.spring.UserDetailsImpl;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    
    private HeaderRequest headerRequest;

    public CustomSecurityExpressionRoot(HeaderRequest headerRequest, Authentication authentication) {
        super(authentication);
        this.headerRequest = headerRequest;
    }

    public boolean isLoggedUser(Long userId) {
        final Flatmate user = ((UserDetailsImpl) this.getPrincipal()).getFlatmate();
        return user.getId().longValue() == userId.longValue();
    }

    public boolean isDashboardOwner() {
    	Dashboard dashboard = headerRequest.getDashboard();
        final Flatmate user = ((UserDetailsImpl) this.getPrincipal()).getFlatmate();
        return dashboard.isOwner(user);
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }

}
