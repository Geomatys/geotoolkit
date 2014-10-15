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

import org.geotoolkit.temporal.reference.DefaultOrdinalReferenceSystem;
import java.util.Collection;
import org.geotoolkit.metadata.Citations;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.datum.DefaultTemporalDatum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalReferenceSystem;
import org.opengis.temporal.TemporalReferenceSystem;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultOrdinalReferenceSystemTest {

    private OrdinalReferenceSystem ordinalReferenceSystem1;
    private OrdinalReferenceSystem ordinalReferenceSystem2;

    @Before
    public void setUp() {
////        TemporalDatum tempdat = CommonCRS.Temporal.UNIX.datum();
////        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Ordinal1");
////        final Map<String, Object> properties1 = new HashMap<>();
////        properties1.put(IdentifiedObject.NAME_KEY, name1);
//////        TemporalReferenceSystem frame1 = new DefaultTemporalReferenceSystem(properties1, tempdat, null);
////        
////        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Ordinal2");
////        final Map<String, Object> properties2 = new HashMap<>();
////        properties2.put(IdentifiedObject.NAME_KEY, name2);
//////        TemporalReferenceSystem frame2 = new DefaultTemporalReferenceSystem(properties2, tempdat, null);
//////        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Ordinal1");
//////        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Ordinal2");
////        ordinalReferenceSystem1 = new DefaultOrdinalReferenceSystem(properties1, tempdat, null, null);
////        ordinalReferenceSystem2 = new DefaultOrdinalReferenceSystem(properties2, tempdat, null, null);
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
        Collection<OrdinalEra> result = (Collection<OrdinalEra>) ordinalReferenceSystem1.getOrdinalEraSequence();
        assertEquals(ordinalReferenceSystem2.getOrdinalEraSequence(), result);
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
