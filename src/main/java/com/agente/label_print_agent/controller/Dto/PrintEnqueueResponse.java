package com.agente.label_print_agent.controller.Dto;

public class PrintEnqueueResponse {
    public String jobId;
    public String status; // QUEUED
    public PrintEnqueueResponse(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
    }
}
