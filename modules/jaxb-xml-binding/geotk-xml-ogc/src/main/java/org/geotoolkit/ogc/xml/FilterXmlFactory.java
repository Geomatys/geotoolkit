/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.ogc.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.opengis.filter.And;
import org.opengis.filter.Or;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.Operator;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.DistanceBufferOperator;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.TEquals;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterXmlFactory {

    public static XMLFilter buildFilter(final String currentVersion, final Object filter) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.FilterType(filter);

        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.FilterType(filter);

        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.FilterType(filter);

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static XMLFilter buildFeatureIDFilter(final String currentVersion, final String featureId) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.FilterType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(featureId));

        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.FilterType(new org.geotoolkit.ogc.xml.v110.FeatureIdType(featureId));

        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.FilterType(new org.geotoolkit.ogc.xml.v100.FeatureIdType(featureId));

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static XMLFilter buildFeatureIDFilter(final String currentVersion, final List<String> featureIds) {
        if (featureIds.size() == 1) {
            return buildFeatureIDFilter(currentVersion, featureIds.get(0));
        }
        if ("2.0.0".equals(currentVersion)) {

            List<org.geotoolkit.ogc.xml.v200.ResourceIdType> filters = new ArrayList<>();
            for (String featureId : featureIds) {
                filters.add(new org.geotoolkit.ogc.xml.v200.ResourceIdType(featureId));
            }
            return new org.geotoolkit.ogc.xml.v200.FilterType(new org.geotoolkit.ogc.xml.v200.OrType(filters.toArray()));

        } else if ("1.1.0".equals(currentVersion)) {
            List<org.geotoolkit.ogc.xml.v110.FeatureIdType> filters = new ArrayList<>();
            for (String featureId : featureIds) {
                filters.add(new org.geotoolkit.ogc.xml.v110.FeatureIdType(featureId));
            }
            return new org.geotoolkit.ogc.xml.v110.FilterType(new org.geotoolkit.ogc.xml.v110.OrType(filters.toArray()));

        } else if ("1.0.0".equals(currentVersion)) {
            List<org.geotoolkit.ogc.xml.v100.FeatureIdType> filters = new ArrayList<>();
            for (String featureId : featureIds) {
                filters.add(new org.geotoolkit.ogc.xml.v100.FeatureIdType(featureId));
            }
            return new org.geotoolkit.ogc.xml.v100.FilterType(new org.geotoolkit.ogc.xml.v100.OrType(filters.toArray()));

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Literal buildLiteral(final String currentVersion, final Object value) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.LiteralType(value);

        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.LiteralType(value);

        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.LiteralType(value);

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static And buildAnd(final String currentVersion, final Object... operators) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.AndType(operators);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.AndType(operators);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.AndType(operators);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Or buildOr(final String currentVersion, final Object... operators) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.OrType(operators);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.OrType(operators);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.OrType(operators);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static BBOX buildBBOX(final String currentVersion, final String propertyName, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static DistanceBufferOperator buildDistanceSpatialFilter(final String currentVersion, final String operator, final String propertyName,
            final Object geom, final double distance, final String unit) {
        if ("2.0.0".equals(currentVersion)) {
            switch (operator) {
                case "DWithin"  : return new org.geotoolkit.ogc.xml.v200.DWithinType(propertyName, geom, distance, unit);
                case "Beyond"   : return new org.geotoolkit.ogc.xml.v200.BeyondType(propertyName, geom, distance, unit);
                default: throw new IllegalArgumentException("unexpected distance spatial operator:" + operator);
            }
        } else if ("1.1.0".equals(currentVersion)) {
            if (geom != null && !(geom instanceof  org.geotoolkit.gml.xml.v311.AbstractGeometryType)) {
                throw new IllegalArgumentException("unexpected geometry type:" + geom);
            }
            switch (operator) {
                case "DWithin"  : return new org.geotoolkit.ogc.xml.v110.DWithinType(propertyName, (org.geotoolkit.gml.xml.v311.AbstractGeometryType)geom, distance, unit);
                case "Beyond"   : return new org.geotoolkit.ogc.xml.v110.BeyondType(propertyName,  (org.geotoolkit.gml.xml.v311.AbstractGeometryType)geom, distance, unit);
                default: throw new IllegalArgumentException("unexpected distance spatial operator:" + operator);
            }
        } else if ("1.0.0".equals(currentVersion)) {
            switch (operator) {
                case "DWithin"  : return new org.geotoolkit.ogc.xml.v100.DWithinType(propertyName, geom, distance, unit);
                case "Beyond"   : return new org.geotoolkit.ogc.xml.v100.BeyondType(propertyName, geom, distance, unit);
                default: throw new IllegalArgumentException("unexpected distance spatial operator:" + operator);
            }
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static BinarySpatialOperator buildBinarySpatial(final String currentVersion, final String operator, final String propertyName, final Object geom) {
        if ("2.0.0".equals(currentVersion)) {
            switch (operator) {
                case "Contains"  : return new org.geotoolkit.ogc.xml.v200.ContainsType(propertyName, geom);
                case "Crosses"   : return new org.geotoolkit.ogc.xml.v200.CrossesType(propertyName, geom);
                case "Disjoint"  : return new org.geotoolkit.ogc.xml.v200.DisjointType(propertyName, geom);
                case "Equals"    : return new org.geotoolkit.ogc.xml.v200.EqualsType(propertyName, geom);
                case "Intersects": return new org.geotoolkit.ogc.xml.v200.IntersectsType(propertyName, geom);
                case "Overlaps"  : return new org.geotoolkit.ogc.xml.v200.OverlapsType(propertyName, geom);
                case "Touches"   : return new org.geotoolkit.ogc.xml.v200.TouchesType(propertyName, geom);
                case "Within"    : return new org.geotoolkit.ogc.xml.v200.WithinType(propertyName, geom);
                default: throw new IllegalArgumentException("unexpected binary spatial operator:" + operator);
            }
        } else if ("1.1.0".equals(currentVersion)) {
            switch (operator) {
                case "Contains"  : return new org.geotoolkit.ogc.xml.v110.ContainsType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Crosses"   : return new org.geotoolkit.ogc.xml.v110.CrossesType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Disjoint"  : return new org.geotoolkit.ogc.xml.v110.DisjointType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Equals"    : return new org.geotoolkit.ogc.xml.v110.EqualsType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Intersects": return new org.geotoolkit.ogc.xml.v110.IntersectsType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Overlaps"  : return new org.geotoolkit.ogc.xml.v110.OverlapsType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Touches"   : return new org.geotoolkit.ogc.xml.v110.TouchesType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                case "Within"    : return new org.geotoolkit.ogc.xml.v110.WithinType(new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName), geom);
                default: throw new IllegalArgumentException("unexpected binary spatial operator:" + operator);
            }
        } else if ("1.0.0".equals(currentVersion)) {
            switch (operator) {
                case "Contains"  : return new org.geotoolkit.ogc.xml.v100.ContainsType(propertyName, geom);
                case "Crosses"   : return new org.geotoolkit.ogc.xml.v100.CrossesType(propertyName, geom);
                case "Disjoint"  : return new org.geotoolkit.ogc.xml.v100.DisjointType(propertyName, geom);
                case "Equals"    : return new org.geotoolkit.ogc.xml.v100.EqualsType(propertyName, geom);
                case "Intersects": return new org.geotoolkit.ogc.xml.v100.IntersectsType(propertyName, geom);
                case "Overlaps"  : return new org.geotoolkit.ogc.xml.v100.OverlapsType(propertyName, geom);
                case "Touches"   : return new org.geotoolkit.ogc.xml.v100.TouchesType(propertyName, geom);
                case "Within"    : return new org.geotoolkit.ogc.xml.v100.WithinType(propertyName, geom);
                default: throw new IllegalArgumentException("unexpected binary spatial operator:" + operator);
            }
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static After buildTimeAfter(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeAfterType(propertyName, temporal);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeAfterType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("Time After is not implemented in 1.0.0 filter.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static During buildTimeDuring(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeDuringType(propertyName, temporal);

        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeDuringType(propertyName, temporal);

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("Time During is not implemented in 1.0.0 filter.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Before buildTimeBefore(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeBeforeType(propertyName, temporal);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeBeforeType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("Time Before is not implemented in 1.0.0 filter.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static TEquals buildTimeEquals(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeEqualsType(propertyName, temporal);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeEqualsType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("Time Equals is not implemented in 1.0.0 filter.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static BinaryTemporalOperator buildBynaryTemporal(final String currentVersion, final String operator, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            switch (operator) {
                case "After"  : return new org.geotoolkit.ogc.xml.v200.TimeAfterType(propertyName, temporal);
                case "Before"  : return new org.geotoolkit.ogc.xml.v200.TimeBeforeType(propertyName, temporal);
                case "Begins"  : return new org.geotoolkit.ogc.xml.v200.TimeBeginsType(propertyName, temporal);
                case "BegunBy"  : return new org.geotoolkit.ogc.xml.v200.TimeBegunByType(propertyName, temporal);
                case "TContains"  : return new org.geotoolkit.ogc.xml.v200.TimeContainsType(propertyName, temporal);
                case "During"  : return new org.geotoolkit.ogc.xml.v200.TimeDuringType(propertyName, temporal);
                case "EndedBy"  : return new org.geotoolkit.ogc.xml.v200.TimeEndedByType(propertyName, temporal);
                case "Ends"  : return new org.geotoolkit.ogc.xml.v200.TimeEndsType(propertyName, temporal);
                case "TEquals"  : return new org.geotoolkit.ogc.xml.v200.TimeEqualsType(propertyName, temporal);
                case "Meets"  : return new org.geotoolkit.ogc.xml.v200.TimeMeetsType(propertyName, temporal);
                case "MetBy"  : return new org.geotoolkit.ogc.xml.v200.TimeMetByType(propertyName, temporal);
                case "TOverlaps"  : return new org.geotoolkit.ogc.xml.v200.TimeOverlapsType(propertyName, temporal);
                case "OverlappedBy"  : return new org.geotoolkit.ogc.xml.v200.TimeOverlappedByType(propertyName, temporal);
                case "AnyInteracts"  : return new org.geotoolkit.ogc.xml.v200.TimeAnyInteractsType(propertyName, temporal);
                default: throw new IllegalArgumentException("unexpected temporal operator:" + operator);
            }
        } else if ("1.1.0".equals(currentVersion)) {
            switch (operator) {
                case "After"  : return new org.geotoolkit.ogc.xml.v110.TimeAfterType(propertyName, temporal);
                case "Before"  : return new org.geotoolkit.ogc.xml.v110.TimeBeforeType(propertyName, temporal);
                case "Begins"  : return new org.geotoolkit.ogc.xml.v110.TimeBeginsType(propertyName, temporal);
                case "BegunBy"  : return new org.geotoolkit.ogc.xml.v110.TimeBegunByType(propertyName, temporal);
                case "TContains"  : return new org.geotoolkit.ogc.xml.v110.TimeContainsType(propertyName, temporal);
                case "During"  : return new org.geotoolkit.ogc.xml.v110.TimeDuringType(propertyName, temporal);
                case "EndedBy"  : return new org.geotoolkit.ogc.xml.v110.TimeEndedByType(propertyName, temporal);
                case "Ends"  : return new org.geotoolkit.ogc.xml.v110.TimeEndsType(propertyName, temporal);
                case "TEquals"  : return new org.geotoolkit.ogc.xml.v110.TimeEqualsType(propertyName, temporal);
                case "Meets"  : return new org.geotoolkit.ogc.xml.v110.TimeMeetsType(propertyName, temporal);
                case "MetBy"  : return new org.geotoolkit.ogc.xml.v110.TimeMetByType(propertyName, temporal);
                case "TOverlaps"  : return new org.geotoolkit.ogc.xml.v110.TimeOverlapsType(propertyName, temporal);
                case "OverlappedBy"  : return new org.geotoolkit.ogc.xml.v110.TimeOverlappedByType(propertyName, temporal);
                case "AnyInteracts"  : return new org.geotoolkit.ogc.xml.v110.TimeAnyInteractsType(propertyName, temporal);
                default: throw new IllegalArgumentException("unexpected distance spatial operator:" + operator);
            }
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("There is no time filters in v1.0.0.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsNotEqualTo buildPropertyIsNotEquals(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsNotEqualToType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsNotEqualToType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsNotEqualToType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsEqualTo buildPropertyIsEquals(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsEqualToType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsEqualToType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsEqualToType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsLessThan buildPropertyIsLessThan(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsLessThanType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsLessThanType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsLessThanType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsLike buildPropertyIsLike(final String currentVersion, final String propertyName, final String pattern,
            final String wildChar, final String singleChar, final String escapeChar) {

        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.PropertyIsLikeType(propertyName, pattern, wildChar, singleChar, escapeChar);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.PropertyIsLikeType(propertyName, pattern, wildChar, singleChar, escapeChar);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.PropertyIsLikeType(propertyName, pattern, wildChar, singleChar, escapeChar);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsNull buildPropertyIsNull(final String currentVersion, final String propertyName) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.PropertyIsNullType(propertyName);
        } else if ("1.1.0".equals(currentVersion)) {
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsNullType(pName);
        } else if ("1.0.0".equals(currentVersion)) {
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsNullType(pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsLessThanOrEqualTo buildPropertyIsLessThanOrEqualTo(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsLessThanOrEqualToType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsLessThanOrEqualToType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsLessThanOrEqualToType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsGreaterThan buildPropertyIsGreaterThan(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsGreaterThanType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsGreaterThanType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static PropertyIsGreaterThanOrEqualTo buildPropertyIsGreaterThanOrEqualTo(final String currentVersion, final String propertyName, final Literal lit, final boolean matchCase) {
        if ("2.0.0".equals(currentVersion)) {
            if (lit != null && !(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (lit != null && !(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (lit != null && !(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static SpatialCapabilities buildSpatialCapabilities(final String currentVersion, final GeometryOperand[] geometryOperands, final SpatialOperators spatial) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.SpatialCapabilitiesType(geometryOperands, spatial);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.SpatialCapabilitiesType(spatial);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.SpatialCapabilitiesType(geometryOperands, spatial);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static SpatialOperator buildSpatialOperator(final String currentVersion, final String name, final GeometryOperand[] geometryOperands) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.SpatialOperatorType(name, geometryOperands);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.SpatialOperatorType(name, geometryOperands);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static SpatialOperators buildSpatialOperators(final String currentVersion, final SpatialOperator[] operators) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.SpatialOperatorsType(operators);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.SpatialOperatorsType(operators);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.SpatialOperatorsType(operators);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static ComparisonOperators buildComparisonOperators(final String currentVersion, final Operator[] operators) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.ComparisonOperatorsType(operators);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.ComparisonOperatorsType(operators);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.ComparisonOperatorsType(operators);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static FilterCapabilities buildFilterCapabilities(final String currentVersion, final ScalarCapabilities sc, final SpatialCapabilities spa,
            final IdCapabilities id, final TemporalCapabilities temp, final Conformance conf) {
        if ("1.1.0".equals(currentVersion)) {
            if (sc != null && !(sc instanceof org.geotoolkit.ogc.xml.v110.ScalarCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for sc.");
            }
            if (spa != null && !(spa instanceof org.geotoolkit.ogc.xml.v110.SpatialCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for spa.");
            }
            if (id != null && !(id instanceof org.geotoolkit.ogc.xml.v110.IdCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for id.");
            }
            if (temp != null && !(temp instanceof org.geotoolkit.ogc.xml.v110.TemporalCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for temp.");
            }
            return new org.geotoolkit.ogc.xml.v110.FilterCapabilities((org.geotoolkit.ogc.xml.v110.ScalarCapabilitiesType)sc,
                                                                      (org.geotoolkit.ogc.xml.v110.SpatialCapabilitiesType)spa,
                                                                      (org.geotoolkit.ogc.xml.v110.IdCapabilitiesType)id,
                                                                      (org.geotoolkit.ogc.xml.v110.TemporalCapabilitiesType)temp);
        } else if ("1.0.0".equals(currentVersion)) {
            if (sc != null && !(sc instanceof org.geotoolkit.ogc.xml.v100.ScalarCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for sc.");
            }
            if (spa != null && !(spa instanceof org.geotoolkit.ogc.xml.v100.SpatialCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for spa.");
            }
            return new org.geotoolkit.ogc.xml.v100.FilterCapabilities((org.geotoolkit.ogc.xml.v100.SpatialCapabilitiesType)spa,
                                                                      (org.geotoolkit.ogc.xml.v100.ScalarCapabilitiesType)sc);
        } else if ("2.0.0".equals(currentVersion)) {
            if (sc != null && !(sc instanceof org.geotoolkit.ogc.xml.v200.ScalarCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for sc.");
            }
            if (spa != null && !(spa instanceof org.geotoolkit.ogc.xml.v200.SpatialCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for spa.");
            }
            if (id != null && !(id instanceof org.geotoolkit.ogc.xml.v200.IdCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for id.");
            }
            if (temp != null && !(temp instanceof org.geotoolkit.ogc.xml.v200.TemporalCapabilitiesType)) {
                throw new IllegalArgumentException("unexpected element version for temp.");
            }
            if (conf != null && !(conf instanceof org.geotoolkit.ogc.xml.v200.ConformanceType)) {
                throw new IllegalArgumentException("unexpected element version for conf.");
            }
            return new org.geotoolkit.ogc.xml.v200.FilterCapabilities((org.geotoolkit.ogc.xml.v200.ScalarCapabilitiesType)sc,
                                                                      (org.geotoolkit.ogc.xml.v200.SpatialCapabilitiesType)spa,
                                                                      (org.geotoolkit.ogc.xml.v200.TemporalCapabilitiesType)temp,
                                                                      (org.geotoolkit.ogc.xml.v200.IdCapabilitiesType)id,
                                                                      (org.geotoolkit.ogc.xml.v200.ConformanceType)conf);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Conformance buildConformance(final String currentVersion, final List<AbstractDomain> constraints) {
        if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.DomainType> const200 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain d : constraints) {
                     if (d instanceof org.geotoolkit.ows.xml.v110.DomainType) {
                         const200.add((org.geotoolkit.ows.xml.v110.DomainType)d);
                     } else {
                         throw new IllegalArgumentException("unexpected version for domain");
                     }
                }
            }
            return new org.geotoolkit.ogc.xml.v200.ConformanceType(const200);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static SortBy buildSortBy(String currentVersion, List<org.opengis.filter.sort.SortBy> sorts) {
        if ("2.0.0".equals(currentVersion)) {

            List<org.geotoolkit.ogc.xml.v200.SortPropertyType> filters = new ArrayList<>();
            for (org.opengis.filter.sort.SortBy sb : sorts) {
                if (sb instanceof org.geotoolkit.ogc.xml.v200.SortPropertyType) {
                    filters.add((org.geotoolkit.ogc.xml.v200.SortPropertyType)sb);
                } else {
                    throw new IllegalArgumentException("unexpected version for sortProperty.");
                }
            }
            return new org.geotoolkit.ogc.xml.v200.SortByType(filters);

        } else if ("1.1.0".equals(currentVersion)) {
            List<org.geotoolkit.ogc.xml.v110.SortPropertyType> filters = new ArrayList<>();
            for (org.opengis.filter.sort.SortBy sb : sorts) {
                if (sb instanceof org.geotoolkit.ogc.xml.v110.SortPropertyType) {
                    filters.add((org.geotoolkit.ogc.xml.v110.SortPropertyType)sb);
                } else {
                    throw new IllegalArgumentException("unexpected version for sortProperty.");
                }
            }
            return new org.geotoolkit.ogc.xml.v110.SortByType(filters);

        } else if ("1.0.0".equals(currentVersion)) {

            throw new IllegalArgumentException("No sort object for 1.0.0 filter version.");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static org.opengis.filter.sort.SortBy buildSortProperty(String currentVersion, String propName, SortOrder orderType) {
        if ("2.0.0".equals(currentVersion)) {

            return new org.geotoolkit.ogc.xml.v200.SortPropertyType(propName, orderType);
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.SortPropertyType(propName, orderType);
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("No sort object for 1.0.0 filter version.");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
