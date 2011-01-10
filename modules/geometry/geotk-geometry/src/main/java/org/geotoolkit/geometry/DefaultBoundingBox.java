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

import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultBoundingBox extends GeneralEnvelope implements BoundingBox,Envelope{

    public DefaultBoundingBox(final CoordinateReferenceSystem crs){
        super(crs);
    }

    public DefaultBoundingBox(final Envelope env){
        super(env);
    }

    public DefaultBoundingBox(final BoundingBox bounds, final CoordinateReferenceSystem crs){
        super(crs);
        setBounds(bounds);
    }

    public DefaultBoundingBox(final double[] min, final double[] max){
        super(min,max);
    }

    @Override
    public void setBounds(final BoundingBox bounds) {
        for(int dim=0;dim<bounds.getDimension();dim++){
            setRange(dim, bounds.getMinimum(dim),bounds.getMaximum(dim));
        }
    }

    @Override
    public double getMinX() {
        return getMinimum(0);
    }

    @Override
    public double getMaxX() {
        return getMaximum(0);
    }

    @Override
    public double getMinY() {
        return getMinimum(1);
    }

    @Override
    public double getMaxY() {
        return getMaximum(1);
    }

    @Override
    public double getWidth() {
        return getSpan(0);
    }

    @Override
    public double getHeight() {
        return getSpan(1);
    }

    @Override
    public void include(final BoundingBox bounds) {
        add(bounds);
    }

    @Override
    public void include(final double x, final double y) {
        add(new DirectPosition2D(getCoordinateReferenceSystem(), x, y));
    }

    @Override
    public boolean intersects(final BoundingBox bounds) {
        return intersects(bounds, true);
    }

    @Override
    public boolean contains(final BoundingBox bounds) {
        return contains(bounds, true);
    }

    @Override
    public boolean contains(final double x, final double y) {
        return contains(new DirectPosition2D(this.getCoordinateReferenceSystem(), x, y));
    }

    @Override
    public BoundingBox toBounds(final CoordinateReferenceSystem targetCRS) throws TransformException {
        return new DefaultBoundingBox(CRS.getEnvelope(targetCRS));
    }

}
