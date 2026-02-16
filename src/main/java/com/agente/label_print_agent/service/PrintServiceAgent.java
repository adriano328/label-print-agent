package com.agente.label_print_agent.service;

import com.agente.label_print_agent.controller.Dto.PrintLabelRequest;
import com.agente.label_print_agent.exception.PrintJobException;
import com.agente.label_print_agent.exception.PrinterNotFoundException;
import com.agente.label_print_agent.model.LabelRenderer;
import com.agente.label_print_agent.model.PdfLabelWriter;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Arrays;

@Service
public class PrintServiceAgent {

    private final LabelRenderer renderer;
    private final PdfLabelWriter pdfWriter;

    public PrintServiceAgent(LabelRenderer renderer, PdfLabelWriter pdfWriter) {
        this.renderer = renderer;
        this.pdfWriter = pdfWriter;
    }

    public PrintService[] listPrintServices() {
        return PrintServiceLookup.lookupPrintServices(null, null);
    }

    public PrintService findPrinterByName(String printerName) {
        if (printerName == null || printerName.trim().isEmpty()) {
            throw new PrinterNotFoundException("printerName é obrigatório");
        }

        return Arrays.stream(listPrintServices())
                .filter(s -> s.getName().equalsIgnoreCase(printerName.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new PrinterNotFoundException("Impressora não encontrada: " + printerName)
                );
    }

    public void printLabel(PrintLabelRequest req) {
        PrintService ps = findPrinterByName(req.printerName);

        try {
            BufferedImage img = renderer.render(req);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(ps);

            PageFormat pf = job.defaultPage();

            Printable printable = (graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;

                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double ix = pageFormat.getImageableX();
                double iy = pageFormat.getImageableY();
                double iw = pageFormat.getImageableWidth();
                double ih = pageFormat.getImageableHeight();

                if (iw <= 1 || ih <= 1) {
                    throw new PrinterException("Área imprimível inválida (iw/ih): " + iw + " x " + ih);
                }

                double sx = iw / img.getWidth();
                double sy = ih / img.getHeight();
                double scale = Math.min(sx, sy);

                int drawW = (int) Math.round(img.getWidth() * scale);
                int drawH = (int) Math.round(img.getHeight() * scale);

                int x = (int) Math.round(ix + (iw - drawW) / 2.0);
                int y = (int) Math.round(iy + (ih - drawH) / 2.0);

                g2.drawImage(img, x, y, drawW, drawH, null);

                return Printable.PAGE_EXISTS;
            };

            job.setPrintable(printable, pf);

            int copies = Math.max(1, req.copies);
            job.setCopies(copies);

            job.print();

        } catch (Exception e) {
            throw new PrintJobException(
                    "Falha ao imprimir etiqueta (PrinterJob)",
                    "Printer=" + ps.getName() + " | " + e.getClass().getName() + " | " + e.getMessage(),
                    e
            );
        }
    }
}
