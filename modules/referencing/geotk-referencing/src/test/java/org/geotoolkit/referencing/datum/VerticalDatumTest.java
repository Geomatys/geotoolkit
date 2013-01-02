/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.lang.reflect.Field;
import org.apache.sis.util.Arrays;
import org.opengis.referencing.datum.VerticalDatumType;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link DefaultVerticalDatum} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class VerticalDatumTest extends ReferencingTestBase {
    /**
     * Tests the list of vertical datum types.
     */
    @Test
    public void testVerticalDatumTypes() {
        final VerticalDatumType[] types = VerticalDatumTypes.values();
        assertEquals("Check for first code list element.", VerticalDatumType.OTHER_SURFACE, types[0]);
        assertTrue(Arrays.contains(types, VerticalDatumTypes.ELLIPSOIDAL));
    }

    /**
     * Tests the {@link DefaultVerticalDatum#afterUnmarshal} method.
     *
     * @throws NoSuchFieldException   Should never happen.
     * @throws IllegalAccessException Should never happen.
     */
    @Test
    public void testAfterUnmarshal() throws NoSuchFieldException, IllegalAccessException {
        final Field typeField = DefaultVerticalDatum.class.getDeclaredField("type");
        typeField.setAccessible(true);
        assertEquals(VerticalDatumType .GEOIDAL,     typeForName(typeField, "Geoidal height"));
        assertEquals(VerticalDatumType .DEPTH,       typeForName(typeField, "Some depth measurement"));
        assertEquals(VerticalDatumTypes.ELLIPSOIDAL, typeForName(typeField, "Ellipsoidal height"));
        assertEquals(VerticalDatumTypes.ELLIPSOIDAL, typeForName(typeField, "NotADepth"));
    }

    /**
     * Returns the vertical datum type inferred by {@link DefaultVerticalDatum} for the given name.
     */
    private static VerticalDatumType typeForName(final Field typeField, final String name) throws IllegalAccessException {
        final DefaultVerticalDatum datum = new DefaultVerticalDatum(name, VerticalDatumType.OTHER_SURFACE);
        typeField.set(datum, null);
        return datum.getVerticalDatumType();
    }
}
