package com.agente.label_print_agent.model;

import com.agente.label_print_agent.controller.Dto.PrintLabelRequest;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;


import java.awt.*;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Locale;

@Component
public class LabelRenderer {

    public BufferedImage render(PrintLabelRequest req) {
        int w = mmToPx(req.widthMm, req.dpi);
        int h = mmToPx(req.heightMm, req.dpi);

        if (req.landscape && h > w) {
            int tmp = w; w = h; h = tmp;
        }

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = (req.text == null ? "" : req.text).toUpperCase(Locale.ROOT);

        int padX = (int) Math.round(w * 0.06);
        int padY = (int) Math.round(h * 0.18);

        int maxW = w - (2 * padX);
        int maxH = h - (2 * padY);

        int fontSize = Math.max(10, maxH);
        Font font;
        FontMetrics fm;

        do {
            font = new Font("Arial", Font.BOLD, fontSize);
            g.setFont(font);
            fm = g.getFontMetrics();
            fontSize--;
        } while (fontSize > 10 && (fm.stringWidth(text) > maxW || fm.getHeight() > maxH));

        int textW = fm.stringWidth(text);
        int x = (w - textW) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();

        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
        g.dispose();

        return img;
    }

    private int mmToPx(int mm, int dpi) {
        return (int) Math.round((mm / 25.4) * dpi);
    }
}