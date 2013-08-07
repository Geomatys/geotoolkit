/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree.io;

import org.geotoolkit.index.tree.FileTreeElementMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Adapted class to test TreeElementMapper with file writing.
 *
 * @author Remi Marechal (Geomatys).
 */
public class FileTreeElementMapperTest extends FileTreeElementMapper<double[]> {
    
    final CoordinateReferenceSystem crs;
    final int boundLength;

    public FileTreeElementMapperTest(final CoordinateReferenceSystem crs, final File inOutPut) throws IOException {
        super(inOutPut, ((crs.getCoordinateSystem().getDimension() << 1) * Double.SIZE)>>3);
        this.crs = crs;
        boundLength = crs.getCoordinateSystem().getDimension() << 1;
    }

    @Override
    protected boolean areEquals(double[] objectA, double[] objectB) {
        return Arrays.equals(objectA, objectB);
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public Envelope getEnvelope(double[] object) throws IOException {
        final GeneralEnvelope gE = new GeneralEnvelope(crs);
        gE.setEnvelope(object);
        return gE;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    protected void writeObject(double[] Object) throws IOException {
        for (int i = 0; i < boundLength; i++) {
            byteBuffer.putDouble(Object[i]);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected double[] readObject() throws IOException {
        final double[] result = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            result[i] = byteBuffer.getDouble();
        }
        return result;
    }
}
