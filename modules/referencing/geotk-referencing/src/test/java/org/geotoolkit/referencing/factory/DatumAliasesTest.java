/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Collection;
import java.util.Collections;

import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;

import org.geotools.referencing.datum.GeotoolsFactory; // A dummy factory for testing purpose.

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the creation of {@link CoordinateReferenceSystem} objects and dependencies through
 * factories (not authority factories).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.1
 */
@DependsOn(FactoryFinderTest.class)
public final strictfp class DatumAliasesTest extends TestBase {
    /**
     * Tests the registration. {@link DatumAliases} should be before
     * {@link ReferencingObjectFactory}. The dummy GeoTools factory
     * should be last.
     */
    @Test
    public void testRegistration() {
        int aliases  = -1;
        int objects  = -1;
        int geotools = -1;
        int position = 0;
        for (final DatumFactory factory : FactoryFinder.getDatumFactories(null)) {
            if (factory instanceof DatumAliases) {
                assertEquals(-1, aliases);
                aliases = position;
            }
            if (factory instanceof ReferencingObjectFactory) {
                assertEquals(-1, objects);
                objects = position;
            }
            if (factory instanceof GeotoolsFactory) {
                assertEquals(-1, geotools);
                geotools = position;
            }
            position++;
        }
        assertTrue("DatumAliases factory not found.",      aliases  >= 0);
        assertTrue("ReferencingObjectFactory not found.",  objects  >= 0);
        assertTrue("Pseudo-GeotoolsFactory not found.",    geotools >= 0);
        assertTrue("DatumAliases should have precedence.", aliases < objects);
        assertTrue("GeotoolsFactory should be last.",      objects < geotools);
    }

    /**
     * Tests datum aliases. Note: ellipsoid and prime meridian are dummy values just
     * (not conform to the usage in real world) just for testing purpose.
     *
     * @throws FactoryException should never happen.
     */
    @Test
    public void testDatumAliases() throws FactoryException {
        final String           name0 = "Nouvelle Triangulation Francaise (Paris)";
        final String           name1 = "Nouvelle_Triangulation_Francaise_Paris";
        final String           name2 = "NTF (Paris meridian)";
        final Ellipsoid    ellipsoid = DefaultEllipsoid.WGS84;
        final PrimeMeridian meridian = DefaultPrimeMeridian.GREENWICH;
        DatumFactory         factory = new ReferencingObjectFactory();
        final Map<String,?> properties = Collections.singletonMap("name", name1);
        GeodeticDatum datum = factory.createGeodeticDatum(properties, ellipsoid, meridian);
        assertTrue(datum.getAlias().isEmpty());

        for (int i=0; i<=2; i++) {
            switch (i) {
                case  0: factory = new DatumAliases(factory);           break;
                case  1: factory = FactoryFinder.getDatumFactory(null); break;
                case  2: ((DatumAliases) factory).freeUnused();         break;
                default: throw new AssertionError(); // Should not occurs.
            }
            final String pass = "Pass #"+i;
            assertTrue(pass, factory instanceof DatumAliases);
            datum = factory.createGeodeticDatum(properties, ellipsoid, meridian);
            final GenericName[] aliases = datum.getAlias().toArray(new GenericName[0]);
            assertEquals(pass, 3, aliases.length);
            assertEquals(pass, name0, aliases[0].tip().toString());
            assertEquals(pass, name1, aliases[1].tip().toString());
            assertEquals(pass, name2, aliases[2].tip().toString());
        }

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Tokyo"), ellipsoid, meridian);
        Collection<GenericName> aliases = datum.getAlias();
        assertEquals(4, aliases.size());

        ((DatumAliases) factory).freeUnused();
        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "_toKyo  _"), ellipsoid, meridian);
        assertEquals(4, datum.getAlias().size());
        assertTrue(aliases.equals(datum.getAlias()));

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "D_Tokyo"), ellipsoid, meridian);
        assertEquals(4, datum.getAlias().size());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Luxembourg 1930"), ellipsoid, meridian);
        assertEquals(3, datum.getAlias().size());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Dummy"), ellipsoid, meridian);
        assertTrue("Non existing datum should have no alias.", datum.getAlias().isEmpty());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "WGS 84"), ellipsoid, meridian);
        assertTrue (IdentifiedObjects.nameMatches(datum, "WGS 84"));
        assertTrue (IdentifiedObjects.nameMatches(datum, "WGS_1984"));
        assertTrue (IdentifiedObjects.nameMatches(datum, "World Geodetic System 1984"));
        assertFalse(IdentifiedObjects.nameMatches(datum, "WGS 72"));
    }
}
