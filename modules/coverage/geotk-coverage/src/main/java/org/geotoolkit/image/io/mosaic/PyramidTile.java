/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
