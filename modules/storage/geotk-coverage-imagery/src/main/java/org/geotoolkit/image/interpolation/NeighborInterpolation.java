/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.interpolation;

import java.awt.Rectangle;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define Interpolation from neighbor.
 *
 * Neighbor interpolation round coordinates values at nearest integer value
 * and return nearest pixel value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class NeighborInterpolation extends Interpolation {
    final int maxxId;
    final int maxyId;

    /**
     * Create a NeighBor Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     */
    public NeighborInterpolation(PixelIterator pixelIterator, double[] fillValue) {
        super(pixelIterator, 0, ResampleBorderComportement.EXTRAPOLATION, fillValue);
        Rectangle rect = pixelIterator.getBoundary(false);
        maxxId = rect.x + rect.width  - 1;
        maxyId = rect.y + rect.height - 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean checkInterpolate(double x, double y) {
        return !(x < bminX || x > bmaxX || y < bminY || y > bmaxY);
    }

    /**
     * Return nearest pixel value.
     *
     * {@inheritDoc }
     */
    @Override
    public double interpolate(double x, double y, int band) {
//        if (x < bminX || x > bmaxX || y < bminY || y > bmaxY) return fillValue[band];//-- no interpolation available
        x = Math.round(x);
        y = Math.round(y);
        pixelIterator.moveTo((int) Math.min(maxxId, x), (int) Math.min(maxyId, y), band);
        return pixelIterator.getSampleDouble();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double[] interpolate(double x, double y) {
//        if (x < bminX || x > bmaxX || y < bminY || y > bmaxY) return fillValue;//-- no interpolation available
        x = Math.round(x);
        y = Math.round(y);
        pixelIterator.moveTo((int) Math.min(maxxId, x), (int) Math.min(maxyId, y), 0);
        result[0] = pixelIterator.getSampleDouble();
        for (int band = 1; band < numBands; band++) {
            pixelIterator.next();
            result[band] = pixelIterator.getSampleDouble();
        }
        return result;
    }
}
