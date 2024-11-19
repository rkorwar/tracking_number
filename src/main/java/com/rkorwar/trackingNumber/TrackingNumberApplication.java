package com.rkorwar.trackingNumber;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Tracking Number API",
				description = "API for generating and managing tracking numbers",
				version = "v1",
				contact = @Contact(
						name = "Rakesh Korwar",
						email = "********@gmail.com",
						url = "https://www.*****.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "API for generating and managing tracking numbers"
		)
)
public class TrackingNumberApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingNumberApplication.class, args);
	}

}
