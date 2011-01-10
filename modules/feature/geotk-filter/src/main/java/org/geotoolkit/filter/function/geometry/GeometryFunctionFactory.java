/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;


/**
 * Factory registering the various functions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class GeometryFunctionFactory implements FunctionFactory {

    public static final String AREA     = "area";
    public static final String BETWEEN  = "between";
    public static final String BOUNDARY = "boundary";
    public static final String BOUNDARY_DIMENSION = "boundaryDimension";
    public static final String BUFFER   = "buffer";
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

    private static final String[] NAMES;

    static {
        NAMES = new String[] {
                    AREA, BETWEEN, BOUNDARY, BOUNDARY_DIMENSION, BUFFER, BUFFER_WITH_SEGMENTS,
                    CENTROID, CONTAINS, CONVEX_HULL, CROSSES, DIFFERENCE, DIMENSION, DISJOINT,
                    DISTANCE, ENDPOINT, ENVELOPE, EXTERIOR_RING, GEOMETRY_TYPE, GEOM_FROM_WKT,
                    GEOM_LENGTH, GET_GEOMETRY_N, GET_X, GET_Y, GET_Z, INTERIOR_POINT, INTERIOR_RING_N,
                    INTERSECTION, INTERSECTS, IS_CLOSED, IS_EMPTY, IS_RING, IS_SIMPLE, IS_VALID,
                    IS_WITHIN_DISTANCE, NUM_GEOMETRIES, NUM_INTERIOR_RING, OVERLAPS, POINT_N,
                    RELATE, RELATE_PATTERN, START_POINT, SYM_DIFFERENCE, TOUCHES, TO_WKT, UNION,
                    WITHIN
        };
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getNames() {
        return NAMES;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(final String name, final Literal fallback, final Expression... parameters) throws IllegalArgumentException {

        if (name.equals(AREA))     return new AreaFunction  (parameters[0]);
        if (name.equals(BETWEEN))  return new BetweenFunction(parameters[0], parameters[1], parameters[2]);
        if (name.equals(BOUNDARY)) return new BoundaryFunction(parameters[0]);
        if (name.equals(BOUNDARY_DIMENSION)) return new BoundaryDimensionFunction(parameters[0]);
        if (name.equals(BUFFER))   return new BufferFunction(parameters[0], parameters[1]);
        if (name.equals(BUFFER_WITH_SEGMENTS)) return new BufferWithSegmentsFunction(parameters[0], parameters[1], parameters[2]);
        if (name.equals(CENTROID)) return new CentroidFunction(parameters[0]);
        if (name.equals(CONTAINS)) return new ContainsFunction(parameters[0], parameters[1]);
        if (name.equals(CONVEX_HULL)) return new ConvexHullFunction(parameters[0]);
        if (name.equals(CROSSES)) return new CrossesFunction(parameters[0], parameters[1]);
        if (name.equals(DIFFERENCE)) return new DifferenceFunction(parameters[0], parameters[1]);
        if (name.equals(DIMENSION)) return new DimensionFunction(parameters[0]);
        if (name.equals(DISJOINT)) return new DisjointFunction(parameters[0], parameters[1]);
        if (name.equals(DISTANCE)) return new DistanceFunction(parameters[0], parameters[1]);
        if (name.equals(ENDPOINT)) return new EndPointFunction(parameters[0]);
        if (name.equals(ENVELOPE)) return new EnvelopeFunction(parameters[0]);
        if (name.equals(EXTERIOR_RING)) return new ExteriorRingFunction(parameters[0]);
        if (name.equals(GEOMETRY_TYPE)) return new GeometryTypeFunction(parameters[0]);
        if (name.equals(GEOM_FROM_WKT)) return new GeomFromWKTFunction(parameters[0]);
        if (name.equals(GEOM_LENGTH)) return new GeomLengthFunction(parameters[0]);
        if (name.equals(GET_GEOMETRY_N)) return new GetGeometryNFunction(parameters[0], parameters[1]);
        if (name.equals(GET_X)) return new GetXFunction(parameters[0]);
        if (name.equals(GET_Y)) return new GetYFunction(parameters[0]);
        if (name.equals(GET_Z)) return new GetZFunction(parameters[0]);
        if (name.equals(INTERIOR_POINT)) return new InteriorPointFunction(parameters[0]);
        if (name.equals(INTERIOR_RING_N)) return new InteriorRingNFunction(parameters[0], parameters[1]);
        if (name.equals(INTERSECTION)) return new IntersectionFunction(parameters[0], parameters[1]);
        if (name.equals(INTERSECTS)) return new IntersectsFunction(parameters[0], parameters[1]);
        if (name.equals(IS_CLOSED)) return new IsClosedFunction(parameters[0]);
        if (name.equals(IS_EMPTY)) return new IsEmptyFunction(parameters[0]);
        if (name.equals(IS_RING)) return new IsRingFunction(parameters[0]);
        if (name.equals(IS_SIMPLE)) return new IsSimpleFunction(parameters[0]);
        if (name.equals(IS_VALID)) return new IsValidFunction(parameters[0]);
        if (name.equals(IS_WITHIN_DISTANCE)) return new IsWithinDistanceFunction(parameters[0], parameters[1], parameters[2]);
        if (name.equals(NUM_GEOMETRIES)) return new NumGeometriesFunction(parameters[0]);
        if (name.equals(NUM_INTERIOR_RING)) return new NumInteriorRingFunction(parameters[0]);
        if (name.equals(OVERLAPS)) return new OverlapsFunction(parameters[0], parameters[1]);
        if (name.equals(POINT_N)) return new PointNFunction(parameters[0], parameters[1]);
        if (name.equals(RELATE)) return new RelateFunction(parameters[0], parameters[1]);
        if (name.equals(RELATE_PATTERN)) return new RelatePatternFunction(parameters[0], parameters[1], parameters[2]);
        if (name.equals(START_POINT)) return new StartPointFunction(parameters[0]);
        if (name.equals(SYM_DIFFERENCE)) return new SymDifferenceFunction(parameters[0], parameters[1]);
        if (name.equals(TOUCHES)) return new TouchesFunction(parameters[0], parameters[1]);
        if (name.equals(TO_WKT)) return new ToWKTFunction(parameters[0]);
        if (name.equals(UNION)) return new UnionFunction(parameters[0], parameters[1]);
        if (name.equals(WITHIN)) return new WithinFunction(parameters[0], parameters[1]);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
