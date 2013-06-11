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

import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.TEquals;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterXmlFactory {

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
            if (!(lit instanceof org.geotoolkit.ogc.xml.v200.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            return new org.geotoolkit.ogc.xml.v200.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v200.LiteralType)lit,
                                                                         propertyName, matchCase);
        } else if ("1.1.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v110.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pName = new org.geotoolkit.ogc.xml.v110.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v110.LiteralType)lit,
                                                                         pName, matchCase);
        } else if ("1.0.0".equals(currentVersion)) {
            if (!(lit instanceof org.geotoolkit.ogc.xml.v100.LiteralType)) {
                throw new IllegalArgumentException("unexpected element version for literal.");
            }
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pName = new org.geotoolkit.ogc.xml.v100.PropertyNameType(propertyName);
            return new org.geotoolkit.ogc.xml.v100.PropertyIsGreaterThanOrEqualToType((org.geotoolkit.ogc.xml.v100.LiteralType)lit, pName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
