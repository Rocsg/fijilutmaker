package io.github.rocsg.fijilutmaker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel to preview a colormap (LUT) as a horizontal color strip.
 */
public class LUTPreviewPanel extends JPanel {

    private int[][] lut;
    private int barHeight = 32;

    public LUTPreviewPanel(int[][] lut) {
        this.lut = lut;
        setPreferredSize(new Dimension(lut != null ? lut.length : 256, barHeight));
        setMinimumSize(new Dimension(64, barHeight));
    }

    /**
     * Set (or update) the LUT to preview.
     */
    public void setLut(int[][] lut) {
        this.lut = lut;
        setPreferredSize(new Dimension(lut != null ? lut.length : 256, barHeight));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lut == null) return;
        int w = getWidth();
        int h = getHeight();
        int N = lut.length;
        // Draw each color as a vertical line (scaled if needed)
        for (int i = 0; i < w; i++) {
            int idx = (int)Math.round(i * (N-1.0)/(w-1.0));
            int[] rgb = lut[idx];
            g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
            g.drawLine(i, 0, i, h-1);
        }
        // Draw border
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, w-1, h-1);
    }
}
