/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.internal;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 * IconBundle, manage icons and avoid double loading
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class GeotkFXIconBundle {

    public static final BufferedImage EMPTY_ICON_16;
    static {
        try {
            EMPTY_ICON_16 = ImageIO.read(GeotkFXIconBundle.class.getResource("/org/geotoolkit/gui/javafx/icon/empty16.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    private GeotkFXIconBundle() {}

    public static BufferedImage getBufferedImage(final String key) {
        try {
            return ImageIO.read(GeotkFXIconBundle.class.getResourceAsStream("/org/geotoolkit/gui/javafx/icon/"+key+".png"));
        } catch (IOException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }
    
    public static BufferedImage getBufferedImage(final String key, Dimension resize) {
        try {
            final BufferedImage img = ImageIO.read(GeotkFXIconBundle.class.getResourceAsStream("/org/geotoolkit/gui/javafx/icon/"+key+".png"));
            
            final BufferedImage resized = new BufferedImage(resize.width, resize.height, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, 0, 0, resize.width, resize.height, null);
            g.dispose();
            
            return resized;
            
        } catch (IOException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }
    
    public static Image getImage(final String key) throws IOException{
       return SwingFXUtils.toFXImage(getBufferedImage(key), null);
    }

}
