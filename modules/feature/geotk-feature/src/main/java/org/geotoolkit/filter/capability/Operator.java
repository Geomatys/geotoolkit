/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.capability;


import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Immutable operator.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class Operator {
    public static final Operator
            LESS_THAN               = new Operator("LessThan"),
            GREATER_THAN            = new Operator("GreaterThan"),
            LESS_THAN_EQUAL_TO      = new Operator("LessThanEqualTo"),
            GREATER_THAN_EQUAL_TO   = new Operator("GreaterThanEqualTo"),
            EQUAL_TO                = new Operator("EqualTo"),
            NOT_EQUAL_TO            = new Operator("NotEqualTo"),
            LIKE                    = new Operator("Like"),
            BETWEEN                 = new Operator("Between"),
            NULL_CHECK              = new Operator("NullCheck");

    private final String name;

    public Operator(final String name) {
        ensureNonNull("operator name", name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Operator other = (Operator) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return (5*31) + name.hashCode();
    }
}
