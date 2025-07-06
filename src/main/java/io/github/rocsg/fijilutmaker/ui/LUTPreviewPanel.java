package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.color.DaltonizeUtils;
import javax.swing.*;
import java.awt.*;

public class LUTPreviewPanel extends JPanel {
    private int[][] lut; // 256x3
    private int bandHeight = 18;
    private static final String[] LABELS = {"Normal", "Deuteranope", "Tritanope"};

    public LUTPreviewPanel(int[][] lut) {
        setPreferredSize(new Dimension(256, bandHeight * 3 + 24));
        setLut(lut);
    }

    public void setLut(int[][] lut) {
        this.lut = lut;
        repaint();
    }

    public int[][] getLut() {
        return lut;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lut == null) return;

        int w = getWidth();
        int bh = bandHeight;
        int gap = 7;
        int[] ys = {0, bh + gap, 2 * (bh + gap)};

        // Draw each band
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < lut.length; i++) {
                int[] rgb = (j == 0) ? lut[i]
                        : (j == 1) ? DaltonizeUtils.rgbDeuteranope(lut[i])
                        : DaltonizeUtils.rgbTritanope(lut[i]);
                g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
                int x = i * w / lut.length;
                g.fillRect(x, ys[j], w / lut.length + 1, bh);
            }
            // Draw band label with white box behind for readability
            int labelY = ys[j] + bh / 2 + 5;
            Graphics2D g2 = (Graphics2D) g;
            String label = LABELS[j];
            FontMetrics fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(label) + 8;
            int labelH = fm.getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(8, labelY - labelH + 3, labelW, labelH);
            g2.setColor(Color.GRAY);
            g2.drawRect(8, labelY - labelH + 3, labelW, labelH);
            g2.setColor(Color.BLACK);
            g2.drawString(label, 12, labelY);
        }
        // Draw border
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, w - 1, 3 * bh + 2 * gap - 1);
    }
}
