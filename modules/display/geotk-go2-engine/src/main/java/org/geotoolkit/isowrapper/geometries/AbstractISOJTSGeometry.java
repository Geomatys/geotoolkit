/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.isowrapper.geometries;

import com.vividsolutions.jts.geom.Point;

import java.util.Collections;
import java.util.Set;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.isowrapper.WrappingUtilities;
import org.geotoolkit.referencing.CRS;

import org.geotools.geometry.jts.JTS;

import org.opengis.geometry.Boundary;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractISOJTSGeometry<T extends com.vividsolutions.jts.geom.Geometry> implements Geometry{

    protected final Set<Primitive> EMPTY_PRIMITIVE_SET = Collections.emptySet();
    protected final Set<Complex> EMPTY_COMPLEXE_SET = Collections.emptySet();

    protected final T jtsGeometry;
    protected final CoordinateReferenceSystem crs;

    public AbstractISOJTSGeometry(T jtsGeometry, CoordinateReferenceSystem crs) {
        this.jtsGeometry = jtsGeometry;
        this.crs = crs;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public Precision getPrecision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getMbRegion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getRepresentativePoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Complex getClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSimple() {
        return jtsGeometry.isSimple();
    }

    @Override
    public boolean isCycle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double distance(Geometry candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return this.jtsGeometry.distance(other);
    }

    @Override
    public int getDimension(DirectPosition point) {
        return 2;
    }

    @Override
    public int getCoordinateDimension() {
        return 2;
    }

    @Override
    public Set<? extends Complex> getMaximalComplex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS) throws TransformException {
        if(this.crs == null){
            throw new TransformException("The geometry hasn't been defined, impossible to reproject it.");
        }
        try {
            MathTransform trs = CRS.findMathTransform(crs, newCRS, true);
            return transform(newCRS, trs);
        } catch (FactoryException ex) {
            throw new TransformException("No crs factory found",ex);
        }
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS, MathTransform transform) throws TransformException {
        com.vividsolutions.jts.geom.Geometry geo = JTS.transform(jtsGeometry, transform);
        return WrappingUtilities.wrap(geo,newCRS);
    }

    @Override
    public Envelope getEnvelope() {
        com.vividsolutions.jts.geom.Envelope env = jtsGeometry.getEnvelopeInternal();
        return new ImmutableEnvelope(null, env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY());
    }

    @Override
    public DirectPosition getCentroid() {
        Point p = jtsGeometry.getCentroid();
        return new DirectPosition2D(p.getX(), p.getY());
    }

    @Override
    public Geometry getConvexHull() {
        return WrappingUtilities.wrap(jtsGeometry.convexHull(),crs);
    }

    @Override
    public Geometry getBuffer(double distance) {
        return WrappingUtilities.wrap(jtsGeometry.buffer(distance),crs);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Geometry toImmutable() {
        throw new UnsupportedOperationException("ISO JTS geometries are immutable");
    }

    @Override
    public Geometry clone() throws CloneNotSupportedException {
        //return same object since thoses are immutable
        return this;
    }

    @Override
    public boolean contains(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return this.jtsGeometry.contains(other);
    }

    @Override
    public boolean contains(DirectPosition candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return this.jtsGeometry.contains(other);
    }

    @Override
    public boolean intersects(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return this.jtsGeometry.intersects(other);
    }

    @Override
    public boolean equals(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return this.jtsGeometry.equals(other);
    }

    @Override
    public TransfiniteSet union(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return WrappingUtilities.wrap(this.jtsGeometry.union(other),crs);
    }

    @Override
    public TransfiniteSet intersection(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return WrappingUtilities.wrap(this.jtsGeometry.intersection(other),crs);
    }

    @Override
    public TransfiniteSet difference(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return WrappingUtilities.wrap(this.jtsGeometry.difference(other),crs);
    }

    @Override
    public TransfiniteSet symmetricDifference(TransfiniteSet candidate) {
        if(!(candidate instanceof AbstractISOJTSGeometry)){
            throw new UnsupportedOperationException(
            "Operations are supported only against other ISO JTS wrapped objects.");
        }

        com.vividsolutions.jts.geom.Geometry other = ((AbstractISOJTSGeometry)candidate).jtsGeometry;
        return WrappingUtilities.wrap(this.jtsGeometry.symDifference(other),crs);
    }

}
