package io.github.rocsg.fijilutmaker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for managing loaded images: stacking, mosaicking, navigation.
 */
public class ImageStackPanel extends JPanel {
    // TODO: Handle loading, stacking/mosaicing images, navigation controls.

    public ImageStackPanel() {
        setBorder(BorderFactory.createTitledBorder("Images"));
        setLayout(new BorderLayout());
        add(new JLabel("Load/compose images, stack or mosaic, Z-slider."), BorderLayout.CENTER);
    }
}
