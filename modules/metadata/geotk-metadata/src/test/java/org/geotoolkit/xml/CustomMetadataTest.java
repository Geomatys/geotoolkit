/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.xml;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.JAXBException;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.identification.*;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.util.InternationalString;
import org.opengis.util.NameFactory;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.xml.XML;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests XML marshalling of custom implementation of metadata interfaces. The custom implementations
 * need to be converted to implementations from the {@link org.apache.sis.metadata.iso} package by
 * the JAXB converters.
 *
 * @author Damiano Albani (for code snippet on the mailing list)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.14
 */
public final strictfp class CustomMetadataTest extends TestBase {
    /**
     * Tests the marshalling of a metadata implemented by {@link java.lang.reflect.Proxy}.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testProxy() throws JAXBException {
        /*
         * A trivial metadata implementation which return the method name
         * for every attribute of type String.
         */
        final InvocationHandler handler = new InvocationHandler() {
            @Override public Object invoke(Object proxy, Method method, Object[] args) {
                if (method.getReturnType() == String.class) {
                    return method.getName();
                }
                return null;
            }
        };
        Metadata data = (Metadata) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] { Metadata.class }, handler);
        /*
         * Wrap the metadata in a DefaultMetadata, and ensure
         * we can marshall it without an exception being throw.
         */
        data = new DefaultMetadata(data);
        final String xml = XML.marshal(data);
        /*
         * A few simple checks.
         */
        assertTrue(xml.contains("getMetadataStandardName"));
        assertTrue(xml.contains("getMetadataStandardVersion"));
    }

    /**
     * Tests that the attributes defined in subtypes are also marshalled.
     *
     * @throws JAXBException Should never happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-108">GEOTK-108</a>
     */
    @Test
    public void testSubtypeAttributes() throws JAXBException {
        final DataIdentification identification = new DataIdentification() {
            @Override public InternationalString getAbstract() {
                NameFactory factory = FactoryFinder.getNameFactory(null);
                Map<Locale, String> names = new HashMap<>();
                names.put(Locale.ENGLISH, "Description");
                return factory.createInternationalString(names);
            }

            @Override public InternationalString getEnvironmentDescription() {
                NameFactory factory = FactoryFinder.getNameFactory(null);
                Map<Locale, String> names = new HashMap<>();
                names.put(Locale.ENGLISH, "Environment");
                return factory.createInternationalString(names);
            }

            @Override public InternationalString                   getSupplementalInformation()    {return null;}
            @Override public Citation                              getCitation()                   {return null;}
            @Override public InternationalString                   getPurpose()                    {return null;}
            @Override public Collection<SpatialRepresentationType> getSpatialRepresentationTypes() {return null;}
            @Override public Collection<Resolution>                getSpatialResolutions()         {return null;}
            @Override public Collection<Locale>                    getLanguages()                  {return null;}
            @Override public Collection<CharacterSet>              getCharacterSets()              {return null;}
            @Override public Collection<TopicCategory>             getTopicCategories()            {return null;}
            @Override public Collection<Extent>                    getExtents()                    {return null;}
            @Override public Collection<String>                    getCredits()                    {return null;}
            @Override public Collection<Progress>                  getStatus()                     {return null;}
            @Override public Collection<ResponsibleParty>          getPointOfContacts()            {return null;}
            @Override public Collection<MaintenanceInformation>    getResourceMaintenances()       {return null;}
            @Override public Collection<BrowseGraphic>             getGraphicOverviews()           {return null;}
            @Override public Collection<Format>                    getResourceFormats()            {return null;}
            @Override public Collection<Keywords>                  getDescriptiveKeywords()        {return null;}
            @Override public Collection<Usage>                     getResourceSpecificUsages()     {return null;}
            @Override public Collection<Constraints>               getResourceConstraints()        {return null;}
            @Override public Collection<AggregateInformation>      getAggregationInfo()            {return null;}
        };
        final DefaultMetadata data = new DefaultMetadata();
        assertTrue(data.getIdentificationInfo().add(identification));
        final String xml = XML.marshal(data);
        /*
         * A few simple checks.
         */
        assertTrue("Missing Identification attribute.",     xml.contains("Description"));
        assertTrue("Missing DataIdentification attribute.", xml.contains("Environment"));
    }
}
