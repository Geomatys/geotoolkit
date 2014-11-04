/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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
package org.geotoolkit.filter.function.geometry;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;


/**
 * Factory registering the various functions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class GeometryFunctionFactory extends AbstractFunctionFactory {

    public static final String AREA     = "area";
    public static final String BETWEEN  = "between";
    public static final String BOUNDARY = "boundary";
    public static final String BOUNDARY_DIMENSION = "boundaryDimension";
    public static final String BUFFER   = "buffer";
    public static final String BUFFERGEO   = "bufferGeo";
    public static final String BUFFER_WITH_SEGMENTS = "bufferWithSegments";
    public static final String CENTROID = "centroid";
    public static final String CONTAINS = "contains";
    public static final String CONVEX_HULL = "convexHull";
    public static final String CROSSES = "crosses";
    public static final String DIFFERENCE = "difference";
    public static final String DIMENSION = "dimension";
    public static final String DISJOINT = "disjoint";
    public static final String DISTANCE = "distance";
    public static final String ENDPOINT = "endPoint";
    public static final String ENVELOPE = "envelope";
    public static final String EXTERIOR_RING = "exteriorRing";
    public static final String GEOM_FROM_WKT = "geomFromWKT";
    public static final String GEOM_LENGTH = "geomLength";
    public static final String GEOMETRY_TYPE = "geometryType";
    public static final String GET_GEOMETRY_N = "getGeometryN";
    public static final String GET_X = "getX";
    public static final String GET_Y = "getY";
    public static final String GET_Z = "getZ";
    public static final String INTERIOR_POINT = "interiorPoint";
    public static final String INTERIOR_RING_N = "interiorRingN";
    public static final String INTERSECTION = "intersection";
    public static final String INTERSECTS = "intersects";
    public static final String IS_CLOSED = "isClosed";
    public static final String IS_EMPTY = "isEmpty";
    public static final String IS_RING = "isRing";
    public static final String IS_SIMPLE = "isSimple";
    public static final String IS_VALID = "isValid";
    public static final String IS_WITHIN_DISTANCE = "isWithinDistance";
    public static final String NUM_GEOMETRIES = "numGeometries";
    public static final String NUM_INTERIOR_RING = "numInteriorRing";
    public static final String NUM_POINTS = "numPoints";
    public static final String OVERLAPS = "overlaps";
    public static final String POINT_N = "pointN";
    public static final String RELATE   = "relate";
    public static final String RELATE_PATTERN = "relatePattern";
    public static final String START_POINT = "startPoint";
    public static final String SYM_DIFFERENCE = "symDifference";
    public static final String TO_WKT = "toWKT";
    public static final String TOUCHES = "touches";
    public static final String UNION = "union";
    public static final String WITHIN = "within";

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put(AREA,                 AreaFunction.class);
        FUNCTIONS.put(BETWEEN,              BetweenFunction.class);
        FUNCTIONS.put(BOUNDARY,             BoundaryFunction.class);
        FUNCTIONS.put(BOUNDARY_DIMENSION,   BoundaryDimensionFunction.class);
        FUNCTIONS.put(BUFFER,               BufferFunction.class);
        FUNCTIONS.put(BUFFERGEO,            BufferGeoFunction.class);
        FUNCTIONS.put(BUFFER_WITH_SEGMENTS, BufferWithSegmentsFunction.class);
        FUNCTIONS.put(CENTROID,             CentroidFunction.class);
        FUNCTIONS.put(CONTAINS,             ContainsFunction.class);
        FUNCTIONS.put(CONVEX_HULL,          ConvexHullFunction.class);
        FUNCTIONS.put(CROSSES,              CrossesFunction.class);
        FUNCTIONS.put(DIFFERENCE,           DifferenceFunction.class);
        FUNCTIONS.put(DIMENSION,            DimensionFunction.class);
        FUNCTIONS.put(DISJOINT,             DisjointFunction.class);
        FUNCTIONS.put(DISTANCE,             DistanceFunction.class);
        FUNCTIONS.put(ENDPOINT,             EndPointFunction.class);
        FUNCTIONS.put(ENVELOPE,             EnvelopeFunction.class);
        FUNCTIONS.put(EXTERIOR_RING,        ExteriorRingFunction.class);
        FUNCTIONS.put(GEOMETRY_TYPE,        GeometryTypeFunction.class);
        FUNCTIONS.put(GEOM_FROM_WKT,        GeomFromWKTFunction.class);
        FUNCTIONS.put(GEOM_LENGTH,          GeomLengthFunction.class);
        FUNCTIONS.put(GET_GEOMETRY_N,       GetGeometryNFunction.class);
        FUNCTIONS.put(GET_X,                GetXFunction.class);
        FUNCTIONS.put(GET_Y,                GetYFunction.class);
        FUNCTIONS.put(GET_Z,                GetZFunction.class);
        FUNCTIONS.put(INTERIOR_POINT,       InteriorPointFunction.class);
        FUNCTIONS.put(INTERIOR_RING_N,      InteriorRingNFunction.class);
        FUNCTIONS.put(INTERSECTION,         IntersectionFunction.class);
        FUNCTIONS.put(INTERSECTS,           IntersectsFunction.class);
        FUNCTIONS.put(IS_CLOSED,            IsClosedFunction.class);
        FUNCTIONS.put(IS_EMPTY,             IsEmptyFunction.class);
        FUNCTIONS.put(IS_RING,              IsRingFunction.class);
        FUNCTIONS.put(IS_SIMPLE,            IsSimpleFunction.class);
        FUNCTIONS.put(IS_VALID,             IsValidFunction.class);
        FUNCTIONS.put(IS_WITHIN_DISTANCE,   IsWithinDistanceFunction.class);
        FUNCTIONS.put(NUM_GEOMETRIES,       NumGeometriesFunction.class);
        FUNCTIONS.put(NUM_INTERIOR_RING,    NumInteriorRingFunction.class);
        FUNCTIONS.put(OVERLAPS,             OverlapsFunction.class);
        FUNCTIONS.put(POINT_N,              PointNFunction.class);
        FUNCTIONS.put(RELATE,               RelateFunction.class);
        FUNCTIONS.put(RELATE_PATTERN,       RelatePatternFunction.class);
        FUNCTIONS.put(START_POINT,          StartPointFunction.class);
        FUNCTIONS.put(SYM_DIFFERENCE,       SymDifferenceFunction.class);
        FUNCTIONS.put(TOUCHES,              TouchesFunction.class);
        FUNCTIONS.put(TO_WKT,               ToWKTFunction.class);
        FUNCTIONS.put(UNION,                UnionFunction.class);
        FUNCTIONS.put(WITHIN,               WithinFunction.class);
    }

    public GeometryFunctionFactory() {
        super("geometry", FUNCTIONS);
    }

}
