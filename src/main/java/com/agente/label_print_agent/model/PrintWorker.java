package com.agente.label_print_agent.model;
import com.agente.label_print_agent.service.PrintServiceAgent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PrintWorker {

    private final PrintQueue printQueue;
    private final PrintServiceAgent agent;

    public PrintWorker(PrintQueue printQueue, PrintServiceAgent agent) {
        this.printQueue = printQueue;
        this.agent = agent;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Thread t = new Thread(() -> {

            while (true) {
                try {
                    // ⬇⬇⬇ AQUI É O TRECHO QUE VOCÊ PERGUNTOU ⬇⬇⬇
                    PrintJobItem job = printQueue.take();
                    printQueue.setStatus(job.id, "PRINTING");

                    try {
                        agent.printLabel(job.request);
                        printQueue.setStatus(job.id, "DONE");
                    } catch (Exception ex) {
                        printQueue.setStatus(job.id, "FAILED");
                        // opcional: logar erro
                        // ex.printStackTrace();
                    }
                    // ⬆⬆⬆ FIM DO TRECHO ⬆⬆⬆

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

        }, "print-worker");

        t.setDaemon(true);
        t.start();
    }
}