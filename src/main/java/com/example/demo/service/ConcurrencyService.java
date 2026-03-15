package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;
import java.time.Instant;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Service demonstrating Java 25 Virtual Threads (Project Loom).
 * Provides methods to compare virtual threads vs platform threads for concurrent task execution.
 */
@Service
public class ConcurrencyService {

    /**
     * Supported task types for concurrency demos
     */
    public enum TaskType {
        /**
         * I/O-bound task that simulates waiting (sleep)
         */
        IO_BOUND,
        /**
         * CPU-bound task that performs calculations
         */
        CPU_BOUND,
        /**
         * Custom task (no-op)
         */
        CUSTOM
    }

    /**
     * Metrics collected from a concurrency run
     */
    public record ConcurrencyMetrics(
        long elapsedTimeMs,
        double throughput, // tasks per second
        int activeThreads,
        int taskCount,
        int successCount,
        int errorCount,
        Instant timestamp
    ) {}

    /**
     * Result of an individual task execution
     */
    public record TaskResult(
        boolean success,
        long durationMs,
        String error
    ) {}

    /**
     * Response DTO for concurrency endpoints
     */
    public record ConcurrencyResponse(
        ConcurrencyMetrics metrics,
        List<TaskResult> results,
        String threadType,
        String message
    ) {}

    /**
     * Information about virtual threads for educational purposes
     */
    public record ConcurrencyInfo(
        String title,
        String description,
        VirtualThreadDetails virtualThreads,
        PlatformThreadDetails platformThreads,
        List<String> endpoints,
        List<String> usageExamples
    ) {}

    public record VirtualThreadDetails(
        String name,
        String characteristics,
        List<String> benefits,
        List<String> limitations
    ) {}

    public record PlatformThreadDetails(
        String name,
        String characteristics,
        List<String> benefits,
        List<String> limitations
    ) {}

    /**
     * Execute tasks using Java 25 Virtual Threads.
     * Virtual threads are lightweight and enable massive concurrency (1000s of threads).
     *
     * @param count number of concurrent tasks to run
     * @param durationMs how long each task should simulate work (ms)
     * @param type type of work to simulate
     * @return response with metrics and individual task results
     */
    public ConcurrencyResponse executeVirtualThreads(int count, int durationMs, TaskType type) {
        return executeWithExecutor(
            count,
            durationMs,
            type,
            Thread.ofVirtual().name("vt-%d").factory()
        );
    }

    /**
     * Execute tasks using traditional platform threads.
     * Platform threads are OS threads and are limited by OS resources (~1000s max).
     *
     * @param count number of concurrent tasks to run (capped at 1000)
     * @param durationMs how long each task should simulate work (ms)
     * @param type type of work to simulate
     * @return response with metrics and individual task results
     */
    public ConcurrencyResponse executePlatformThreads(int count, int durationMs, TaskType type) {
        // Cap platform threads to avoid overwhelming the system
        int cappedCount = Math.min(count, 1000);

        ThreadFactory platformThreadFactory = Thread.ofPlatform()
            .name("pt-%d")
            .factory();

        return executeWithExecutor(
            cappedCount,
            durationMs,
            type,
            platformThreadFactory
        );
    }

    /**
     * Benchmark comparing virtual threads vs platform threads.
     * Executes the same workload with both thread types and returns comparative metrics.
     *
     * @param count number of concurrent tasks
     * @param durationMs duration of each simulated task
     * @return response with metrics for both thread types
     */
    public record BenchmarkResponse(
        ConcurrencyResponse virtual,
        ConcurrencyResponse platform,
        String comparison
    ) {}

    public BenchmarkResponse benchmark(int count, int durationMs) {
        ConcurrencyResponse virtualResponse = executeVirtualThreads(count, durationMs, TaskType.IO_BOUND);
        ConcurrencyResponse platformResponse = executePlatformThreads(count, durationMs, TaskType.IO_BOUND);

        String comparison = String.format(
            "Virtual threads: %.2f tasks/sec, Platform threads: %.2f tasks/sec. " +
            "Virtual threads achieved %.2fx throughput improvement.",
            virtualResponse.metrics().throughput(),
            platformResponse.metrics().throughput(),
            virtualResponse.metrics().throughput() / Math.max(platformResponse.metrics().throughput(), 0.001)
        );

        return new BenchmarkResponse(virtualResponse, platformResponse, comparison);
    }

    /**
     * Execute a pipeline demo where each item is processed through multiple stages.
     * Each item gets its own virtual thread that runs all stages sequentially.
     * This demonstrates that even with many concurrent items, virtual threads remain lightweight.
     *
     * @param stages number of pipeline stages (each simulated with sleep)
     * @param items number of items to process through the pipeline
     * @return response with metrics
     */
    public ConcurrencyResponse executePipeline(int stagesParam, int itemsParam) {
        // Create final copies for lambda capture
        final int stages = (stagesParam < 1) ? 3 : stagesParam;
        final int items = (itemsParam < 1) ? 100 : itemsParam;

        Instant startTime = Instant.now();
        List<TaskResult> results = new ArrayList<>(items);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<TaskResult>> futures = new ArrayList<>(items);

            for (int i = 0; i < items; i++) {
                Future<TaskResult> future = executor.submit(() -> {
                    Instant taskStart = Instant.now();
                    try {
                        // Simulate multiple pipeline stages
                        for (int stage = 1; stage <= stages; stage++) {
                            // Each stage takes 100ms
                            Thread.sleep(100);
                        }
                        long duration = Duration.between(taskStart, Instant.now()).toMillis();
                        return new TaskResult(true, duration, null);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        long duration = Duration.between(taskStart, Instant.now()).toMillis();
                        return new TaskResult(false, duration, "Interrupted: " + e.getMessage());
                    }
                });
                futures.add(future);
            }

            // Collect results
            for (Future<TaskResult> future : futures) {
                try {
                    TaskResult result = future.get(30, TimeUnit.SECONDS);
                    results.add(result);
                } catch (Exception e) {
                    results.add(new TaskResult(false, 0, "Pipeline error: " + e.getMessage()));
                }
            }
        } catch (Exception e) {
            return new ConcurrencyResponse(
                new ConcurrencyMetrics(0, 0, 0, items, 0, items, Instant.now()),
                results,
                "VirtualThreadPipeline",
                "Pipeline executor failed: " + e.getMessage()
            );
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        long elapsedMs = elapsed.toMillis();
        long successCount = results.stream().mapToInt(r -> r.success() ? 1 : 0).sum();
        double throughput = (items * 1000.0) / elapsedMs;

        ConcurrencyMetrics metrics = new ConcurrencyMetrics(
            elapsedMs,
            throughput,
            getActiveThreadCount(),
            items,
            (int) successCount,
            items - (int) successCount,
            Instant.now()
        );

        String message = String.format(
            "Pipeline: %d items through %d stages using virtual threads. " +
            "Completed in %d ms, throughput: %.2f items/sec, successes: %d",
            items, stages, elapsedMs, throughput, successCount
        );

        return new ConcurrencyResponse(metrics, results, "VirtualThreadPipeline", message);
    }

    /**
     * Get educational information about concurrency in Java.
     */
    public ConcurrencyInfo getInfo() {
        return new ConcurrencyInfo(
            "Java 25: Virtual Threads (Project Loom)",
            "Virtual threads are lightweight threads that reduce the effort of writing, debugging, and maintaining concurrent applications. " +
            "They are implemented by the Java runtime, not the OS, allowing you to run millions of concurrent tasks efficiently.",
            new VirtualThreadDetails(
                "Virtual Threads (java.lang.VirtualThread)",
                "Lightweight threads managed by JVM, not OS. Very cheap to create and block.",
                List.of(
                    "Can create 1000s-10000s of concurrent threads easily",
                    "Low context-switching overhead",
                    "Ideal for I/O-bound applications (servers, APIs)",
                    "Maintains same programming model as platform threads",
                    "Minimal memory footprint (~2KB per thread stack)"
                ),
                List.of(
                    "Slight overhead for CPU-bound tasks (pinning to carrier thread)",
                    "Debugging can be harder due to many threads",
                    "Thread dumps are larger with many virtual threads"
                )
            ),
            new PlatformThreadDetails(
                "Platform Threads (java.lang.Thread)",
                "Traditional OS threads. One-to-one mapping with OS kernel threads.",
                List.of(
                    "Good for CPU-bound tasks",
                    "Direct OS scheduling and control",
                    "Mature tooling and debugging support",
                    "Stable performance characteristics"
                ),
                List.of(
                    "Expensive to create (1-2MB stack each)",
                    "Limited to ~1000-2000 concurrent threads",
                    "High context-switching overhead",
                    "Blocking I/O wastes threads"
                )
            ),
            List.of(
                "POST /threads/virtual - Execute N concurrent tasks with virtual threads",
                "POST /threads/platform - Execute N concurrent tasks with platform threads (max 1000)",
                "GET /threads/info - This information",
                "GET /threads/benchmark?count=N&durationMs=D - Compare both approaches",
                "POST /threads/pipeline - Demo virtual thread per request pattern"
            ),
            List.of(
                "curl -X POST http://localhost:8080/threads/virtual -H 'Content-Type: application/json' -d '{\"count\": 10000, \"durationMs\": 500, \"type\": \"IO_BOUND\"}'",
                "curl -X POST http://localhost:8080/threads/platform -H 'Content-Type: application/json' -d '{\"count\": 100, \"durationMs\": 500, \"type\": \"IO_BOUND\"}'",
                "curl 'http://localhost:8080/threads/benchmark?count=5000&durationMs=100'"
            )
        );
    }

    // ========== Private Helper Methods ==========

    private record TaskRequest(
        int index,
        int durationMs,
        TaskType type
    ) {}

    private ConcurrencyResponse executeWithExecutor(
        int count,
        int durationMs,
        TaskType type,
        ThreadFactory threadFactory
    ) {
        Instant startTime = Instant.now();
        List<TaskResult> results = new ArrayList<>(count);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(threadFactory)) {
            List<Future<TaskResult>> futures = new ArrayList<>(count);

            // Submit all tasks
            for (int i = 0; i < count; i++) {
                TaskRequest request = new TaskRequest(i, durationMs, type);
                Future<TaskResult> future = executor.submit(() -> executeTask(request));
                futures.add(future);
            }

            // Collect results
            for (Future<TaskResult> future : futures) {
                try {
                    TaskResult result = future.get(5, TimeUnit.SECONDS);
                    results.add(result);
                    if (result.success()) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    results.add(new TaskResult(false, 0, "Future timeout/error: " + e.getMessage()));
                    errorCount.incrementAndGet();
                }
            }
        } catch (Exception e) {
            // If executor fails, return error response with what we have
            return new ConcurrencyResponse(
                new ConcurrencyMetrics(0, 0, 0, count, 0, count, Instant.now()),
                results,
                threadFactory.getClass().getSimpleName(),
                "Executor failed: " + e.getMessage()
            );
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        long elapsedMs = elapsed.toMillis();
        double throughput = elapsedMs > 0 ? (count * 1000.0) / elapsedMs : 0;

        int activeThreads = getActiveThreadCount();

        ConcurrencyMetrics metrics = new ConcurrencyMetrics(
            elapsedMs,
            throughput,
            activeThreads,
            count,
            successCount.get(),
            errorCount.get(),
            Instant.now()
        );

        String message = String.format(
            "Completed %d tasks in %d ms (%.2f tasks/sec). Success: %d, Errors: %d",
            count, elapsedMs, throughput, successCount.get(), errorCount.get()
        );

        String threadTypeName = threadFactory.getClass().getSimpleName();

        return new ConcurrencyResponse(metrics, results, threadTypeName, message);
    }

    private TaskResult executeTask(TaskRequest request) {
        Instant taskStart = Instant.now();

        try {
            switch (request.type()) {
                case IO_BOUND -> {
                    // Simulate I/O wait (database query, HTTP call, file read)
                    Thread.sleep(request.durationMs());
                }
                case CPU_BOUND -> {
                    // Simulate CPU-intensive calculation
                    long result = 0;
                    long iterations = request.durationMs() * 1000L;
                    for (long i = 0; i < iterations; i++) {
                        result += i * i;
                    }
                }
                case CUSTOM -> {
                    // Minimal work
                    Thread.sleep(10);
                }
            }

            long duration = Duration.between(taskStart, Instant.now()).toMillis();
            return new TaskResult(true, duration, null);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long duration = Duration.between(taskStart, Instant.now()).toMillis();
            return new TaskResult(false, duration, "Interrupted: " + e.getMessage());
        } catch (Exception e) {
            long duration = Duration.between(taskStart, Instant.now()).toMillis();
            return new TaskResult(false, duration, "Error: " + e.getMessage());
        }
    }

    private int getActiveThreadCount() {
        // Get current thread count - this is approximate
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        return threadBean.getThreadCount();
    }
}
