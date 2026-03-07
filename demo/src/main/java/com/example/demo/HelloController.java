package com.example.demo;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final AppUserRepository appUserRepository;

    public HelloController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot with H2 Database!";
    }

    @GetMapping("/users")
    public String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (AppUser appUser : appUserRepository.findAll()) {
            sb.append(appUser.toString()).append("\n");
        }
        return sb.toString();
    }
}