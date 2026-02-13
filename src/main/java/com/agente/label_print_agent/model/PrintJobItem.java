package com.agente.label_print_agent.model;

import com.agente.label_print_agent.controller.Dto.PrintLabelRequest;

public class PrintJobItem {
    public final String id;
    public final PrintLabelRequest request;

    public PrintJobItem(String id, PrintLabelRequest request) {
        this.id = id;
        this.request = request;
    }
}
