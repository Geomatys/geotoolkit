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
package org.geotoolkit.metadata;

import org.apache.sis.metadata.TypeValuePolicy;
import org.apache.sis.metadata.KeyNamePolicy;
import java.util.Map;
import java.util.List;

import org.opengis.metadata.citation.Citation;
import org.opengis.coverage.grid.RectifiedGrid;

import org.geotoolkit.test.Depend;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MetadataStandard} class. Unless otherwise specified,
 * the tests use the {@link MetadataStandard#ISO_19115} constant.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 */
@Depend(PropertyAccessorTest.class)
public final strictfp class MetadataStandardTest {
    /**
     * Tests the {@link MetadataStandard#ISO_19123} constant. Getters shall
     * be accessible even if there is no implementation on the classpath.
     */
    @Test
    public void testISO_19123() {
        final MetadataStandard std = MetadataStandard.ISO_19123;
        assertFalse(std.isMetadata(Citation.class));
        assertFalse(std.isMetadata(DefaultCitation.class));
        assertTrue (std.isMetadata(RectifiedGrid.class));
        /*
         * Ensure that the getters have been found.
         */
        final Map<String, String> names = std.asNameMap(RectifiedGrid.class, KeyNamePolicy.JAVABEANS_PROPERTY, KeyNamePolicy.UML_IDENTIFIER);
        assertFalse("Getters should have been found even if there is no implementation.", names.isEmpty());
        assertEquals("dimension", names.get("dimension"));
        assertEquals("cells", names.get("cell"));
        /*
         * Ensure that the type are recognized, especially RectifiedGrid.getOffsetVectors()
         * which is of type List<double[]>.
         */
        Map<String, Class<?>> types;
        types = std.asTypeMap(RectifiedGrid.class, TypeValuePolicy.PROPERTY_TYPE, KeyNamePolicy.UML_IDENTIFIER);
        assertEquals("The return type is the int primitive type.", Integer.TYPE, types.get("dimension"));
        assertEquals("The offset vectors are stored in a List.",   List.class,   types.get("offsetVectors"));

        types = std.asTypeMap(RectifiedGrid.class, TypeValuePolicy.ELEMENT_TYPE, KeyNamePolicy.UML_IDENTIFIER);
        assertEquals("If the type was elements in a List, it would be the Integer wrapper.",
                Integer.class,  types.get("dimension"));
        assertEquals(double[].class, types.get("offsetVectors"));
    }
}
