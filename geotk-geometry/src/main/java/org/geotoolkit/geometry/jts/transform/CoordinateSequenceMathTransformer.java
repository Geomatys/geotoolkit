/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.jts.transform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import java.util.Arrays;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * A default implementation of {@linkplain CoordinateSequenceTransformer coordinate sequence
 * transformer}. This transformer applies the coordinate transformations immediately (which
 * means that caller are immediately notified if a transformation fails).
 * <p>
 * This transformer support {@linkplain MathTransform math transform} with up to 3 source
 * or target dimensions. This transformer is not thread-safe.
 *
 * @module
 * @since 2.1
 * @version $Id$
 * @author Andrea Aime
 * @author Martin Desruisseaux
 */
public class CoordinateSequenceMathTransformer implements CoordinateSequenceTransformer {

    /**
     * The coordinate sequence factory to use.
     */
    static final CoordinateSequenceFactory DEFAULT_CS_FACTORY = CoordinateArraySequenceFactory.instance();
    /**
     * A buffer for coordinate transformations. We choose a length which is divisible by
     * both 2 and 3, since JTS coordinates may be up to three-dimensional. If the number
     * of coordinates point to transform is greater than the buffer capacity, then the
     * buffer will be flushed to the destination array before to continue. We avoid to
     * create a buffer as large than the number of point to transforms, because it would
     * consume a large amount of memory for big geometries.
     */
    private final transient double[] buffer = new double[96];

    private final CoordinateSequenceFactory csf;

    private MathTransform transform = null;

    /**
     * Constructs a default coordinate sequence transformer.
     */
    public CoordinateSequenceMathTransformer(final MathTransform transform) {
        this(null,transform);
    }

    /**
     * Constructs a coordinate sequence transformer with the given CoordinateSequenceFactory.
     */
    public CoordinateSequenceMathTransformer(final CoordinateSequenceFactory csf, final MathTransform transform) {
        if(csf == null){
            this.csf = DEFAULT_CS_FACTORY;
        }else{
            this.csf = csf;
        }
        this.transform =transform;
    }

    public synchronized void setTransform(final MathTransform transform) {
        this.transform = transform;
    }

    public synchronized MathTransform getTransform() {
        return transform;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized CoordinateSequence transform(final CoordinateSequence sequence, final int minpoints)
            throws TransformException {
        final int sourceDim = transform.getSourceDimensions();
        final int targetDim = transform.getTargetDimensions();
        final int size = sequence.size();
        final Coordinate[] tcs = new Coordinate[size];
        final int bufferCapacity = buffer.length / Math.max(sourceDim, targetDim);
        int remainingBeforeFlush = Math.min(bufferCapacity, size);
        int ib = 0; // Index in the buffer array.
        int it = 0; // Index in the target array.

        for (int i = 0; i < size; i++) {
            switch (sourceDim) { // Fall through in every cases.
                default: Arrays.fill(buffer, ib + 3, ib + sourceDim, Double.NaN);
                case 3:  buffer[ib + 2] = sequence.getOrdinate(i, 2);
                case 2:  buffer[ib + 1] = sequence.getY(i);
                case 1:  buffer[ib] = sequence.getX(i);
                case 0:  break;
            }

            ib += sourceDim;

            if (--remainingBeforeFlush == 0) {
                /*
                 * The buffer is full, or we just copied the last coordinates.
                 * Transform the coordinates and flush to the destination array.
                 */
                assert (ib % sourceDim) == 0;

                final int n = ib / sourceDim;
                transform.transform(buffer, 0, buffer, 0, n);
                ib = 0;

                for (int j = 0; j < n; j++) {
                    switch (targetDim) {
                        default: throw new MismatchedDimensionException();
                        case 3:
                            tcs[it++] = new Coordinate(buffer[ib++], buffer[ib++], buffer[ib++]);
                            break;
                        case 2:
                            tcs[it++] = new Coordinate(buffer[ib++], buffer[ib++]);
                            break;
                        case 1:
                            tcs[it++] = new Coordinate(buffer[ib++], Double.NaN);
                            break;
                        case 0:
                            tcs[it++] = new Coordinate(Double.NaN, Double.NaN);
                            break;
                    }
                }
                assert ib == (n * targetDim);
                ib = 0;
                remainingBeforeFlush = Math.min(bufferCapacity, size - (i + 1));
            }
        }
        assert it == tcs.length : tcs.length - it;

        return csf.create(tcs);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CoordinateSequenceTransformer : Mathtransform : ");
        sb.append(transform);
        return sb.toString();
    }

}
