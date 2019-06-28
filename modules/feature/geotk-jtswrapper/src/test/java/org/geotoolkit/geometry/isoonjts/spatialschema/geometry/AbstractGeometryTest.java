/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.logging.Level;
import org.apache.sis.internal.system.DefaultFactories;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.Surface;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPrimitiveFactory;
import org.apache.sis.util.logging.Logging;

/**
 * Provided test case.
 *
 * @author Jody Garnett
 * @author Joel Skelton
 * @module
 */
public abstract class AbstractGeometryTest {

    protected static final JTSGeometryFactory GEOMETRY_FACTORY;

    protected static final PrimitiveFactory PRIMITIVE_FACTORY;

    protected static CoordinateReferenceSystem crs;

    protected static final String WGS84_WKT =
            "GEOGCS[\"WGS84\", DATUM[\"WGS84\", SPHEROID[\"WGS84\", 6378137.0, 298.257223563]]," +
                    "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.017453292519943295], " +
                    "AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]]";

    /**
     * setUp
     * Called before each test.
     *
     * @throws FactoryException
     */
    static {
        CRSFactory crsFact = DefaultFactories.forBuildin(CRSFactory.class);
        try {
            crs = crsFact.createFromWKT(WGS84_WKT);
        } catch (FactoryException ex) {
            Logging.getLogger("org.geotoolkit.geometry.isoonjts.spatialschema.geometry").log(Level.WARNING, null, ex);
        }
        GEOMETRY_FACTORY = new JTSGeometryFactory(crs);
        PRIMITIVE_FACTORY = new JTSPrimitiveFactory(crs);
    }

    protected DirectPosition createDirectPosition(final double x, final double y) {
        double[] coords = new double[2];
        coords[0] = x;
        coords[1] = y;
        return GEOMETRY_FACTORY.createDirectPosition(coords);
    }

    /**
     * A helper method for creating a Curve from an array of DirectPositions
     *
     * @param points
     * @return a <tt>Curve</tt>
     */
    protected Curve createCurve(final DirectPosition[] points) {
        final List curveSegmentList = Collections.singletonList(createLineString(points));
        final Curve curve = PRIMITIVE_FACTORY.createCurve(curveSegmentList);
        return curve;
    }

    /**
     * A helper method for creating a lineString from an array of DirectPositions
     *
     * @param points
     * @return <tt>LineString</tt>
     */
    protected LineString createLineString(final DirectPosition[] points) {
        final LineString lineString = GEOMETRY_FACTORY.createLineString(new ArrayList(Arrays.asList(points)));
        return lineString;
    }

    /**
     * A helper method for creating a Ring from an array of DirectPositions
     *
     * @param curve
     * @return a <tt>Ring</tt>
     */
    protected Ring createRing(final Curve curve) {
        final List curveList = Collections.singletonList(curve);
        final Ring ring = PRIMITIVE_FACTORY.createRing(curveList);
        return ring;
    }

    /**
     * creates a SurfaceBoundary using a curve as the exterior
     *
     * @param exterior
     * @return <tt>SurfaceBoundary</tt>
     */
    protected SurfaceBoundary createSurfaceBoundary(final Curve exterior) {
        final Ring exteriorRing = createRing(exterior);
        List interiorRingList = Collections.EMPTY_LIST;
        SurfaceBoundary surfaceBoundary = null;
        surfaceBoundary = PRIMITIVE_FACTORY.createSurfaceBoundary(exteriorRing, interiorRingList);
        return surfaceBoundary;
    }

    /**
     * Creates a simple polygon with no holes
     * @param points points defining the polygon (surface)
     * @return the surface created out of the points
     */
    protected Surface createSurface(final DirectPosition[] points) {
        Curve curve = createCurve(points);
        SurfaceBoundary surfaceBoundary = createSurfaceBoundary(curve);
        Surface surface = PRIMITIVE_FACTORY.createSurface(surfaceBoundary);
        return surface;
    }

}
