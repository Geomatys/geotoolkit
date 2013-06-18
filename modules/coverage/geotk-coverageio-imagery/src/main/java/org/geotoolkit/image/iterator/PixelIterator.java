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
import java.awt.image.*;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.coverage.grid.SequenceType;

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
     * Current raster which is followed by Iterator.
     */
    protected Raster currentRaster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    protected final RenderedImage renderedImage;

    /**
     * Number of band.
     */
    protected final int fixedNumBand;
    
    /**
     * Number of raster band.
     * WARNING ! this is used a bit everywhere in iterator as a 'updateTileRaster' flag.
     */
    protected int rasterNumBand;

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
     * Current x tile position in rendered image tile array.
     */
    protected int tX;

    /**
     * Current y tile position in rendered image tile array.
     */
    protected int tY;

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    PixelIterator(final Raster raster, final Rectangle subArea) {
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.currentRaster = raster;
        this.renderedImage = null;
        final int minx  = raster.getMinX();
        final int miny  = raster.getMinY();
        final int maxx  = minx + raster.getWidth();
        final int maxy  = miny + raster.getHeight();

        if (subArea != null) {
            final int sAMX = subArea.x;
            final int sAMY = subArea.y;
            //intersection
            this.areaIterateMinX = Math.max(sAMX, minx);
            this.areaIterateMinY = Math.max(sAMY, miny);
            this.maxX = this.areaIterateMaxX = Math.min(sAMX + subArea.width, maxx);
            this.maxY = this.areaIterateMaxY = Math.min(sAMY + subArea.height, maxy);
        } else {
            //areaIterate
            this.areaIterateMinX = minx;
            this.areaIterateMinY = miny;
            this.areaIterateMaxX = maxx;
            this.areaIterateMaxY = maxy;
        }
        this.rasterNumBand = raster.getNumBands();
        this.fixedNumBand = this.rasterNumBand;
        if(areaIterateMinX > areaIterateMaxX || areaIterateMinY > areaIterateMaxY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and raster"+raster+subArea);
        this.band = -1;
        tX = tY  = 0;
        tMaxX = tMaxY = 1;
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
        this.renderedImage = renderedImage;

        final int rIminX = renderedImage.getMinX();
        final int rIminY = renderedImage.getMinY();
        final int rImaxX = rIminX + renderedImage.getWidth();
        final int rImaxY = rIminY + renderedImage.getHeight();

        final int rITWidth   = renderedImage.getTileWidth();
        final int rITHeight  = renderedImage.getTileHeight();
        final int rIMinTileX = renderedImage.getMinTileX();
        final int rIMinTileY = renderedImage.getMinTileY();

        if (subArea != null) {
            final int sAMX = subArea.x;
            final int sAMY = subArea.y;
            //intersection
            this.areaIterateMinX = Math.max(sAMX, rIminX);
            this.areaIterateMinY = Math.max(sAMY, rIminY);
            this.areaIterateMaxX = Math.min(sAMX + subArea.width, rImaxX);
            this.areaIterateMaxY = Math.min(sAMY + subArea.height, rImaxY);
        } else {
            //areaIterate
            this.areaIterateMinX = rIminX;
            this.areaIterateMinY = rIminY;
            this.areaIterateMaxX = rImaxX;
            this.areaIterateMaxY = rImaxY;
        }

        //intersection test
        if (areaIterateMinX > areaIterateMaxX || areaIterateMinY > areaIterateMaxY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and RenderedImage"+renderedImage+subArea);

        //tiles attributs
        this.tMinX = (areaIterateMinX - rIminX) / rITWidth  + rIMinTileX;
        this.tMinY = (areaIterateMinY - rIminY) / rITHeight + rIMinTileY;
        this.tMaxX = (areaIterateMaxX - rIminX + rITWidth - 1)  / rITWidth  + rIMinTileX;
        this.tMaxY = (areaIterateMaxY - rIminY + rITHeight - 1) / rITHeight + rIMinTileY;

        //initialize attributs to first iteration
        this.rasterNumBand = this.maxY = this.maxX = 1;
        this.fixedNumBand = renderedImage.getSampleModel().getNumBands();
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
     * User must call next() method before getX() method.
     *
     * @return X iterator position.
     */
    public abstract int getX();

    /**
     * Returns next Y iterator coordinate without move forward it.
     * User must call next() method before getY() method.
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
     * Return type of sequence iteration direction.
     *
     * @return type of sequence.
     */
    public abstract SequenceType getIterationDirection();

    /**
     * <p>Move forward iterator cursor at x, y coordinates. Cursor is automatically
     * positioned at band index.<br/>
     *
     * Code example :<br/>
     * {@code PixelIterator.moveTo(x, y, b);}<br/>
     *
     * {@code       do} {<br/>
     * {@code           PixelIterator.getSample();//for example}<br/>
     *        } {@code while (PixelIterator.next());}<br/>
     *
     * MoveTo method is configure to use do...while() loop after moveTo call.</p>
     *
     * @param x the x coordinate cursor position.
     * @param y the y coordinate cursor position.
     * @param b the band index cursor position.
     * @throws IllegalArgumentException if coordinates are out of iteration area boundary.
     */
    public void moveTo(int x, int y, int b){
        if (x < areaIterateMinX || x >= areaIterateMaxX
            ||  y < areaIterateMinY || y >= areaIterateMaxY)
                throw new IllegalArgumentException("coordinate out of iteration area define by: "
                        +"("+areaIterateMinX+", "+areaIterateMinY+")"+" ; ("+areaIterateMaxX+", "+areaIterateMaxY+") given coord is "+x+" "+y);
        if (b<0 || b>=fixedNumBand)
            throw new IllegalArgumentException("band index out of numband border define by: [0;"+fixedNumBand+"]");
    }

    /**
     * Returns the number of bands (samples per pixel) from Image or Raster within this Iterator.
     *
     * @return the number of bands (samples per pixel) from current raster or Image.
     */
    public int getNumBands() {
        return fixedNumBand;
    }

    /**
     * Returns {@code Rectangle} which is Image or Raster boundary within this Iterator.
     *
     * @param areaIterate true to get area iterate boundary, false to get boundary of object that iterate.
     * @return {@code Rectangle} which is Image or Raster boundary within this Iterator.
     */
    public Rectangle getBoundary(final boolean areaIterate) {
        if (areaIterate)
            return new Rectangle(areaIterateMinX, areaIterateMinY, areaIterateMaxX-areaIterateMinX, areaIterateMaxY-areaIterateMinY);
        int x, y, w, h;
        if (renderedImage == null) {
            x = currentRaster.getMinX();
            y = currentRaster.getMinY();
            w = currentRaster.getWidth();
            h = currentRaster.getHeight();
        } else {
            x = renderedImage.getMinX();
            y = renderedImage.getMinY();
            w = renderedImage.getWidth();
            h = renderedImage.getHeight();
        }
        return new Rectangle(x, y, w, h);
    }

    /**
     * Verify raster conformity.
     */
    protected void checkRasters(final Raster readableRaster, final WritableRaster writableRaster){
        //raster dimension
        if (readableRaster.getMinX()     != writableRaster.getMinX()
         || readableRaster.getMinY()     != writableRaster.getMinY()
         || readableRaster.getWidth()    != writableRaster.getWidth()
         || readableRaster.getHeight()   != writableRaster.getHeight()
         || readableRaster.getNumBands() != writableRaster.getNumBands())
         throw new IllegalArgumentException("raster and writable raster are not in same dimension"+readableRaster+writableRaster);
        //raster data type
        if (readableRaster.getDataBuffer().getDataType() != writableRaster.getDataBuffer().getDataType())
            throw new IllegalArgumentException("raster and writable raster haven't got same datas type");
    }

    /**
     * Verify Rendered image conformity.
     */
    protected void checkRenderedImage(final RenderedImage renderedImage, final WritableRenderedImage writableRI) {
        //image dimensions
        if (renderedImage.getMinX()   != writableRI.getMinX()
         || renderedImage.getMinY()   != writableRI.getMinY()
         || renderedImage.getWidth()  != writableRI.getWidth()
         || renderedImage.getHeight() != writableRI.getHeight()
         || renderedImage.getSampleModel().getNumBands() != writableRI.getSampleModel().getNumBands())
         throw new IllegalArgumentException("rendered image and writable rendered image dimensions are not conform"+renderedImage+writableRI);
        final int wrimtx = writableRI.getMinTileX();
        final int wrimty = writableRI.getMinTileY();
        final int rimtx  = writableRI.getMinTileX();
        final int rimty  = writableRI.getMinTileY();
        //tiles dimensions
        if (rimtx != wrimtx
         || rimty != wrimty
         || renderedImage.getNumXTiles() != writableRI.getNumXTiles()
         || renderedImage.getNumYTiles() != writableRI.getNumYTiles()
         || renderedImage.getTileGridXOffset() != writableRI.getTileGridXOffset()
         || renderedImage.getTileGridYOffset() != writableRI.getTileGridYOffset()
         || renderedImage.getTileHeight() != writableRI.getTileHeight()
         || renderedImage.getTileWidth()  != writableRI.getTileWidth())
            throw new IllegalArgumentException("rendered image and writable rendered image tiles configuration are not conform"+renderedImage+writableRI);
        //data type
        if (renderedImage.getTile(rimtx, rimty).getDataBuffer().getDataType() != writableRI.getTile(wrimtx, wrimty).getDataBuffer().getDataType())
            throw new IllegalArgumentException("rendered image and writable rendered image haven't got same datas type");
    }

    /**
     * Verify raster conformity.
     */
    protected void checkRasters(final Raster readableRaster, final WritableRaster writableRaster, final Rectangle subArea) {
        final int wRmx = writableRaster.getMinX();
        final int wRmy = writableRaster.getMinY();
        final int wRw  = writableRaster.getWidth();
        final int wRh  = writableRaster.getHeight();
        if ((wRmx != areaIterateMinX)
          || wRmy != areaIterateMinY
          || wRw  != areaIterateMaxX - areaIterateMinX
          || wRh  != areaIterateMaxY - areaIterateMinY)

        //raster dimension
        if ((readableRaster.getMinX()   != wRmx)
          || readableRaster.getMinY()   != wRmy
          || readableRaster.getWidth()  != wRw
          || readableRaster.getHeight() != wRh)
         throw new IllegalArgumentException("raster and writable raster are not in same dimension"+readableRaster+writableRaster);

        if (readableRaster.getNumBands() != writableRaster.getNumBands())
            throw new IllegalArgumentException("raster and writable raster haven't got same band number");
        //raster data type
        if (readableRaster.getDataBuffer().getDataType() != writableRaster.getDataBuffer().getDataType())
            throw new IllegalArgumentException("raster and writable raster haven't got same datas type");
    }

    /**
     * Verify Rendered image conformity.
     */
    protected void checkRenderedImage(final RenderedImage renderedImage, final WritableRenderedImage writableRI, final Rectangle subArea) {
        if (renderedImage.getSampleModel().getNumBands() != writableRI.getSampleModel().getNumBands())
            throw new IllegalArgumentException("renderedImage and writableRenderedImage haven't got same band number");
        final int riMinX   = renderedImage.getMinX();
        final int riMinY   = renderedImage.getMinY();
        final int riTileWidth = renderedImage.getTileWidth();
        final int riTileHeight = renderedImage.getTileHeight();
        final int rimtx  = renderedImage.getMinTileX();
        final int rimty  = renderedImage.getMinTileY();

        final int wrimtx = writableRI.getMinTileX();
        final int wrimty = writableRI.getMinTileY();

        //data type
        if (renderedImage.getTile(rimtx, rimty).getDataBuffer().getDataType() != writableRI.getTile(wrimtx, wrimty).getDataBuffer().getDataType())
            throw new IllegalArgumentException("rendered image and writable rendered image haven't got same datas type");

        //tiles dimensions
        if (renderedImage.getTileHeight() != writableRI.getTileHeight()
         || renderedImage.getTileWidth()  != writableRI.getTileWidth()
         || renderedImage.getTileGridXOffset() != writableRI.getTileGridXOffset()
         || renderedImage.getTileGridYOffset() != writableRI.getTileGridYOffset())
            throw new IllegalArgumentException("rendered image and writable rendered image tiles configuration are not conform"+renderedImage+writableRI);

        //verifier les index de tuiles au depart
        final boolean minTileX = (wrimtx == (areaIterateMinX-riMinX)/riTileWidth+rimtx);
        final boolean minTileY = (wrimty == (areaIterateMinY-riMinY)/riTileHeight+rimty);

        //writable image correspond with iteration area
        if (writableRI.getMinX()  != areaIterateMinX    //areaiteration
         || writableRI.getMinY()  != areaIterateMinY    //areaiteration
         || writableRI.getWidth() != areaIterateMaxX-areaIterateMinX//longueuriteration
         || writableRI.getHeight()!= areaIterateMaxY-areaIterateMinY//largeuriteration
         || !minTileX || !minTileY )

        //image dimensions
        if (renderedImage.getMinX()   != writableRI.getMinX()
         || renderedImage.getMinY()   != writableRI.getMinY()
         || renderedImage.getWidth()  != writableRI.getWidth()
         || renderedImage.getHeight() != writableRI.getHeight()
         || rimtx != wrimtx || rimty != wrimty
         || renderedImage.getNumXTiles() != writableRI.getNumXTiles()
         || renderedImage.getNumYTiles() != writableRI.getNumYTiles())
         throw new IllegalArgumentException("rendered image and writable rendered image dimensions are not conform"+renderedImage+writableRI);
    }

    /**
     * Return type data from iterate source.
     * @return type data from iterate source.
     */
    public int getSourceDatatype() {
        return (renderedImage == null) ? currentRaster.getSampleModel().getDataType() : renderedImage.getSampleModel().getDataType();
    }
}
