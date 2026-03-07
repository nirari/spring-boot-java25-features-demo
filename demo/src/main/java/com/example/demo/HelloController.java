package com.example.demo;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final PersonRepository personRepository;

    public HelloController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot with H2 Database!";
    }

    @GetMapping("/users")
    public String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (Person person : personRepository.findAll()) {
            sb.append(person.toString()).append("\n");
        }
        return sb.toString();
    }
}