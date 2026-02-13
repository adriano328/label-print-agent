package com.agente.label_print_agent.controller;

import com.agente.label_print_agent.controller.Dto.PrintEnqueueResponse;
import com.agente.label_print_agent.controller.Dto.PrintLabelRequest;
import com.agente.label_print_agent.model.LabelRenderer;
import com.agente.label_print_agent.model.PrintJobItem;
import com.agente.label_print_agent.model.PrintQueue;
import com.agente.label_print_agent.service.PrintServiceAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@RestController
public class PrintController {

    private final PrintServiceAgent agent;
    private final LabelRenderer renderer;
    private final PrintQueue queue;


    public PrintController(PrintServiceAgent agent, LabelRenderer renderer, PrintQueue queue) {
        this.agent = agent;
        this.renderer = renderer;
        this.queue = queue;
    }

    @GetMapping("/printers")
    public ResponseEntity<List<String>> printers() {
        PrintService[] services = agent.listPrintServices();

        List<String> names = Arrays.stream(services)
                .map(PrintService::getName)
                .sorted()
                .toList();

        return ResponseEntity.ok(names);
    }

    @PostMapping(value = "/preview", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> preview(@RequestBody PrintLabelRequest req) {
        BufferedImage img = renderer.render(req);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return ResponseEntity.ok(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/print")
    public ResponseEntity<PrintEnqueueResponse> print(@RequestBody PrintLabelRequest req) {
        String id = UUID.randomUUID().toString();
        queue.enqueue(new PrintJobItem(id, req));
        return ResponseEntity.accepted().body(new PrintEnqueueResponse(id, "QUEUED"));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> jobStatus(@PathVariable String id) {
        return ResponseEntity.ok(queue.getStatus(id));
    }
}
