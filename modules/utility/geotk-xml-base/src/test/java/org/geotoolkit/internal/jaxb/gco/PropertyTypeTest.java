/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import org.opengis.metadata.citation.Citation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Test {@link PropertyType}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final class PropertyTypeTest {
    /**
     * A dummy implementation of {@link PropertyType}.
     */
    private static final class CI_Citation extends PropertyType<CI_Citation, Citation> {
        /**
         * This method is not of interest for this test.
         */
        @Override
        protected CI_Citation wrap(final Citation value) {
            return null;
        }

        /**
         * This method is not of interest for this test.
         */
        @Override
        public Object getElement() {
            return null;
        }
    }

    /**
     * Tests {@link PropertyType#getBoundType()}.
     */
    @Test
    public void testGetBoundType() {
        final CI_Citation adapter = new CI_Citation();
        assertEquals(Citation.class, adapter.getBoundType());
    }
}
