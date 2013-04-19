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

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;

import org.geotoolkit.metadata.iso.citation.DefaultCitation;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MetadataFactory} class. Note that a more extensive test is performed
 * by the {@link ReferencingFactoryTest} defined in the referencing module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public final strictfp class MetadataFactoryTest {
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
     * Tests the {@link MetadataFactory#create} method with a citation.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCreate() throws FactoryException {
        final Map<String,Object> properties = new HashMap<>();
        assertNull(properties.put("title", "Undercurrent"));
        assertNull(properties.put("ISBN", "9782505004509"));
        assertNull(properties.put("presentationForm", "Document hardcopy"));
        /*
         * Creates a new citation by specfying only the interface.
         */
        final Citation citation = factory.create(Citation.class, properties);
        assertEquals("Undercurrent",  citation.getTitle().toString());
        assertEquals("9782505004509", citation.getISBN());
        assertSame(PresentationForm.DOCUMENT_HARDCOPY, citation.getPresentationForms().iterator().next());
        /*
         * Creates the same citation, but now specifying the implementation class.
         */
        final DefaultCitation df = factory.create(DefaultCitation.class, properties);
        assertEquals(citation, df);
    }
}
