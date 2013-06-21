/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.datum;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Locale;
import javax.measure.unit.SI;

import org.opengis.test.Validators;
import org.opengis.util.GenericName;
import org.opengis.referencing.datum.VerticalDatumType;

import org.junit.*;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.geotoolkit.referencing.IdentifiedObjectTest;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;

import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.datum.DefaultPrimeMeridian.*;
import static org.geotoolkit.referencing.datum.DefaultGeodeticDatum.*;
import static org.geotoolkit.referencing.datum.DefaultVerticalDatum.*;
import static org.geotoolkit.referencing.datum.DefaultTemporalDatum.*;


/**
 * Tests {@link AbstractDatum} and well-know text formatting.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.15
 *
 * @since 2.2
 */
@DependsOn({IdentifiedObjectTest.class, TemporalDatumTest.class})
public final strictfp class DatumTest extends ReferencingTestBase {
    /**
     * Validates constant definitions.
     */
    @Test
    public void validate() {
        Validators.validate(GREENWICH);
        Validators.validate(WGS84);
        Validators.validate(ELLIPSOIDAL);
        Validators.validate(GEOIDAL);
        Validators.validate(JULIAN);
        Validators.validate(MODIFIED_JULIAN);
        Validators.validate(TRUNCATED_JULIAN);
        Validators.validate(DUBLIN_JULIAN);
        Validators.validate(UNIX);
    }

    /**
     * Tests constants definitions.
     */
    @Test
    public void testConstants() {
        assertEquals("Ellipsoid",     DefaultEllipsoid.WGS84, WGS84.getEllipsoid());
        assertEquals("PrimeMeridian", GREENWICH, WGS84.getPrimeMeridian());
        assertFalse ("VerticalDatum", GEOIDAL.equals(ELLIPSOIDAL));
        assertEquals("Geoidal",       VerticalDatumType. GEOIDAL,     GEOIDAL    .getVerticalDatumType());
        assertEquals("Ellipsoidal",   VerticalDatumTypes.ELLIPSOIDAL, ELLIPSOIDAL.getVerticalDatumType());
    }

    /**
     * Tests alias of WGS84 constant.
     */
    @Test
    public void testAlias() {
        final Collection<GenericName> alias = WGS84.getAlias();
        assertNotNull("WGS84 alias should not be null.", alias);
        assertFalse("WGS84 alias should not be empty.", alias.isEmpty());
        final Set<String> strings = new HashSet<>();
        for (final GenericName name : alias) {
            assertNotNull("Collection should not contains null element.", name);
            assertTrue("Duplicated name in alias.", strings.add(name.toString()));
        }
        assertTrue(strings.contains("OGC:WGS84"));
        assertTrue(strings.contains("Oracle:WGS 84"));
        assertTrue(strings.contains("WGS_84"));
        assertTrue(strings.contains("WGS 1984"));
        assertTrue(strings.contains("WGS_1984"));
        assertTrue(strings.contains("ESRI:D_WGS_1984"));
        assertTrue(strings.contains("EPSG:World Geodetic System 1984"));
    }

    /**
     * Tests WKT formatting.
     */
    @Test
    public void testWKT() {
        assertWktEquals(DefaultEllipsoid.WGS84, "SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”,“7030”]]");
        assertWktEquals(GREENWICH,              "PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”,“8901”]]");
        assertWktEquals(GEOIDAL,                "VERT_DATUM[“Geoidal”, 2005]");
        assertWktEquals(ELLIPSOIDAL,            "VERT_DATUM[“Ellipsoidal”, 2002]");
        assertWktEquals(WGS84,                  "DATUM[“WGS84”, SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”,“7030”]], AUTHORITY[“EPSG”,“6326”]]");
    }

    /**
     * Tests the creation of a new datum.
     */
    @Test
    public void testCreate() {
        final Map<String,Object> properties = new HashMap<>();
        properties.put("name",          "This is a name");
        properties.put("scope",         "This is a scope");
        properties.put("scope_fr",      "Valide dans ce domaine");
        properties.put("remarks",       "There is remarks");
        properties.put("remarks_fr",    "Voici des remarques");

        DefaultGeodeticDatum datum = new DefaultGeodeticDatum(properties,
                DefaultEllipsoid.createEllipsoid("Test", 1000, 1000, SI.METRE),
                new DefaultPrimeMeridian("Test", 12));

        assertEquals("name",          "This is a name",         datum.getName   ().getCode());
        assertEquals("scope",         "This is a scope",        datum.getScope  ().toString(null));
        assertEquals("scope_fr",      "Valide dans ce domaine", datum.getScope  ().toString(Locale.FRENCH));
        assertEquals("remarks",       "There is remarks",       datum.getRemarks().toString(null));
        assertEquals("remarks_fr",    "Voici des remarques",    datum.getRemarks().toString(Locale.FRENCH));
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        assertSerializedEquals(GREENWICH);
        assertSerializedEquals(WGS84);
    }
}
