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

import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.gml.xml.v311.AbstractGMLEntry;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CurveInterpolationType;
import org.geotoolkit.gml.xml.v311.CurvePropertyType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.MultiGeometryType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonPatchArrayPropertyType;
import org.geotoolkit.gml.xml.v311.PolygonPatchType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.geotoolkit.gml.xml.v311.SurfaceInterpolationType;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * Utility class to manipulate gml jaxb bindings.
 *
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GMLUtilities {

    private static final GeometryFactory GF = new GeometryFactory();

    public GMLUtilities(){}

    public static AbstractGeometryType getGMLFromISO(org.opengis.geometry.Geometry geometry) {
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

       } else if (geometry instanceof LineString) {
            LineString line = (LineString) geometry;

            PointArray array = line.getSamplePoints();
            List<DirectPosition> positions = new ArrayList<DirectPosition>();
            for (int i =0; i < array.size(); i++) {
                positions.add(array.getDirectPosition(i, null));
            }
            LineStringType gmlLine = new LineStringType(positions);

            return gmlLine;

       } else if (geometry instanceof Polygon) {
           Polygon polygon          = (Polygon) geometry;
           SurfaceBoundary boundary = polygon.getBoundary();
           Ring exterior            = boundary.getExterior();

           List<CurvePropertyType> curves = new ArrayList<CurvePropertyType>();
           for (Primitive p : exterior.getElements()) {
               curves.add(new CurvePropertyType((CurveType) getGMLFromISO(p)));
           }
           RingType gmlExterior = new RingType();
           gmlExterior.getCurveMember().addAll(curves);

           List<Ring> interiors = boundary.getInteriors();
           List<RingType> gmlInteriors = new ArrayList<RingType>();
           for (Ring interior : interiors) {
               List<CurvePropertyType> intcurves = new ArrayList<CurvePropertyType>();
               for (Primitive p : interior.getElements()) {
                   intcurves.add(new CurvePropertyType((CurveType) getGMLFromISO(p)));
               }
               RingType gmlinterior = new RingType();
               gmlinterior.getCurveMember().addAll(intcurves);
               gmlInteriors.add(gmlinterior);
           }
           PolygonType gmlPolygon = new PolygonType(gmlExterior, gmlInteriors);
           return gmlPolygon;
       } else if (geometry instanceof PolyhedralSurface) {
           PolyhedralSurface polySurface = (PolyhedralSurface) geometry;
           List<PolygonPatchType> gmlPatches = new ArrayList<PolygonPatchType>();
           List<? extends Polygon> patches = polySurface.getPatches();
           for (Polygon polygon : patches) {

               SurfaceInterpolationType interpolation = SurfaceInterpolationType.fromValue(polygon.getInterpolation().identifier());
               SurfaceBoundary boundary               = polygon.getBoundary();
               Ring exterior                          = boundary.getExterior();
               
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

}
