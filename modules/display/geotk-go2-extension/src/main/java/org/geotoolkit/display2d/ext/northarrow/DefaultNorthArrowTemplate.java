/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.TranscoderException;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.renderer.svg.SvgUtils;

/**
 * Default north arrow template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultNorthArrowTemplate implements NorthArrowTemplate{

    private final URL svgFile;
    private final BackgroundTemplate background;
    private final Dimension size;
    private Image buffer = null;

    public DefaultNorthArrowTemplate(BackgroundTemplate background,URL file, Dimension size){

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
            Logger.getLogger(DefaultNorthArrowTemplate.class.getName()).log(Level.WARNING, "image might be an svg", ex);
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
            buffer = SvgUtils.read(svgFile.openStream(), size);
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (TranscoderException ex) {
            ex.printStackTrace();
        }

        return buffer;
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
