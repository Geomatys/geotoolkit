/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.logging.Level;
import org.apache.sis.util.ArgumentChecks;
import static org.geotoolkit.internal.sql.DefaultDataSource.LOGGER;

/**
 *
 * @author Matthieu Bastianelli (Geomatys)
 */
public enum AxisResolve {
    /**
     * Keep definition as close as possible to origin authority
     */
    STRICT,
    /**
     * Always force right handed convention on read coordinate systems
     */
    RIGHT_HANDED,
    /**
     * Applies heuristic rules to decode CRS. For example, keep strict
     * definition on proper URN source, but force longitude on ambiguous codes
     */
    AUTO;

    public static AxisResolve forName(final String name) {
        ArgumentChecks.ensureNonNull("Assessed name", name);

        switch (name.trim().toUpperCase()) {
            case "STRICT":
                return STRICT;
            case "RIGHT_HANDED":
                return RIGHT_HANDED;
            case "AUTO":
                return AUTO;
            default:
                throw new IllegalArgumentException("String input " + name + " doesn't match any AxisResolveValue");
        }
    }

    public static AxisResolve getDefault() {
        String confVal = System.getProperty(PROPERTY_KEY, System.getenv("GEOTK_GML_AXIS_RESOLVE"));
        if (confVal == null || (confVal = confVal.trim()).isEmpty()) {
            return AxisResolve.STRICT;
        } else {
            try {
                return AxisResolve.forName(confVal);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unknown axis resolve value: " + confVal);
                return AxisResolve.STRICT;
            }
        }
    }

    public static void setDefault(final AxisResolve axisResolve) {
        System.setProperty(PROPERTY_KEY, axisResolve.toString());
    }

    private static final String PROPERTY_KEY = "geotk.gml.axis.resolve";

}
