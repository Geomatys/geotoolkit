/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.temporal.reference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import org.geotoolkit.metadata.Citations;
import java.util.Date;
import java.util.List;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalReferenceSystem;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultOrdinalReferenceSystemTest extends org.geotoolkit.test.TestBase {

    private OrdinalReferenceSystem ordinalReferenceSystem1;
    private OrdinalReferenceSystem ordinalReferenceSystem2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {
        Calendar cal = Calendar.getInstance();
        List<OrdinalEra> seq = new ArrayList<>();
        cal.set(1900, 1, 1);
        Date beginning1 = cal.getTime();
        cal.set(2000, 1, 1);
        Date end1 = cal.getTime();
        cal.set(2000, 1, 1);
        Date beginning2 = cal.getTime();
        cal.set(2012, 1, 1);
        Date end2 = cal.getTime();
        seq.add(FACTORY.createOrdinalEra(new SimpleInternationalString("old Era"), beginning1, end1, null));
        seq.add(FACTORY.createOrdinalEra(new SimpleInternationalString("new Era"), beginning2, end2, null));

        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Ordinal1");
        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Ordinal2");
        ordinalReferenceSystem1 = FACTORY.createOrdinalReferenceSystem(name1, new DefaultExtent(), seq);
        ordinalReferenceSystem2 = FACTORY.createOrdinalReferenceSystem(name2, new DefaultExtent(), seq);
    }

    @After
    public void tearDown() {
        ordinalReferenceSystem1 = null;
        ordinalReferenceSystem2 = null;
    }

    /**
     * Test of getOrdinalEraSequence method, of class DefaultOrdinalReferenceSystem.
     */
    @Test
    public void testGetOrdinalEraSequence() {
        Collection<OrdinalEra> result = (Collection<OrdinalEra>) ordinalReferenceSystem1.getComponents();
        assertEquals(ordinalReferenceSystem2.getComponents(), result);
    }

    /**
     * Test of toWKT method, of class DefaultOrdinalReferenceSystem.
     */
    @Test
    public void testToWKT() {
        //@todo this method is not implemented yet!
    }

    /**
     * Test of equals method, of class DefaultOrdinalReferenceSystem.
     */
    @Test
    public void testEquals() {
        assertFalse(ordinalReferenceSystem1.equals(null));
        assertEquals(ordinalReferenceSystem1, ordinalReferenceSystem1);
        assertFalse(ordinalReferenceSystem1.equals(ordinalReferenceSystem2));
    }

    /**
     * Test of hashCode method, of class DefaultOrdinalReferenceSystem.
     */
    @Test
    public void testHashCode() {
        int result = ordinalReferenceSystem1.hashCode();
        assertFalse(ordinalReferenceSystem2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultOrdinalReferenceSystem.
     */
    @Test
    public void testToString() {
        String result = ordinalReferenceSystem1.toString();
        assertFalse(ordinalReferenceSystem2.toString().equals(result));
    }
}
