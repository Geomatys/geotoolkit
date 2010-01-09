/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal;

import org.junit.*;
import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.identification.CharacterSet;

import static org.junit.Assert.*;


/**
 * Tests the {@link CodeLists} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public final class CodeListsTest {
    /**
     * Tests {@link CodeLists#valueOf}.
     */
    @Test
    public void testValueOf() {
        assertSame(CharacterSet.UTF_8, CodeLists.valueOf(CharacterSet.class, "UTF_8"));
        assertSame(CharacterSet.UTF_8, CodeLists.valueOf(CharacterSet.class, "utf8"));
    }

    /**
     * Tests {@link CodeLists#identifiers}.
     */
    @Test
    @SuppressWarnings({"unchecked","rawtypes"})
    public void testIdentifiers() {
        assertArrayEquals(new String[] {
            "image",
            "thematicClassification",
            "physicalMeasurement"
        }, CodeLists.identifiers((Class) CoverageContentType.class));
    }
}
