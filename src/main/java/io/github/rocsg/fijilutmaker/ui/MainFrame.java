package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.model.Colormap;
import io.github.rocsg.fijilutmaker.model.ColorPoint;

import javax.swing.*;
import java.awt.*;

/**
 * Standalone main application window for FijiLUTMaker, assembling all UI panels.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Fiji LUT Maker");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);

        // --- Model ---
        Colormap colormap = new Colormap();
        colormap.addPoint(new ColorPoint(0.0, new int[]{0, 0, 0}));
        colormap.addPoint(new ColorPoint(1.0, new int[]{255, 255, 255}));

        // --- Core Panels ---
        TestImagePanel testPanel = new TestImagePanel();
        ColormapEditorPanel lutPanel = new ColormapEditorPanel(colormap,testPanel);
        LabCurvesPanel curvesPanel = new LabCurvesPanel(colormap);

        // Synchronisation bidirectionnelle (chaque panel écoute les changements de l'autre)
        lutPanel.addColormapChangeListener(curvesPanel);
//        curvesPanel.addColormapChangeListener(lutPanel);

        // --- Optionally, Preview Panel(s) ---
        LUTPreviewPanel previewPanel = new LUTPreviewPanel(colormap.generateLUT());
        // Tu ajouteras plus tard la version multi-daltonienne ici

        JPanel testImagePanel = new TestImagePanel(); // Pour la preview image, si dispo

        // --- Autres Panels (espace réservé) ---
        JPanel colorPickerPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 100));
            setBorder(BorderFactory.createTitledBorder("Color Picker"));
        }};
        JPanel imageStackPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 200));
            setBorder(BorderFactory.createTitledBorder("Image Stack"));
        }};
        JPanel settingsPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 40));
            setBorder(BorderFactory.createTitledBorder("Settings"));
        }};

        // --- Assemblage de l'UI ---
        // Gauche : stack d'images, picker, etc.
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(imageStackPanel, BorderLayout.CENTER);
        leftPanel.add(colorPickerPanel, BorderLayout.SOUTH);

        // Droite : éditeur LUT + courbes + previews
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(lutPanel, BorderLayout.CENTER);
        editorPanel.add(curvesPanel, BorderLayout.SOUTH);

        JPanel previewStack = new JPanel(new GridLayout(2, 1));
        previewStack.add(previewPanel);
        previewStack.add(testImagePanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(editorPanel, BorderLayout.CENTER);
        rightPanel.add(previewStack, BorderLayout.SOUTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setResizeWeight(0.22);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainSplit, BorderLayout.CENTER);
        getContentPane().add(settingsPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
