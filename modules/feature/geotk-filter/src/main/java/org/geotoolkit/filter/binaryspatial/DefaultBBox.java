/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binaryspatial;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Immutable "BBOX" filter.
 *
 * @author Johann Sorel (Geomatys).
 * @module pending
 */
public class DefaultBBox extends AbstractBinarySpatialOperator<PropertyName,DefaultLiteral<BoundingBox>> implements BBOX {

    private static final LinearRing[] EMPTY_RINGS = new LinearRing[0];
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final PreparedGeometryFactory PREPARED_FACTORY = new PreparedGeometryFactory();

    //cache the bbox geometry
    private final PreparedGeometry boundingGeometry;
    private final com.vividsolutions.jts.geom.Envelope boundingEnv;
    private final CoordinateReferenceSystem crs;
    private final int srid;

    public DefaultBBox(PropertyName property, DefaultLiteral<BoundingBox> bbox) {
        super(nonNullPropertyName(property),bbox);
        boundingGeometry = toGeometry(bbox.getValue());
        boundingEnv = boundingGeometry.getGeometry().getEnvelopeInternal();
        this.crs = bbox.getValue().getCoordinateReferenceSystem();
        if(crs != null){
            this.srid = SRIDGenerator.toSRID(crs, Version.V1);
        }else{
            this.srid = 0;
        }
    }

    private static PropertyName nonNullPropertyName(PropertyName proper) {
        if (proper == null)
            return new DefaultPropertyName("");
        return proper;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getPropertyName() {
        return left.getPropertyName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSRS() {
        return CRS.getDeclaredIdentifier(right.getValue().getCoordinateReferenceSystem());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMinX() {
        return right.getValue().getMinimum(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMinY() {
        return right.getValue().getMinimum(1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMaxX() {
        return right.getValue().getMaximum(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMaxY() {
        return right.getValue().getMaximum(1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object object) {
        Geometry candidate;
        if (object instanceof Feature && left.getPropertyName().isEmpty()) {
            candidate = (Geometry) ((Feature)object).getDefaultGeometryProperty().getValue();
        } else {
            candidate = left.evaluate(object, Geometry.class);
        }

        if(candidate == null){
            return false;
        }

        final int srid = candidate.getSRID();
        if(srid != 0 && srid != this.srid){
            //check that the geometry has the same crs as the boundingbox
            final CoordinateReferenceSystem crs;
            try {
                 crs = CRS.decode(SRIDGenerator.toSRS(srid, Version.V1));

                 if(!CRS.equalsIgnoreMetadata(crs, this.crs)){
                    //we must reproject the geometry
                    MathTransform trs = CRS.findMathTransform(crs, this.crs);
                    candidate = JTS.transform(candidate, trs);
                }

            } catch (FactoryException ex) {
                //should not append if we have a srid
                Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
                return false;
            } catch (TransformException ex) {
                Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
                return false;
            }
            
        }

        final com.vividsolutions.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();

        if(boundingEnv.contains(candidateEnv) || candidateEnv.contains(boundingEnv)) {
            return true;
        } else if(boundingEnv.intersects(candidateEnv)) {
            return boundingGeometry.intersects(candidate);
        } else {
            return false;
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return new StringBuilder("BBOX{")
                .append(left).append(',')
                .append(right).append('}')
                .toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractBinarySpatialOperator other = (AbstractBinarySpatialOperator) obj;
        if (this.left != other.left && !this.left.equals(other.left)) {
            return false;
        }
        if (this.right != other.right && !this.right.equals(other.right)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 15;
        hash = 71 * hash + this.left.hashCode();
        hash = 71 * hash + this.right.hashCode();
        return hash;
    }

    /**
     * Utility method to transform an envelope in geometry.
     * @param env
     * @return Geometry
     */
    private static PreparedGeometry toGeometry(Envelope env){
        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
        coords[1] = new Coordinate(env.getMinimum(0), env.getMaximum(1));
        coords[2] = new Coordinate(env.getMaximum(0), env.getMaximum(1));
        coords[3] = new Coordinate(env.getMaximum(0), env.getMinimum(1));
        coords[4] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
        final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        Geometry geom = GEOMETRY_FACTORY.createPolygon(ring, EMPTY_RINGS);
        return PREPARED_FACTORY.create(geom);
    }
}
