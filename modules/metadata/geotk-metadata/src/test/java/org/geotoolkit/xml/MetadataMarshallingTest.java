/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Collection;
import javax.xml.bind.JAXBException;

import org.opengis.util.InternationalString;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.metadata.spatial.DimensionNameType;

import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultDistributor;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessing;
import org.geotoolkit.metadata.iso.quality.AbstractElement;
import org.geotoolkit.metadata.iso.quality.DefaultCompletenessCommission;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.metadata.MetadataStandardTest;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.SimpleInternationalString;

import org.junit.*;
import static org.junit.Assert.*;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import static org.geotoolkit.test.Commons.*;


/**
 * A test class for annotations written in the Metadata module.
 * First, it marshalls all annotations in a XML temporary file, starting with the
 * {@link DefaultMetadata} class as root element. Then, the temporary XML file is
 * unmarshalled, in order to get a {@code DefaultMetadata} object. Finally some
 * fields of this object are compared with the original value.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 2.5
 */
@Depend(MetadataStandardTest.class)
public final class MetadataMarshallingTest {
    /**
     * The previous locale before the test is run.
     * This is usually the default locale.
     */
    private Locale defaultLocale;

    /**
     * Sets the locale to a compile-time value. We need to use a fixed value because the
     * value of an international string is locale-sensitive in this test.
     */
    @Before
    public void fixLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
    }

    /**
     * Restores the locales to its original value.
     */
    @After
    public void restoreLocale() {
        Locale.setDefault(defaultLocale);
    }

    /**
     * Generates a XML tree using the annotations on the {@link DefaultMetadata} class,
     * and writes it in a temporary buffer. The buffer is then read by the unmarshaller.
     * Some assertions about the validity of the unmarshalled data are checked.
     *
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     * @throws IOException If an error occurred while reading the XML file.
     */
    @Test
    public void testMetadata() throws JAXBException, IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));

        // Note: the text in the comment blocks below were produced by:
        //
        //       System.out.println(theObjectCreated);
        //
        //       Run this line again if the text needs to be updated.
        /*
         * Metadata:
         *   Contacts:
         *     [1] Geotoolkit.org:
         *       Role: principal investigator
         *       Organisation Name: Geotoolkit.org
         *       Contact Info:
         *         Online Resource:
         *           Linkage: http://www.geotoolkit.org
         *           Function: information
         *     [2] OpenGIS consortium:
         *       Role: resource provider
         *       Organisation Name: OpenGIS consortium
         *       Contact Info:
         *         Online Resource:
         *           Linkage: http://www.opengis.org
         *           Function: information
         *   Date Stamp: 16 décembre 2009 12:00:29 CET
         *   Character Set: utf 8
         *   Language: fr
         */
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setLanguage(Locale.FRENCH);
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setDateStamp(new Date(1260961229580L));
        metadata.setContacts(Arrays.asList(
            DefaultResponsibleParty.GEOTOOLKIT, DefaultResponsibleParty.OPEN_GIS
        ));
        metadata.setMetadataStandardVersion("ISO-19115");
        /*
         * Data Identification:
         *   Abstract: Geotoolkit.org, projet OpenSource
         *   Citation:
         *     Title: Geotoolkit.org
         *     Cited Responsible Parties:
         *       Role: principal investigator
         *       Organisation Name: Geotoolkit.org
         *       Contact Info:
         *         Online Resource:
         *           Linkage: http://www.geotoolkit.org
         *           Function: information
         *   Languages: fr
         */
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
        /*
         * Grid Spatial Representation:
         *   Axis Dimension Properties:
         *     Dimension Name: column
         *     Dimension Size: 830
         *     Resolution: 70,5
         *   Transformation Parameter Available: false
         */
        final DefaultDimension dimension = new DefaultDimension();
        dimension.setDimensionName(DimensionNameType.COLUMN);
        dimension.setDimensionSize(830);
        dimension.setResolution(70.5);
        final DefaultGridSpatialRepresentation gridSpatialRepres = new DefaultGridSpatialRepresentation();
        gridSpatialRepres.setAxisDimensionProperties(Arrays.asList(dimension));
        metadata.setSpatialRepresentationInfo(Arrays.asList(gridSpatialRepres));
        /*
         * Distribution:
         *   Distributors:
         *     Distributor Contact:
         *       Role: principal investigator
         *       Organisation Name: Geotoolkit.org
         *       Contact Info:
         *         Online Resource:
         *           Linkage: http://www.geotoolkit.org
         *           Function: information
         */
        final DefaultDistribution distrib = new DefaultDistribution();
        distrib.setDistributors(Arrays.asList(
            new DefaultDistributor(DefaultResponsibleParty.GEOTOOLKIT)
        ));
        metadata.setDistributionInfo(distrib);
        /*
         * Data Quality:
         *   Reports:
         *     Results:
         *       Explanation: conformance to the WMS standard
         *       Pass: true
         *       Specification:
         *         Title: Geotoolkit.org
         *         Cited Responsible Parties:
         *           Role: principal investigator
         *           Organisation Name: Geotoolkit.org
         *           Contact Info:
         *             Online Resource:
         *               Linkage: http://www.geotoolkit.org
         *               Function: information
         *     Evaluation Method Description: method
         *     Evaluation Method Type: indirect
         *     Evaluation Procedure:
         *       Title: Geotoolkit.org
         *       Cited Responsible Parties:
         *         Role: principal investigator
         *         Organisation Name: Geotoolkit.org
         *         Contact Info:
         *           Online Resource:
         *             Linkage: http://www.geotoolkit.org
         *             Function: information
         *     Measure Description: description
         *     Measure Identification:
         *       Code: ident measure
         *     Names Of Measure: my measure
         */
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
        final DefaultDataQuality dataQuality = new DefaultDataQuality();
        dataQuality.setReports(Arrays.asList(completeComm));
        metadata.setDataQualityInfo(Arrays.asList(dataQuality));
        /*
         * Writes in output buffer.
         */
        final String xml = XML.marshal(metadata);
        assertFalse("Nothing to write.", xml.length() == 0);
        assertXmlEquals(TestData.readText(MetadataMarshallingTest.class, "Metadata.xml"), xml);
        /*
         * Validation tests.
         */
        final Object obj = XML.unmarshal(xml);
        assertNotNull(obj);
        assertTrue("The unmarshalled object gotten from the XML file marshalled is not an instance " +
                   "of DefaultMetadata. So the unmarshalling process fails for that XML string.",
                   obj instanceof DefaultMetadata);

        final DefaultMetadata dataUnmarsh = (DefaultMetadata) obj;
        assertEquals(metadata.getCharacterSet(),       dataUnmarsh.getCharacterSet());
        assertEquals(metadata.getLanguage(),           dataUnmarsh.getLanguage());
        assertEquals(metadata.getIdentificationInfo(), dataUnmarsh.getIdentificationInfo());
        assertEquals(metadata.getDataQualityInfo(),    dataUnmarsh.getDataQualityInfo());
        assertEquals(metadata,                         dataUnmarsh);
    }

    /**
     * Tests the marshalling of {@link DefaultProcessStep}.
     * This metadata mixes elements from ISO 19115 and ISO 19115-2 standards.
     *
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     * @throws IOException If an error occurred while reading the XML file.
     *
     * @since 3.07
     */
    @Test
    public void testProcessStep() throws JAXBException, IOException {
        final DefaultProcessing info = new DefaultProcessing();
        info.setProcedureDescription(new SimpleInternationalString("Some procedure."));
        final DefaultProcessStep process = new DefaultProcessStep();
        process.setDescription(new SimpleInternationalString("Some process step."));
        process.setProcessingInformation(info);
        /*
         * XML marshalling.
         */
        final String xml = XML.marshal(process);
        assertFalse("Empty XML.", xml.length() == 0);
        assertXmlEquals(TestData.readText(MetadataMarshallingTest.class, "ProcessStep.xml"), xml);
        /*
         * Validation tests.
         */
        final Object obj = XML.unmarshal(xml);
        assertTrue(obj instanceof DefaultProcessStep);
        assertEquals(process, obj);
    }

    /**
     * Tests the unmarshalling of a text group with a default {@code gco:CharacterString}
     * element.
     *
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     * @throws IOException If an error occurred while reading the XML file.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-107">GEOTK-107</a>
     *
     * @since 3.14
     */
    @Test
    public void testTextGroup() throws JAXBException, IOException {
        final String xml = TestData.readText(MetadataMarshallingTest.class, "AbstractElement.xml");
        final Object obj = XML.unmarshal(xml);
        assertTrue(obj instanceof AbstractElement);

        final Collection<InternationalString> nameOfMeasures = ((AbstractElement) obj).getNamesOfMeasure();
        assertEquals(1, nameOfMeasures.size());
        final InternationalString nameOfMeasure = nameOfMeasures.iterator().next();

        assertEquals("Mesure qualité quantitative de type pourcentage de représentation de la "
                + "classe par rapport à la surface totale", nameOfMeasure.toString(Locale.FRENCH));
        assertEquals("Mesure qualité quantitative de type pourcentage de représentation de la "
                + "classe par rapport à la surface totale", nameOfMeasure.toString());
        assertEquals("Quantitative quality measure focusing on the effective class percent "
                + "regarded to the total surface size", nameOfMeasure.toString(null));
        assertEquals("Quantitative quality measure focusing on the effective class percent "
                + "regarded to the total surface size", nameOfMeasure.toString(Locale.ENGLISH));
    }
}
