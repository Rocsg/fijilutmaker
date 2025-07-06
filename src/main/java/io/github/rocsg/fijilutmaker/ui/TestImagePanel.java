package io.github.rocsg.fijilutmaker.ui;

import io.github.rocsg.fijilutmaker.color.DaltonizeUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;

public class TestImagePanel extends JPanel implements ColormapEditorPanel.ColormapChangeListener {
    private BufferedImage testImg;        // grayscale input
    private BufferedImage[] mappedImgs;   // [0]=normal, [1]=deut, [2]=trit
    private int[][] lut;                  // currently applied LUT

    public TestImagePanel() {
        setPreferredSize(new Dimension(256*3 + 30, 110));
        testImg = loadTestImage();
        mappedImgs = new BufferedImage[3];
        applyLut(null); // Render as grayscale at startup
    }

    // Charge testLut.tif ou génère un gradient si absent
    private BufferedImage loadTestImage() {
        try (InputStream is = getClass().getResourceAsStream("/testLut.tif")) {
            if (is != null) return ImageIO.read(is);
        } catch (IOException e) { /* ignore, fallback below */ }
        // Fallback: horizontal gradient
        BufferedImage img = new BufferedImage(256, 100, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < 256; x++)
            for (int y = 0; y < 100; y++)
                img.getRaster().setSample(x, y, 0, x);
        return img;
    }

    // Applique la LUT sur testImg et pré-calcule les 3 rendus
    public void applyLut(int[][] lut) {
        this.lut = lut;
        if (testImg == null) return;
        int w = testImg.getWidth(), h = testImg.getHeight();
        mappedImgs = new BufferedImage[3];
        for (int v = 0; v < 3; v++)
            mappedImgs[v] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int val = (testImg.getRaster().getSample(x, y, 0)); // [0,255]
                int[] rgb = lut == null ? new int[]{val, val, val} : lut[val];
                mappedImgs[0].setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
                int[] deut = DaltonizeUtils.rgbDeuteranope(rgb);
                mappedImgs[1].setRGB(x, y, new Color(deut[0], deut[1], deut[2]).getRGB());
                int[] trit = DaltonizeUtils.rgbTritanope(rgb);
                mappedImgs[2].setRGB(x, y, new Color(trit[0], trit[1], trit[2]).getRGB());
            }
        }
        repaint();
    }

    // Listener: appelé quand la colormap change (à connecter)
    @Override
    public void colormapChanged() {
        if (lut != null) {
            applyLut(lut);
        }
    }

    // Permet d'actualiser la LUT depuis l'extérieur
    public void setLut(int[][] lut) {
        applyLut(lut);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int pad = 5, bandW = getWidth() / 3, h = getHeight() - 22;
        String[] labels = {"Normal", "Deut.", "Trit."};
        for (int i = 0; i < 3; i++) {
            if (mappedImgs[i] != null)
                g.drawImage(mappedImgs[i], i*bandW+pad, 18, bandW-2*pad, h, null);
            g.setColor(Color.BLACK);
            g.drawString(labels[i], i*bandW + pad + 8, 15);
        }
    }
}
