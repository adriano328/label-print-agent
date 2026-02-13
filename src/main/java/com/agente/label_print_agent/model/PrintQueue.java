package com.agente.label_print_agent.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class PrintQueue {

    private final BlockingQueue<PrintJobItem> queue = new LinkedBlockingQueue<>(500);

    // status básico (opcional, mas muito útil pro Angular)
    private final ConcurrentMap<String, String> statusById = new ConcurrentHashMap<>();

    public String enqueue(PrintJobItem item) {
        boolean ok = queue.offer(item);
        if (!ok) throw new RejectedExecutionException("Fila cheia (capacidade 500).");
        statusById.put(item.id, "QUEUED");
        return item.id;
    }

    public PrintJobItem take() throws InterruptedException {
        return queue.take();
    }

    public void setStatus(String id, String status) {
        statusById.put(id, status);
    }

    public String getStatus(String id) {
        return statusById.getOrDefault(id, "UNKNOWN");
    }
}