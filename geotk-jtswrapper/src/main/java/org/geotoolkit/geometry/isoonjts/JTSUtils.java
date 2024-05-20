/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.geometry.isoonjts.spatialschema.JTSPositionFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.AbstractJTSAggregate;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPrimitiveFactory;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.FactoryException;

/**
 * Class with static methods to help the conversion process between JTS
 * geometries and ISO geometries.
 * @module
 */
public final class JTSUtils {

    /**
     * Common instance of GEOMETRY_FACTORY with the default JTS precision model
     * that can be used to make new geometries.
     */
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * This class has only static methods, so we make the constructor private
     * to prevent instantiation.
     */
    private JTSUtils() {
    }

    /**
     * Creates a 19107 primitive geometry from the given JTS geometry.
     */
    public static Geometry toISO(final org.locationtech.jts.geom.Geometry jtsGeom,
            CoordinateReferenceSystem crs) {

        if (jtsGeom == null) {
            return null;
        }

        if(crs == null){
            //try to extract the crs from the srid
            final int srid = jtsGeom.getSRID();
            if(srid != 0){
                final String strCRS = SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1);
                try {
                    crs = CRS.forCode(strCRS);
                } catch (FactoryException ex) {
                    Logger.getLogger("org.geotoolkit.geometry.isoonjts").log(Level.SEVERE, null, ex);
                }
            }
        }

        //TODO use factory finder when primitive factory and geometry factory are ready.
        final var pf = new JTSPrimitiveFactory(crs);//FactoryFinder.getPrimitiveFactory(hints);
        final var gf  = new JTSGeometryFactory(crs); //FactoryFinder.getGeometryFactory(hints);

        if (jtsGeom instanceof org.locationtech.jts.geom.Point) {
            org.locationtech.jts.geom.Point candidate = (org.locationtech.jts.geom.Point) jtsGeom;
            DirectPosition dp = pointToDirectPosition(candidate, crs);
            return pf.createPoint(dp);

        } else if (jtsGeom instanceof org.locationtech.jts.geom.LineString) {
            org.locationtech.jts.geom.LineString candidate = (org.locationtech.jts.geom.LineString) jtsGeom;
            LineString ls = gf.createLineString(new ArrayList<Position>());
            PointArray pointList = ls.getControlPoints();
            for (int i = 0, n = candidate.getNumPoints(); i < n; i++) {
                pointList.add(coordinateToDirectPosition(candidate.getCoordinateN(i), crs));
            }
            return (JTSLineString)ls;

        } else if (jtsGeom instanceof org.locationtech.jts.geom.LinearRing) {
            return linearRingToRing((org.locationtech.jts.geom.LinearRing) jtsGeom, crs);

        } else if (jtsGeom instanceof org.locationtech.jts.geom.Polygon) {
            org.locationtech.jts.geom.Polygon jtsPolygon = (org.locationtech.jts.geom.Polygon) jtsGeom;
            Ring externalRing = linearRingToRing(
                    (org.locationtech.jts.geom.LinearRing) jtsPolygon.getExteriorRing(),
                    crs);
            ArrayList internalRings = new ArrayList();
            for (int i = 0, n = jtsPolygon.getNumInteriorRing(); i < n; i++) {
                internalRings.add(linearRingToRing(
                        (org.locationtech.jts.geom.LinearRing) jtsPolygon.getInteriorRingN(i),
                        crs));
            }
            SurfaceBoundary boundary = pf.createSurfaceBoundary(externalRing, internalRings);
            Polygon polygon = gf.createPolygon(boundary);
            return (JTSPolygon) polygon;

            /*ArrayList<Polygon> patches = new ArrayList<Polygon>();
            patches.add(polygon);
            PolyhedralSurface result = gf.createPolyhedralSurface(patches);
            return result;*/

        } else if (jtsGeom instanceof GeometryCollection) {
            org.locationtech.jts.geom.GeometryCollection jtsCollection = (org.locationtech.jts.geom.GeometryCollection) jtsGeom;
            boolean multiPoint   = jtsGeom instanceof MultiPoint;
            boolean multiCurve   = jtsGeom instanceof MultiLineString;
            boolean multiSurface = jtsGeom instanceof MultiPolygon;

            // We cannot determine geometry nature using its type. We will try to
            // determine it by analyzing its content.
            if (!(multiPoint || multiCurve || multiSurface || jtsGeom.isEmpty())) {
                multiPoint = multiCurve = multiSurface = true;
                for (int i = 0, n = jtsCollection.getNumGeometries(); i < n && (multiPoint || multiCurve || multiSurface); i++) {
                    if (!(jtsCollection.getGeometryN(i) instanceof org.locationtech.jts.geom.Point)) {
                        multiPoint = false;
                    }
                    if (!(jtsCollection.getGeometryN(i) instanceof org.locationtech.jts.geom.LineString)) {
                        multiCurve = false;
                    }
                    if (!(jtsCollection.getGeometryN(i) instanceof org.locationtech.jts.geom.Polygon)) {
                        multiSurface = false;
                    }
                }
            }
            AbstractJTSAggregate result;
            if (multiPoint) {
                result = new JTSMultiPoint(crs);
                Set elements = result.getElements();
                for (int i = 0, n = jtsCollection.getNumGeometries(); i < n; i++) {
                    //result.getElements().add(jtsToGo1(jtsCollection.getGeometryN(i), crs));
                    elements.add(toISO(jtsCollection.getGeometryN(i), crs));
                }
            } else if (multiCurve) {
                result = new JTSMultiCurve(crs);
                Set elements = result.getElements();
                for (int i = 0, n = jtsCollection.getNumGeometries(); i < n; i++) {
                    //result.getElements().add(jtsToGo1(jtsCollection.getGeometryN(i), crs));
                    Geometry element = toISO(jtsCollection.getGeometryN(i), crs);
                    if (element instanceof JTSLineString) {
                        JTSCurve curve = new JTSCurve(crs);
                        curve.getSegments().add((JTSLineString) element);
                        element = curve;

                    }
                    elements.add(element);
                }
            }  else if (multiSurface) {
                result = new JTSMultiSurface(crs);
                Set elements = result.getElements();
                for (int i = 0, n = jtsCollection.getNumGeometries(); i < n; i++) {
                    //result.getElements().add(jtsToGo1(jtsCollection.getGeometryN(i), crs));
                    elements.add(toISO(jtsCollection.getGeometryN(i), crs));
                }
            } else {
                result = new JTSMultiPrimitive();
                Set elements = result.getElements();
                for (int i = 0, n = jtsCollection.getNumGeometries(); i < n; i++) {
                    //result.getElements().add(jtsToGo1(jtsCollection.getGeometryN(i), crs));
                    elements.add(toISO(jtsCollection.getGeometryN(i), crs));
                }
            }
            return result;

        } else {
            throw new IllegalArgumentException("Unsupported geometry type: " + jtsGeom.getGeometryType());
        }
    }

    /**
     * Converts a DirectPosition to a JTS Coordinate.  Returns a newly
     * instantiated Coordinate object.
     */
    public static org.locationtech.jts.geom.Coordinate directPositionToCoordinate(final DirectPosition dp) {
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;
        final int d = dp.getDimension();
        if (d >= 1) {
            x = dp.getCoordinate(0);
            if (d >= 2) {
                y = dp.getCoordinate(1);
                if (d >= 3) {
                    z = dp.getCoordinate(2);
                }
            }
        }
        return new org.locationtech.jts.geom.Coordinate(x, y, z);
    }

    /**
     * Sets the coordinate values of an existing JTS Coordinate by extracting
     * values from a DirectPosition.  If the dimension of the DirectPosition is
     * less than three, then the unused coordinates of the Coordinate are set to
     * Double.NaN.
     */
    public static void directPositionToCoordinate(final DirectPosition dp, final org.locationtech.jts.geom.Coordinate result) {
        final int d = dp.getDimension();
        if (d >= 1) {
            result.x = dp.getCoordinate(0);
            if (d >= 2) {
                result.y = dp.getCoordinate(1);
                if (d >= 3) {
                    result.z = dp.getCoordinate(3);
                } else {
                    result.z = Double.NaN;
                }
            } else {
                result.y = result.z = Double.NaN;
            }
        } else {
            // I can't imagine a DirectPosition with dimension zero, but it
            // can't hurt to have code to handle that case...
            result.x = result.y = result.z = Double.NaN;
        }
    }

    /**
     * Converts a DirectPosition to a JTS Point primitive.  Returns a newly
     * instantiated Point object that was created using the default
     * GeometryFactory instance.
     */
    public static org.locationtech.jts.geom.Point directPositionToPoint(final DirectPosition dp) {
        return GEOMETRY_FACTORY.createPoint(directPositionToCoordinate(dp));
    }

    /**
     * Converts a JTS Coordinate to a DirectPosition with the given CRS.
     */
    public static DirectPosition coordinateToDirectPosition(final org.locationtech.jts.geom.Coordinate c,
            final CoordinateReferenceSystem crs) {

        PositionFactory pf = new JTSPositionFactory(crs);

        double[] vertices;
        if (crs == null) {
            vertices = new double[3];
            vertices[0] = c.x;
            vertices[1] = c.y;
            vertices[2] = c.z;
        } else {
            vertices = new double[crs.getCoordinateSystem().getDimension()];
            if(vertices.length > 0){
                vertices[0] = c.x;
                if(vertices.length > 1){
                    vertices[1] = c.y;
                    if(vertices.length > 2){
                        vertices[2] = c.z;
                    }
                }
            }
        }

        return pf.createDirectPosition(vertices);
    }

    /**
     * Extracts the values of a JTS coordinate into an existing DirectPosition
     * object.
     */
    public static void coordinateToDirectPosition(final org.locationtech.jts.geom.Coordinate c,
            final DirectPosition result) {
        // Get the CRS so we can figure out the dimension of the result.
        CoordinateReferenceSystem crs = result.getCoordinateReferenceSystem();
        int d;

        if (crs != null) {
            d = crs.getCoordinateSystem().getDimension();
        } else {
            // If the result DP has no CRS, then we just assume 2 dimensions.
            // This could result in IndexOutOfBounds exceptions if the DP has
            // fewer than 2 coordinates.
            d = 2;
        }
        final CoordinateSystem cs = crs.getCoordinateSystem();

        if (d >= 1) {
            int xIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.EAST);
            result.setCoordinate(xIndex, c.x);//0
            if (d >= 2) {
                int yIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.NORTH);
                result.setCoordinate(yIndex, c.y);//1
                if (d >= 3) {
                    int zIndex = GeometryUtils.getDirectedAxisIndex(cs, AxisDirection.UP);
                    result.setCoordinate(zIndex, c.z);//2
                    // If d > 3, then the remaining coordinates of the DP are
                    // (so far) left with their original values.  So we init
                    // them to zero here.
                    if (d > 3) {
                        for (int i = 3; i < d; i++) {
                            result.setCoordinate(i, 0.0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Converts a JTS Point to a DirectPosition with the given CRS.
     */
    public static DirectPosition pointToDirectPosition(final org.locationtech.jts.geom.Point p,
            final CoordinateReferenceSystem crs) {
        return coordinateToDirectPosition(p.getCoordinate(), crs);
    }

    public static Ring linearRingToRing(final org.locationtech.jts.geom.LineString jtsLinearRing,
            final CoordinateReferenceSystem crs) {
        int numPoints = jtsLinearRing.getNumPoints();
        if (numPoints != 0 && !jtsLinearRing.getCoordinateN(0).equals(jtsLinearRing.getCoordinateN(numPoints - 1))) {
            throw new IllegalArgumentException("LineString must be a ring");
        }

        var pf = new JTSPrimitiveFactory(crs); //FactoryFinder.getPrimitiveFactory(hints);
        var gf = new JTSGeometryFactory(crs); //FactoryFinder.getGeometryFactory(hints);

        LineString ls = gf.createLineString(new ArrayList());
        List pointList = ls.getControlPoints();
        for (int i = 0; i < numPoints; i++) {
            pointList.add(coordinateToDirectPosition(jtsLinearRing.getCoordinateN(i), crs));
        }
        Curve curve = pf.createCurve(new ArrayList());
        // Cast below can be removed when Types will be allowed to abandon Java 1.4 support.
        ((List) curve.getSegments()).add(ls);
        Ring result = pf.createRing(new ArrayList());
        // Cast below can be removed when Types will be allowed to abandon Java 1.4 support.
        ((List) result.getGenerators()).add(curve);
        return result;
    }

    /**
     * Computes the distance between two JTS geometries.  Unfortunately, JTS's
     * methods do not allow for either parameter to be a collection.  So we have
     * to implement the logic of dealing with collection geometries separately.
     */
    public static double distance(final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        if (g1 instanceof org.locationtech.jts.geom.GeometryCollection) {
            double minDistance = Double.POSITIVE_INFINITY;
            org.locationtech.jts.geom.GeometryCollection gc1 =
                    (org.locationtech.jts.geom.GeometryCollection) g1;
            int n = gc1.getNumGeometries();
            for (int i = 0; i < n; i++) {
                double d = distance(gc1.getGeometryN(i), g2);
                if (d < minDistance) {
                    minDistance = d;
                }
            }
            return minDistance;
        } else if (g2 instanceof org.locationtech.jts.geom.GeometryCollection) {
            double minDistance = Double.POSITIVE_INFINITY;
            org.locationtech.jts.geom.GeometryCollection gc2 =
                    (org.locationtech.jts.geom.GeometryCollection) g2;
            int n = gc2.getNumGeometries();
            for (int i = 0; i < n; i++) {
                // This call will result in a redundant check of
                // g1 instanceof GeometryCollection.  Maybe we oughta fix that
                // somehow.
                double d = distance(g1, gc2.getGeometryN(i));
                if (d < minDistance) {
                    minDistance = d;
                }
            }
            return minDistance;
        } else {
            return g1.distance(g2);
        }
    }

    /**
     * Returns the union of the two geometries.  In the case of primitive
     * geometries, this simply delegates to the JTS method.  In the case of
     * aggregates, creates an aggregate containing all the parts of both.
     */
    public static org.locationtech.jts.geom.Geometry union(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return null;
    }

    public static org.locationtech.jts.geom.Geometry intersection(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return null;
    }

    public static org.locationtech.jts.geom.Geometry difference(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return null;
    }

    public static org.locationtech.jts.geom.Geometry symmetricDifference(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return null;
    }

    public static boolean contains(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return false;
    }

    public static boolean equals(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        return false;
    }

    /**
     * Returns true if the two given geometries intersect.  In the case of
     * primitive geometries, this simply delegates to the JTS method.  In the
     * case of Aggregates, loops over pairs of children looking for
     * intersections.
     */
    public static boolean intersects(
            final org.locationtech.jts.geom.Geometry g1,
            final org.locationtech.jts.geom.Geometry g2) {
        if (g1 instanceof org.locationtech.jts.geom.GeometryCollection) {
            org.locationtech.jts.geom.GeometryCollection gc1 =
                    (org.locationtech.jts.geom.GeometryCollection) g1;
            int n = gc1.getNumGeometries();
            for (int i = 0; i < n; i++) {
                org.locationtech.jts.geom.Geometry g = gc1.getGeometryN(i);
                if (intersects(g, g2)) {
                    return true;
                }
            }
            return false;
        } else if (g2 instanceof org.locationtech.jts.geom.GeometryCollection) {
            org.locationtech.jts.geom.GeometryCollection gc2 =
                    (org.locationtech.jts.geom.GeometryCollection) g2;
            int n = gc2.getNumGeometries();
            for (int i = 0; i < n; i++) {
                org.locationtech.jts.geom.Geometry g = gc2.getGeometryN(i);
                if (intersects(g1, g)) {
                    return true;
                }
            }
            return false;
        } else {
            return g1.intersects(g2);
        }
    }

    /**
     * Creates a JTS LineString from the four corners of the specified Envelope.
     * @param envelope The Envelope to be converted
     * @return A JTS Geometry
     */
    public static org.locationtech.jts.geom.Geometry getEnvelopeGeometry(
            final Envelope envelope) {
        // PENDING(NL): Add code to check for CRS compatibility
        // Must consider possibility that this is a pixel envelope
        // rather than geo coordinate; only way to be sure is to check Units
        DirectPosition topCorner = envelope.getUpperCorner();
        DirectPosition botCorner = envelope.getLowerCorner();
        DirectPosition topLeft = new GeneralDirectPosition(topCorner);
        DirectPosition botRight = new GeneralDirectPosition(botCorner);

        //Again, making assumption we can ignore this LatLonAlt stuff - colin

        /*
        // If the Envelope coordinates are LatLonAlts,
        // calling setOrdinate causes Error-level logging messages,
        // including a stack trace,
        // though it still works.  But in principal we should
        // call get/setLat and get/setLon instead if we have LatLonAlts
        if (topLeft instanceof LatLonAlt && botRight instanceof LatLonAlt) {
        ((LatLonAlt) topLeft).setLon(((LatLonAlt)
        botCorner).getLon(Units.DEGREE), Units.DEGREE);
        ((LatLonAlt) botRight).setLon(((LatLonAlt)
        topCorner).getLon(Units.DEGREE), Units.DEGREE);
        } else {*/

        topLeft.setCoordinate(1, botCorner.getCoordinate(1));
        botRight.setCoordinate(1, topCorner.getCoordinate(1));

        //}//end of else statment associated with above LatLongAlt stuff
        // Create a JTS Envelope
        Coordinate jtsTopRight =
                JTSUtils.directPositionToCoordinate(topCorner);
        Coordinate jtsTopLeft =
                JTSUtils.directPositionToCoordinate(topLeft);
        Coordinate jtsBotLeft =
                JTSUtils.directPositionToCoordinate(botCorner);
        Coordinate jtsBotRight =
                JTSUtils.directPositionToCoordinate(botRight);

        org.locationtech.jts.geom.Geometry jtsEnv = GEOMETRY_FACTORY.createLineString(
                new Coordinate[]{jtsTopLeft, jtsTopRight, jtsBotRight, jtsBotLeft,
                    jtsTopLeft}).getEnvelope();
        return jtsEnv;
    }
}
