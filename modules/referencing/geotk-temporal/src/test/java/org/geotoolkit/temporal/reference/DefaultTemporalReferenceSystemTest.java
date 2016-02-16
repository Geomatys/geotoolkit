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

import java.util.Collection;
import java.util.Set;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.Identifier;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultTemporalReferenceSystemTest extends org.geotoolkit.test.TestBase {

    private TemporalReferenceSystem temporalReferenceSystem1;
    private TemporalReferenceSystem temporalReferenceSystem2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {
        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "ref system1");
        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "ref system2");
        temporalReferenceSystem1 = FACTORY.createTemporalReferenceSystem(name1, new DefaultExtent());
        temporalReferenceSystem2 = FACTORY.createTemporalReferenceSystem(name2, new DefaultExtent());
    }

    @After
    public void tearDown() {
        temporalReferenceSystem1 = null;
        temporalReferenceSystem2 = null;
    }

    /**
     * Test of getName method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetName() {
        Identifier result = temporalReferenceSystem1.getName();
        assertFalse(temporalReferenceSystem2.getName().equals(result));
    }

    /**
     * Test of getDomainOfValidity method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetDomainOfValidity() {
        Extent result = temporalReferenceSystem1.getDomainOfValidity();
        assertEquals(temporalReferenceSystem2.getDomainOfValidity(), result);
    }

    /**
     * Test of getScope method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetScope() {
        InternationalString result = temporalReferenceSystem1.getScope();
        assertEquals(temporalReferenceSystem2.getScope(), result);
    }

    /**
     * Test of getAlias method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetAlias() {
        Collection<GenericName> result = temporalReferenceSystem1.getAlias();
        assertEquals(temporalReferenceSystem2.getAlias(), result);
    }

    /**
     * Test of getIdentifiers method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetIdentifiers() {
        Set<Identifier> result = temporalReferenceSystem1.getIdentifiers();
        assertNotEquals(temporalReferenceSystem2.getIdentifiers(), result);
    }

    /**
     * Test of getRemarks method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testGetRemarks() {
        InternationalString result = temporalReferenceSystem1.getRemarks();
        assertEquals(temporalReferenceSystem2.getRemarks(), result);
    }

    /**
     * Test of toWKT method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testToWKT() {
        //@todo this method is not implemented yet!
    }

//    /**
//     * Test of setName method, of class DefaultTemporalReferenceSystem.
//     */
//    @Test
//    public void testSetName() {
//        Identifier result = temporalReferenceSystem1.getName();
////        ((DefaultTemporalReferenceSystem) temporalReferenceSystem1).setName(new NamedIdentifier(Citations.CRS, "new name"));
//        assertFalse(temporalReferenceSystem1.getName().equals(result));
//    }

//    /**
//     * Test of setDomainOfValidity method, of class DefaultTemporalReferenceSystem.
//     */
//    @Test
//    public void testSetDomainOfValidity() {
//        Extent result = temporalReferenceSystem1.getDomainOfValidity();
//        DefaultExtent domainOfValidity = new DefaultExtent();
//        domainOfValidity.setDescription(new SimpleInternationalString("Western Europe"));
//        Calendar cal = Calendar.getInstance();
//        cal.set(0, 0, 0);
//        DefaultTemporalExtent temporalExt = new DefaultTemporalExtent();
//        temporalExt.setBounds(cal.getTime(), new Date());
//        Collection<TemporalExtent> collection = new ArrayList<TemporalExtent>();
//        collection.add(temporalExt);
//        domainOfValidity.setTemporalElements(collection);
////        ((DefaultTemporalReferenceSystem) temporalReferenceSystem1).setDomainOfValidity(domainOfValidity);
//        assertFalse(temporalReferenceSystem1.getDomainOfValidity().equals(result));
//    }
//    /**
//     * Test of setName method, of class DefaultTemporalReferenceSystem.
//     */
//    @Test
//    public void testSetName() {
//        ReferenceIdentifier result = temporalReferenceSystem1.getName();
//        ((DefaultTemporalReferenceSystem) temporalReferenceSystem1).setName(new NamedIdentifier(Citations.CRS, "new name"));
//        assertFalse(temporalReferenceSystem1.getName().equals(result));
//    }
//
//    /**
//     * Test of setDomainOfValidity method, of class DefaultTemporalReferenceSystem.
//     */
//    @Test
//    public void testSetDomainOfValidity() {
//        Extent result = temporalReferenceSystem1.getDomainOfValidity();
//        DefaultExtent domainOfValidity = new DefaultExtent();
//        domainOfValidity.setDescription(new SimpleInternationalString("Western Europe"));
//        Calendar cal = Calendar.getInstance();
//        cal.set(0, 0, 0);
//        DefaultTemporalExtent temporalExt = new DefaultTemporalExtent();
//        temporalExt.setBounds(cal.getTime(), new Date());
//        Collection<TemporalExtent> collection = new ArrayList<TemporalExtent>();
//        collection.add(temporalExt);
//        domainOfValidity.setTemporalElements(collection);
//        ((DefaultTemporalReferenceSystem) temporalReferenceSystem1).setDomainOfValidity(domainOfValidity);
//        assertFalse(temporalReferenceSystem1.getDomainOfValidity().equals(result));
//    }

    /**
     * Test of setValidArea method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testSetValidArea() {
        //This method is deprecated
    }

    /**
     * Test of setScope method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testSetScope() {
        InternationalString result = ((DefaultTemporalReferenceSystem) temporalReferenceSystem1).getScope();
        assertEquals(((DefaultTemporalReferenceSystem) temporalReferenceSystem1).getScope(), result);
    }

    /**
     * Test of equals method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testEquals() {
        assertFalse(temporalReferenceSystem1.equals(null));
        assertEquals(temporalReferenceSystem1, temporalReferenceSystem1);
        assertFalse(temporalReferenceSystem1.equals(temporalReferenceSystem2));
    }

    /**
     * Test of hashCode method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testHashCode() {
        int result = temporalReferenceSystem1.hashCode();
        assertFalse(temporalReferenceSystem2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultTemporalReferenceSystem.
     */
    @Test
    public void testToString() {
        String result = temporalReferenceSystem1.toString();
        assertFalse(temporalReferenceSystem2.toString().equals(result));
    }
}
