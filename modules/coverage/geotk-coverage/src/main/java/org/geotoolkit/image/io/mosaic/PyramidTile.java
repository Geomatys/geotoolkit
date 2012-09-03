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
package org.geotoolkit.image.io.mosaic;

import java.io.File;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarech
 */
public class PyramidTile implements Envelope {

    /**
     * Pyramid tile name.
     */
    private final File path;

    /**
     * Pyramid Tile boundary.
     */
    private final Envelope boundary;

    public PyramidTile(File path, Envelope boundary) {
        ArgumentChecks.ensureNonNull("name", path);
        ArgumentChecks.ensureNonNull("boundary", boundary);
        this.path = path;
        this.boundary = boundary;
    }

    /**
     * Return tile name.
     * @return tile name.
     */
    File getPath() {
        return path;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return boundary.getCoordinateReferenceSystem();
    }

    @Override
    public int getDimension() {
        return 3;
    }

    @Override
    public DirectPosition getLowerCorner() {
        return boundary.getLowerCorner();
    }

    @Override
    public DirectPosition getUpperCorner() {
        return boundary.getUpperCorner();
    }

    @Override
    public double getMinimum(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMinimum(dimension);
    }

    @Override
    public double getMaximum(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMaximum(dimension);
    }

    @Override
    public double getMedian(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMedian(dimension);
    }

    @Override
    public double getSpan(int dimension) throws IndexOutOfBoundsException {
        return boundary.getSpan(dimension);
    }
}
