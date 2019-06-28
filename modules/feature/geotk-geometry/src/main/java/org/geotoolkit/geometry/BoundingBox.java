/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.geometry;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 *
 * @deprecated Use {@link Envelope} instead.
 */
@Deprecated
public class BoundingBox extends GeneralEnvelope {

    public BoundingBox(final CoordinateReferenceSystem crs){
        super(crs);
    }

    public BoundingBox(final Envelope env){
        super(env);
    }

    public BoundingBox(final BoundingBox bounds, final CoordinateReferenceSystem crs){
        super(bounds.getLowerCorner().getCoordinate(), bounds.getUpperCorner().getCoordinate());
        if(crs != null){
            setCoordinateReferenceSystem(crs);
        }
    }

    public BoundingBox(final double[] min, final double[] max){
        super(min,max);
    }

    public static BoundingBox castOrCopy(final Envelope box) {
        if (box == null || box instanceof BoundingBox) {
            return (BoundingBox) box;
        }
        return new BoundingBox(box);
    }

    public void setBounds(final BoundingBox bounds) {
        for(int dim=0;dim<bounds.getDimension();dim++){
            setRange(dim, bounds.getMinimum(dim),bounds.getMaximum(dim));
        }
    }

    public double getMinX() {
        return getMinimum(0);
    }

    public double getMaxX() {
        return getMaximum(0);
    }

    public double getMinY() {
        return getMinimum(1);
    }

    public double getMaxY() {
        return getMaximum(1);
    }

    public double getWidth() {
        return getSpan(0);
    }

    public double getHeight() {
        return getSpan(1);
    }

    public void include(final BoundingBox bounds) {
        add(bounds);
    }

    public void include(final double x, final double y) {
        add(new DirectPosition2D(getCoordinateReferenceSystem(), x, y));
    }

    public boolean intersects(final BoundingBox bounds) {
        return intersects(bounds, true);
    }

    public boolean contains(final BoundingBox bounds) {
        return contains(bounds, true);
    }

    public boolean contains(final double x, final double y) {
        return contains(new DirectPosition2D(this.getCoordinateReferenceSystem(), x, y));
    }

    public BoundingBox toBounds(final CoordinateReferenceSystem targetCRS) throws TransformException {
        return new BoundingBox(CRS.getDomainOfValidity(targetCRS));
    }
}
