package com.deepak.job_scheduler.service;

import com.deepak.job_scheduler.model.Job;
import com.deepak.job_scheduler.config.handler.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

@Service
public class JobService {
    private final Queue<Job> jobQueue = new PriorityQueue<>();
    private final List<Job> jobList = Collections.synchronizedList(new ArrayList<>());
    private static final String FILE_PATH = "jobs.dat";

    @Autowired
    private WebSocketHandler jobWebSocketHandler;

    public JobService() {
        loadJobsFromFile();
    }

    public synchronized void submitJob(Job job) {
        jobQueue.offer(job);
        jobList.add(job);
        saveJobsToFile();
        processJobs();
    }

    public List<Job> getJobs() {
        return new ArrayList<>(jobList);
    }

    private void processJobs() {
        new Thread(() -> {
            while (!jobQueue.isEmpty()) {
                Job job = jobQueue.poll();
                if (job != null) {
                    job.setStatus("running");
                    jobWebSocketHandler.broadcastJobUpdate(job);
                    saveJobsToFile();
                    try {
                        Thread.sleep(job.getDuration());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    job.setStatus("completed");
                    jobWebSocketHandler.broadcastJobUpdate(job);
                    saveJobsToFile();
                }
            }
        }).start();
    }

    private void saveJobsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(jobList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadJobsFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Job> savedJobs = (List<Job>) ois.readObject();
                jobList.addAll(savedJobs);
                for (Job job : savedJobs) {
                    if ("pending".equals(job.getStatus()) || "running".equals(job.getStatus())) {
                        jobQueue.offer(job);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
