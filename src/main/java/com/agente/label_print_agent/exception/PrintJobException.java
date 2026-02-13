package com.agente.label_print_agent.exception;

public class PrintJobException extends RuntimeException {
    private final String details;

    public PrintJobException(String message, String details) {
        super(message);
        this.details = details;
    }

    public PrintJobException(String message, String details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
