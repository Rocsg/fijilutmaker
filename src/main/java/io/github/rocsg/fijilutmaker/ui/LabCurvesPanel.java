package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.model.Colormap;
import io.github.rocsg.fijilutmaker.model.ColorPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LabCurvesPanel extends JPanel implements ColormapEditorPanel.ColormapChangeListener {
    private final Colormap colormap;
    private static final int PAD_X = 28, PAD_Y = 28;
    private static final int HANDLE_RADIUS = 7;
    private int selectedPoint = -1;     // index du point drag
    private int selectedChannel = 0;    // 0: L, 1: a, 2: b
    private int hoverPoint = -1;        // -1 sinon
    private int hoverChannel = 0;
    private boolean dragging = false;

    private static final Color[] CURVE_COLORS = {new Color(70,160,255), new Color(230,80,80), new Color(60,180,60)};
    private static final String[] CURVE_LABELS = {"L", "a", "b"};
    private static final double[] MINVAL = {0.0, -128.0, -128.0};
    private static final double[] MAXVAL = {100.0, 128.0, 128.0};

    // Listeners for colormap changes
    private final java.util.List<ColormapEditorPanel.ColormapChangeListener> listeners = new java.util.ArrayList<>();
    public void addColormapChangeListener(ColormapEditorPanel.ColormapChangeListener l) { listeners.add(l); }
    private void fireColormapChanged() {
        for (ColormapEditorPanel.ColormapChangeListener l : listeners) l.colormapChanged();
    }

    public LabCurvesPanel(Colormap colormap) {
        this.colormap = colormap;
        setPreferredSize(new Dimension(410, 160));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D closest = getClosestHandle(e.getX(), e.getY());
                if (closest.index >= 0) {
                    selectedPoint = closest.index;
                    selectedChannel = closest.channel;
                    dragging = true;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging) {
                    fireColormapChanged(); // Notifie tous les listeners (synchronise la LUT)
                }
                dragging = false;
                selectedPoint = -1;
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Clic droit = supprimer le point (sauf extrémités)
                Point2D closest = getClosestHandle(e.getX(), e.getY());
                if (SwingUtilities.isRightMouseButton(e) && closest.index > 0 && closest.index < colormap.getPoints().size() - 1) {
                    colormap.removePoint(closest.index);
                    fireColormapChanged(); // synchronise partout
                    repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hoverPoint = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D closest = getClosestHandle(e.getX(), e.getY());
                hoverPoint = closest.index;
                hoverChannel = closest.channel;
                repaint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging && selectedPoint >= 0) {
                    List<ColorPoint> pts = colormap.getPoints();
                    int w = getWidth() - 2 * PAD_X;
                    int h = getHeight() - 2 * PAD_Y;

                    // Position horizontale -> [0,1]
                    double pos = Math.max(0.0, Math.min(1.0, (e.getX() - PAD_X) / (double) w));
                    // Evite de dépasser les voisins (sauf pour extrémités)
                    if (selectedPoint > 0)
                        pos = Math.max(pts.get(selectedPoint - 1).getPosition() + 0.01, pos);
                    if (selectedPoint < pts.size() - 1)
                        pos = Math.min(pts.get(selectedPoint + 1).getPosition() - 0.01, pos);
                    pts.get(selectedPoint).setPosition(pos);

                    // Valeur verticale -> Lab
                    float[] lab = pts.get(selectedPoint).getLab();
                    double min = MINVAL[selectedChannel], max = MAXVAL[selectedChannel];
                    double v = Math.max(min, Math.min(max,
                                max - (e.getY() - PAD_Y) * (max - min) / h));
                    lab[selectedChannel] = (float) v;
                    pts.get(selectedPoint).setLab(lab);

                    fireColormapChanged(); // synchronise la LUT
                    repaint();
                }
            }
        });
    }

    // Helper interne : point + channel le plus proche de la souris
    private Point2D getClosestHandle(int mx, int my) {
        List<ColorPoint> pts = colormap.getPoints();
        int w = getWidth() - 2 * PAD_X;
        int h = getHeight() - 2 * PAD_Y;
        double minDist = 18;
        int minIdx = -1, minCh = 0;
        for (int ch = 0; ch < 3; ch++) {
            for (int i = 0; i < pts.size(); i++) {
                double px = PAD_X + pts.get(i).getPosition() * w;
                float[] lab = pts.get(i).getLab();
                double py = PAD_Y + (MAXVAL[ch] - lab[ch]) * h / (MAXVAL[ch] - MINVAL[ch]);
                double d = Math.hypot(mx - px, my - py);
                if (d < minDist) {
                    minDist = d;
                    minIdx = i;
                    minCh = ch;
                }
            }
        }
        return new Point2D(minIdx, minCh);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<ColorPoint> pts = colormap.getPoints();
        int n = pts.size();
        int w = getWidth() - 2 * PAD_X;
        int h = getHeight() - 2 * PAD_Y;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.2f));
        // Draw curves
        for (int ch = 0; ch < 3; ch++) {
            g2.setColor(CURVE_COLORS[ch]);
            for (int i = 0; i < n - 1; i++) {
                int x1 = PAD_X + (int) (pts.get(i).getPosition() * w);
                int x2 = PAD_X + (int) (pts.get(i + 1).getPosition() * w);
                float[] lab1 = pts.get(i).getLab();
                float[] lab2 = pts.get(i + 1).getLab();
                int y1 = PAD_Y + (int) ((MAXVAL[ch] - lab1[ch]) * h / (MAXVAL[ch] - MINVAL[ch]));
                int y2 = PAD_Y + (int) ((MAXVAL[ch] - lab2[ch]) * h / (MAXVAL[ch] - MINVAL[ch]));
                g2.drawLine(x1, y1, x2, y2);
            }
        }
        // Draw handles for all points
        for (int ch = 0; ch < 3; ch++) {
            for (int i = 0; i < n; i++) {
                double px = PAD_X + pts.get(i).getPosition() * w;
                float[] lab = pts.get(i).getLab();
                double py = PAD_Y + (MAXVAL[ch] - lab[ch]) * h / (MAXVAL[ch] - MINVAL[ch]);
                g2.setColor(CURVE_COLORS[ch]);
                g2.fillOval((int) px - HANDLE_RADIUS, (int) py - HANDLE_RADIUS, HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
                // Highlight selected or hovered handle
                if ((i == selectedPoint && ch == selectedChannel) || (i == hoverPoint && ch == hoverChannel)) {
                    g2.setStroke(new BasicStroke(3.5f));
                    g2.setColor(i == selectedPoint ? Color.RED : new Color(90, 90, 255));
                    g2.drawOval((int) px - HANDLE_RADIUS - 2, (int) py - HANDLE_RADIUS - 2, (HANDLE_RADIUS + 2) * 2, (HANDLE_RADIUS + 2) * 2);
                    g2.setStroke(new BasicStroke(2.2f));
                }
                g2.setColor(Color.BLACK);
                g2.drawOval((int) px - HANDLE_RADIUS, (int) py - HANDLE_RADIUS, HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
            }
        }
        // Draw axes/labels
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(PAD_X, PAD_Y, w, h);
        for (int ch = 0; ch < 3; ch++) {
            g2.setColor(CURVE_COLORS[ch]);
            g2.drawString(CURVE_LABELS[ch], getWidth() - 18, PAD_Y + 20 + 20 * ch);
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Lab Curves", PAD_X, PAD_Y - 10);
    }

    // Méthode appelée pour actualiser le panel depuis un autre panel (ex : ColormapEditorPanel)
    @Override
    public void colormapChanged() {
        repaint();
    }

    // Helper struct
    private static class Point2D {
        int index, channel;
        Point2D(int i, int ch) { index = i; channel = ch; }
    }
}
