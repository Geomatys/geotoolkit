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
 * <p>RTree tile.<br/>
 *
 * This class contains tile boundary and tile path.</p>
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidTile implements Envelope {

    /**
     * Pyramid tile path.
     */
    private final File path;

    /**
     * Pyramid Tile boundary.
     */
    private final Envelope boundary;

    /**
     * RTree tile.
     *
     * @param path path to tile.
     * @param boundary tile boundary.
     */
    public PyramidTile(File path, Envelope boundary) {
        ArgumentChecks.ensureNonNull("name", path);
        ArgumentChecks.ensureNonNull("boundary", boundary);
        this.path = path;
        this.boundary = boundary;
    }

    /**
     * Return tile path.
     *
     * @return tile path.
     */
    File getPath() {
        return path;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return boundary.getCoordinateReferenceSystem();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getDimension() {
        return 3;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public DirectPosition getLowerCorner() {
        return boundary.getLowerCorner();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public DirectPosition getUpperCorner() {
        return boundary.getUpperCorner();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getMinimum(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMinimum(dimension);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getMaximum(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMaximum(dimension);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getMedian(int dimension) throws IndexOutOfBoundsException {
        return boundary.getMedian(dimension);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSpan(int dimension) throws IndexOutOfBoundsException {
        return boundary.getSpan(dimension);
    }
}
