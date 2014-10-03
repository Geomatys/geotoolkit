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
import java.io.Closeable;
import java.nio.Buffer;

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
 * TODO : Move setSample* methods in a separate WritablePixelIterator interface.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class PixelIterator implements Closeable {

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
     * Tile width of object that iterate.
     * @see RenderedImage#getTileWidth() 
     * @see Raster#getWidth() 
     */
    protected final int tileWidth;
    
    /**
     * Tile height of object that iterate.
     * @see RenderedImage#getTileHeight() 
     * @see Raster#getHeight() 
     */
    protected final int tileHeight;
    
    /**
     * Define area travel by iterator in the current object that iterate.
     * @see #getBoundary(boolean) 
     */
    protected Rectangle areaIterate;
    
    /**
     * Define area of the object that iterate.
     * @see #getBoundary(boolean) 
     */
    protected Rectangle generalObjectArea;
    
    /**
     * {@link SampleModel} from the iterate object.
     */
    protected final SampleModel currentSampleModel;

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
        tileWidth       = raster.getWidth();
        tileHeight      = raster.getHeight();
        final int maxx  = minx + tileWidth;
        final int maxy  = miny + tileHeight;
        
        currentSampleModel = raster.getSampleModel();

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
        if(areaIterateMinX >= areaIterateMaxX || areaIterateMinY >= areaIterateMaxY)
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

        tileWidth        = renderedImage.getTileWidth();
        tileHeight       = renderedImage.getTileHeight();
        final int rIMinTileX = renderedImage.getMinTileX();
        final int rIMinTileY = renderedImage.getMinTileY();
        
        currentSampleModel = renderedImage.getSampleModel();

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
        if (areaIterateMinX >= areaIterateMaxX || areaIterateMinY >= areaIterateMaxY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and RenderedImage"+renderedImage+subArea);

        //tiles attributs
        this.tMinX = (areaIterateMinX - rIminX) / tileWidth  + rIMinTileX;
        this.tMinY = (areaIterateMinY - rIminY) / tileHeight + rIMinTileY;
        this.tMaxX = (areaIterateMaxX - rIminX + tileWidth - 1)  / tileWidth  + rIMinTileX;
        this.tMaxY = (areaIterateMaxY - rIminY + tileHeight - 1) / tileHeight + rIMinTileY;

        //initialize attributs to first iteration
        this.rasterNumBand = this.maxY = this.maxX = 1;
        this.fixedNumBand = currentSampleModel.getNumBands();
    }

    /**
     * Returns true if the iteration has more pixel(in other words if {@linkplain #next()} is possible)
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
        if (areaIterate){
            if (this.areaIterate == null) this.areaIterate = new Rectangle(areaIterateMinX, areaIterateMinY, areaIterateMaxX-areaIterateMinX, areaIterateMaxY-areaIterateMinY);
            return this.areaIterate;
        }
        if (generalObjectArea == null) {
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
            generalObjectArea = new Rectangle(x, y, w, h);
        }    
        return generalObjectArea;
    }

    /**
     * Return {@link RenderedImage} that this iterator travel. 
     * 
     * @return {@link RenderedImage} that this iterator travel. 
     */
    public RenderedImage getRenderedImage() {
        return renderedImage;
    }

    /**
     * Check that the two input rasters are compatible for coupling in a {@link WritablePixelIterator}
     */
    public static void checkRasters(final Raster readableRaster, final WritableRaster writableRaster){
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
    public static void checkRenderedImage(final RenderedImage renderedImage, final WritableRenderedImage writableRI) {
        //image dimensions
        if (renderedImage.getMinX()   != writableRI.getMinX()
         || renderedImage.getMinY()   != writableRI.getMinY()
         || renderedImage.getWidth()  != writableRI.getWidth()
         || renderedImage.getHeight() != writableRI.getHeight()
         || renderedImage.getSampleModel().getNumBands() != writableRI.getSampleModel().getNumBands())
         throw new IllegalArgumentException("rendered image and writable rendered image dimensions are not conform.\n" +
                 "First : "+renderedImage+"\nSecond : "+writableRI);
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
            throw new IllegalArgumentException("rendered image and writable rendered image tiles configuration are not conform.\n" +
                    "First : "+renderedImage+"\nSecond : "+writableRI);
        //data type
        // TODO : Should be required only for Direct iterators (working directly with data buffers)
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
     * Fill given buffer with samples within the given area at the specified image band.
     * Adapted for a {@link PixelInterleavedSampleModel} {@link SampleModel} type.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     * @param band the interest band.
     */
    private void getAreaByInterleaved(final Rectangle area, final Object buffer, final int band) {
        rewind();
        final Rectangle generalArea = getBoundary(false);//-- compute one time
        final int sourceDataType = getSourceDatatype();
        
        final int minTX, minTY, maxTX, maxTY;
        
        if (renderedImage != null) {
            minTX = tMinX + (area.x - generalArea.x) / tileWidth;
            minTY = tMinY + (area.y - generalArea.y) / tileHeight;
            maxTX = tMinX + (area.x + area.width + tileWidth - 1) / tileWidth;
            maxTY = tMinY + (area.y + area.height + tileHeight - 1) / tileHeight;
        } else {
            minTX = minTY = 0;
            maxTX = maxTY = 1;
        }
        
        for (int ty = minTY; ty < maxTY; ty++) {
            for (int tx = minTX; tx < maxTX; tx++) {

                //-- intersection sur 
                final Raster rast = (renderedImage != null) ? renderedImage.getTile(tx, ty) : currentRaster;
                final int minX = Math.max(rast.getMinX(), area.x);
                final int minY = Math.max(rast.getMinY(), area.y);
                final int maxX = Math.min(rast.getMinX() + tileWidth, area.x + area.width);
                final int maxY = Math.min(rast.getMinY() + tileHeight, area.y + area.height);
                if (minX > maxY || minY > maxY) throw new IllegalArgumentException("Expected area don't intersect internal data.");
                
                final int readLength = (maxX - minX);
                int destId = 0;
                for (int y = minY; y < maxY; y++) {
                    moveTo(minX, y, band);
                    int s = 0;
                    int id = destId;
                    while (s < readLength) {
                        
                        switch (sourceDataType) {
                            case DataBuffer.TYPE_BYTE : {
                                ((byte[])buffer)[id++] = (byte) getSample();
                                break; 
                            }
                            case DataBuffer.TYPE_USHORT :
                            case DataBuffer.TYPE_SHORT  : {
                                ((short[])buffer)[id++] = (short) getSample();
                                break;
                            }
                            case DataBuffer.TYPE_INT : {
                                ((int[])buffer)[id++] = getSample();
                                break;
                            }
                            case DataBuffer.TYPE_FLOAT : {
                                ((float[])buffer)[id++] = getSampleFloat();
                                break;
                            }
                            case DataBuffer.TYPE_DOUBLE : {
                                ((double[])buffer)[id++] = getSampleDouble();
                                break;
                            }
                            default : {
                                throw new IllegalStateException("Unknow datatype.");
                            }
                        }
                        int b = 0;
                        while (next()) {
                            if (++b == getNumBands()) break;
                        }
                        s++;
                    }
                    destId    += area.width;
                }
            }
        }
    }
    
    
    /**
     * Fill given buffer with samples within the given area and from all image band.
     * Adapted for a {@link PixelInterleavedSampleModel} {@link SampleModel} type.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     */
    private void getAreaByInterleaved(final Rectangle area, final Object[] buffer) {
        rewind();
        final Rectangle generalArea = getBoundary(false);//-- compute one time
        final int sourceDataType = getSourceDatatype();
        
        final int minTX, minTY, maxTX, maxTY;
        
        if (renderedImage != null) {
            minTX = tMinX + (area.x - generalArea.x) / tileWidth;
            minTY = tMinY + (area.y - generalArea.y) / tileHeight;
            maxTX = tMinX + (area.x + area.width + tileWidth - 1) / tileWidth;
            maxTY = tMinY + (area.y + area.height + tileHeight - 1) / tileHeight;
        } else {
            minTX = minTY = 0;
            maxTX = maxTY = 1;
        }
        
        for (int ty = minTY; ty < maxTY; ty++) {
            for (int tx = minTX; tx < maxTX; tx++) {

                //-- intersection sur 
                final Raster rast = (renderedImage != null) ? renderedImage.getTile(tx, ty) : currentRaster;
                final int minX = Math.max(rast.getMinX(), area.x);
                final int minY = Math.max(rast.getMinY(), area.y);
                final int maxX = Math.min(rast.getMinX() + tileWidth, area.x + area.width);
                final int maxY = Math.min(rast.getMinY() + tileHeight, area.y + area.height);
                if (minX > maxY || minY > maxY) throw new IllegalArgumentException("Expected area don't intersect internal data.");

                final int readLength = (maxX - minX);
                int destId = 0;
                for (int y = minY; y < maxY; y++) {
                    moveTo(minX, y, 0);
                    int s = 0;
                    int id = destId;
                    while (s < readLength) {
                        int b = 0;
                        while (b < getNumBands()) {
                            switch (sourceDataType) {
                                case DataBuffer.TYPE_BYTE : {
                                    ((byte[])buffer[b])[id++] = (byte) getSample();
                                    break; 
                                }
                                case DataBuffer.TYPE_USHORT :
                                case DataBuffer.TYPE_SHORT  : {
                                    ((short[])buffer[b])[id++] = (short) getSample();
                                    break;
                                }
                                case DataBuffer.TYPE_INT : {
                                    ((int[])buffer[b])[id++] = getSample();
                                    break;
                                }
                                case DataBuffer.TYPE_FLOAT : {
                                    ((float[])buffer[b])[id++] = getSampleFloat();
                                    break;
                                }
                                case DataBuffer.TYPE_DOUBLE : {
                                    ((double[])buffer[b])[id++] = getSampleDouble();
                                    break;
                                }
                                default : {
                                    throw new IllegalStateException("Unknow datatype.");
                                }
                            }
                            b++;
                            next();
                        }
                        s++;
                    }
                    destId    += area.width;
                }
            }
        }
    }
    
    /**
     * Fill given buffer with samples within the given area and from all image band.
     * Adapted for a {@link BandedSampleModel} {@link SampleModel} type.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     */
    private void getAreaByBanded (final Rectangle area, final Object[] buffer) {
        
        final ComponentSampleModel compSM = (ComponentSampleModel) currentSampleModel;
        final int[] bankIndices = compSM.getBankIndices();
        assert bankIndices.length == getNumBands();
        final int[] bandOffsets = compSM.getBandOffsets();
        assert bandOffsets.length == getNumBands();
        
        final Rectangle generalArea = getBoundary(false);//-- compute one time
        final int sourceDataType = getSourceDatatype();
        
        final int minTX, minTY, maxTX, maxTY;
        
        if (renderedImage != null) {
            minTX = tMinX + (area.x - generalArea.x) / tileWidth;
            minTY = tMinY + (area.y - generalArea.y) / tileHeight;
            maxTX = tMinX + (area.x + area.width + tileWidth - 1) / tileWidth;
            maxTY = tMinY + (area.y + area.height + tileHeight - 1) / tileHeight;
        } else {
            minTX = minTY = 0;
            maxTX = maxTY = 1;
        }
        for (int b = 0; b < getNumBands(); b++) {
            for (int ty = minTY; ty < maxTY; ty++) {
                for (int tx = minTX; tx < maxTX; tx++) {

                    //-- intersection sur 
                    final Raster rast = (renderedImage != null) ? renderedImage.getTile(tx, ty) : currentRaster;
                    final int minX = Math.max(rast.getMinX(), area.x);
                    final int minY = Math.max(rast.getMinY(), area.y);
                    final int maxX = Math.min(rast.getMinX() + tileWidth, area.x + area.width);
                    final int maxY = Math.min(rast.getMinY() + tileHeight, area.y + area.height);
                    if (minX > maxY || minY > maxY) throw new IllegalArgumentException("Expected area don't intersect internal data.");

                    final DataBuffer databuff = rast.getDataBuffer();
                    int srcRastId = bandOffsets[b] + ((minY - rast.getMinY()) * tileWidth + minX - rast.getMinX());
                    final int readLength = (maxX - minX);
                    int destId = 0;
                    for (int y = minY; y < maxY; y++) {

                        switch (sourceDataType) {
                            case DataBuffer.TYPE_BYTE : {
                                final byte[] src  = ((DataBufferByte) databuff).getData(bankIndices[b]);
                                System.arraycopy(src, srcRastId, (byte[]) buffer[b], destId, readLength);
                                break; 
                            }
                            case DataBuffer.TYPE_USHORT :
                            case DataBuffer.TYPE_SHORT  : {
                                final short[] src  = ((DataBufferShort) databuff).getData(bankIndices[b]);
                                System.arraycopy(src, srcRastId, (short[]) buffer[b], destId, readLength);
                                break;
                            }
                            case DataBuffer.TYPE_INT : {
                                final int[] src  = ((DataBufferInt) databuff).getData(bankIndices[b]);
                                System.arraycopy(src, srcRastId, (int[]) buffer[b], destId, readLength);
                                break;
                            }
                            case DataBuffer.TYPE_FLOAT : {
                                final float[] src  = ((DataBufferFloat) databuff).getData(bankIndices[b]);
                                System.arraycopy(src, srcRastId, (float[]) buffer[b], destId, readLength);
                                break;
                            }
                            case DataBuffer.TYPE_DOUBLE : {
                                final double[] src  = ((DataBufferDouble) databuff).getData(bankIndices[b]);
                                System.arraycopy(src, srcRastId, (double[]) buffer[b], destId, readLength);
                                break;
                            }
                            default : {
                                throw new IllegalStateException("Unknow datatype.");
                            }
                        }
                        srcRastId += tileWidth;
                        destId    += area.width;
                    }
                }
            }
        }
    }
    
    /**
     * Fill given buffer with samples within the given area at the specified image band.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     * @param band the interest band.
     */
    public void getArea(final Rectangle area, final Object buffer, int band) {
        ArgumentChecks.ensureNonNull("area", area);
        ArgumentChecks.ensureNonNull("buffer", buffer);
        
        final int sourceDataType = getSourceDatatype();
        final int areaLength = area.width * area.height;
        
        switch (sourceDataType) {
            case DataBuffer.TYPE_BYTE : {
                if (!(buffer instanceof byte[])) throw new IllegalArgumentException("Buffer argument must be instance of byte[][] array");
                if (((byte[]) buffer).length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_USHORT : 
            case DataBuffer.TYPE_SHORT  : {
                if (!(buffer instanceof short[])) throw new IllegalArgumentException("Buffer argument must be instance of short[][] array");
                if (((short[])buffer).length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_INT : {
                if (!(buffer instanceof int[])) throw new IllegalArgumentException("Buffer argument must be instance of int[][] array");
                if (((int[])buffer).length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_FLOAT : {
                if (!(buffer instanceof float[])) throw new IllegalArgumentException("Buffer argument must be instance of float[][] array");
                if (((float[])buffer).length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_DOUBLE : {
                if (!(buffer instanceof double[])) throw new IllegalArgumentException("Buffer argument must be instance of double[][] array");
                if (((double[])buffer).length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            default : {
                throw new IllegalStateException("Unknow datatype.");
            }
        }
        
        if (currentSampleModel instanceof ComponentSampleModel) { 
            if (((ComponentSampleModel)currentSampleModel).getPixelStride() == 1) {
                getAreaByBanded(area, buffer, band);
                return;
            }
        }
        getAreaByInterleaved(area, buffer, band);
    }
    
    /**
     * Fill given buffer with samples within the given area at the specified image band.
     * Adapted for a {@link BandedSampleModel} {@link SampleModel} type.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     * @param band the interest band.
     */
    public void getAreaByBanded(final Rectangle area, final Object buffer, final int band) {
        final ComponentSampleModel compSM = (ComponentSampleModel) currentSampleModel;
        final int bankIndices = compSM.getBankIndices()[band];
        final int bandOffsets = compSM.getBandOffsets()[band];
        
        final Rectangle generalArea = getBoundary(false);//-- compute one time
        final int sourceDataType = getSourceDatatype();
        
        final int minTX, minTY, maxTX, maxTY;
        
        if (renderedImage != null) {
            minTX = tMinX + (area.x - generalArea.x) / tileWidth;
            minTY = tMinY + (area.y - generalArea.y) / tileHeight;
            maxTX = tMinX + (area.x + area.width + tileWidth - 1) / tileWidth;
            maxTY = tMinY + (area.y + area.height + tileHeight - 1) / tileHeight;
        } else {
            minTX = minTY = 0;
            maxTX = maxTY = 1;
        }
        for (int ty = minTY; ty < maxTY; ty++) {
            for (int tx = minTX; tx < maxTX; tx++) {

                //-- intersection sur 
                final Raster rast = (renderedImage != null) ? renderedImage.getTile(tx, ty) : currentRaster;
                final int minX = Math.max(rast.getMinX(), area.x);
                final int minY = Math.max(rast.getMinY(), area.y);
                final int maxX = Math.min(rast.getMinX() + tileWidth, area.x + area.width);
                final int maxY = Math.min(rast.getMinY() + tileHeight, area.y + area.height);
                if (minX > maxY || minY > maxY) throw new IllegalArgumentException("Expected area don't intersect internal data.");

                final DataBuffer databuff = rast.getDataBuffer();
                int srcRastId = bandOffsets + ((minY - rast.getMinY()) * tileWidth + minX - rast.getMinX());
                final int readLength = (maxX - minX);
                int destId = 0;
                for (int y = minY; y < maxY; y++) {

                    switch (sourceDataType) {
                        case DataBuffer.TYPE_BYTE : {
                            final byte[] src  = ((DataBufferByte) databuff).getData(bankIndices);
                            System.arraycopy(src, srcRastId, (byte[]) buffer, destId, readLength);
                            break; 
                        }
                        case DataBuffer.TYPE_USHORT :
                        case DataBuffer.TYPE_SHORT  : {
                            final short[] src  = ((DataBufferShort) databuff).getData(bankIndices);
                            System.arraycopy(src, srcRastId, (short[]) buffer, destId, readLength);
                            break;
                        }
                        case DataBuffer.TYPE_INT : {
                            final int[] src  = ((DataBufferInt) databuff).getData(bankIndices);
                            System.arraycopy(src, srcRastId, (int[]) buffer, destId, readLength);
                            break;
                        }
                        case DataBuffer.TYPE_FLOAT : {
                            final float[] src  = ((DataBufferFloat) databuff).getData(bankIndices);
                            System.arraycopy(src, srcRastId, (float[]) buffer, destId, readLength);
                            break;
                        }
                        case DataBuffer.TYPE_DOUBLE : {
                            final double[] src  = ((DataBufferDouble) databuff).getData(bankIndices);
                            System.arraycopy(src, srcRastId, (double[]) buffer, destId, readLength);
                            break;
                        }
                        default : {
                            throw new IllegalStateException("Unknow datatype.");
                        }
                    }
                    srcRastId += tileWidth;
                    destId    += area.width;
                }
            }
        }
    }
    
    /**
     * Fill given buffer with samples within the given area and from all the source image band.
     * 
     * @param area define needed samples area.
     * @param buffer array which will be filled by samples.
     * @param band the interest band.
     */
    public void getArea(final Rectangle area, final Object[] buffer) {
        ArgumentChecks.ensureNonNull("area", area);
        ArgumentChecks.ensureNonNull("buffer", buffer);
        if (buffer.length < getNumBands()) throw new IllegalArgumentException("buffer must have length equals to numbands. Found : "+buffer.length+". Expected : "+getNumBands());
        
        final int sourceDataType = getSourceDatatype();
        final int areaLength = area.width * area.height * getNumBands();
        
        switch (sourceDataType) {
            case DataBuffer.TYPE_BYTE : {
                if (!(buffer instanceof byte[][])) throw new IllegalArgumentException("Buffer argument must be instance of byte[][] array");
                if (((byte[][]) buffer)[0].length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_USHORT : 
            case DataBuffer.TYPE_SHORT  : {
                if (!(buffer instanceof short[][])) throw new IllegalArgumentException("Buffer argument must be instance of short[][] array");
                if (((short[][])buffer)[0].length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_INT : {
                if (!(buffer instanceof int[][])) throw new IllegalArgumentException("Buffer argument must be instance of int[][] array");
                if (((int[][])buffer)[0].length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_FLOAT : {
                if (!(buffer instanceof float[][])) throw new IllegalArgumentException("Buffer argument must be instance of float[][] array");
                if (((float[][])buffer)[0].length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            case DataBuffer.TYPE_DOUBLE : {
                if (!(buffer instanceof double[][])) throw new IllegalArgumentException("Buffer argument must be instance of double[][] array");
                if (((double[][])buffer)[0].length < areaLength) throw new IllegalArgumentException("Buffer must have a length equal or upper than area sample number. Expected : "+areaLength);
                break;
            }
            default : {
                throw new IllegalStateException("Unknow datatype.");
            }
        }
        
        if (currentSampleModel instanceof ComponentSampleModel) { 
            if (((ComponentSampleModel)currentSampleModel).getPixelStride() == 1) {
                getAreaByBanded(area, buffer);
                return;
            }
        }
        getAreaByInterleaved(area, buffer);
    }
    
    /**
     * Return type data from iterate source.
     * @return type data from iterate source.
     */
    public int getSourceDatatype() {
        return (renderedImage == null) ? currentRaster.getSampleModel().getDataType() : renderedImage.getSampleModel().getDataType();
    }

    /**
     * Compute an array which give the number of data elements until the next sample in the pixel. Note that the first
     * element gives number of elements between the last sample of the previous pixel and the first sample of current one.
     * @param bandOffsets The bandOffsets table given by {@link java.awt.image.ComponentSampleModel#getBandOffsets()}.
     * @param pixelStride The pixel stride value given by {@link java.awt.image.ComponentSampleModel#getPixelStride()}
     * @return An array whose components are the number of elements to skip until the next sample.
     */
     public static int[] getBandSteps(final int[] bandOffsets, final int pixelStride) {
        final int[] bandSteps = new int[bandOffsets.length];
        bandSteps[0] = bandOffsets[0] + pixelStride - bandOffsets[bandOffsets.length-1];
        for (int i = 1 ; i < bandSteps.length ; i++) {
            bandSteps[i] = bandOffsets[i] - bandOffsets[i-1];
        }
        return bandSteps;
    }
}
