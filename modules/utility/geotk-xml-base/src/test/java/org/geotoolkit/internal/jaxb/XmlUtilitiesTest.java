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
package org.geotoolkit.internal.jaxb;

import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.test.LocaleDependantTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;


/**
 * Test {@link XmlUtilities}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final strictfp class XmlUtilitiesTest extends LocaleDependantTestBase {
    /**
     * Tests the {@link XmlUtilities#toXML} method.
     */
    @Test
    public void testToXML() {
        final XMLGregorianCalendar cal = XmlUtilities.toXML(new Date(1230786000000L));
        assertEquals("2009-01-01T06:00:00.000+01:00", cal.toString());

        cal.setMillisecond(FIELD_UNDEFINED);
        assertEquals("2009-01-01T06:00:00+01:00", cal.toString());
    }
}
