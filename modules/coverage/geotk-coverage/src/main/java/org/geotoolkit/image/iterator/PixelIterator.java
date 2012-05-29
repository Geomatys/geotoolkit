/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.geotoolkit.util.ArgumentChecks;

/**
 * Define standard iterator for image pixel.
 *
 * Iteration order is define in sub-classes implementation.
 * However iteration begging by Bands.
 *
 * Moreover comportment not specify if iterator exceed image limits.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class PixelIterator {

    /**
     * Current raster which is followed by Iterator.
     */
    protected Raster currentRaster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    protected RenderedImage renderedImage;

    /**
     * The X coordinate of the upper-left pixel of iteration area.
     */
    protected final int areaIterateMinX;

    /**
     * The Y coordinate of the upper-left pixel of iteration area.
     */
    protected final int areaIterateMinY;

    /**
     * The X coordinate of the lower-right pixel of iteration area.
     */
    protected final int areaIterateMaxX;

    /**
     * The X coordinate of the lower-right pixel of iteration area.
     */
    protected final int areaIterateMaxY;

    /**
     * Number of raster band .
     */
    protected int numBand;

    /**
     * The X coordinate of the upper-left pixel of this current raster.
     */
    protected int minX;

    /**
     * The Y coordinate of the upper-left pixel of this current raster.
     */
    protected int minY;

    /**
     * The X coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxY;

    /**
     * Current band position in this current raster.
     */
    protected int band;

    /**
     * The X index coordinate of the upper-left tile of this rendered image.
     */
    protected int tMinX;

    /**
     * The Y index coordinate of the upper-left tile of this rendered image.
     */
    protected int tMinY;

    /**
     * The X index coordinate of the bottom-right tile of this rendered image.
     */
    protected int tMaxX;

    /**
     * The Y index coordinate of the bottom-right tile of this rendered image.
     */
    protected int tMaxY;

    /**
     * The X coordinate of the sub-Area upper-left corner.
     */
    protected int subAreaMinX;

    /**
     * The Y coordinate of the sub-Area upper-left corner.
     */
    protected int subAreaMinY;

    /**
     * The X index coordinate of the sub-Area bottom-right corner.
     */
    protected int subAreaMaxX;

    /**
     * The Y index coordinate of the sub-Area bottom-right corner.
     */
    protected int subAreaMaxY;

    /**
     * Current x tile position in rendered image tile array.
     */
    protected int tX;
    /**
     * Current y tile position in rendered image tile array.
     */
    protected int tY;

    /**
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    PixelIterator(final Raster raster) {
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.currentRaster = raster;
        this.numBand       = raster.getNumBands();
        this.minX  = this.areaIterateMinX = raster.getMinX();
        this.minY  = this.areaIterateMinY = raster.getMinY();
        this.maxY  = this.areaIterateMaxX = minY + raster.getHeight();
        this.maxX  = this.areaIterateMaxY =minX + raster.getWidth();
        this.band  = -1;
        this.tX = this.tY    = 0;
        this.tMaxX = this.tMaxY = 1;
    }

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    PixelIterator(final Raster raster, final Rectangle subArea) {
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.currentRaster = raster;
        final int minx  = raster.getMinX();
        final int miny  = raster.getMinY();
        final int maxx  = minx + raster.getWidth();
        final int maxy  = miny + raster.getHeight();
        //subarea attributs
        this.subAreaMinX = subArea.x;
        this.subAreaMinY = subArea.y;
        this.subAreaMaxX = subAreaMinX + subArea.width;
        this.subAreaMaxY = subAreaMinY + subArea.height;

        this.numBand    = raster.getNumBands();
        this.minX       = this.areaIterateMinX = Math.max(subAreaMinX, minx);
        this.minY       = this.areaIterateMinY = Math.max(subAreaMinY, miny);
        this.maxX       = this.areaIterateMaxX = Math.min(subAreaMaxX, maxx);
        this.maxY       = this.areaIterateMaxY = Math.min(subAreaMaxY, maxy);
        if(minX > maxX || minY > maxY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and raster"+raster+subArea);
        this.band = -1;
        tX = tY  = 0;
        tMaxX = tMaxY = 1;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     */
    PixelIterator(final RenderedImage renderedImage) {
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        this.renderedImage = renderedImage;
        //rect attributs
        this.subAreaMinX = this.areaIterateMinX = renderedImage.getMinX();
        this.subAreaMinY = this.areaIterateMinY = renderedImage.getMinY();
        this.subAreaMaxX = this.areaIterateMaxX = this.subAreaMinX + renderedImage.getWidth();
        this.subAreaMaxY = this.areaIterateMaxY = this.subAreaMinY + renderedImage.getHeight();
        //tiles attributs
        this.tMinX = renderedImage.getMinTileX();
        this.tMinY = renderedImage.getMinTileY();
        this.tMaxX = tMinX + renderedImage.getNumXTiles();
        this.tMaxY = tMinY + renderedImage.getNumYTiles();
        //initialize attributs to first iteration
        this.numBand = this.maxY = this.maxX= 1;
        this.tY = tMinY - 1;
        this.tX = tMaxX - 1;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    PixelIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.renderedImage = renderedImage;
        //rect attributs
        this.subAreaMinX = subArea.x;
        this.subAreaMinY = subArea.y;
        this.subAreaMaxX = this.subAreaMinX + subArea.width;
        this.subAreaMaxY = this.subAreaMinY + subArea.height;

        final int rIminX = renderedImage.getMinX();
        final int rIminY = renderedImage.getMinY();
        final int rImaxX = rIminX + renderedImage.getWidth();
        final int rImaxY = rIminY + renderedImage.getHeight();

        //areaIterate
        this.areaIterateMinX = Math.max(subAreaMinX, rIminX);
        this.areaIterateMinY = Math.max(subAreaMinY, rIminY);
        this.areaIterateMaxX = Math.min(subAreaMaxX, rImaxX);
        this.areaIterateMaxY = Math.min(subAreaMaxY, rImaxY);


        //define min max intervals
        final int minIAX = Math.max(rIminX, subAreaMinX) - rIminX;
        final int minIAY = Math.max(rIminY, subAreaMinY) - rIminY;
        final int maxIAX = Math.min(rImaxX, subAreaMaxX) - rIminX;
        final int maxIAY = Math.min(rImaxY, subAreaMaxY) - renderedImage.getMinY();

        //intersection test
        if (minIAX > maxIAX || minIAY > maxIAY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and RenderedImage"+renderedImage+subArea);

        //tiles attributs
        final int rITWidth   = renderedImage.getTileWidth();
        final int rITHeight  = renderedImage.getTileHeight();
        final int rIMinTileX = renderedImage.getMinTileX();
        final int rIMinTileY = renderedImage.getMinTileY();
        this.tMinX = minIAX / rITWidth  + rIMinTileX;
        this.tMinY = minIAY / rITHeight + rIMinTileY;


        this.tMaxX = (maxIAX + rITWidth - 1) / rITWidth  + rIMinTileX;
        this.tMaxY = (maxIAY + rITHeight-1) / rITHeight + rIMinTileY;
        this.tY = tMinY-1;
        //initialize attributs to first iteration
        this.numBand = this.maxY = this.maxX = 1;
        this.tX = tMaxX - 1;

    }

    /**
     * Returns true if the iteration has more pixel(in other words if {@link PixelIterator#nextSample() } is possible)
     * and move forward iterator.
     *
     * @return true if next value exist else false.
     */
    public abstract boolean next();

    /**
     * Returns next X iterator coordinate without move forward it.
     *
     * @return X iterator position.
     */
    public abstract int getX();

    /**
     * Returns next Y iterator coordinate without move forward it.
     *
     * @return Y iterator position.
     */
    public abstract int getY();

    /**
     * Returns the next integer value from iteration.
     *
     * @return the next integer value.
     */
    public abstract int getSample();

    /**
     * Returns the next float value from iteration.
     *
     * @return the next float value.
     */
    public abstract float getSampleFloat();

    /**
     * Returns the next double value from iteration.
     *
     * @return the next double value.
     */
    public abstract double getSampleDouble();

    /**
     * Initializes iterator.
     * Carry back iterator at its initial position like iterator is just build.
     */
    public abstract void rewind();

    /**
     * Write integer value at current iterator position.
     *
     * @param value integer to write.
     */
    public abstract void setSample(final int value);

    /**
     * Write float value at current iterator position.
     *
     * @param value float to write.
     */
    public abstract void setSampleFloat(final float value);

    /**
     * Write double value at current iterator position.
     *
     * @param value double to write.
     */
    public abstract void setSampleDouble(final double value);

    /**
     * To release last tiles iteration from writable rendered image tiles array.
     * if this method is invoked in read-only iterator, method is idempotent (has no effect).
     */
    public abstract void close();

    /**
     * Move forward iterator cursor at x, y coordinates. Cursor is automatically
     * positioned just before first band index.
     *
     * User must call next() method before get() or set() method. Code example :
     * {@code
     *       PixelIterator.moveTo(x, y);
     *       while (PixelIterator.next()) {
     *            PixelIterator.getSample();//for example
     *       }
     * }
     *
     * MoveTo method is configure to use while loop after moveTo call.
     *
     * @param x the x coordinate cursor position.
     * @param y the y coordinate cursor position.
     * @throws IllegalArgumentException if coordinates are out of iteration area boundary.
     */
    public void moveTo(int x, int y){
        if (x < areaIterateMinX || x >= areaIterateMaxX
            ||  y < areaIterateMinY || y >= areaIterateMaxY)
                throw new IllegalArgumentException("coordinate out of iteration area define by: "
                        +"("+areaIterateMinX+", "+areaIterateMinY+")"+" ; ("+areaIterateMaxX+", "+areaIterateMaxY+")");
    }
}
