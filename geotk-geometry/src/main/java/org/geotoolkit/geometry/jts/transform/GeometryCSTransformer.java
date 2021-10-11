/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.geometry.jts.transform;

import org.locationtech.jts.geom.*;
import org.geotoolkit.geometry.jts.JTS;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * Service object that takes a geometry use the cs tranformer on it.
 *
 * @author Andrea Aime
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GeometryCSTransformer implements GeometryTransformer{

    private final CoordinateSequenceTransformer csTransformer;
    private CoordinateReferenceSystem crs;

    public GeometryCSTransformer(final CoordinateSequenceTransformer transformer) {
        csTransformer = transformer;
    }

    public CoordinateSequenceTransformer getCSTransformer() {
        return csTransformer;
    }

    /**
     * Sets the target coordinate reference system.
     * <p>
     * This value is used to set the coordinate reference system of geometries
     * after they have been transformed.
     * </p>
     * @param crs The target coordinate reference system.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    /**
     * Applies the transform to the provided geometry, given
     * @param g
     * @throws TransformException
     */
    @Override
    public Geometry transform(final Geometry g) throws TransformException {
        final GeometryFactory factory = g.getFactory();
        final Geometry transformed;

        if (g instanceof Point) {
            transformed = transformPoint((Point) g, factory);
        } else if (g instanceof MultiPoint) {
            MultiPoint mp = (MultiPoint) g;
            Point[] points = new Point[mp.getNumGeometries()];

            for (int i = 0; i < points.length; i++) {
                points[i] = transformPoint((Point) mp.getGeometryN(i), factory);
            }

            transformed = factory.createMultiPoint(points);
        } else if (g instanceof LineString) {
            transformed = transformLineString((LineString) g, factory);
        } else if (g instanceof MultiLineString) {
            MultiLineString mls = (MultiLineString) g;
            LineString[] lines = new LineString[mls.getNumGeometries()];

            for (int i = 0; i < lines.length; i++) {
                lines[i] = transformLineString((LineString) mls.getGeometryN(i), factory);
            }

            transformed = factory.createMultiLineString(lines);
        } else if (g instanceof Polygon) {
            transformed = transformPolygon((Polygon) g, factory);
        } else if (g instanceof MultiPolygon) {
            MultiPolygon mp = (MultiPolygon) g;
            Polygon[] polygons = new Polygon[mp.getNumGeometries()];

            for (int i = 0; i < polygons.length; i++) {
                polygons[i] = transformPolygon((Polygon) mp.getGeometryN(i), factory);
            }

            transformed = factory.createMultiPolygon(polygons);
        } else if (g instanceof GeometryCollection) {
            GeometryCollection gc = (GeometryCollection) g;
            Geometry[] geoms = new Geometry[gc.getNumGeometries()];

            for (int i = 0; i < geoms.length; i++) {
                geoms[i] = transform(gc.getGeometryN(i));
            }

            transformed = factory.createGeometryCollection(geoms);
        } else {
            throw new IllegalArgumentException("Unsupported geometry type " + g.getClass());
        }

        //copy over user data, do a special check for coordinate reference systeme
        transformed.setUserData(g.getUserData());

        //set the new one to be the target crs
        if (crs != null) {
            JTS.setCRS(transformed, crs);
        }

        return transformed;
    }

    /**
     *
     * @throws TransformException
     */
    public LineString transformLineString(final LineString ls, final GeometryFactory gf)
        throws TransformException {
        CoordinateSequence cs = projectCoordinateSequence(ls.getCoordinateSequence());
        final LineString transformed;

        if (ls instanceof LinearRing) {
            cs = ensureClosed(cs);
            transformed = gf.createLinearRing(cs);
        } else {
            transformed = gf.createLineString(cs);
        }

        transformed.setUserData( ls.getUserData() );
        return transformed;
    }

    /**
     * @param point
     *
     * @throws TransformException
     */
    public Point transformPoint(final Point point, final GeometryFactory gf)
        throws TransformException {
        final CoordinateSequence cs = projectCoordinateSequence(point.getCoordinateSequence());
        final Point transformed = gf.createPoint(cs);
        transformed.setUserData( point.getUserData() );
        return transformed;
    }

    /**
     * @param cs a CoordinateSequence
     *
     * @throws TransformException
     */
    public CoordinateSequence projectCoordinateSequence(final CoordinateSequence cs)
        throws TransformException {
        return csTransformer.transform(cs,1);
    }

    /**
     * @param polygon
     * @throws TransformException
     */
    public Polygon transformPolygon(final Polygon polygon, final GeometryFactory gf)
        throws TransformException {
        final LinearRing exterior = (LinearRing) transformLineString(polygon.getExteriorRing(), gf);
        final LinearRing[] interiors = new LinearRing[polygon.getNumInteriorRing()];

        for (int i = 0; i < interiors.length; i++) {
            interiors[i] = (LinearRing) transformLineString(polygon.getInteriorRingN(i), gf);
        }

        final Polygon transformed = gf.createPolygon(exterior, interiors);
        transformed.setUserData( polygon.getUserData() );
        return transformed;
    }

    @Override
    public CoordinateSequence transform(final CoordinateSequence sequence, final int minpoints) throws TransformException {
        return csTransformer.transform(sequence,minpoints);
    }

    public static CoordinateSequence ensureClosed(final CoordinateSequence sequence){
        final Coordinate first = sequence.getCoordinate(0);
        final int lastIndex = sequence.size()-1;
        if(!first.equals2D(sequence.getCoordinate(lastIndex))){
            sequence.setOrdinate(lastIndex, 0, first.x);
            sequence.setOrdinate(lastIndex, 1, first.y);
            if(sequence.getDimension()>2){
                sequence.setOrdinate(lastIndex, 2, first.z);
            }
        }
        return sequence;
    }

}
