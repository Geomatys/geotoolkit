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
import java.awt.image.RenderedImage;
import org.opengis.coverage.grid.SequenceType;

/**
 * {@inheritDoc }
 *
 * An Iterator for traversing anyone rendered Image.
 * <p>
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; next X tile position in rendered image tiles array
 * --&gt; current tiles y coordinates --&gt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Code example :
 * {@code
 *                  final RowMajorIterator dRII = new RowMajorIterator(renderedImage);
 *                  while (dRII.next()) {
 *                      dRii.getSample();
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
class RowMajorIterator extends PixelIterator{

    /**
     * Current X pixel coordinate in this current raster.
     */
    protected int x;

    /**
     * Current Y pixel coordinate in this current raster.
     */
    protected int y;

    /**
     * Create row-major rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea Rectangle which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image.
     */
    RowMajorIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super(renderedImage, subArea);
        //initialize attributs to first iteration
        this.rasterNumBand = this.maxX = this.maxY = 1;
        this.tY = tMinY - 1;
        this.tX = tMaxX - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++band == rasterNumBand) {
            band = 0;
            if (++x == maxX) {
                if (++tX == tMaxX) {
                    tX = tMinX;
                    if (++y == maxY) {
                        if(++tY == tMaxY) return false;
                        //initialize from new tile(raster) after tiles Y move.
                        updateCurrentRaster(tX, tY);
                        final int cRMinY = currentRaster.getMinY();
                        this.y    = Math.max(areaIterateMinY, cRMinY);
                        this.maxY = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight());
                    }
                }
                //initialize from new tile(raster) after tiles X move.
                updateCurrentRaster(tX, tY);
                final int cRMinX = currentRaster.getMinX();
                this.x = Math.max(areaIterateMinX, cRMinX);
                this.maxX = Math.min(areaIterateMaxX, cRMinX + currentRaster.getWidth());
                this.rasterNumBand = currentRaster.getNumBands();
            }
        }
        return (x <= maxX && y <= maxY);
    }

    /**
     * Update current raster from tiles array coordinates.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY){
        currentRaster = renderedImage.getTile(tileX, tileY);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        this.x    = this.y    = this.band    = 0;
        this.maxX = this.maxY = this.rasterNumBand = 1;
        this.tX = tMaxX - 1;
        this.tY = tMinY - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getSample() {
        return currentRaster.getSample(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public float getSampleFloat() {
        return currentRaster.getSampleFloat(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSampleDouble() {
        return currentRaster.getSampleDouble(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void moveTo(int x, int y, int b) {
        super.moveTo(x, y, b);
        final int riMinX = renderedImage.getMinX();
        final int riMinY = renderedImage.getMinY();
        tX = (x - riMinX) / renderedImage.getTileWidth() + renderedImage.getMinTileX();
        tY = (y - riMinY) / renderedImage.getTileHeight() + renderedImage.getMinTileY();
        updateCurrentRaster(tX, tY);
        final int cRMinX = currentRaster.getMinX();
        final int cRMinY = currentRaster.getMinY();
        this.maxX = Math.min(areaIterateMaxX, cRMinX + currentRaster.getWidth());
        this.maxY = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight());
        this.rasterNumBand = currentRaster.getNumBands();
        this.x = x;
        this.y = y;
        this.band = b;// -1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SequenceType getIterationDirection() {
        return SequenceType.LINEAR;
    }
}
