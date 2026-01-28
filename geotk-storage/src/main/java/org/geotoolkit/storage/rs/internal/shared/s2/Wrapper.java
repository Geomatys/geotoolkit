/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared.s2;

import com.google.common.geometry.S2Region;
import java.util.Iterator;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.geometry.wrapper.GeometryWrapper;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * The wrapper of S2 geometries.
 *
 * @author Johann Sorel (Geomatys)
 */
final class Wrapper extends GeometryWrapper {

    /**
     * The wrapped implementation.
     */
    private final S2Region geometry;

    /**
     * Creates a new wrapper around the given geometry.
     */
    Wrapper(final S2Region geometry) {
        this.geometry = geometry;
    }

    /**
     * Returns the implementation-dependent factory of geometric object.
     */
    @Override
    public Geometries<S2Region> factory() {
        return Factory.INSTANCE;
    }

    /**
     * Returns the geometry specified at construction time.
     */
    @Override
    protected S2Region implementation() {
        return geometry;
    }

    @Override
    public GeneralEnvelope getEnvelope() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public DirectPosition getCentroid() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public double[] getPointCoordinates() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public double[] getAllCoordinates() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object mergePolylines(Iterator<?> paths) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String formatWKT(double flatness) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
