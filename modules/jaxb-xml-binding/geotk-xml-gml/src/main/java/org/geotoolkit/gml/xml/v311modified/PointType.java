/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311modified;

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Java class for PointType complex type.
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "pos",
    "coordinates"
})
@XmlRootElement(name="Point")
public class PointType extends AbstractGeometricPrimitiveType implements Point {

    private DirectPositionType pos;
    private CoordinatesType coordinates;

    /**
     * An empty constructor used by JAXB.
     */
    public PointType() {}
    
    /**
     * Build a new Point with the specified identifier and DirectPositionType
     *  
     * @param id The identifier of the point.
     * @param pos A direcPosition locating the point.
     */
    public PointType(String id, DirectPosition pos) {
        super.setId(id);
        if (pos instanceof DirectPositionType)
            this.pos = (DirectPositionType)pos;
        else
            this.pos = new DirectPositionType(pos);
    }

    /**
     * Build a point Type with the specified coordinates.
     * 
     * @param coordinates a list of coordinates.
     */
    public PointType(CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }
     
    /**
     * Gets the value of the pos property.
     * 
     */
    public DirectPositionType getPos() {
        return pos;
    }

    /**
     * Gets the value of the coordinates property.
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }
   
    
    /**
     * Return a String description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s =new StringBuilder("id = ").append(this.getId()).append('\n'); 
        if(pos != null) {
            s.append("position : ").append(pos.toString()).append('\n'); 
        }
        
        if( coordinates != null) {
            s.append(" coordinates : ").append(coordinates.toString()).append('\n'); 
        }
        
        return s.toString();
    }
    
    /**
     * Verify that the point is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PointType && super.equals(object)) {
            final PointType that = (PointType) object;
            return  Utilities.equals(this.pos, that.pos) &&
                    Utilities.equals(this.coordinates, that.coordinates);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public DirectPosition getDirectPosition() {
        return pos;
    }

    @Override
    public void setDirectPosition(DirectPosition position) throws UnmodifiableGeometryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPosition(DirectPosition position) throws UnmodifiableGeometryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PrimitiveBoundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Bearing getBearing(Position toPoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrientablePrimitive[] getProxy() {
        return null;
    }

    @Override
    public Set<Primitive> getContainedPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainingPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Complex> getComplexes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public Complex getClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSimple() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCycle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double distance(Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDimension(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCoordinateDimension() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<? extends Complex> getMaximalComplex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS, MathTransform transform) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope getEnvelope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getCentroid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getConvexHull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getBuffer(double distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry toImmutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointType clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean intersects(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet union(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet intersection(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet difference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet symmetricDifference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
