package com.deepak.job_scheduler.controller;
import com.deepak.job_scheduler.config.handler.WebSocketHandler;
import com.deepak.job_scheduler.service.JobService;
import com.deepak.job_scheduler.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class ShedulerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @PostMapping
    public void submitJob(@RequestBody Job job) {
        jobService.submitJob(job);
        webSocketHandler.broadcastJobUpdate(job);
    }

    @GetMapping
    public List<Job> getJobs() {
        return jobService.getJobs();
    }

    private String jobToJson(Job job) {
        return "{\"name\": \"" + job.getName() + "\", \"duration\": " + job.getDuration() + ", \"status\": \"" + job.getStatus() + "\"}";
    }
}