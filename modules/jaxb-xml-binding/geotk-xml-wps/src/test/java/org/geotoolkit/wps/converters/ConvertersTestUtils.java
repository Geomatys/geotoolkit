/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

/**
 *
 * @author Quentin Boileau (Geoamtys)
 */
public final class ConvertersTestUtils {

    private ConvertersTestUtils() {
    }
    
    public static RenderedImage makeRendredImage() {
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);        
        final Graphics2D g2d = img.createGraphics();
        final GradientPaint gp = new GradientPaint(0, 0, Color.BLACK,
        500, 500, Color.WHITE, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 500, 500);
        g2d.dispose();
        return img;
    }
    
    public static GridCoverage2D makeCoverage() {
        //first create a matrix table
         //allow reprojection even if grid or bursawolf parameters are missing
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        
        //global initialization
        Setup.initialize(null);
        
        //force loading all image readers/writers
        ImageIO.scanForPlugins();
        
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);        
        final Graphics2D g2d = img.createGraphics();
        final GradientPaint gp = new GradientPaint(0, 0, Color.RED,
        500, 500, Color.BLUE, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 500, 500);
        g2d.dispose();
        
        
        //set it's envelope
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);
        
        //create the coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(img);
        return gcb.getGridCoverage2D();
    }
    
}
