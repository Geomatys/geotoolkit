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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.xml.AbstractGeometricAggregate;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.referencing.IdentifiedObjects;
import static org.geotoolkit.gml.xml.GMLXmlFactory.*;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Set of converters from JTS Geometry to GML Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @module pending
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

        } else if (jts instanceof LineString) {
            return toGML(gmlVersion,(LineString) jts, crs);

        } else if (jts instanceof Polygon) {
            return toGML(gmlVersion,(Polygon) jts, crs);

        } else if (jts instanceof LinearRing) {
            return toGML(gmlVersion,(LinearRing) jts, crs);

        } else if (jts instanceof GeometryCollection) {
            return toGML(gmlVersion,(GeometryCollection) jts, crs);

        } else if (jts instanceof MultiPoint) {
            return toGML(gmlVersion,(MultiPoint) jts, crs);

        } else if (jts instanceof MultiLineString) {
            return toGML(gmlVersion,(MultiLineString) jts, crs);

        } else if (jts instanceof MultiPolygon) {
            return toGML(gmlVersion,(MultiPolygon) jts, crs);

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

        //Get th e class of the first geometry in the GeometryCollection
        Class buffer = null;
        if (jtsGeom.getNumGeometries() > 0) {
            final Geometry geom = jtsGeom.getGeometryN(0);
            if (geom.getClass().isAssignableFrom(Polygon.class) || geom.getClass().isAssignableFrom(Point.class)
                    || geom.getClass().isAssignableFrom(LineString.class)) {
                buffer = geom.getClass();
            }
        }
        //Verify if all other geometries contained by the GeometryCollection is from the same class
        boolean isSupported = true;
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            if (!(jtsGeom.getGeometryN(i).getClass().isAssignableFrom(buffer))) {
                isSupported = false;
                break;
            }
        }

        if (isSupported) {
            final GeometryFactory gf = new GeometryFactory();
            //Convert to a MultiPoint
            if (buffer.equals(Point.class)) {
                List<Point> ptList = new ArrayList<>();
                for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
                    ptList.add((Point) jtsGeom.getGeometryN(i));
                }
                final MultiPoint mutlPt = gf.createMultiPoint(ptList.toArray(new Point[ptList.size()]));
                JTS.setCRS(mutlPt, crs);
                return toGML(gmlVersion, mutlPt, crs);

                //Convert to a MultiLineString
            } else if (buffer.equals(LineString.class)) {
                List<LineString> lsList = new ArrayList<>();
                for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
                    lsList.add((LineString) jtsGeom.getGeometryN(i));
                }
                final MultiLineString multLineString = gf.createMultiLineString(lsList.toArray(new LineString[lsList.size()]));
                JTS.setCRS(multLineString, crs);
                return toGML(gmlVersion, multLineString, crs);

            } else if (buffer.equals(Polygon.class)) {
                List<Polygon> polyList = new ArrayList<>();
                for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
                    polyList.add((Polygon) jtsGeom.getGeometryN(i));
                }
                final MultiPolygon multPoly = gf.createMultiPolygon(polyList.toArray(new Polygon[polyList.size()]));
                JTS.setCRS(multPoly, crs);
                return toGML(gmlVersion, multPoly, crs);
            } else {
                throw new IllegalArgumentException("Unssupported geometry type : " + jtsGeom);
            }
        } else {
            throw new IllegalArgumentException("Unssupported geometry type : " + jtsGeom);
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

        final List<org.geotoolkit.gml.xml.Point> pointList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            pointList.add(toGML(gmlVersion, (Point) jtsGeom.getGeometryN(i), crs));
        }

        final String srs = getSRS(crs);
        return buildMultiPoint(gmlVersion, pointList, srs);
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

        final List<org.geotoolkit.gml.xml.LineString> lineList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {

            lineList.add(toGML(gmlVersion, (LineString) jtsGeom.getGeometryN(i), crs));
        }

        final String srs = getSRS(crs);
        return buildMultiLineString(gmlVersion, lineList, srs);
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

        final List<org.geotoolkit.gml.xml.Polygon> polyList = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumGeometries(); i++) {
            polyList.add(toGML(gmlVersion, (Polygon) jtsGeom.getGeometryN(i), crs));
        }

        final String srs = getSRS(crs);
        return buildMultiPolygon(gmlVersion, polyList, srs);
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

        //get exterior ring
        final AbstractRing gmlExterior = toGML(gmlVersion, (LinearRing) jtsGeom.getExteriorRing(), crs);
        //get interiors ring
        final List<AbstractRing> gmlInterior = new ArrayList<>();
        for (int i = 0; i < jtsGeom.getNumInteriorRing(); i++) {
            gmlInterior.add(toGML(gmlVersion, (LinearRing) jtsGeom.getInteriorRingN(i), crs));
        }
        return buildPolygon(gmlVersion, gmlExterior, gmlInterior, getSRS(crs));
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

        final List<DirectPosition> dpList = new ArrayList<>();

        for (Coordinate c : jtsCoord) {
            dpList.add(coordinateToDirectPosition(c, crs));
        }
        final String srsName = getSRS(crs);
        final org.geotoolkit.gml.xml.LineString gmlString = buildLineString(gmlVersion, null, srsName, dpList);
        gmlString.setSrsName(getSRS(crs));

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

        return buildLinearRing(gmlVersion, coordList, getSRS(crs));
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

        final org.geotoolkit.gml.xml.Point gmlPoint = buildPoint(gmlVersion, null, coordinateToDirectPosition(jtsPoint.getCoordinate(), crs));

        gmlPoint.setSrsName(getSRS(crs));
        return gmlPoint;
    }

    /**
     * Convert Coordinate to DirectPosition only in 2D
     * @param coord
     * @param crs
     * @return DirectPostion with x and y
     * @throws IllegalArgumentException if isn't a 2D Geometry
     */
    private static DirectPosition coordinateToDirectPosition(final Coordinate coord, final CoordinateReferenceSystem crs) {
        if (coord.z != Double.NaN) {
            //throw new IllegalArgumentException("This service support only 2D coordinate.");
        }

        return new DirectPosition2D(crs, coord.x, coord.y);
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
        final String method1 = org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, crs, false);

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
}
