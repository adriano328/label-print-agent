package com.agente.label_print_agent.controller.Dto;

public class PrintLabelRequest {
    public String printerName;
    public String text;
    public int widthMm;
    public int heightMm;
    public int dpi;
    public boolean landscape;
    public int copies;
}
