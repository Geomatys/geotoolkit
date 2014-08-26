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
package org.geotoolkit.image.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.opengis.coverage.grid.SequenceType;

/**
 * An Iterator for traversing anyone rendered Image with Byte type data.
 * <p>
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; next X tile position in rendered image tiles array
 * --&gt; current tiles y coordinates --&gt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Furthermore iterator directly read in data table within raster {@code DataBuffer}.
 *
 * No build is allowed for single {@link java.awt.image.Raster} browsing, because {@link org.geotoolkit.image.iterator.DefaultDirectIterator}
 * will do the job as fast as it, and with the same behavior.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Alexis Manin        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class RowMajorDirectIterator extends DefaultDirectIterator {

    /**
     * Current position in Y axis of the parsed image.
     */
    protected int currentY;

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    RowMajorDirectIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super(renderedImage, subArea);
        currentY = areaIterateMinY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        band = ++band % rasterNumBand;
        // We reach current raster line end.
        if (band == 0 && ++currentX >= maxX) {
            // We check if there's more tiles in the same row. If not, we go to the next one.
            if (++tX >= tMaxX) {
                tX = tMinX;
                // If we reach raster limit in y axis, we must take next raster below. We also check if we reached end
                // of iteration area.
                if (++currentY >= maxY && ++tY >= tMaxY) {
                    return false;
                }
            }
            updateCurrentRaster(tX, tY);

        } else {
            dataCursor += bandSteps[band];
        }

        return true;
    }

    @Override
    public int getY() {
        return currentY;
    }

    @Override
    public void rewind() {
        super.rewind();
        currentY = areaIterateMinY;
    }

    @Override
    protected void updateCurrentRaster(int tileX, int tileY) {
        super.updateCurrentRaster(tileX, tileY);
        // Row major iterator can update raster to position NOT at the first line or iteration minimum row.We must
        // ensure we don't have to get on a specific line.
        if (currentY > minY) {
            dataCursor += (currentY - minY) * scanLineStride;
        }
    }

    @Override
    public void moveTo(int x, int y, int b) {
        super.moveTo(x, y, b);
        currentY = y;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SequenceType getIterationDirection() {
        return SequenceType.LINEAR;
    }
}
