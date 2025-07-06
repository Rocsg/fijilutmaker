package io.github.rocsg.fijilutmaker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel to display (composed) images and allow color picking.
 * Should use ImageJ's ImageCanvas/ImagePlus when possible.
 */
public class ColorPickerPanel extends JPanel {
    // TODO: Reference to images/stack, implement color picking (on click).
    public ColorPickerPanel() {
        setBorder(BorderFactory.createTitledBorder("Color Picker"));
        setLayout(new BorderLayout());
        // Placeholder (replace with ImageCanvas or custom component)
        add(new JLabel("Image preview and color picking goes here."), BorderLayout.CENTER);
    }
}
