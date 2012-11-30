/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.style.interval.Palette;
import org.geotoolkit.util.logging.Logging;

/**
 * Renderer of Palette in combobox in Style editor. 
 * @author Quentin Boileau (Geomatys).
 */
public class PaletteCellRenderer extends DefaultListCellRenderer {

    private static final Logger LOGGER = Logging.getLogger(PaletteCellRenderer.class);
    private final PaletteFactory PF = PaletteFactory.getDefault();
    private Object paletteValue = null;

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        this.setText(" Palette ");
        paletteValue = value;
        return this;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (paletteValue != null) {
            Dimension d = this.getSize();
            Rectangle rect = new Rectangle(d);
            rect.grow(-2, -2);
            
            if (paletteValue instanceof String) {
                try {
                    final String paletteName = String.valueOf(paletteValue);
                    final org.geotoolkit.image.io.Palette palette = PF.getPalette(paletteName, 10);
                    final RenderedImage img = palette.getImage(rect.getSize());
                    ((Graphics2D) g).drawRenderedImage(img, new AffineTransform());
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            } else if (paletteValue instanceof Palette) {
                final Palette palette = (Palette) paletteValue;
                palette.render((Graphics2D) g, rect);
            }
        }
    }
}
