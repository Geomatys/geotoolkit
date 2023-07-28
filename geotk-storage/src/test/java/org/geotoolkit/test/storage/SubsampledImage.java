/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.test.storage;

import java.awt.Point;
import java.util.Arrays;
import java.util.Vector;
import java.util.Objects;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.apache.sis.image.PlanarImage;
import org.apache.sis.internal.util.Strings;

import static java.lang.StrictMath.floorDiv;
import static org.junit.Assert.*;


/**
 * Copy of a test class from Apache SIS.
 */
final class SubsampledImage extends PlanarImage {
    private final RenderedImage source;
    private final int subX, subY;
    private final int offX, offY;
    private final SampleModel model;

    private SubsampledImage(final RenderedImage source, final int subX, final int subY, final int offX, final int offY) {
        this.source = source;
        this.subX   = subX;
        this.subY   = subY;
        this.offX   = offX;
        this.offY   = offY;
        final SampleModel sourceModel = source.getSampleModel();
        if (sourceModel instanceof PixelInterleavedSampleModel) {
            final PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel) sourceModel;
            final int   pixelStride    = sm.getPixelStride();
            final int   scanlineStride = sm.getScanlineStride();
            final int   strideOffset   = pixelStride*offX + scanlineStride*offY;
            final int[] bandOffsets    = sm.getBandOffsets();
            for (int i=0; i<bandOffsets.length; i++) {
                bandOffsets[i] += strideOffset;
            }
            model = new PixelInterleavedSampleModel(sm.getDataType(),
                    divExclusive(sm.getWidth(),  subX),
                    divExclusive(sm.getHeight(), subY),
                    pixelStride*subX, scanlineStride*subY, bandOffsets);
        } else if (sourceModel instanceof MultiPixelPackedSampleModel) {
            final MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel) sourceModel;
            assertEquals("Subsampling on the X axis is not supported.", 1, subX);
            model = new MultiPixelPackedSampleModel(sm.getDataType(),
                    divExclusive(sm.getWidth(),  subX),
                    divExclusive(sm.getHeight(), subY),
                    sm.getPixelBitStride(),
                    sm.getScanlineStride() * subY,
                    sm.getDataBitOffset());
        } else {
            throw new AssertionError("Unsupported sample model: " + sourceModel);
        }
        if (getNumXTiles() > 1) assertEquals(0, sourceModel.getWidth()  % subX);
        if (getNumYTiles() > 1) assertEquals(0, sourceModel.getHeight() % subY);
    }

    static RenderedImage create(final RenderedImage source, final int subX, final int subY, final int offX, final int offY) {
        if (subX == 1 && subY == 1) {
            return source;
        } else {
            final SubsampledImage image;
            try {
                image = new SubsampledImage(source, subX, subY, offX, offY);
            } catch (IllegalArgumentException e) {
                final PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel) source.getSampleModel();
                final int pixelStride    = sm.getPixelStride() * subX;
                final int scanlineStride = sm.getScanlineStride() * subY;
                final int width = divExclusive(sm.getWidth(), subX);
                if (pixelStride * width > scanlineStride) {
                    final int minBandOff = Arrays.stream(sm.getBandOffsets()).min().getAsInt();
                    final int maxBandOff = Arrays.stream(sm.getBandOffsets()).max().getAsInt();
                    if (pixelStride * (width - 1) + (maxBandOff - minBandOff) < scanlineStride) {
                        return null;
                    }
                }
                throw e;
            }
            final String warning = image.verify();
            if (warning != null && (source instanceof PlanarImage)) {
                final String s = Strings.orEmpty(((PlanarImage) source).verify());
                assertEquals(s, s.substring(s.lastIndexOf('.') + 1), warning);
            }
            return image;
        }
    }

    @Override
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public Vector<RenderedImage> getSources() {
        final Vector<RenderedImage> sources = new Vector<>(1);
        sources.add(source);
        return sources;
    }

    @Override public SampleModel getSampleModel() {return model;}
    @Override public ColorModel  getColorModel()  {return source.getColorModel();}
    @Override public int         getNumXTiles()   {return source.getNumXTiles();}
    @Override public int         getNumYTiles()   {return source.getNumYTiles();}
    @Override public int         getMinTileX()    {return source.getMinTileX();}
    @Override public int         getMinTileY()    {return source.getMinTileY();}
    @Override public int         getTileWidth()   {return divExclusive(source.getTileWidth(),  subX);}
    @Override public int         getTileHeight()  {return divExclusive(source.getTileHeight(), subY);}
    @Override public int         getWidth()       {return divExclusive(source.getWidth(),  subX);}
    @Override public int         getHeight()      {return divExclusive(source.getHeight(), subY);}
    @Override public int         getMinX()        {return divInclusive(source.getMinX(), subX);}
    @Override public int         getMinY()        {return divInclusive(source.getMinY(), subY);}

    private static int divInclusive(final int coordinate, final int subsampling) {
        return floorDiv(coordinate, subsampling);
    }

    private static int divExclusive(final int coordinate, final int subsampling) {
        return floorDiv(coordinate - 1, subsampling) + 1;
    }

    @Override
    public Raster getTile(final int tileX, final int tileY) {
        final Raster tile = source.getTile(tileX, tileY);
        final int x       = divInclusive(tile.getMinX(),   subX);
        final int y       = divInclusive(tile.getMinY(),   subY);
        final int width   = divExclusive(tile.getWidth(),  subX);
        final int height  = divExclusive(tile.getHeight(), subY);
        int tx = tile.getMinX() - tile.getSampleModelTranslateX();
        int ty = tile.getMinY() - tile.getSampleModelTranslateY();
        if ((tx % subX) != 0 || (ty % subY) != 0) {
            return rewriteTile(tile, tile.createCompatibleWritableRaster(x, y, width, height));
        }
        Raster subsampled = Raster.createRaster(model, tile.getDataBuffer(), new Point(x, y));
        if ((tx | ty) != 0 || subsampled.getWidth() != width || subsampled.getHeight() != height) {
            tx = x + divInclusive(tx, subX);
            ty = y + divInclusive(ty, subY);
            subsampled = subsampled.createChild(tx, ty, width, height, x, y, null);
        }
        assertEquals(x, subsampled.getMinX());
        assertEquals(y, subsampled.getMinY());
        return subsampled;
    }

    private Raster rewriteTile(final Raster tile, final WritableRaster target) {
        final int width  = target.getWidth();
        final int height = target.getHeight();
        final int xmin   = target.getMinX();
        final int ymin   = target.getMinY();
        final int xs     = tile.getMinX() + offX;
        final int ys     = tile.getMinY() + offY;
        double[] buffer = null;
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                buffer = tile.getPixel(x*subX + xs, y*subY + ys, buffer);
                target.setPixel(x + xmin, y + ymin, buffer);
            }
        }
        return target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, subX, subY, offX, offY);
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof SubsampledImage) {
            final SubsampledImage other = (SubsampledImage) object;
            return source.equals(other.source) &&
                   subX == other.subX &&
                   subY == other.subY &&
                   offX == other.offX &&
                   offY == other.offY;
        }
        return false;
    }
}
