/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.metadata.iso.DefaultMetaData;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultDistributor;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.xml.Namespaces;

import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.distribution.Distributor;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.spatial.DimensionNameType;

import org.junit.*;
import static org.junit.Assert.*;
import org.geotoolkit.test.Depend;


/**
 * A test class for annotations written in the Metadata module.
 * First, it marshalls all annotations in a XML temporary file, starting with the
 * {@link DefaultMetaData} class as root element. Then, the temporary XML file is
 * unmarshalled, in order to get a {@code DefaultMetaData} object. Finally some
 * fields of this object are compared with the original value.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 */
@Depend(MetadataStandardTest.class)
public final class MetadataAnnotationsTest {
    /**
     * Generates an XML tree from the annotations on the {@link DefaultMetaData} class,
     * and writes it in a temporary file. This file is then read by the unmarshaller.
     * Some assertions about the validity of the unmarshalled data are checked.
     *
     * @throws JAXBException If an error occured during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     * @throws IOException If a writing error in the temporary file occured.
     */
    @Test
    public void testMetadataAnnotations() throws JAXBException, IOException {
        final MarshallerPool pool =
                new MarshallerPool(Namespaces.GMD, DefaultMetaData.class);
        final Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Fill metadata values.
         */
        final DefaultMetaData metadata = new DefaultMetaData();
        metadata.setLanguage(Locale.FRENCH);
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setDateStamp(new Date());
        metadata.setContacts(Arrays.asList(new ResponsibleParty[] {
            DefaultResponsibleParty.GEOTOOLKIT, DefaultResponsibleParty.OPEN_GIS
        }));
        metadata.setMetadataStandardVersion("ISO-19115");
        final DefaultDataIdentification dataIdent = new DefaultDataIdentification();
        dataIdent.setCitation(Citations.GEOTOOLKIT);
        final DefaultInternationalString localizedString = new DefaultInternationalString();
        localizedString.add(Locale.ENGLISH, "Geotoolkit, OpenSource Project");
        localizedString.add(Locale.FRENCH,  "Geotoolkit, projet OpenSource");
        localizedString.add(Locale.ITALIAN, "Geotoolkit, progetto OpenSource");
        dataIdent.setAbstract(localizedString);
        dataIdent.setLanguage(Arrays.asList(new Locale[] {
            Locale.FRENCH
        }));
        metadata.setIdentificationInfo(Arrays.asList(new Identification[] {
            dataIdent
        }));
        final DefaultDimension dimension = new DefaultDimension();
        dimension.setDimensionName(DimensionNameType.COLUMN);
        dimension.setDimensionSize(830);
        dimension.setResolution(70.5);
        final DefaultGridSpatialRepresentation gridSpatialRepres = new DefaultGridSpatialRepresentation();
        gridSpatialRepres.setAxisDimensionsProperties(Arrays.asList(dimension));
        metadata.setSpatialRepresentationInfo(Arrays.asList(gridSpatialRepres));
        final DefaultDistribution distrib = new DefaultDistribution();
        distrib.setDistributors(Arrays.asList(new Distributor[] {
            new DefaultDistributor(DefaultResponsibleParty.GEOTOOLKIT)
        }));
        metadata.setDistributionInfo(distrib);
        final StringWriter writer = new StringWriter();
        /*
         * Write in output buffer.
         */
        marshaller.marshal(metadata, writer);
        final String xml = writer.toString();
        pool.release(marshaller);
        writer.close();
        assertFalse("Nothing has be written.", xml.length() == 0);
        /*
         * Parses the xml string.
         */
        final StringReader reader = new StringReader(xml);
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final Object obj = unmarshaller.unmarshal(reader);
        pool.release(unmarshaller);
        reader.close();
        /*
         * Validation tests.
         */
        assertNotNull(obj);
        assertTrue("The unmarshalled object gotten from the XML file marshalled is not an instance " +
                   "of DefaultMetaData. So the unmarshalling has failed on this XML file.",
                   obj instanceof DefaultMetaData);

        final DefaultMetaData dataUnmarsh = (DefaultMetaData) obj;
        assertEquals(metadata.getCharacterSet(), dataUnmarsh.getCharacterSet());
        assertEquals(metadata.getLanguage(), dataUnmarsh.getLanguage());
        assertEquals(metadata.getIdentificationInfo().iterator().next().getAbstract(),
                     dataUnmarsh.getIdentificationInfo().iterator().next().getAbstract());
    }
}
