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
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

/**
 * @todo class description
 *
 * @author jeichar
 * @module
 * @since 2.1.x
 */
public class LiteCoordinateSequenceFactory implements CoordinateSequenceFactory {

    private static final LiteCoordinateSequenceFactory INSTANCE = new LiteCoordinateSequenceFactory();

    private LiteCoordinateSequenceFactory(){}


    /* (non-Javadoc)
     * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.Coordinate[])
     */
    @Override
    public CoordinateSequence create(final Coordinate[] coordinates) {
        return new LiteCoordinateSequence(coordinates);
    }

    /* (non-Javadoc)
     * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.CoordinateSequence)
     */
    @Override
    public CoordinateSequence create(final CoordinateSequence coordSeq) {
        return new LiteCoordinateSequence(coordSeq.toCoordinateArray());
    }

    /* (non-Javadoc)
     * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(int, int)
     */
    @Override
    public CoordinateSequence create(final int size, final int dimension) {
        return new LiteCoordinateSequence(size, dimension);
    }

    /**
     * @param points
     */
    public CoordinateSequence create(final double[] points) {
        return new LiteCoordinateSequence(points);
    }

    public static LiteCoordinateSequenceFactory instance() {
        return INSTANCE;
    }

}
