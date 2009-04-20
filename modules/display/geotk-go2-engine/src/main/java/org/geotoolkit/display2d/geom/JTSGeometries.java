/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.geom;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.display2d.array.JTSArray;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.util.converter.Classes;


/**
 * A geometry collection backed by one or many JTS
 * {@link com.vividsolutions.jts.geom.Geometry} objects.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/JTSGeometries.java $
 * @version $Id: JTSGeometries.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
public class JTSGeometries extends org.geotoolkit.display2d.geom.GeometryCollection {
    /**
     * Num�ro de version pour compatibilit� avec des
     * bathym�tries enregistr�es sous d'anciennes versions.
     */
    private static final long serialVersionUID = 1390543865440404086L;

    /**
     * Construct an initially empty collection using the
     * {@linkplain #DEFAULT_COORDINATE_SYSTEM default coordinate system}.
     * Geometries can be added using {@link #add} method.
     */
    public JTSGeometries() {
    }

    /**
     * Construct an initialy empty collection.
     * Geometries can be added using {@link #add} method.
     *
     * @param cs The coordinate system to use for all points in this geometry,
     *           or <code>null</code> if unknow.
     */
    public JTSGeometries(final CoordinateReferenceSystem cs) {
        super(cs);
    }

    /**
     * Construct a collection for the specified geometry. The {@link #getValue value} is
     * computed from the mean value of all {@link Coordinate#z} in the specified geometry.
     *
     * @param geometry The geometry to wrap, or <code>null</code> if none.
     *
     * @task TODO: The coordinate system currently default to PROMISCUOUS.
     *             We should find it from the SRID code.
     */
    public JTSGeometries(final Geometry geometry) {
        if (geometry!=null) try {
            add(geometry);
        } catch (TransformException exception) {
            // Should not happen, since this collection is suppose to be
            // set to the same coordinate system than the geometry.
            final IllegalArgumentException e;
            e = new IllegalArgumentException(exception.getLocalizedMessage());
            e.initCause(exception);
            throw e;
        }
        setValue((float)statistics(geometry).mean());
    }

    /**
     * Returns the coordinate system for the specified JTS geometry.
     *
     * @task TODO: We should construct the coordinate system from SRID using
     *             {@link org.geotools.cs.CoordinateSystemAuthorityFactory}.
     */
    private CoordinateReferenceSystem getCoordinateReferenceSystem(final Geometry geometry) {
//      final int id = geometry.getSRID();
        // TODO: construct CS here.
        return getCoordinateReferenceSystem();
    }

    /**
     * Compute statistics about the <var>z</var> values in the specified geometry.
     * Statistics include minimum, maximum, mean value and standard deviation.
     * Unknow classes are ignored.
     *
     * @param  geometry The geometry to analyse.
     * @return The statistics.
     */
    private static Statistics statistics(final Geometry geometry) {
        if (geometry instanceof Polygon) {
            final Polygon polygon = (Polygon) geometry;
            final Statistics stats = statistics(polygon.getExteriorRing());
            final int n = polygon.getNumInteriorRing();
            for (int i=0; i<n; i++) {
                stats.add(statistics(polygon.getInteriorRingN(i)));
            }
            return stats;
        }
        final Statistics stats = new Statistics();
        if (geometry instanceof GeometryCollection) {
            final GeometryCollection collection = (GeometryCollection) geometry;
            final int n = collection.getNumGeometries();
            for (int i=0; i<n; i++) {
                stats.add(statistics(collection.getGeometryN(i)));
            }
        }
        else if (geometry instanceof Point) {
            stats.add(((Point) geometry).getCoordinate().z);
        }
        else if (geometry instanceof LineString) {
            final LineString line = (LineString) geometry;
            final int n = line.getNumPoints();
            for (int i=0; i<n; i++) {
                stats.add(line.getCoordinateN(i).z);
            }
        }
        return stats;
    }

    /**
     * Returns the specified line string as a {@link Polyline} object.
     *
     * @param geometry The line string to add.
     */
    private Polyline toPolyline(final LineString geometry) {
        final Coordinate[] coords = geometry.getCoordinates();
        final Polyline polyline = new Polyline(new JTSArray(coords), getCoordinateReferenceSystem(geometry));
        if (coords[0].equals(coords[coords.length - 1])) {
            polyline.close();
        }
        return polyline;
    }

    /**
     * Add the specified point to this collection. This method should rarely be
     * used, since polylines are not designed for handling individual points.
     *
     * @param  geometry The point to add.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection's coordinate system.
     */
    private org.geotoolkit.display2d.geom.Geometry addSF(final Point geometry)
            throws TransformException
    {
        Coordinate coord = geometry.getCoordinate();
        return add(new org.geotoolkit.display2d.geom.Point(coord, getCoordinateReferenceSystem(geometry)));
    }

    /**
     * Add the specified line string to this collection.
     *
     * @param  geometry The line string to add.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection's coordinate system.
     */
    private org.geotoolkit.display2d.geom.Geometry addSF(final LineString geometry)
            throws TransformException
    {
        return add(toPolyline(geometry));
    }

    /**
     * Add the specified polygon to this collection.
     *
     * @param  geometry The polygon to add.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection's coordinate system.
     */
    private org.geotoolkit.display2d.geom.Geometry addSF(final Polygon geometry)
            throws TransformException
    {
        final org.geotoolkit.display2d.geom.Polygon polygon =
                new org.geotoolkit.display2d.geom.Polygon(toPolyline(geometry.getExteriorRing()));
        final int n = geometry.getNumInteriorRing();
        for (int i=0; i<n; i++) {
            polygon.addHole(toPolyline(geometry.getInteriorRingN(i)));
        }
        return add(polygon);
    }

    /**
     * Add the specified geometry collection to this collection.
     *
     * @param  geometry The geometry collection to add.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection's coordinate system.
     */
    private org.geotoolkit.display2d.geom.Geometry addSF(final GeometryCollection geometry)
            throws TransformException
    {
        final JTSGeometries collection = new JTSGeometries(getCoordinateReferenceSystem());
        final int n = geometry.getNumGeometries();
        for (int i=0; i<n; i++) {
            collection.add(geometry.getGeometryN(i));
        }
        return add(collection);
    }

    /**
     * Add the specified geometry to this collection. The geometry must be one of
     * the following classes: {@link Point}, {@link LineString}, {@link Polygon}
     * or {@link GeometryCollection}.
     *
     * @param  geometry The geometry to add.
     * @return The geometry as a {@link org.geotools.renderer.geom.Geometry} wrapper. The style can
     *         be set using <code>add(geometry).{@link org.geotools.renderer.geom.Geometry#setStyle
     *         setStyle}(style)</code>.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection's coordinate system.
     * @throws IllegalArgumentException if the geometry is not a a valid class.
     */
    public org.geotoolkit.display2d.geom.Geometry add(final Geometry geometry)
            throws TransformException, IllegalArgumentException
    {
        if (geometry instanceof Point) {
            return addSF((Point) geometry);
        }
        if (geometry instanceof LineString) {
            return addSF((LineString) geometry);
        }
        if (geometry instanceof Polygon) {
            return addSF((Polygon) geometry);
        }
        if (geometry instanceof GeometryCollection) {
            return addSF((GeometryCollection) geometry);
        }
        throw new IllegalArgumentException(Classes.getShortClassName(geometry));
    }

    /**
     * Freeze this collection. 
     */
    @Override
    final void freeze() {
        super.freeze();
    }
}
