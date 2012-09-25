/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;

/**
 * Swing component displaying an image.
 * Image is scaled and clipped to always fill the complete space.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class JImagePane extends JComponent{

    private Image image = null;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics gra) {
        super.paintComponent(gra);
        
        if(image == null) return;
        
        final Graphics2D g = (Graphics2D) gra.create();        
        g.clip(new Rectangle(getSize()));

        final AffineTransform trs = new AffineTransform();        
        final double scaleX = (double)getWidth() / (double)image.getWidth(null);
        final double scaleY = (double)getHeight() / (double)image.getHeight(null);
        final double scale = Math.max(scaleX, scaleY);
        
//        trs.translate(getWidth()/2 - image.getWidth(null)/2, getHeight()/2 - image.getHeight(null)/2);
        trs.scale(scale, scale);
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, trs, null);
    }
    
}
