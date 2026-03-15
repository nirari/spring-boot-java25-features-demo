package com.example.demo.controller;

import com.example.demo.service.ConcurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller demonstrating Java 25 Virtual Threads.
 * Provides endpoints to compare virtual threads vs platform threads for concurrent task execution.
 *
 * All responses include performance metrics: elapsed time, throughput, active threads, etc.
 */
@RestController
@RequestMapping("/threads")
public class ConcurrencyController {

    private final ConcurrencyService concurrencyService;

    public ConcurrencyController(ConcurrencyService concurrencyService) {
        this.concurrencyService = concurrencyService;
    }

    /**
     * POST /threads/virtual
     * Executes N concurrent tasks using Java 25 Virtual Threads.
     *
     * Request body (JSON):
     * {
     *   "count": 1000,           // number of concurrent tasks (1-10000)
     *   "durationMs": 500,       // how long each task runs in ms
     *   "type": "IO_BOUND"       // task type: IO_BOUND, CPU_BOUND, CUSTOM
     * }
     *
     * @param request execution parameters
     * @return response with metrics and individual task results
     */
    @PostMapping("/virtual")
    public ResponseEntity<ConcurrencyService.ConcurrencyResponse> executeVirtual(
            @RequestBody ThreadExecutionRequest request
    ) {
        request.validate();
        var response = concurrencyService.executeVirtualThreads(
            request.getCount(),
            request.getDurationMs(),
            request.getType()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /threads/platform
     * Executes N concurrent tasks using traditional Platform Threads (OS threads).
     * Maximum of 1000 threads to avoid system overload.
     *
     * Request body (JSON):
     * {
     *   "count": 100,            // number of concurrent tasks (1-1000, will be capped)
     *   "durationMs": 500,
     *   "type": "IO_BOUND"
     * }
     */
    @PostMapping("/platform")
    public ResponseEntity<ConcurrencyService.ConcurrencyResponse> executePlatform(
            @RequestBody ThreadExecutionRequest request
    ) {
        request.validate();
        var response = concurrencyService.executePlatformThreads(
            request.getCount(),
            request.getDurationMs(),
            request.getType()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /threads/info
     * Returns educational information about Virtual Threads vs Platform Threads.
     * Includes characteristics, benefits, limitations, and usage examples.
     */
    @GetMapping("/info")
    public ResponseEntity<ConcurrencyService.ConcurrencyInfo> getInfo() {
        var info = concurrencyService.getInfo();
        return ResponseEntity.ok(info);
    }

    /**
     * GET /threads/benchmark
     * Compares performance of virtual threads vs platform threads for the same workload.
     *
     * Query parameters:
     * - count: number of concurrent tasks (default: 1000, max: 10000)
     * - durationMs: duration of each simulated task (default: 100)
     * - type: task type (default: IO_BOUND)
     *
     * Example: /threads/benchmark?count=5000&durationMs=200
     */
    @GetMapping("/benchmark")
    public ResponseEntity<ConcurrencyService.BenchmarkResponse> benchmark(
            @RequestParam(defaultValue = "1000") int count,
            @RequestParam(defaultValue = "100") int durationMs,
            @RequestParam(defaultValue = "IO_BOUND") ConcurrencyService.TaskType type
    ) {
        var response = concurrencyService.benchmark(count, durationMs);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /threads/pipeline
     * Demonstrates a pipeline pattern where each stage runs on virtual threads.
     * Creates a simple producer-consumer pipeline.
     *
     * Request body:
     * {
     *   "stages": 3,             // number of pipeline stages
     *   "items": 1000            // number of items to process through pipeline
     * }
     */
    @PostMapping("/pipeline")
    public ResponseEntity<ConcurrencyService.ConcurrencyResponse> executePipeline(
            @RequestBody PipelineRequest request
    ) {
        request.validate();
        var response = concurrencyService.executePipeline(request.getStages(), request.getItems());
        return ResponseEntity.ok(response);
    }

    // ========== DTOs ==========

    /**
     * Request DTO for thread execution endpoints
     */
    public static class ThreadExecutionRequest {
        private int count;
        private int durationMs;
        private ConcurrencyService.TaskType type;

        public ThreadExecutionRequest() {}

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public int getDurationMs() { return durationMs; }
        public void setDurationMs(int durationMs) { this.durationMs = durationMs; }

        public ConcurrencyService.TaskType getType() { return type; }
        public void setType(ConcurrencyService.TaskType type) { this.type = type; }

        /**
         * Validates request parameters and applies defaults
         */
        public void validate() {
            if (count <= 0) {
                count = 100;
            }
            if (durationMs <= 0) {
                durationMs = 100;
            }
            if (type == null) {
                type = ConcurrencyService.TaskType.IO_BOUND;
            }
        }
    }

    /**
     * Request DTO for pipeline demo
     */
    public static class PipelineRequest {
        private int stages;
        private int items;

        public PipelineRequest() {}

        public int getStages() { return stages; }
        public void setStages(int stages) { this.stages = stages; }

        public int getItems() { return items; }
        public void setItems(int items) { this.items = items; }

        public void validate() {
            if (stages <= 0) stages = 3;
            if (items <= 0) items = 1000;
        }
    }
}
