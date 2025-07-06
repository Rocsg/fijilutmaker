package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.model.Colormap;
import io.github.rocsg.fijilutmaker.model.ColorPoint;

import javax.swing.*;
import java.awt.*;

/**
 * Standalone main application window for FijiLUTMaker, all UI panels as inner classes.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Fiji LUT Maker - All-in-One");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);

        // --- Model ---
        Colormap colormap = new Colormap();
        colormap.addPoint(new ColorPoint(0.0, new int[]{0, 0, 0}));
        colormap.addPoint(new ColorPoint(1.0, new int[]{255, 255, 255}));

        // --- Panels (as inner classes) ---
        JPanel colorPickerPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 100));
            setBorder(BorderFactory.createTitledBorder("Color Picker"));
        }};

        JPanel previewPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 40));
            setBorder(BorderFactory.createTitledBorder("LUT Preview"));
        }};

        JPanel imageStackPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 200));
            setBorder(BorderFactory.createTitledBorder("Image Stack"));
        }};

        JPanel settingsPanel = new JPanel() {{
            setPreferredSize(new Dimension(200, 40));
            setBorder(BorderFactory.createTitledBorder("Settings"));
        }};

        ColormapEditorPanel editorPanel = new ColormapEditorPanel(colormap);

        // --- Layout ---
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imageStackPanel, colorPickerPanel);
        leftSplit.setResizeWeight(0.5);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(editorPanel, BorderLayout.CENTER);
        rightPanel.add(previewPanel, BorderLayout.SOUTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, rightPanel);
        mainSplit.setResizeWeight(0.3);

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
