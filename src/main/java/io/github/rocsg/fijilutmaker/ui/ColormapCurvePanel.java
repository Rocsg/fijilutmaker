package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.color.ColorUtils;

import javax.swing.*;
import java.awt.*;

public class ColormapCurvePanel extends JPanel {
    private int[][] lut; // 256x3

    public ColormapCurvePanel(int[][] lut) {
        setPreferredSize(new Dimension(256, 80));
        setLut(lut);
    }

    public void setLut(int[][] lut) {
        this.lut = lut;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lut == null) return;
        int w = getWidth(), h = getHeight();
        float[] L = new float[lut.length], a = new float[lut.length], b = new float[lut.length];
        float Lmin = 100, Lmax = 0, amin = 100, amax = -100, bmin = 100, bmax = -100;
        for (int i = 0; i < lut.length; i++) {
            float[] lab = ColorUtils.rgb2labFloat(lut[i]);
            L[i] = lab[0]; a[i] = lab[1]; b[i] = lab[2];
            Lmin = Math.min(Lmin, L[i]); Lmax = Math.max(Lmax, L[i]);
            amin = Math.min(amin, a[i]); amax = Math.max(amax, a[i]);
            bmin = Math.min(bmin, b[i]); bmax = Math.max(bmax, b[i]);
        }
        // Normalize curves to panel height
        float ymin = Math.min(Lmin, Math.min(amin, bmin)), ymax = Math.max(Lmax, Math.max(amax, bmax));
        float yspan = ymax - ymin + 1e-3f;
        // Draw axes
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, h / 2, w, h / 2);
        // Draw L (black), a (red), b (blue)
        drawCurve(g, L, Color.BLACK, ymin, yspan, w, h);
        drawCurve(g, a, Color.RED, ymin, yspan, w, h);
        drawCurve(g, b, Color.BLUE, ymin, yspan, w, h);
        g.setColor(Color.BLACK);
        g.drawString("L", 5, 14); g.setColor(Color.RED); g.drawString("a", 25, 14); g.setColor(Color.BLUE); g.drawString("b", 45, 14);
    }

    private void drawCurve(Graphics g, float[] curve, Color color, float ymin, float yspan, int w, int h) {
        g.setColor(color);
        for (int i = 1; i < curve.length; i++) {
            int x0 = (i - 1) * w / curve.length, x1 = i * w / curve.length;
            int y0 = h - (int) ((curve[i - 1] - ymin) / yspan * (h - 5));
            int y1 = h - (int) ((curve[i] - ymin) / yspan * (h - 5));
            g.drawLine(x0, y0, x1, y1);
        }
    }
}
