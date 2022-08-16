/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.filter;

import java.time.Instant;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.geometry.WraparoundMethod;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.binaryspatial.DefaultBBox;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.ResourceId;
import org.opengis.filter.ValueReference;
import org.opengis.filter.Version;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Default implementation of a Types filterFactory.
 * All objects created by this factory are immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 *
 * @deprecated Use {@link DefaultFilterFactory} instead.
 */
@Deprecated
public class FilterFactory2 extends DefaultFilterFactory<Object,Object,Object> {

    public FilterFactory2() {
        super(Object.class, Object.class, WraparoundMethod.NONE);
    }

    /**
     * Filter by resource identifier. Contrarily to the implementation provided in Apache SIS,
     * this implementation accepts {@link java.util.Map} in addition of {@link org.opengis.feature.Feature} instances.
     */
    @Override
    public ResourceId<Object> resourceId(final String identifier, final Version version,
                                         final Instant startTime, final Instant endTime)
    {
        return new FilterByIdentifier(identifier);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  SPATIAL FILTERS
//
////////////////////////////////////////////////////////////////////////////////

    public BinarySpatialOperator<Object> bbox(final String propertyName, final double minx,
            final double miny, final double maxx, final double maxy, final String srs)
    {
        return bbox(property(propertyName), minx, miny, maxx, maxy, srs);
    }

    public BinarySpatialOperator<Object> bbox(final Expression e, final double minx, final double miny,
            final double maxx, final double maxy, final String srs)
    {
        if (srs == null || srs.trim().isEmpty()) {
            final Envelope env = new ImmutableEnvelope(new double[]{minx, miny}, new double[]{maxx, maxy}, null);
            return bbox(e, env);
        }
        CoordinateReferenceSystem crs = null;
        FactoryException firstException = null;
        try {
            crs = CRS.forCode(srs);
        } catch (FactoryException ex) {
            firstException = ex;
        }

        //TODO : featurestore from geotools sucks, they dont even provide the authority name sometimes !!!
        // we are forced add the two next tests

        if(crs == null && !srs.startsWith("CRS:") && !srs.startsWith("EPSG:")){
            //we presume all epsg given are using the epsg authority
            //this is a necessity since the last geotools modules aren't correctly providing the authority
            final String test = "EPSG:"+srs;
            try {
                crs = CRS.forCode(test);
            } catch (FactoryException ex) {
                if(firstException == null){
                    firstException = ex;
                }
            }
        }
        if(crs == null && !srs.startsWith("CRS:") && !srs.startsWith("EPSG:")){
            //we presume all epsg given are using the epsg authority
            //this is a necessity since the last geotools modules aren't correctly providing the authority
            final String test = "CRS:"+srs;
            try {
                crs = CRS.forCode(test);
            } catch (FactoryException ex) {
                if(firstException == null){
                    firstException = ex;
                }
            }
        }
        if(crs == null){
            throw new IllegalArgumentException("Invalid srs : " +srs +" , check that you have the corresponding authority registered." +
                    "\n primary exception : "+firstException.getMessage(), firstException);
        }

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, minx, maxx);
        env.setRange(1, miny, maxy);
        return bbox(e,env);
    }

    @Override
    public BinarySpatialOperator<Object> bbox(final Expression e, final Envelope bounds) {
        if (e instanceof ValueReference) {
            return new DefaultBBox((ValueReference) e, super.literal(bounds));
        }
        return super.bbox(e, bounds);
    }

    public DistanceOperator<Object> beyond(final String propertyName, final Geometry geometry,
            final double distance, final String units)
    {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return beyond(name, geom,distance,units);
    }

    public DistanceOperator<Object> beyond(final Expression left, final Expression right,
            final double distance, final String units)
    {
        return beyond(left, right, distance(distance, units));
    }

    public BinarySpatialOperator<Object> contains(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return contains(name, geom);
    }

    public BinarySpatialOperator<Object> crosses(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return crosses(name, geom);
    }

    public BinarySpatialOperator<Object> disjoint(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return disjoint(name, geom);
    }

    public DistanceOperator<Object> dwithin(final String propertyName, final Geometry geometry,
            final double distance, final String units)
    {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return dwithin(name, geom,distance,units);
    }

    public DistanceOperator<Object> dwithin(final Expression left, final Expression right,
            final double distance, final String units)
    {
        return within(left, right, distance(distance, units));
    }

    private static Quantity<Length> distance(final double distance, String units) {
        if (units == null || units.trim().isEmpty()) {
            if (distance != 0) {
                throw new IllegalArgumentException("Units of measurement must be specified.");
            }
            units = "m";    // Unit does not matter if distance is zero.
        }
        return Quantities.create(distance, Units.valueOf(units)).asType(Length.class);
    }

    public BinarySpatialOperator<Object> equals(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return equals(name, geom);
    }

    public BinarySpatialOperator<Object> intersects(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return intersects(name, geom);
    }

    public BinarySpatialOperator<Object> overlaps(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return overlaps(name, geom);
    }

    public BinarySpatialOperator<Object> touches(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return touches(name, geom);
    }

    public BinarySpatialOperator<Object> within(final String propertyName, final Geometry geometry) {
        final ValueReference name = property(propertyName);
        final Literal geom = super.literal(geometry);
        return within(name, geom);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  FILTERS
//
////////////////////////////////////////////////////////////////////////////////

    public ValueReference<Object,Object> property(final GenericName name) {
        return property(name.toString());
    }

    @Override
    public ValueReference property(String name, Class type) {
        return property(name);
    }

    @Override
    public ValueReference<Object,Object> property(final String name) {
        return new DefaultPropertyName(name);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  EXPRESSIONS
//
////////////////////////////////////////////////////////////////////////////////

    private static org.locationtech.jts.geom.Geometry toGeometry(final Envelope env){
        final double minx = env.getMinimum(0);
        final double miny = env.getMinimum(1);
        final double maxx = env.getMaximum(0);
        final double maxy = env.getMaximum(1);
        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(minx, miny);
        coords[1] = new Coordinate(minx, maxy);
        coords[2] = new Coordinate(maxx, maxy);
        coords[3] = new Coordinate(maxx, miny);
        coords[4] = new Coordinate(minx, miny);
        final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();
        final LinearRing ring = GF.createLinearRing(coords);
        final Polygon poly = GF.createPolygon(ring, new LinearRing[0]);
        CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        if (crs != null) {
            final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
            poly.setSRID(srid);
            poly.setUserData(crs);
        }
        return poly;
    }

    @Override
    public Literal literal(Object obj) {
        if (obj instanceof JTSGeometry) {
            obj = ((JTSGeometry) obj).getJTSGeometry();
        } else if (obj instanceof Envelope) {
            //special case for envelopes to change them in JTS geometries
            obj = toGeometry((Envelope) obj);
        }
        /*
         * Apache SIS is not aware of the SRIDGenerator convention, which adds a (1 << 28) bitmask if
         * the authority is CRS. For avoiding a "no such EPSG code error", resolve the CRS in advance.
         */
        if (obj instanceof org.locationtech.jts.geom.Geometry) {
            final org.locationtech.jts.geom.Geometry g = (org.locationtech.jts.geom.Geometry) obj;
            if (g.getUserData() == null) {
                int srid = g.getSRID();
                if (srid != 0) try {
                    g.setUserData(CRS.forCode(SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1)));
                } catch (FactoryException e) {
                    Logging.unexpectedException(null, getClass(), "literal", e);
                }
            }
        }
        return super.literal(obj);
    }

    public Literal literal(final byte b) {
        return super.literal(b);
    }

    public Literal literal(final short s) {
        return super.literal(s);
    }

    public Literal literal(final int i) {
        return super.literal(i);
    }

    public Literal literal(final long l) {
        return super.literal(l);
    }

    public Literal literal(final float f) {
        return super.literal(f);
    }

    public Literal literal(final double d) {
        return super.literal(d);
    }

    public Literal literal(final char c) {
        return super.literal(c);
    }

    public Literal literal(final boolean b) {
        return super.literal(b);
    }
}
