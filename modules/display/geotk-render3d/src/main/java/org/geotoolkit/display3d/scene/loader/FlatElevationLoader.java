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
import java.awt.image.DataBuffer;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Flat elevation.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FlatElevationLoader extends AbstractElevationLoader {

    private final double elevation;

    public FlatElevationLoader(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public double getMinimumElevation() {
        return elevation;
    }

    @Override
    public double getMaximumElevation() {
        return elevation;
    }

    @Override
    public void setOutputCRS(CoordinateReferenceSystem outputCrs) throws PortrayalException {

    }

    @Override
    public BufferedImage getBufferedImageOf(Envelope outputEnv, Dimension outputDimension) throws PortrayalException {
        final BufferedImage img = BufferedImages.createImage(outputDimension.width, outputDimension.height, 1, DataBuffer.TYPE_DOUBLE);
        ImageUtilities.fill(img, elevation);
        return img;
    }

}
