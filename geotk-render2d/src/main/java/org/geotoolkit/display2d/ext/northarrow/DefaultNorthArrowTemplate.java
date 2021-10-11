/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.ext.northarrow;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.renderer.style.DynamicSymbolFactoryFinder;
import org.apache.sis.util.logging.Logging;

/**
 * Default north arrow template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultNorthArrowTemplate implements NorthArrowTemplate{

    private final URL svgFile;
    private final BackgroundTemplate background;
    private final Dimension size;
    private Image buffer = null;

    public DefaultNorthArrowTemplate(final BackgroundTemplate background,final URL file, Dimension size){

        if(size == null) size = new Dimension(100,100);

        this.svgFile = file;
        this.background = background;
        this.size = size;

        try {
            buffer = ImageIO.read(file);

            if(buffer != null && (buffer.getHeight(null) != size.getHeight() || buffer.getWidth(null) != size.getWidth())){
                final BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
                img.getGraphics().drawImage(buffer, 0, 0, size.width, size.height, null);

                buffer = img;
            }

        } catch (IOException ex) {
            Logging.getLogger("org.geotoolkit.display2d.ext.northarrow").log(Level.WARNING, "image might be an svg", ex);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getImage() {
        if(buffer != null){
            return buffer;
        }else{
            buffer = null;
        }
        try {
            buffer = DynamicSymbolFactoryFinder.getImage(svgFile.toURI(),"svg",(float)size.height,null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return buffer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void renderImage(final Graphics2D g, final Point2D center) {
        try {
            DynamicSymbolFactoryFinder.renderImage(svgFile.toURI(),"svg",(float)size.height,g,center,null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public BackgroundTemplate getBackground() {
        return background;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

}
