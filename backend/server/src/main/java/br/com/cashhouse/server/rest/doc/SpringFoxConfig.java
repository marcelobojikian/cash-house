package br.com.cashhouse.server.rest.doc;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

	@Bean
	public Docket api() {
		// @formatter:off
        return new Docket(DocumentationType.SWAGGER_2)
        		.select()
					.apis(RequestHandlerSelectors.basePackage("br.com.cashhouse.server.rest"))
					.paths(PathSelectors.any())
				.build()
					.apiInfo(metadata())
			        .globalOperationParameters(Arrays.asList(
			        		new ParameterBuilder()
				        		.name("Authorization")
				        		.description("bearer token")
				        		.modelRef(new ModelRef("string"))
				        		.parameterType("header")
				        		.required(true)
			        		.build(),
			        		new ParameterBuilder()
				        		.name("Dashboard")
				        		.description("id")
				        		.modelRef(new ModelRef("integer"))
				        		.parameterType("header")
				        		.required(false)
			        		.build()
	        		));
        // @formatter:on
	}
	
	private ApiInfo metadata() {
		// @formatter:off
		return new ApiInfoBuilder()
				.title("Cash House")
				.description("The project server contains all the services that mobile and web applications can access, such as flatmates, savings cashier and transactions. The system has the option of accessing other accounts in case the user was invited.")
				.version("1.0")
				.license("MIT license")
		        .licenseUrl("https://opensource.org/licenses/MIT")
			.build();
        // @formatter:on
	}

}
