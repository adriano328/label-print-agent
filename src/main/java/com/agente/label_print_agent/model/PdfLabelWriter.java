package com.agente.label_print_agent.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Component
public class PdfLabelWriter {

    public byte[] toPdfBytes(BufferedImage img, int dpi) {
        // Converte pixels -> pontos (PDF usa 72 pontos por polegada)
        float widthPt = (float) (img.getWidth() * 72.0 / dpi);
        float heightPt = (float) (img.getHeight() * 72.0 / dpi);

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(new PDRectangle(widthPt, heightPt));
            doc.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, img);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.drawImage(pdImage, 0, 0, widthPt, heightPt);
            }

            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da etiqueta", e);
        }
    }
}
