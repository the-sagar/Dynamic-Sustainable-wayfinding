package ie.tcd.cs7cs3.wayfinding.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(ProjectApplication.class);
		appBuilder.properties("spring.config.name:application")
			.build()
			.run(args);
	}

}
