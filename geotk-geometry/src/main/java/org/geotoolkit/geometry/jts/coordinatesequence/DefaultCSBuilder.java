/*
 *    GeotoolKit - The Open Source Java GIS Toolkit
 *    http://geotoolkit.org
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
 *
 * Created on 31-dic-2004
 */
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;

/**
 * A CSBuilder that generates DefaultCoordinateSequence objects, that is,
 * coordinate sequences backed by a Coordinate[] array.
 * @author wolf
 * @module
 */
public class DefaultCSBuilder implements CSBuilder {

    private Coordinate[] coordinateArray;
    private CoordinateSequenceFactory factory = CoordinateArraySequenceFactory.instance();

    /**
     * {@inheritDoc }
     */
    @Override
    public void start(final int size, final int dimensions) {
        coordinateArray = new Coordinate[size];
        for (int i = 0; i < size; i++) {
            coordinateArray[i] = new Coordinate();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateSequence end() {
        CoordinateSequence cs = factory.create(coordinateArray);
        coordinateArray = null;
        return cs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOrdinate(final double value, final int ordinateIndex, final int coordinateIndex) {
        Coordinate c = coordinateArray[coordinateIndex];
        switch (ordinateIndex) {
            case 0:
                c.x = value;
                break;
            case 1:
                c.y = value;
                break;
            case 2:
                c.z = value;
                break;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getOrdinate(final int ordinateIndex, final int coordinateIndex) {
        Coordinate c = coordinateArray[coordinateIndex];
        switch (ordinateIndex) {
            case 0:
                return c.x;
            case 1:
                return c.y;
            case 2:
                return c.z;
            default:
                return 0.0;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getSize() {
        if (coordinateArray != null) {
            return coordinateArray.length;
        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getDimension() {
        if (coordinateArray != null) {
            return 2;
        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOrdinate(final CoordinateSequence sequence, final double value, final int ordinateIndex, final int coordinateIndex) {
        Coordinate c = sequence.getCoordinate(coordinateIndex);
        switch (ordinateIndex) {
            case 0:
                c.x = value;
                break;
            case 1:
                c.y = value;
                break;
            case 2:
                c.z = value;
                break;
        }

    }
}
