/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.ArrayList;
import java.util.List;

import java.util.StringTokenizer;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.gml.xml.v311.AbstractGMLEntry;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CoordinatesType;
import org.geotoolkit.gml.xml.v311.CurveInterpolationType;
import org.geotoolkit.gml.xml.v311.CurvePropertyType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.MultiGeometryType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonPatchArrayPropertyType;
import org.geotoolkit.gml.xml.v311.PolygonPatchType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.geotoolkit.gml.xml.v311.SurfaceInterpolationType;

import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class to manipulate gml jaxb bindings.
 *
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
public class GMLUtilities {

    private static final GeometryFactory GF = new GeometryFactory();

    private GMLUtilities(){}

    public static AbstractGMLEntry getGMLFromISO(org.opengis.geometry.Geometry geometry) {
       if (geometry instanceof Point) {
           Point point     = (Point) geometry;
           PointType gmlPoint = new PointType(null, point.getDirectPosition());
           return gmlPoint;
       } else if (geometry instanceof MultiPrimitive) {
           MultiPrimitive multiPrim           = (MultiPrimitive) geometry;
           List<GeometryPropertyType> geometries = new ArrayList<GeometryPropertyType>();
           for (Primitive prim : multiPrim.getElements()) {
               AbstractGMLEntry element = getGMLFromISO(prim);
               GeometryPropertyType gp  = new GeometryPropertyType((AbstractGeometryType)element);
               geometries.add(gp);
           }
           MultiGeometryType gmlMulti = new MultiGeometryType(geometries);
           return gmlMulti;

       } else if (geometry instanceof Curve) {
            Curve curve = (Curve) geometry;
            List<? extends CurveSegment> segments = curve.getSegments();
            List<LineStringSegmentType> gmlSegments = new ArrayList<LineStringSegmentType>();
            for (CurveSegment segment : segments) {
                CurveInterpolationType interpolation = CurveInterpolationType.fromValue(segment.getInterpolation().identifier());
                PointArray array = segment.getSamplePoints();
                List<DirectPosition> positions = new ArrayList<DirectPosition>();
                for (int i =0; i < array.size(); i++) {
                    positions.add(array.getDirectPosition(i, null));
                }
                LineStringSegmentType gmlSegment = new LineStringSegmentType(segment.getNumDerivativesAtStart(),
                                                                             segment.getNumDerivativesAtEnd(),
                                                                             segment.getNumDerivativesInterior(),
                                                                             interpolation,
                                                                             positions);
                gmlSegments.add(gmlSegment);
            }
            CurveType gmlCurve = new CurveType(gmlSegments);
            return gmlCurve;

       } else if (geometry instanceof PolyhedralSurface) {
           PolyhedralSurface polySurface = (PolyhedralSurface) geometry;
           List<PolygonPatchType> gmlPatches = new ArrayList<PolygonPatchType>();
           List<? extends Polygon> patches = polySurface.getPatches();
           for (Polygon polygon : patches) {

               SurfaceInterpolationType interpolation = SurfaceInterpolationType.fromValue(polygon.getInterpolation().identifier());
               SurfaceBoundary boundary = polygon.getBoundary();

               Ring exterior            = boundary.getExterior();
               List<CurvePropertyType> curves   = new ArrayList<CurvePropertyType>();
               for (Primitive p : exterior.getElements()) {
                   curves.add(new CurvePropertyType((CurveType) getGMLFromISO(p)));
               }
               RingType gmlExterior     = new RingType();
               gmlExterior.getCurveMember().addAll(curves);

               List<Ring> interiors        = boundary.getInteriors();
               List<RingType> gmlInteriors = new ArrayList<RingType>();
               for (Ring interior : interiors) {
                   List<CurvePropertyType> intcurves   = new ArrayList<CurvePropertyType>();
                   for (Primitive p : interior.getElements()) {
                        intcurves.add(new CurvePropertyType((CurveType) getGMLFromISO(p)));
                    }
                    RingType gmlinterior = new RingType();
                    gmlinterior.getCurveMember().addAll(intcurves);
                    gmlInteriors.add(gmlinterior);
               }
               PolygonPatchType patche  = new PolygonPatchType(interpolation, gmlExterior, gmlInteriors);
               gmlPatches.add(patche);
           }
           PolygonPatchArrayPropertyType pathArray = new PolygonPatchArrayPropertyType(gmlPatches);
           PolyhedralSurfaceType gmlPolySurface    = new PolyhedralSurfaceType(pathArray);
           return gmlPolySurface;
       } else {
           System.out.println("unexpected iso geometry type:" + geometry.getClass().getName());
       }
       return null;
   }

    /**
     * Transform A GML envelope into a treatable geometric object : GeneralEnvelope
     *
     * @param GMLenvelope A GML envelope.
     *
     * @return A general Envelope.
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public static GeneralEnvelope gmlEnvelopeToGeneralEnvelope(EnvelopeEntry gmlEnvelope)
            throws NoSuchAuthorityCodeException, FactoryException {
        final String crsName = gmlEnvelope.getSrsName();
        if (crsName == null) {
            throw new IllegalArgumentException("An operator BBOX must specified a CRS (coordinate Reference system) for the envelope.");
        }

        final List<Double> lmin = gmlEnvelope.getLowerCorner().getValue();
        final double[] min      = new double[lmin.size()];
        for (int i = 0; i < min.length; i++) {
            min[i] = lmin.get(i);
        }

        final List<Double> lmax = gmlEnvelope.getUpperCorner().getValue();
        final double[] max = new double[lmax.size()];
        for (int i = 0; i < min.length; i++) {
            max[i] = lmax.get(i);
        }

        final GeneralEnvelope envelopeF     = new GeneralEnvelope(min, max);
        final CoordinateReferenceSystem crs = CRS.decode(crsName, true);
        envelopeF.setCoordinateReferenceSystem(crs);
        return envelopeF;
    }


    /**
     * Transform A GML envelope into JTS Polygon
     *
     * @param GMLenvelope A GML envelope.
     *
     * @return A JTS Polygon
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public static com.vividsolutions.jts.geom.Polygon toJTS(EnvelopeEntry gmlEnvelope)
            throws NoSuchAuthorityCodeException, FactoryException {
        final String crsName = gmlEnvelope.getSrsName();
        if (crsName == null) {
            throw new IllegalArgumentException("An operator BBOX must specified a CRS (coordinate Reference system) for the envelope.");
        }

        final List<Double> lmin = gmlEnvelope.getLowerCorner().getValue();
        final double[] min      = new double[lmin.size()];
        for (int i = 0; i < min.length; i++) {
            min[i] = lmin.get(i);
        }

        final List<Double> lmax = gmlEnvelope.getUpperCorner().getValue();
        final double[] max = new double[lmax.size()];
        for (int i = 0; i < min.length; i++) {
            max[i] = lmax.get(i);
        }

        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(min[0], min[1]),
            new Coordinate(min[0], max[1]),
            new Coordinate(max[0], max[1]),
            new Coordinate(max[0], min[1]),
            new Coordinate(min[0], min[1])
        });
        final com.vividsolutions.jts.geom.Polygon polygon = GF.createPolygon(ring, new LinearRing[0]);
        final CoordinateReferenceSystem crs = CRS.decode(crsName, true);
        final int srid = SRIDGenerator.toSRID(crs, Version.V1);
        polygon.setSRID(srid);

        return polygon;
    }

    /**
     * Transform A GML lineString into a JTS lineString
     *
     * @param GMLlineString A GML lineString.
     *
     * @return A JTS LineString.
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public static LineString toJTS(LineStringType gmlLine) throws NoSuchAuthorityCodeException, FactoryException {
        final String crsName = gmlLine.getSrsName();
        if (crsName == null) {
            throw new FactoryException("A CRS (coordinate Reference system) must be specified for the line.");
        }

        final CoordinatesType coord = gmlLine.getCoordinates();
        String s = coord.getValue();

        double x1 = Double.parseDouble(s.substring(0, s.indexOf(coord.getCs())));
        s = s.substring(s.indexOf(coord.getCs()) + 1);
        double y1 = Double.parseDouble(s.substring(0, s.indexOf(coord.getTs())));
        s = s.substring(s.indexOf(coord.getTs()) + 1);
        double x2 = Double.parseDouble(s.substring(0, s.indexOf(coord.getCs())));
        s = s.substring(s.indexOf(coord.getCs()) + 1);
        double y2 = Double.parseDouble(s);

        final int srid = SRIDGenerator.toSRID(crsName, Version.V1);
        final LineString ls = GF.createLineString(new Coordinate[]{
            new Coordinate(x1,y1),
            new Coordinate(x2,y2)
        });
        ls.setSRID(srid);

        return ls;
    }

    /**
     * Transform A GML point into a treatable geometric object : GeneralDirectPosition
     *
     * @param GMLpoint The GML point to transform.
     *
     * @return A GeneralDirectPosition.
     *
     * @throws org.constellation.coverage.web.CstlServiceException
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public static com.vividsolutions.jts.geom.Point toJTS(PointType gmlPoint)
            throws NoSuchAuthorityCodeException, FactoryException {

        final String crsName = gmlPoint.getSrsName();

        if (crsName == null) {
            throw new IllegalArgumentException("A GML point must specify Coordinate Reference System.");
        }

        //we get the coordinate of the point (if they are present)
        if (gmlPoint.getCoordinates() == null && gmlPoint.getPos() == null) {
            throw new IllegalArgumentException("A GML point must specify coordinates or direct position.");
        }

        final double[] coordinates = new double[2];
        if (gmlPoint.getCoordinates() != null) {
            final String coord = gmlPoint.getCoordinates().getValue();

            final StringTokenizer tokens = new StringTokenizer(coord, " ");
            int index = 0;
            while (tokens.hasMoreTokens()) {
                final double value = parseDouble(tokens.nextToken());
                if (index >= coordinates.length) {
                    throw new IllegalArgumentException("This service support only 2D point.");
                }
                coordinates[index++] = value;
            }
        } else if (gmlPoint.getPos().getValue() != null && gmlPoint.getPos().getValue().size() == 2){
            coordinates[0] = gmlPoint.getPos().getValue().get(0);
            coordinates[0] = gmlPoint.getPos().getValue().get(1);
        } else {
            throw new IllegalArgumentException("The GML point is malformed.");
        }


        final CoordinateReferenceSystem crs = CRS.decode(crsName, true);
        final int srid = SRIDGenerator.toSRID(crs, Version.V1);

        final com.vividsolutions.jts.geom.Point pt =
                GF.createPoint(new Coordinate(coordinates[0], coordinates[1]));
        pt.setSRID(srid);

        return pt;
    }

    /**
     * Parses a value as a floating point.
     *
     * @throws CstlServiceException if the value can't be parsed.
     */
    private static double parseDouble(String value) {
        value = value.trim();
        return Double.parseDouble(value);
    }

}
