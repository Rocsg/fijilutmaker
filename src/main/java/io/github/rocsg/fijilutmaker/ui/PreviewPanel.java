package io.github.rocsg.fijilutmaker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying colormap previews: normal, colorblind, ramp, and image test.
 */
public class PreviewPanel extends JPanel {
    // TODO: Render preview bars, daltonian views, ramp with LUT applied.

    public PreviewPanel() {
        setBorder(BorderFactory.createTitledBorder("Preview"));
        setLayout(new BorderLayout());
        add(new JLabel("LUT preview (normal and colorblind) goes here."), BorderLayout.CENTER);
    }
}
