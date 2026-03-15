package com.example.demo.service;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SequencedCollectionService {

    private final PersonRepository personRepository;

    public SequencedCollectionService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Get the first person in the collection using Java 25's SequencedCollection.getFirst()
     */
    public Optional<Person> getFirstPerson() {
        List<Person> people = personRepository.findAll();
        if (people.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(people.getFirst());
    }

    /**
     * Get the last person in the collection using Java 25's SequencedCollection.getLast()
     */
    public Optional<Person> getLastPerson() {
        List<Person> people = personRepository.findAll();
        if (people.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(people.getLast());
    }

    /**
     * Get the first N persons from the collection using subList with Java 25'sSequencedCollection
     */
    public List<Person> getFirstN(int n) {
        List<Person> people = personRepository.findAll();
        if (people.isEmpty()) {
            return List.of();
        }
        int end = Math.min(n, people.size());
        return people.subList(0, end);
    }

    /**
     * Get the last N persons from the collection using subList with Java 25's SequencedCollection
     */
    public List<Person> getLastN(int n) {
        List<Person> people = personRepository.findAll();
        if (people.isEmpty()) {
            return List.of();
        }
        int start = Math.max(0, people.size() - n);
        return people.subList(start, people.size());
    }

    /**
     * Get all persons in reverse order using Java 25's SequencedCollection.reversed()
     * This returns a reversed view without copying the underlying list.
     */
    public List<Person> getReversed() {
        List<Person> people = personRepository.findAll();
        return people.reversed();
    }

    /**
     * Get statistics about the ordered collection
     */
    public CollectionStats getStats() {
        List<Person> people = personRepository.findAll();
        if (people.isEmpty()) {
            return new CollectionStats(0, 0, null, null);
        }
        return new CollectionStats(
            people.size(),
            people.size(),
            people.getFirst(),
            people.getLast()
        );
    }

    /**
     * Record to hold statistics about the collection
     */
    public record CollectionStats(
        int totalCount,
        int effectiveSize,
        Person first,
        Person last
    ) {}
}
