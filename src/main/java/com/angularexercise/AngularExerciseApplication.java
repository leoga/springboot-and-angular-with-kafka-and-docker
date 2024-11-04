package com.angularexercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.angularexercise.model.Spaceship;
import com.angularexercise.repository.SpaceshipRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories
@EnableAspectJAutoProxy
public class AngularExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(AngularExerciseApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(SpaceshipRepository repository) {
		return (args) -> {
			// Save a few spaceships
			repository.save(new Spaceship("Enterprise", "Galaxy Class"));
			repository.save(new Spaceship("Voyager", "Intrepid Class"));
			repository.save(new Spaceship("Defiant", "Defiant Class"));
			repository.save(new Spaceship("Enterprise2", "Galaxy Class"));
			repository.save(new Spaceship("Voyager2", "Intrepid Class"));
			repository.save(new Spaceship("Defiant2", "Defiant Class"));
			repository.save(new Spaceship("Enterprise3", "Galaxy Class"));
			repository.save(new Spaceship("Voyager3", "Intrepid Class"));
			repository.save(new Spaceship("Defiant3", "Defiant Class"));
			repository.save(new Spaceship("Enterprise4", "Galaxy Class"));
			repository.save(new Spaceship("Voyager4", "Intrepid Class"));
			repository.save(new Spaceship("Defiant4", "Defiant Class"));
			repository.findAll().forEach(System.out::println);
		};
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
			}
		};
	}

}
