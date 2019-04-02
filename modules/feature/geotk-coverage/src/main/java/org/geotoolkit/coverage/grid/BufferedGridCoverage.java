/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.RasterFormatException;
import java.awt.image.RenderedImage;
import java.util.Collection;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.ImageRenderer;
import org.opengis.coverage.CannotEvaluateException;

/**
 * A GridCoverage with datas stored in a buffer in memory.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BufferedGridCoverage extends org.apache.sis.coverage.grid.GridCoverage {

    private final DataBuffer data;
    private org.apache.sis.coverage.grid.GridCoverage converted = null;

    public BufferedGridCoverage(final GridGeometry grid, final Collection<? extends SampleDimension> bands, int dataType) {
        super(grid, bands);
        long nbSamples = bands.size();
        GridExtent extent = grid.getExtent();
        for (int i = 0; i<grid.getDimension(); i++) {
            nbSamples *= extent.getSize(i);
        }
        final int nbSamplesi = Math.toIntExact(nbSamples);

        switch (dataType) {
            case DataBuffer.TYPE_BYTE   : this.data = new DataBufferByte(nbSamplesi); break;
            case DataBuffer.TYPE_SHORT  : this.data = new DataBufferShort(nbSamplesi); break;
            case DataBuffer.TYPE_USHORT : this.data = new DataBufferUShort(nbSamplesi); break;
            case DataBuffer.TYPE_INT    : this.data = new DataBufferInt(nbSamplesi); break;
            case DataBuffer.TYPE_FLOAT  : this.data = new DataBufferFloat(nbSamplesi); break;
            case DataBuffer.TYPE_DOUBLE : this.data = new DataBufferDouble(nbSamplesi); break;
            default: throw new IllegalArgumentException("Unsupported data type "+ dataType);
        }
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        try {
            final ImageRenderer renderer = new ImageRenderer(this, sliceExtent);
            renderer.setData(data);
            return renderer.image();
        } catch (IllegalArgumentException | ArithmeticException | RasterFormatException e) {
            throw new CannotEvaluateException(e.getMessage(), e);
        }
    }

    public org.apache.sis.coverage.grid.GridCoverage forConvertedValues(boolean converted) {
        if (converted) {
            synchronized (this) {
                if (this.converted == null) {
                    this.converted = ConvertedGridCoverage.convert(this);
                }
                return this.converted;
            }
        }
        return this;
    }

}
