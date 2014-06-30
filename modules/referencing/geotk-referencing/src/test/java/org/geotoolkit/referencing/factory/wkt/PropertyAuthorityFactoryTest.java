/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.wkt;

import java.net.URL;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.awt.RenderingHints;
import javax.measure.unit.NonSI;
import javax.measure.converter.ConversionException;

import org.opengis.util.FactoryException;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.io.wkt.WKTFormatTest;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.apache.sis.referencing.IdentifiedObjects;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory.FILENAME;


/**
 * Tests {@link PropertyAuthorityFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 */
@DependsOn(WKTFormatTest.class)
public final strictfp class PropertyAuthorityFactoryTest {
    /**
     * The filename of the property file having no {@code AXIS} declaration.
     */
    private static final String FILENAME_XY = "epsg-xy.properties";

    /**
     * Tests the value of {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER} when the
     * factory is created with the default set of hints.
     *
     * @throws IOException Should never happen.
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testDefaultHints() throws IOException, FactoryException {
        final URL resources = PropertyEpsgFactory.class.getResource(FILENAME_XY);
        assertNotNull(FILENAME_XY, resources);
        PropertyAuthorityFactory factory = new PropertyAuthorityFactory(null, resources, Citations.EPSG);
        /*
         * Tests the factory when we didn't asked for any hint.
         */
        Map<RenderingHints.Key, ?> hints = factory.getImplementationHints();
        assertNull(hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertNull(hints.get(Hints.FORCE_STANDARD_AXIS_DIRECTIONS));
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_STANDARD_AXIS_UNITS));
        assertEquals(1, factory.getAuthorityCodes(null).size());
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("42101");
        CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(AxisDirection.EAST,  cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.NORTH, cs.getAxis(1).getDirection());
        /*
         * Tests again when we asked for FORCE_LONGITUDE_FIRST_AXIS_ORDER while the factory doesn't
         * really care. Note that we should obtain the same CRS instance (not just a CRS equlals to
         * it) because of caching done in ReferencingObjectFactory.
         */
        Hints userHints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        factory = new PropertyAuthorityFactory(userHints, resources, Citations.EPSG);
        hints = factory.getImplementationHints();
        assertNull(hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertNull(hints.get(Hints.FORCE_STANDARD_AXIS_DIRECTIONS));
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_STANDARD_AXIS_UNITS));
        assertEquals(1, factory.getAuthorityCodes(null).size());
        assertSame(crs, factory.createCoordinateReferenceSystem("42101"));
        /*
         * Tests a CRS sample.
         */
        crs = factory.createCoordinateReferenceSystem("42101");
        assertEquals("WGS 84 / LCC Canada", crs.getName().getCode());
        assertEquals("EPSG:42101", IdentifiedObjects.getIdentifierOrName(crs));
//        assertMultilinesEquals(WKT.PROJCS_LAMBERT_CONIC, crs.toWKT());
        factory.dispose(false);
    }

    /**
     * Tests the value of {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER} when the factory
     * is created with the default set of hints and the CRS contains axis declarations.
     *
     * @throws IOException Should never happen.
     * @throws FactoryException Should never happen.
     * @throws ConversionException Should never happen.
     */
    @Test
    public void testDefaultHintsWithAxis() throws IOException, FactoryException, ConversionException {
        final URL r1 = PropertyEpsgFactory.class.getResource(FILENAME_XY);
        final URL r2 = PropertyEpsgFactory.class.getResource(FILENAME);
        assertNotNull(FILENAME_XY, r1);
        assertNotNull(FILENAME,    r2);
        final List<URL> resources = Arrays.asList(new URL[] {r1, r2});
        PropertyAuthorityFactory factory = new PropertyAuthorityFactory(null, resources, Citations.EPSG);
        /*
         * Tests the factory when we didn't asked for any hint.
         */
        Map<RenderingHints.Key, ?> hints = factory.getImplementationHints();
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_STANDARD_AXIS_DIRECTIONS));
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_STANDARD_AXIS_UNITS));
        assertEquals(3, factory.getAuthorityCodes(null).size());
        CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("3035");
        assertEquals("ETRS89 / ETRS-LAEA", crs.getName().getCode());
        assertEquals("EPSG:3035", IdentifiedObjects.getIdentifierOrName(crs));
        CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(AxisDirection.NORTH, cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.EAST,  cs.getAxis(1).getDirection());
        /*
         * ... Tests again with an other CRS ...
         */
        crs = factory.createCoordinateReferenceSystem("27572");
        assertEquals("NTF (Paris) / Lambert zone II", crs.getName().getCode());
        assertEquals("EPSG:27572", IdentifiedObjects.getIdentifierOrName(crs));
        cs = ((ProjectedCRS) crs).getBaseCRS().getCoordinateSystem();
        assertEquals(AxisDirection.NORTH, cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.EAST,  cs.getAxis(1).getDirection());
        assertEquals("Expected grade units", 1,
                cs.getAxis(0).getUnit().getConverterToAny(NonSI.GRADE).convert(1), 1E-8);
        /*
         * Tests again when we asked for FORCE_LONGITUDE_FIRST_AXIS_ORDER. Now (at the opposite
         * of previous testDefaultHint()) the factory should care about the hints because the
         * WKT contains AXIS declarations.
         */
        Hints userHints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        factory = new PropertyAuthorityFactory(userHints, resources, Citations.EPSG);
        hints = factory.getImplementationHints();
        assertEquals(Boolean.TRUE,  hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertEquals(Boolean.TRUE,  hints.get(Hints.FORCE_STANDARD_AXIS_DIRECTIONS));
        assertEquals(Boolean.FALSE, hints.get(Hints.FORCE_STANDARD_AXIS_UNITS));
        assertEquals(3, factory.getAuthorityCodes(null).size());
        crs = factory.createCoordinateReferenceSystem("3035");
        assertEquals("ETRS89 / ETRS-LAEA", crs.getName().getCode());
        assertEquals("EPSG:3035", IdentifiedObjects.getIdentifierOrName(crs));
        cs = crs.getCoordinateSystem();
        assertEquals(AxisDirection.EAST,  cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.NORTH, cs.getAxis(1).getDirection());
        /*
         * ... then tests the inner GeographicCRS axis...
         */
        crs = factory.createCoordinateReferenceSystem("27572");
        assertEquals("NTF (Paris) / Lambert zone II", crs.getName().getCode());
        assertEquals("EPSG:27572", IdentifiedObjects.getIdentifierOrName(crs));
        cs = ((ProjectedCRS) crs).getBaseCRS().getCoordinateSystem();
        assertEquals(AxisDirection.EAST,  cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.NORTH, cs.getAxis(1).getDirection());
        assertEquals("Expected grade units because units are declared outside AXIS elements.", 1,
                cs.getAxis(0).getUnit().getConverterToAny(NonSI.GRADE).convert(1), 1E-8);
        /*
         * Tests a CRS sample.
         */
        crs = factory.createCoordinateReferenceSystem("42101");
//        assertMultilinesEquals(WKT.PROJCS_LAMBERT_CONIC, crs.toWKT());
        factory.dispose(false);
    }
}
