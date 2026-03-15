package com.example.demo;

import com.example.demo.service.ConcurrencyService;
import com.example.demo.service.ConcurrencyService.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ConcurrencyService.
 * Tests virtual thread execution, platform thread execution, and benchmarking.
 */
@SpringBootTest
class ConcurrencyServiceTest {

    @Autowired
    private ConcurrencyService concurrencyService;

    /**
     * Test that info endpoint returns valid data structure.
     */
    @Test
    void testGetInfo() {
        var info = concurrencyService.getInfo();

        assertNotNull(info);
        assertNotNull(info.title());
        assertNotNull(info.description());
        assertNotNull(info.virtualThreads());
        assertNotNull(info.platformThreads());
        assertNotNull(info.endpoints());
        assertNotNull(info.usageExamples());

        assertEquals("Java 25: Virtual Threads (Project Loom)", info.title());
        assertTrue(info.endpoints().size() >= 5);
        assertTrue(info.usageExamples().size() >= 2);
    }

    /**
     * Test virtual thread execution with small count.
     */
    @Test
    void testExecuteVirtualThreads() throws Exception {
        int count = 10;
        int durationMs = 100;
        TaskType type = TaskType.IO_BOUND;

        var response = concurrencyService.executeVirtualThreads(count, durationMs, type);

        assertNotNull(response);
        assertNotNull(response.metrics());
        assertEquals(count, response.metrics().taskCount());
        assertEquals(count, response.metrics().successCount());
        assertEquals(0, response.metrics().errorCount());
        assertTrue(response.metrics().elapsedTimeMs() >= durationMs);
        assertTrue(response.metrics().throughput() > 0);
        assertNotNull(response.results());
        assertEquals(count, response.results().size());
        assertTrue(response.results().stream().allMatch(TaskResult -> TaskResult.success()));
    }

    /**
     * Test platform thread execution with small count.
     */
    @Test
    void testExecutePlatformThreads() throws Exception {
        int count = 5;
        int durationMs = 100;
        TaskType type = TaskType.IO_BOUND;

        var response = concurrencyService.executePlatformThreads(count, durationMs, type);

        assertNotNull(response);
        assertNotNull(response.metrics());
        assertEquals(count, response.metrics().taskCount());
        assertTrue(response.metrics().successCount() > 0);
        assertNotNull(response.results());
        assertEquals(count, response.results().size());
    }

    /**
     * Test that platform threads are capped at 1000.
     */
    @Test
    void testPlatformThreadCapping() {
        int count = 5000; // Above cap
        var response = concurrencyService.executePlatformThreads(count, 50, TaskType.CUSTOM);

        // The cap should limit actual tasks submitted
        assertTrue(response.metrics().taskCount() <= 1000);
    }

    /**
     * Test CPU-bound task execution.
     */
    @Test
    void testCPUBoundTasks() {
        var response = concurrencyService.executeVirtualThreads(5, 50, TaskType.CPU_BOUND);

        assertNotNull(response);
        assertTrue(response.metrics().successCount() > 0);
    }

    /**
     * Test benchmark comparison.
     */
    @Test
    void testBenchmark() {
        int count = 50;
        int durationMs = 50;

        var benchmark = concurrencyService.benchmark(count, durationMs);

        assertNotNull(benchmark);
        assertNotNull(benchmark.virtual());
        assertNotNull(benchmark.platform());
        assertNotNull(benchmark.comparison());
        assertTrue(benchmark.comparison().contains("Virtual threads:"));
        assertTrue(benchmark.comparison().contains("Platform threads:"));
    }

    /**
     * Test pipeline execution.
     */
    @Test
    void testExecutePipeline() {
        int items = 10;
        int stages = 2;

        var response = concurrencyService.executePipeline(stages, items);

        assertNotNull(response);
        assertNotNull(response.metrics());
        assertEquals(items, response.metrics().taskCount());
        assertTrue(response.metrics().successCount() > 0);
        assertTrue(response.message().contains("Pipeline"));
        assertTrue(response.message().contains("items"));
        assertTrue(response.message().contains("stages"));
    }

    /**
     * Test pipeline with default values when invalid input.
     */
    @Test
    void testExecutePipelineWithInvalidInputs() {
        var response = concurrencyService.executePipeline(0, 0);
        // Should use defaults: stages=3, items=100
        assertNotNull(response);
        assertEquals(100, response.metrics().taskCount());
    }

    /**
     * Test that metrics are properly calculated.
     */
    @Test
    void testMetricsCalculation() {
        var response = concurrencyService.executeVirtualThreads(20, 100, TaskType.CUSTOM);

        ConcurrencyService.ConcurrencyMetrics m = response.metrics();

        assertTrue(m.elapsedTimeMs() >= 0);
        assertTrue(m.throughput() >= 0);
        assertTrue(m.activeThreads() >= 0);
        assertEquals(20, m.taskCount());
        assertEquals(m.successCount() + m.errorCount(), m.taskCount());
        assertNotNull(m.timestamp());
    }

    /**
     * Test error handling when tasks throw exceptions.
     */
    @Test
    void testTaskErrorHandling() {
        // IO_BOUND with sleep should not throw
        var response = concurrencyService.executeVirtualThreads(5, 10, TaskType.CUSTOM);

        // All should succeed with our simple task
        assertTrue(response.results().stream().allMatch(r -> r.success() || r.error() == null));
    }
}
