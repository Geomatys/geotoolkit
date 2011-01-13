/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.referencing.cs;

import org.opengis.referencing.cs.AxisDirection;
import static org.opengis.referencing.cs.AxisDirection.*;

import org.geotoolkit.lang.Static;


/**
 * Returns the direction for a pre-defined name. This class search for a match in the set of
 * known axis direction as returned by {@link AxisDirections#values()}, plus a few special
 * cases link "<cite>Geocentre &gt; equator/90°E</cite>". The later are used in the EPSG
 * database for geocentric CRS.
 * <p>
 * This class does not know about {@link DirectionAlongMeridian}. The later is a parser
 * which may create new directions, while the {@code Directions} class searches only in a
 * set of predefined directions and never create new ones.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Static
final class Directions {
    /**
     * Do not allow instantiation of this class.
     */
    private Directions() {
    }

    /**
     * Returns the direction for the given name, or {@code null} if unknown.
     * This method search in the set of pre-defined axis names.
     */
    static AxisDirection find(String name) {
        name = name.trim();
        final AxisDirection[] directions = AxisDirection.values();
        AxisDirection candidate = find(directions, name);
        if (candidate == null) {
            /*
             * No match found when using the pre-defined axis name. Searches among
             * the set of geocentric directions. Expected directions are:
             *
             *    Geocentre > equator/PM      or    Geocentre > equator/0°E
             *    Geocentre > equator/90dE    or    Geocentre > equator/90°E
             *    Geocentre > north pole
             */
            int split = name.indexOf('>');
            if (split >= 0 && name.substring(0, split).trim().equalsIgnoreCase("Geocentre")) {
                String parse = name.substring(split + 1).replace('_', ' ').trim();
                split = parse.indexOf('/');
                if (split < 0) {
                    if (parse.equalsIgnoreCase("north pole")) {
                        return GEOCENTRIC_Z;
                    }
                } else if (parse.substring(0, split).trim().equalsIgnoreCase("equator")) {
                    parse = parse.substring(split + 1).trim();
                    if (parse.equalsIgnoreCase("PM")) {
                        return GEOCENTRIC_X;
                    }
                    // Limit the scan to 9 characters for avoiding a NumberFormatException.
                    final int length = Math.min(parse.length(), 6);
                    for (split=0; split<length; split++) {
                        final char c = parse.charAt(split);
                        if (c < '0' || c > '9') {
                            if (split == 0) break;
                            final int n = Integer.parseInt(parse.substring(0, split));
                            parse = parse.substring(split).trim().replace('d', '°');
                            if (parse.equalsIgnoreCase("°E")) {
                                switch (n) {
                                    case  0: return GEOCENTRIC_X;
                                    case 90: return GEOCENTRIC_Y;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            /*
             * No match found in the set of geocentric directions neither. Searches again
             * among the standard set of names, replacing space by underscore.
             */
            String modified = name.replace('-', '_');
            if (modified != name) {
                name = modified;
                candidate = find(directions, modified);
            }
            if (candidate == null) {
                modified = name.replace(' ', '_');
                if (modified != name) {
                    candidate = find(directions, modified);
                }
            }
        }
        return candidate;
    }

    /**
     * Searches for the specified name in the specified set of directions.
     * This method accepts abbreviation as well, for example if the given
     * {@code name} is "W", then it will be recognized as "West".
     */
    static AxisDirection find(final AxisDirection[] directions, final String name) {
        final int nl = name.length();
search: for (final AxisDirection candidate : directions) {
            final String cn = candidate.name();
            if (name.equalsIgnoreCase(cn)) {
                return candidate;
            }
            /*
             * Not an exact match. Compares as an abbreviation.
             */
            int ni = 0;
            final int cl = cn.length();
            boolean letters = false;
            for (int i=0; i<cl; i++) {
                final char c = cn.charAt(i);
                if (Character.isLetterOrDigit(c) != letters) {
                    letters = !letters;
                    if (letters) {
                        // Found the first letter of an abbreviation. Check if we match.
                        if (ni >= nl || Character.toUpperCase(c) != Character.toUpperCase(name.charAt(ni))) {
                            // No match. Continue with the next direction to check.
                            continue search;
                        }
                        ni++;
                    }
                }
            }
            if (ni == nl) {
                return candidate;
            }
        }
        return null;
    }
}
