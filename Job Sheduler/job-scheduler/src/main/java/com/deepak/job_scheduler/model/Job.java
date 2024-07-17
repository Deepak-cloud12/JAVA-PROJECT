package com.deepak.job_scheduler.model;

import java.io.Serializable;

public class Job implements Comparable<Job>, Serializable {
    private String name;
    private long duration;
    private String status;

    public Job() {}

    public Job(String name, long duration) {
        this.name = name;
        this.duration = duration;
        this.status = "pending";
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Job other) {
        return Long.compare(this.duration, other.duration);
    }
}


