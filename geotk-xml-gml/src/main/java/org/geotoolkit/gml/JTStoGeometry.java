/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.gml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.xml.AbstractGeometricAggregate;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import static org.geotoolkit.gml.xml.GMLXmlFactory.*;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Set of converters from JTS Geometry to GML Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @module
 */
public final class JTStoGeometry {

    /**
     * Private constructor.
     */
    private JTStoGeometry(){}

    /**
     * Transform A JTS geometry into GML geometry
     *
     * @param gmlVersion The output gml version. (actually 3.1.1 or 3.2.1 are avalable)
     * @param jts The JTS geometry to convert.
     *
     * @return AbstractGeometry gml geometry.
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be extracted from JTS geometry or can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be extracted from JTS
     * geometry or can't be injected into the {@link AbstractGeometry}.
     */
     public static AbstractGeometry toGML(final String gmlVersion, final Geometry jts) throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(jts);
        return toGML(gmlVersion, jts, crs);

     }

    /**
     * Transform A JTS geometry into GML geometry
     *
     * @param gmlVersion The output gml version. (actually 3.1.1 or 3.2.1 are avalable)
     * @param jts The JTS geometry to convert.
     * @param crs
     *
     * @return AbstractGeometry gml geometry.
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be extracted from JTS geometry or can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be extracted from JTS
     * geometry or can't be injected into the {@link AbstractGeometry}.
     */
    public static AbstractGeometry toGML(final String gmlVersion, final Geometry jts, CoordinateReferenceSystem crs) throws NoSuchAuthorityCodeException, FactoryException {

        if (crs == null) {
            crs = JTS.findCoordinateReferenceSystem(jts);
        }
        if (jts instanceof Point) {
            return toGML(gmlVersion, (Point) jts, crs);

        } else if (jts instanceof Polygon) {
            return toGML(gmlVersion,(Polygon) jts, crs);

        } else if (jts instanceof LinearRing) {
            return toGML(gmlVersion,(LinearRing) jts, crs);

        } else if (jts instanceof LineString) {
            return toGML(gmlVersion,(LineString) jts, crs);

        } else if (jts instanceof MultiPoint) {
            return toGML(gmlVersion,(MultiPoint) jts, crs);

        } else if (jts instanceof MultiLineString) {
            return toGML(gmlVersion,(MultiLineString) jts, crs);

        } else if (jts instanceof MultiPolygon) {
            return toGML(gmlVersion,(MultiPolygon) jts, crs);

        } else if (jts instanceof GeometryCollection) {
            return toGML(gmlVersion,(GeometryCollection) jts, crs);

        } else {
            throw new IllegalArgumentException("Unsupported geometry type : " + jts);
        }
    }

    /**
     * Try to convert a JTS GeometryCollection to a GML AbstractGeometricAggregateType
     *
     * @param jtsGeom {@link GeometryCollection collection}
     * @param crs Coordinate Reference System
     * @return AbstractGeometricAggregateType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs} can't be
     * injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static AbstractGeometricAggregate toGML(final String gmlVersion, final GeometryCollection jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);

        if (jtsGeom.isEmpty()) {
            throw new IllegalArgumentException("Cannot write empty geometry for unidentified geometry collection type");
        }

        final Class buffer = jtsGeom.getGeometryN(0).getClass();

        //Get the class of the first geometry in the GeometryCollection
        if (!(buffer.isAssignableFrom(Polygon.class)
            || buffer.isAssignableFrom(Point.class)
            || buffer.isAssignableFrom(LineString.class))) {
            throw new IllegalArgumentException("Writing GML geometry collection works only for Points, LineStrings and polygons");
        }

        //Verify that we've got a single geometry type
        boolean isSupported = true;
        final List<Geometry> innerGeometries = new ArrayList<>(jtsGeom.getNumGeometries());
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            final Geometry subGeom = jtsGeom.getGeometryN(i);
            if (!(subGeom.getClass().isAssignableFrom(buffer))) {
                throw new IllegalArgumentException("Writing GML geometry collection containing different geometry types is not supported");
            }
            innerGeometries.add(subGeom);
        }

            final GeometryFactory gf = JTS.getFactory();
            //Convert to a MultiPoint
            if (buffer.equals(Point.class)) {
                final MultiPoint mutlPt = gf.createMultiPoint(innerGeometries.toArray(new Point[innerGeometries.size()]));
                JTS.setCRS(mutlPt, crs);
                return toGML(gmlVersion, mutlPt, crs);

                //Convert to a MultiLineString
            } else if (buffer.equals(LineString.class)) {
                final MultiLineString multLineString = gf.createMultiLineString(innerGeometries.toArray(new LineString[innerGeometries.size()]));
                JTS.setCRS(multLineString, crs);
                return toGML(gmlVersion, multLineString, crs);

            } else if (buffer.equals(Polygon.class)) {
                final MultiPolygon multPoly = gf.createMultiPolygon(innerGeometries.toArray(new Polygon[innerGeometries.size()]));
                JTS.setCRS(multPoly, crs);
                return toGML(gmlVersion, multPoly, crs);
            } else {
                throw new IllegalArgumentException("Writing GML geometry collection works only for Points, LineStrings and polygons");
            }

    }

    /**
     * Convert JTS MultiPoint to GML MultiPointType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiPointType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.MultiPoint toGML(final String gmlVersion, final MultiPoint jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final String srsName = getSRS(crs);
        final int srsDimension = crs.getCoordinateSystem().getDimension();

        final List<org.geotoolkit.gml.xml.Point> pointList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            org.geotoolkit.gml.xml.Point point = toGML(gmlVersion, (Point) jtsGeom.getGeometryN(i), crs);
            point.setSrsDimension(null);
            point.setSrsName(null);
            pointList.add(point);
        }

        final String srs = getSRS(crs);
        org.geotoolkit.gml.xml.MultiPoint mp = buildMultiPoint(gmlVersion, pointList, srs);
        mp.setSrsName(srsName);
        mp.setSrsDimension(srsDimension);
        return mp;
    }

    /**
     * Convert JTS MultiLineString to GML MultiLineStringType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiLineStringType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.AbstractGeometricAggregate toGML(final String gmlVersion, final MultiLineString jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final String srsName = getSRS(crs);
        final int srsDimension = crs.getCoordinateSystem().getDimension();

        final List<org.geotoolkit.gml.xml.LineString> lineList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            org.geotoolkit.gml.xml.LineString line = toGML(gmlVersion, (LineString) jtsGeom.getGeometryN(i), crs);
            line.setSrsDimension(null);
            line.setSrsName(null);
            lineList.add(line);
        }

        final String srs = getSRS(crs);
        AbstractGeometricAggregate geom = buildMultiLineString(gmlVersion, lineList, srs);
        geom.setSrsDimension(srsDimension);
        geom.setSrsName(srsName);
        return geom;
    }

    /**
     * Convert JTS MultiPolygon to GML MultiPolygonType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return MultiPolygonType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.AbstractGeometricAggregate toGML(final String gmlVersion, final MultiPolygon jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final String srsName = getSRS(crs);
        final int srsDimension = crs.getCoordinateSystem().getDimension();

        final List<org.geotoolkit.gml.xml.Polygon> polyList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            org.geotoolkit.gml.xml.Polygon poly = toGML(gmlVersion, (Polygon) jtsGeom.getGeometryN(i), crs);
            poly.setSrsDimension(null);
            poly.setSrsName(null);
            polyList.add(poly);
        }

        final String srs = getSRS(crs);
        AbstractGeometricAggregate geom = buildMultiPolygon(gmlVersion, polyList, srs);
        geom.setSrsDimension(srsDimension);
        geom.setSrsName(srsName);
        return geom;
    }

    /**
     * Convert JTS Polygon to GML PolygonType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return PolygonType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.Polygon toGML(final String gmlVersion, final Polygon jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final String srsName = getSRS(crs);
        final int srsDimension = crs.getCoordinateSystem().getDimension();

        //get exterior ring
        final AbstractRing gmlExterior = toGML(gmlVersion, (LinearRing) jtsGeom.getExteriorRing(), crs);
        gmlExterior.setSrsName(null);
        gmlExterior.setSrsDimension(null);

        //get interiors ring
        final List<AbstractRing> gmlInterior = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumInteriorRing(); i++) {
            org.geotoolkit.gml.xml.LinearRing interior = toGML(gmlVersion, (LinearRing) jtsGeom.getInteriorRingN(i), crs);
            interior.setSrsName(null);
            interior.setSrsDimension(null);
            gmlInterior.add(interior);
        }
        return buildPolygon(gmlVersion, gmlExterior, gmlInterior, srsName, srsDimension);
    }

    /**
     * Convert JTS LineString to GML LineStringType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return LineStringType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.LineString toGML(final String gmlVersion, final LineString jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final Coordinate[] jtsCoord = jtsGeom.getCoordinates();

        final List<Double> coordList = new ArrayList<>();
        for (Coordinate c : jtsCoord) {
            coordList.add(c.x);
            coordList.add(c.y);
        }

        final String srsName = getSRS(crs);
        final org.geotoolkit.gml.xml.LineString gmlString = buildLineString(gmlVersion, coordList, srsName, crs.getCoordinateSystem().getDimension());
        return gmlString;
    }

    /**
     * Convert JTS LinearRing to GML LinearRingType
     * @param jtsGeom
     * @param crs Coordinate Reference System
     * @return LinearRingType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.LinearRing toGML(final String gmlVersion, final LinearRing jtsGeom, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);
        final Coordinate[] jtsCoord = jtsGeom.getCoordinates();

        final List<Double> coordList = new ArrayList<>();

        for (Coordinate c : jtsCoord) {
            coordList.add(c.x);
            coordList.add(c.y);
        }

        return buildLinearRing(gmlVersion, coordList, getSRS(crs), crs.getCoordinateSystem().getDimension());
    }

    /**
     * Convert JTS Point to GML PointType
     * @param jtsPoint
     * @param crs Coordinate Reference System
     * @return PointType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException - if {@link CoordinateReferenceSystem crs}
     * can't be injected into the {@link AbstractGeometry}.
     * @throws org.opengis.util.FactoryException - if {@link CoordinateReferenceSystem crs} can't be injected into the
     * {@link AbstractGeometry}.
     */
    public static org.geotoolkit.gml.xml.Point toGML(final String gmlVersion, final Point jtsPoint, final CoordinateReferenceSystem crs)
            throws NoSuchAuthorityCodeException, FactoryException {

        //Test if it's a 2D Geometry from CRS
        isValideGeometry(crs);

        final org.geotoolkit.gml.xml.Point gmlPoint = buildPoint(gmlVersion, null, coordinateToDirectPosition(gmlVersion, jtsPoint.getCoordinate(), crs));

        gmlPoint.setSrsName(getSRS(crs));
        gmlPoint.setSrsDimension(getSRSDimension(crs));
        return gmlPoint;
    }

    /**
     * Convert Coordinate to DirectPosition.
     * @param coord
     * @param crs
     * @return DirectPostion with x and y
     * @throws IllegalArgumentException if isn't a 2D Geometry
     */
    private static DirectPosition coordinateToDirectPosition(final String version, final Coordinate coord, final CoordinateReferenceSystem crs) throws FactoryException {
        if (Double.isNaN(coord.z)) {
            return new DirectPosition2D(crs, coord.x, coord.y);
        }

        //preserve Z value
        final String srs = getSRS(crs);
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.DirectPositionType(srs, 3, Arrays.asList(coord.x, coord.y, coord.z));
        } else if ("3.1.1".equals(version)) {
            return new DirectPositionType(srs, 3, Arrays.asList(coord.x, coord.y, coord.z));
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    /**
     * Return the Coordinate Reference System of the Geometry.
     * If the geometry CRS isn't define use <code>JTS.setCrs(geometry,CRS)</code> before call a GML conversion.
     * @param jtsGeom
     * @return the crs if valid geometry
     * @throws NoSuchAuthorityCodeException in case of unknow authority
     * @throws FactoryException in case of unknow factory
     * @throws IllegalArgumentException in case of null CRS
     */
    private static CoordinateReferenceSystem getCRS(final Geometry jtsGeom) throws NoSuchAuthorityCodeException, FactoryException {

        //get JTS CRS
        final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(jtsGeom);
        if (crs == null) {
            throw new IllegalArgumentException("JTS geometry must specify a Coordinate Reference System.");
        }

        return crs;
    }

    /**
     * Check if a geometry is only a 2D geometry
     * @param crs
     * @return <code>true</code> for valid Geometry, <code>false</code> else.
     */
    private static void isValideGeometry(final CoordinateReferenceSystem crs) {
        if (crs == null || crs.getCoordinateSystem() == null) {
            throw new IllegalArgumentException("This service support only 2D JTS Geometry (CRS null, or coordinate system null).");
        } else if (crs.getCoordinateSystem().getDimension() != 2) {
            throw new IllegalArgumentException("This service support only 2D JTS Geometry. (CRS dimension != 2) => " + crs.getCoordinateSystem().getDimension());
        }
    }
    /**
     * Extract Identifier form a Coordinate Reference System
     * @param crs
     * @return CRS identifier
     * @throws FactoryException
     */
    private static String getSRS(final CoordinateReferenceSystem crs) throws FactoryException {
        String srs = null;
        final String method1 = IdentifiedObjects.lookupURN(crs, null);

        if (method1 != null) {
            srs = method1;
        } else {
            //Try to use the deprecated methode
            final String method2 = IdentifiedObjects.getIdentifierOrName(crs);
            if (method2 != null) {
                srs = method2;
            } else {
                throw new IllegalArgumentException("Can't get Coordinate Reference System identifier.");
            }
        }

        return srs;
    }

    private static Integer getSRSDimension(final CoordinateReferenceSystem crs) throws FactoryException {
        if (crs != null && crs.getCoordinateSystem() != null) {
            return crs.getCoordinateSystem().getDimension();
        }
        return null;
    }
}
