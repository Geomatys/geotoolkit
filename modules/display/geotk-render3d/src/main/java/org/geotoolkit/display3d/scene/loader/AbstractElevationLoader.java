/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.scene.loader;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.display.PortrayalException;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractElevationLoader implements ElevationLoader{

    private final static double[][] SMOOTH_MASK = {
            { 0, 1, 2, 1, 0},
            { 1, 2, 3, 2, 1},
            { 2, 3, 4, 3, 2},
            { 1, 2, 3, 2, 1},
            { 0, 1, 2, 1, 0}
        };
    private final double SMOOTH_SUM = 40;

    @Override
    public double getSmoothValueOf(DirectPosition position, double scale) throws PortrayalException {
        scale *= 2.5;
        final GeneralEnvelope env = new GeneralEnvelope(position.getCoordinateReferenceSystem());
        env.setRange(0, position.getOrdinate(0)-scale, position.getOrdinate(0)+scale);
        env.setRange(1, position.getOrdinate(1)-scale, position.getOrdinate(1)+scale);

        final BufferedImage image = getBufferedImageOf(env, new Dimension(5,5));
        final WritableRaster raster = image.getRaster();
        double[] pixel = new double[raster.getNumBands()];
        double result = 0;
        for(int y=0;y<5;y++){
            for(int x=0;x<5;x++){
                pixel = raster.getPixel(0, 0, pixel);
                result += SMOOTH_MASK[x][y] * pixel[0] / SMOOTH_SUM;
            }
        }

        return result;
    }

    @Override
    public double getValueOf(DirectPosition position, double scale) throws PortrayalException {
        scale = scale/2.0;
        final GeneralEnvelope env = new GeneralEnvelope(position.getCoordinateReferenceSystem());
        env.setRange(0, position.getOrdinate(0)-scale, position.getOrdinate(0)+scale);
        env.setRange(1, position.getOrdinate(1)-scale, position.getOrdinate(1)+scale);

        final BufferedImage image = getBufferedImageOf(env, new Dimension(1,1));
        final double[] pixel = image.getRaster().getPixel(0, 0, (double[])null);
        return pixel[0];
    }

}
