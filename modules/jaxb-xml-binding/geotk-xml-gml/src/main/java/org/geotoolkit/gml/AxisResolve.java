/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
