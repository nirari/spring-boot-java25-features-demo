package com.example.demo;

import com.example.demo.service.SequencedCollectionService;
import com.example.demo.model.Person;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sequenced")
public class SequencedCollectionsController {

    private final SequencedCollectionService sequencedService;

    public SequencedCollectionsController(SequencedCollectionService sequencedService) {
        this.sequencedService = sequencedService;
    }

    /**
     * GET /sequenced/first
     * Returns the first person in the collection using Java 25's getFirst() method.
     */
    @GetMapping("/first")
    public Optional<Person> getFirst() {
        return sequencedService.getFirstPerson();
    }

    /**
     * GET /sequenced/last
     * Returns the last person in the collection using Java 25's getLast() method.
     */
    @GetMapping("/last")
    public Optional<Person> getLast() {
        return sequencedService.getLastPerson();
    }

    /**
     * GET /sequenced/first/{n}
     * Returns the first N persons from the ordered collection.
     * Uses subList to get a view of the first elements.
     */
    @GetMapping("/first/{n}")
    public List<Person> getFirstN(@PathVariable int n) {
        if (n <= 0) {
            return List.of();
        }
        return sequencedService.getFirstN(n);
    }

    /**
     * GET /sequenced/last/{n}
     * Returns the last N persons from the ordered collection.
     * Uses subList from the end to get a view of the last elements.
     */
    @GetMapping("/last/{n}")
    public List<Person> getLastN(@PathVariable int n) {
        if (n <= 0) {
            return List.of();
        }
        return sequencedService.getLastN(n);
    }

    /**
     * GET /sequenced/reversed
     * Returns all persons in reverse order using Java 25's reversed() method.
     * This returns a reversed view without copying the underlying list.
     */
    @GetMapping("/reversed")
    public List<Person> getReversed() {
        return sequencedService.getReversed();
    }

    /**
     * GET /sequenced/info
     * Returns information about the Java 25 Sequenced Collections API.
     */
    @GetMapping("/info")
    public FeatureInfo getInfo() {
        return new FeatureInfo(
            "Java 25 Sequenced Collections (JEP 451)",
            List.of(
                "List now extends SequencedCollection, providing getFirst(), getLast(), and reversed() methods",
                "These methods unify the API for ordered collections",
                "No more confusion between Deque's getFirst/getLast and LinkedList's peekFirst/peekLast",
                "The reversed() method returns a reversed view, not a copy, for efficiency",
                "Three new interfaces: SequencedCollection, SequencedSet, SequencedMap"
            ),
            List.of(
                "endpoint: 'first' -> returns first person",
                "endpoint: 'last' -> returns last person",
                "endpoint: 'first/{n}' -> returns first N persons",
                "endpoint: 'last/{n}' -> returns last N persons",
                "endpoint: 'reversed' -> returns all persons in reverse order",
                "endpoint: 'info' -> this information"
            )
        );
    }

    /**
     * Record to hold feature information for the /info endpoint
     */
    public record FeatureInfo(
        String title,
        List<String> description,
        List<String> endpoints
    ) {}
}
