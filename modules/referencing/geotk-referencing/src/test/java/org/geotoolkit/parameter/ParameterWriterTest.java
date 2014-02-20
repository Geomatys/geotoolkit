/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.parameter;

import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.geotoolkit.referencing.operation.provider.Mercator1SP;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link ParameterWriter} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final strictfp class ParameterWriterTest extends LocaleDependantTestBase {
    /**
     * Tests {@link DefaultParameterDescriptorGroup#toString()}.
     * This will test indirectly {@link ParameterWriter}.
     */
    @Test
    public void testDescriptorToString() {
        assertMultilinesEquals("OGC: Mercator_1SP\n" +
            "╔═══════════════════════════╤═════════╤═════════╤═════════╤════════╤════════╗\n" +
            "║ Nom                       │ Type    │ Minimum │ Maximum │ Défaut │ Unités ║\n" +
            "╟───────────────────────────┼─────────┼─────────┼─────────┼────────┼────────╢\n" +
            "║ OGC:   semi_major         │ Double  │       0 │       ∞ │        │ m      ║\n" +
            "║ OGC:   semi_minor         │ Double  │       0 │       ∞ │        │ m      ║\n" +
            "║ Geotk: roll_longitude     │ Boolean │         │         │        │        ║\n" +
            "║ OGC:   latitude_of_origin │ Double  │     -90 │     +90 │      0 │ °      ║\n" +
            "║ OGC:   central_meridian   │ Double  │    -180 │    +180 │      0 │ °      ║\n" +
            "║ OGC:   scale_factor       │ Double  │       0 │       ∞ │      1 │        ║\n" +
            "║ OGC:   false_easting      │ Double  │      -∞ │      +∞ │      0 │ m      ║\n" +
            "║ OGC:   false_northing     │ Double  │      -∞ │      +∞ │      0 │ m      ║\n" +
            "╚═══════════════════════════╧═════════╧═════════╧═════════╧════════╧════════╝\n",
            Mercator1SP.PARAMETERS.toString());
    }

    /**
     * Tests {@link ParameterGroup#toString()} (which have its own implementation) and
     * {@link ParameterWriter#toString(ParameterValueGroup)}.
     */
    @Test
    public void testParameterToString() {
        final ParameterValueGroup parameters = Mercator1SP.PARAMETERS.createValue();
        parameters.parameter("semi_major").setValue(6378137.0);
        parameters.parameter("semi_minor").setValue(6356752.314245179);
        parameters.parameter("latitude_of_origin").setValue(40);
        parameters.parameter("central_meridian").setValue(2.337229166666667);
        assertMultilinesEquals("OGC: Mercator_1SP\n" +
            "╔═════════════════════════╤════════╤═════════╤═════════╤═══════════════╤════════╗\n" +
            "║ Nom                     │ Type   │ Minimum │ Maximum │ Valeur        │ Unités ║\n" +
            "╟─────────────────────────┼────────┼─────────┼─────────┼───────────────┼────────╢\n" +
            "║ OGC: semi_major         │ Double │       0 │       ∞ │     6 378 137 │ m      ║\n" +
            "║ OGC: semi_minor         │ Double │       0 │       ∞ │ 6 356 752,314 │ m      ║\n" +
            "║ OGC: latitude_of_origin │ Double │     -90 │     +90 │            40 │ °      ║\n" +
            "║ OGC: central_meridian   │ Double │    -180 │    +180 │         2,337 │ °      ║\n" +
            "║ OGC: scale_factor       │ Double │       0 │       ∞ │             1 │        ║\n" +
            "║ OGC: false_easting      │ Double │      -∞ │      +∞ │             0 │ m      ║\n" +
            "║ OGC: false_northing     │ Double │      -∞ │      +∞ │             0 │ m      ║\n" +
            "╚═════════════════════════╧════════╧═════════╧═════════╧═══════════════╧════════╝\n",
            ParameterWriter.toString(parameters));

        assertMultilinesEquals(
            "Mercator_1SP : semi_major         = 6378137.0\n" +
            "               semi_minor         = 6356752.314245179\n" +
            "               latitude_of_origin = 40.0\n" +
            "               central_meridian   = 2.337229166666667\n" +
            "               scale_factor       = 1.0\n" +
            "               false_easting      = 0.0\n" +
            "               false_northing     = 0.0\n", parameters.toString());
    }
}
