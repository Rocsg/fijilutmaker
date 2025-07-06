package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.model.Colormap;
import io.github.rocsg.fijilutmaker.model.ColorPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Colormap editor with interactive handles for adding, moving, deleting and recoloring points.
 * Highlights the point under the mouse or dragged, shows tooltip with info.
 */
public class ColormapEditorPanel extends JPanel {
    private boolean movingPoint = false;
    private int currentDraggedPoint = -1;
    private int hoverPoint = -1; // index du point sous la souris, -1 sinon
    private Colormap colormap;
    private LUTPreviewPanel lutPreview;
    private int selectedIdx = -1; // index of point being moved
    private static final int HANDLE_RADIUS = 8;

    public ColormapEditorPanel(Colormap colormap) {
        this.colormap = colormap;
        setLayout(new BorderLayout());
        lutPreview = new LUTPreviewPanel(colormap.generateLUT());
        add(lutPreview, BorderLayout.CENTER);

        // --- Mode selection (interpolation) ---
        JComboBox<Colormap.InterpolationMode> modeBox = new JComboBox<>(Colormap.InterpolationMode.values());
        modeBox.setSelectedItem(colormap.getInterpolationMode());
        modeBox.addActionListener(e -> {
            colormap.setInterpolationMode((Colormap.InterpolationMode) modeBox.getSelectedItem());
            lutPreview.setLut(colormap.generateLUT());
            repaint();
        });
        add(modeBox, BorderLayout.NORTH);

        // --- Mouse interaction ---
        lutPreview.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int idx = getHandleAt(e.getX());
                if (idx >= 0 && SwingUtilities.isLeftMouseButton(e)) {
                    selectedIdx = idx;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                movingPoint = false;
                selectedIdx = -1;
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = getHandleAt(e.getX());
                // Double-click = edit color
                if (idx >= 0 && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    ColorPoint cp = colormap.getPoints().get(idx);
                    Color newColor = JColorChooser.showDialog(ColormapEditorPanel.this, "Pick Color", new Color(cp.getRgb()[0], cp.getRgb()[1], cp.getRgb()[2]));
                    if (newColor != null) {
                        cp.setRgb(new int[]{newColor.getRed(), newColor.getGreen(), newColor.getBlue()});
                        lutPreview.setLut(colormap.generateLUT());
                        repaint();
                    }
                }
                // Clic droit sur un point : supprimer (sauf s'il y en a que 2)
                if (idx >= 0 && SwingUtilities.isRightMouseButton(e) && colormap.getPoints().size() > 2) {
                    colormap.removePoint(idx);
                    lutPreview.setLut(colormap.generateLUT());
                    repaint();
                }
                // Clic gauche hors point : ajouter point à cet endroit (interp couleur)
                if (idx == -1 && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    double pos = Math.max(0.0, Math.min(1.0, (double) e.getX() / (lutPreview.getWidth() - 1)));
                    int[] rgb = colormap.generateLUT()[(int) Math.round(pos * (colormap.getLutSize() - 1))];
                    colormap.addPoint(new ColorPoint(pos, rgb));
                    colormap.getPoints().sort((a, b) -> Double.compare(a.getPosition(), b.getPosition()));
                    lutPreview.setLut(colormap.generateLUT());
                    repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hoverPoint = -1;
                setToolTipText(null);
                repaint();
            }
        });

        lutPreview.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverPoint = getHandleAt(e.getX());
                if (hoverPoint >= 0) {
                    ColorPoint cp = colormap.getPoints().get(hoverPoint);
                    int[] rgb = cp.getRgb();
                    String text = String.format("Pos: %.3f, RGB: %d,%d,%d", cp.getPosition(), rgb[0], rgb[1], rgb[2]);
                    setToolTipText(text);
                } else {
                    setToolTipText(null);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if(!movingPoint) { movingPoint = true; currentDraggedPoint = selectedIdx; }
                else { selectedIdx = currentDraggedPoint; }
                if (selectedIdx >= 0) {
                    double pos = Math.max(0.0, Math.min(1.0, (double) e.getX() / (lutPreview.getWidth() - 1)));
                    List<ColorPoint> pts = colormap.getPoints();
                    ColorPoint cp = pts.get(selectedIdx);

                    /* Facultatif : décommente pour forcer un écart minimum de 0.01 avec voisins
                    if (selectedIdx > 0 && pos <= pts.get(selectedIdx - 1).getPosition() + 0.01)
                        pos = pts.get(selectedIdx - 1).getPosition() + 0.01;
                    if (selectedIdx < pts.size() - 1 && pos >= pts.get(selectedIdx + 1).getPosition() - 0.01)
                        pos = pts.get(selectedIdx + 1).getPosition() - 0.01;
                    */

                    cp.setPosition(pos);
                    colormap.getPoints().sort((a, b) -> Double.compare(a.getPosition(), b.getPosition()));
                    // Corrige l'index si le point a changé de place dans la liste
                    int newIdx = pts.indexOf(cp);
                    if(newIdx != selectedIdx) { selectedIdx = newIdx; currentDraggedPoint = selectedIdx; }
                    lutPreview.setLut(colormap.generateLUT());
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        // Draw handles for color points over preview
        List<ColorPoint> pts = colormap.getPoints();
        int w = lutPreview.getWidth();
        int h = lutPreview.getHeight();
        for (int i = 0; i < pts.size(); i++) {
            ColorPoint cp = pts.get(i);
            int x = (int) Math.round(cp.getPosition() * (w - 1));
            int[] rgb = cp.getRgb();

            g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
            g.fillOval(x - HANDLE_RADIUS, h / 2 - HANDLE_RADIUS, HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);

            // Highlight le point sélectionné (drag) ou sous la souris
            if (i == selectedIdx || i == hoverPoint) {
                ((Graphics2D) g).setStroke(new BasicStroke(3.5f));
                g.setColor(i == selectedIdx ? Color.RED : new Color(120, 120, 255));
                g.drawOval(x - HANDLE_RADIUS - 2, h / 2 - HANDLE_RADIUS - 2, (HANDLE_RADIUS + 2) * 2, (HANDLE_RADIUS + 2) * 2);
                ((Graphics2D) g).setStroke(new BasicStroke(1.0f));
            }
            // Contour noir par défaut
            g.setColor(Color.BLACK);
            g.drawOval(x - HANDLE_RADIUS, h / 2 - HANDLE_RADIUS, HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
        }
    }

    public void updateColormap() {
        lutPreview.setLut(colormap.generateLUT());
        repaint();
    }

    public Colormap getColormap() { return colormap; }
    public void setColormap(Colormap colormap) {
        this.colormap = colormap;
        updateColormap();
    }

    // Helper to detect handle under mouse
    private int getHandleAt(int x) {
        List<ColorPoint> pts = colormap.getPoints();
        int w = lutPreview.getWidth();
        for (int i = 0; i < pts.size(); i++) {
            int px = (int) Math.round(pts.get(i).getPosition() * (w - 1));
            if (Math.abs(px - x) <= HANDLE_RADIUS)
                return i;
        }
        return -1;
    }
}
