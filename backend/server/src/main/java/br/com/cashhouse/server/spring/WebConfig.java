package br.com.cashhouse.server.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.cashhouse.server.converter.ActionToEnumConverter;
import br.com.cashhouse.server.converter.StatusToEnumConverter;
import br.com.cashhouse.server.service.interceptor.HeaderRequest;
import br.com.cashhouse.server.spring.handler.DashboardArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private HeaderRequest dashboardRequestInterceptor;
	
	@Autowired
	private DashboardArgumentResolver dashboardArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dashboardRequestInterceptor);
    }

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(dashboardArgumentResolver);
	}
	
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ActionToEnumConverter());
        registry.addConverter(new StatusToEnumConverter());
    }

}
