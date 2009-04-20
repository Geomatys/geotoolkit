/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.ext.northarrow;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import org.apache.batik.transcoder.TranscoderException;
import org.geotoolkit.renderer.svg.SvgUtils;

/**
 * Default north arrow template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultNorthArrowTemplate implements NorthArrowTemplate{

    private final URL svgFile;
    private Image buffer = null;

    public DefaultNorthArrowTemplate(URL svgFile){
        this.svgFile = svgFile;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getImage(Dimension size) {
        if(buffer != null && buffer.getHeight(null) == size.height && buffer.getWidth(null) == size.width){
            return buffer;
        }else{
            buffer = null;
        }
        try {
            buffer = SvgUtils.getInstance().read(svgFile, size);
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (TranscoderException ex) {
            ex.printStackTrace();
        }

        return buffer;
    }

}
