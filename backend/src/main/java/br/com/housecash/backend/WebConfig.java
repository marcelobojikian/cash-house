package br.com.housecash.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.housecash.backend.converter.ActionToEnumConverter;
import br.com.housecash.backend.converter.StatusToEnumConverter;
import br.com.housecash.backend.handler.DashboardArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private DashboardArgumentResolver dashboardArgumentResolver;

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
