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
package org.geotoolkit.metadata.iso.citation;

import java.util.Date;

import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.CitationDate;

import org.apache.sis.util.ComparisonMode;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link DefaultCitationDate}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 */
public final strictfp class DefaultCitationDateTest {
    /**
     * Tests the shallow copy (through a constructor).
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-170">GEOTK-170</a>
     */
    @Test
    public void testShallowCopy() {
        final CitationDate original = new CitationDate() {
            @Override public Date     getDate()     {return new Date(1305716658508L);}
            @Override public DateType getDateType() {return DateType.CREATION;}
        };
        final DefaultCitationDate copy = new DefaultCitationDate(original);
        assertEquals(new Date(1305716658508L), copy.getDate());
        assertTrue (copy.equals(original, ComparisonMode.BY_CONTRACT));
        assertFalse(copy.equals(original, ComparisonMode.STRICT)); // Opportunist test.
    }
}
