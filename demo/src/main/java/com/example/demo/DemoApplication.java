package com.example.demo;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(PersonRepository repository) {
		return (args) -> {
			// save a few users
			repository.save(new Person("Alice"));
			repository.save(new Person("Bob"));
			repository.save(new Person("Charlie"));

			// fetch all users
			System.out.println("Users found with findAll():");
			System.out.println("--------------------------------");
			for (Person person : repository.findAll()) {
				System.out.println(person);
			}
			System.out.println();
		};
	}

}
