/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.HashMap;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.util.FactoryException;

import org.geotoolkit.referencing.crs.DefaultVerticalCRS;

import org.apache.sis.test.DependsOn;
import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.datum.DefaultVerticalDatum.GEOIDAL;
import static org.geotoolkit.referencing.cs.DefaultVerticalCS.GRAVITY_RELATED_HEIGHT;


/**
 * Tests the {@link MetadataFactory} class. This test is defined in the referencing module
 * instead than the metadata module in order to include the {@link CRSFactory} implementation
 * in the test.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
@DependsOn(FactoryMethodTest.class)
public final strictfp class ReferencingFactoryTest {
    /**
     * The metadata factory.
     */
    private MetadataFactory factory;

    /**
     * Creates the factory which is going to be tested.
     */
    @Before
    public void createFactory() {
        factory = new MetadataFactory();
    }

    /**
     * Tests the {@link MetadataFactory#create} method with a vertical CRS. This method combines
     * both the {@link MetadataFactoryTest#testCreate()} and {@link FactoryMethodTest#testCreate()}
     * methods, so it is a kind of integration test.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreate() throws FactoryException {
        final Map<String,Object> properties = new HashMap<>();
        /*
         * Tests the Citation creation.
         */
        assertNull(properties.put("title", "Undercurrent"));
        assertNull(properties.put("ISBN", "9782505004509"));
        assertNull(properties.put("presentationForm", "Document hardcopy"));
        final Citation citation = factory.create(Citation.class, properties);
        assertEquals("Undercurrent",  citation.getTitle().toString());
        assertEquals("9782505004509", citation.getISBN());
        assertSame(PresentationForm.DOCUMENT_HARDCOPY, citation.getPresentationForms().iterator().next());
        /*
         * Tests the VerticalCRS creation.
         */
        assertNull(properties.put("datum", GEOIDAL));
        assertNull(properties.put("cs",    GRAVITY_RELATED_HEIGHT));
        assertNull(properties.put("name", "Geoidal height"));
        final VerticalCRS crs = factory.create(VerticalCRS.class, properties);
        assertEquals(new DefaultVerticalCRS("Geoidal height", GEOIDAL, GRAVITY_RELATED_HEIGHT), crs);
    }
}
