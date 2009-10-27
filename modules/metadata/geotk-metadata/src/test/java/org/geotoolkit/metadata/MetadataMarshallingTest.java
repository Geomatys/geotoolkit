/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultDistributor;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.quality.DefaultCompletenessCommission;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.xml.Namespaces;

import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.metadata.spatial.DimensionNameType;

import org.junit.*;
import static org.junit.Assert.*;
import org.geotoolkit.test.Depend;


/**
 * A test class for annotations written in the Metadata module.
 * First, it marshalls all annotations in a XML temporary file, starting with the
 * {@link DefaultMetadata} class as root element. Then, the temporary XML file is
 * unmarshalled, in order to get a {@code DefaultMetadata} object. Finally some
 * fields of this object are compared with the original value.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.04
 *
 * @since 2.5
 */
@Depend(MetadataStandardTest.class)
public final class MetadataMarshallingTest {
    /**
     * Generates an XML tree from the annotations on the {@link DefaultMetadata} class,
     * and writes it in a temporary buffer. This file is then read by the unmarshaller.
     * Some assertions about the validity of the unmarshalled data are checked.
     *
     * @throws JAXBException If an error occured during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     * @throws IOException If a writing error in the temporary file occured.
     */
    @Test
    public void testMarshalling() throws JAXBException, IOException {
        final MarshallerPool pool = new MarshallerPool(Collections.singletonMap(
                MarshallerPool.ROOT_NAMESPACE_KEY, Namespaces.GMD), DefaultMetadata.class);
        final Marshaller marshaller = pool.acquireMarshaller();
        /*
         * Fill metadata values.
         */
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setLanguage(Locale.FRENCH);
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setDateStamp(new Date());
        metadata.setContacts(Arrays.asList(
            DefaultResponsibleParty.GEOTOOLKIT, DefaultResponsibleParty.OPEN_GIS
        ));
        metadata.setMetadataStandardVersion("ISO-19115");
        final DefaultCitation citationGeotk = new DefaultCitation();
        citationGeotk.setTitle(new SimpleInternationalString("Geotoolkit.org"));
        citationGeotk.setCitedResponsibleParties(Arrays.asList(DefaultResponsibleParty.GEOTOOLKIT));
        final DefaultDataIdentification dataIdent = new DefaultDataIdentification();
        dataIdent.setCitation(citationGeotk);
        final DefaultInternationalString localizedAbstract = new DefaultInternationalString();
        localizedAbstract.add(Locale.ENGLISH, "Geotoolkit.org, OpenSource Project");
        localizedAbstract.add(Locale.FRENCH,  "Geotoolkit.org, projet OpenSource");
        localizedAbstract.add(Locale.ITALIAN, "Geotoolkit.org, progetto OpenSource");
        dataIdent.setAbstract(localizedAbstract);
        dataIdent.setLanguages(Arrays.asList(
            Locale.FRENCH
        ));
        metadata.setIdentificationInfo(Arrays.asList(
            dataIdent
        ));
        // Spatial representation part.
        final DefaultDimension dimension = new DefaultDimension();
        dimension.setDimensionName(DimensionNameType.COLUMN);
        dimension.setDimensionSize(830);
        dimension.setResolution(70.5);
        final DefaultGridSpatialRepresentation gridSpatialRepres = new DefaultGridSpatialRepresentation();
        gridSpatialRepres.setAxisDimensionProperties(Arrays.asList(dimension));
        metadata.setSpatialRepresentationInfo(Arrays.asList(gridSpatialRepres));
        final DefaultDistribution distrib = new DefaultDistribution();
        distrib.setDistributors(Arrays.asList(
            new DefaultDistributor(DefaultResponsibleParty.GEOTOOLKIT)
        ));
        metadata.setDistributionInfo(distrib);
        // DataQuality part.
        final DefaultDataQuality dataQuality = new DefaultDataQuality();
        final DefaultCompletenessCommission completeComm = new DefaultCompletenessCommission();
        completeComm.setEvaluationMethodDescription(new SimpleInternationalString("method"));
        completeComm.setEvaluationMethodType(EvaluationMethodType.INDIRECT);
        completeComm.setEvaluationProcedure(citationGeotk);
        completeComm.setMeasureDescription(new SimpleInternationalString("description"));
        completeComm.setMeasureIdentification(new DefaultIdentifier("ident measure"));
        completeComm.setNamesOfMeasure(Arrays.asList(new SimpleInternationalString("my measure")));
        final DefaultConformanceResult conformResult = new DefaultConformanceResult(citationGeotk,
                new SimpleInternationalString("conformance to the WMS standard"), true);
        completeComm.setResults(Arrays.asList(conformResult));
        dataQuality.setReports(Arrays.asList(completeComm));
        metadata.setDataQualityInfo(Arrays.asList(dataQuality));
        /*
         * Write in output buffer.
         */
        final StringWriter writer = new StringWriter();
        marshaller.marshal(metadata, writer);
        final String xml = writer.toString();
        pool.release(marshaller);
        writer.close();
        assertFalse("Nothing to write.", xml.length() == 0);
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
                   "of DefaultMetadata. So the unmarshalling process fails for that XML string.",
                   obj instanceof DefaultMetadata);

        final DefaultMetadata dataUnmarsh = (DefaultMetadata) obj;
        assertEquals(metadata.getCharacterSet(), dataUnmarsh.getCharacterSet());
        assertEquals(metadata.getLanguage(), dataUnmarsh.getLanguage());
        assertEquals(metadata.getIdentificationInfo(), dataUnmarsh.getIdentificationInfo());
        assertEquals(metadata.getDataQualityInfo(), dataUnmarsh.getDataQualityInfo());
        assertEquals(metadata, dataUnmarsh);
    }
}
