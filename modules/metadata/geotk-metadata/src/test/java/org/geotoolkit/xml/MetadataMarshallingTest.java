/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Collection;
import javax.xml.bind.JAXBException;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.CellGeometry;

import org.apache.sis.xml.XML;
import org.apache.sis.metadata.iso.*;
import org.apache.sis.metadata.iso.spatial.*;
import org.apache.sis.metadata.iso.quality.*;
import org.apache.sis.metadata.iso.lineage.*;
import org.apache.sis.metadata.iso.citation.*;
import org.apache.sis.metadata.iso.distribution.*;
import org.apache.sis.metadata.iso.identification.*;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.junit.*;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.LocaleDependantTestBase;

import static org.apache.sis.test.Assert.*;
import static org.apache.sis.test.TestUtilities.getSingleton;


/**
 * A test class for annotations written in the Metadata module.
 * First, it marshalls all annotations in a XML temporary file, starting with the
 * {@link DefaultMetadata} class as root element. Then, the temporary XML file is
 * unmarshalled, in order to get a {@code DefaultMetadata} object. Finally some
 * fields of this object are compared with the original value.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 */
@DependsOn({CodeListMarshallingTest.class, FreeTextMarshallingTest.class})
public final strictfp class MetadataMarshallingTest extends LocaleDependantTestBase {
    /**
     * Generates a XML tree using the annotations on the {@link DefaultMetadata} class,
     * and writes it in a temporary buffer. The buffer is then read by the unmarshaller.
     * Some assertions about the validity of the unmarshalled data are checked.
     *
     * @throws IOException If an error occurred while reading the XML file.
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     */
    @Test
    public void testMetadata() throws IOException, JAXBException {
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
         *   Locales:
         *     fr_CA
         *     en_GB
         *   Metadata Standard Version: ISO-19115
         */
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setLocales(Arrays.asList(Locale.CANADA_FRENCH, Locale.UK));
        metadata.setLanguage(Locale.FRENCH);
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setDateStamp(new Date(1260961229580L));
        metadata.setContacts(Arrays.asList(
            getSingleton(Citations.GEOTOOLKIT.getCitedResponsibleParties()),
            getSingleton(Citations.OPEN_GIS.getCitedResponsibleParties())
        ));
        metadata.setMetadataStandardVersion("ISO-19115");
        /*
         * Data Identification:
         *   Abstract: Geotoolkit.org, projet OpenSource
         *   Citation:
         *     Dates:
         *       Date: 10 mai 2007 00:00:00 CEST
         *       Date Type: creation
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
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString("Geotoolkit.org"));
        citation.setCitedResponsibleParties(Arrays.asList(getSingleton(Citations.GEOTOOLKIT.getCitedResponsibleParties())));
        citation.setDates(Arrays.asList(new DefaultCitationDate(new Date(1178748000000L), DateType.CREATION)));
        final DefaultDataIdentification identification = new DefaultDataIdentification();
        identification.setCitation(citation);
        final DefaultInternationalString localizedAbstract = new DefaultInternationalString();
        localizedAbstract.add(Locale.ENGLISH, "Geotoolkit.org, OpenSource Project");
        localizedAbstract.add(Locale.FRENCH,  "Geotoolkit.org, projet OpenSource");
        localizedAbstract.add(Locale.ITALIAN, "Geotoolkit.org, progetto OpenSource");
        identification.setAbstract(localizedAbstract);
        identification.setLanguages(Arrays.asList(
            Locale.FRENCH
        ));
        metadata.setIdentificationInfo(Arrays.asList(
            identification
        ));
        /*
         * Grid Spatial Representation:
         *   Axis Dimension Properties:
         *     Dimension Name: column
         *     Dimension Size: 830
         *     Resolution: 70,5
         *   Cell Geometry: area
         *   Number Of Dimensions: 1
         *   Transformation Parameter Available: false
         */
        final DefaultDimension dimension = new DefaultDimension();
        dimension.setDimensionName(DimensionNameType.COLUMN);
        dimension.setDimensionSize(830);
        dimension.setResolution(70.5);
        final DefaultGridSpatialRepresentation spatialRepresentation = new DefaultGridSpatialRepresentation();
        spatialRepresentation.setNumberOfDimensions(1);
        spatialRepresentation.setAxisDimensionProperties(Arrays.asList(dimension));
        spatialRepresentation.setCellGeometry(CellGeometry.AREA);
        metadata.setSpatialRepresentationInfo(Arrays.asList(spatialRepresentation));
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
        final DefaultDistribution distribution = new DefaultDistribution();
        distribution.setDistributors(Arrays.asList(
            new DefaultDistributor(getSingleton(Citations.GEOTOOLKIT.getCitedResponsibleParties()))
        ));
        metadata.setDistributionInfo(distribution);
        /*
         * Data Quality:
         *   Scope:
         *     Level: software
         *   Reports:
         *     Results:
         *       Explanation: Conformance to the WMS standard
         *       Pass: true
         *       Specification:
         *         Dates:
         *           Date: 10 mai 2007 00:00:00 CEST
         *           Date Type: creation
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
         *       Dates:
         *         Date: 10 mai 2007 00:00:00 CEST
         *         Date Type: creation
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
        final DefaultCompletenessCommission report = new DefaultCompletenessCommission();
        report.setEvaluationMethodDescription(new SimpleInternationalString("method"));
        report.setEvaluationMethodType(EvaluationMethodType.INDIRECT);
        report.setEvaluationProcedure(citation);
        report.setMeasureDescription(new SimpleInternationalString("description"));
        report.setMeasureIdentification(new DefaultIdentifier("ident measure"));
        report.setNamesOfMeasure(Arrays.asList(new SimpleInternationalString("my measure")));
        final DefaultConformanceResult result = new DefaultConformanceResult(citation,
                new SimpleInternationalString("Conformance to the WMS standard"), true);
        report.setResults(Arrays.asList(result));
        final DefaultDataQuality dataQuality = new DefaultDataQuality();
        dataQuality.setReports(Arrays.asList(report));
        dataQuality.setScope(new DefaultScope(ScopeCode.SOFTWARE));
        metadata.setDataQualityInfo(Arrays.asList(dataQuality));
        /*
         * Writes in output buffer, then perform the comparison with the expected output.
         * Unmarshall and compare again.
         */
        final String xml = XML.marshal(metadata);
        assertFalse("Nothing to write.", xml.isEmpty());
        assertXmlEquals(TestData.url(MetadataMarshallingTest.class, "Metadata.xml"),
                xml, "xmlns:*", "xsi:schemaLocation");

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
     * @throws IOException If an error occurred while reading the XML file.
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     *
     * @since 3.07
     */
    @Test
    public void testProcessStep() throws IOException, JAXBException {
        final DefaultProcessing info = new DefaultProcessing();
        info.setProcedureDescription(new SimpleInternationalString("Some procedure."));
        final DefaultProcessStep process = new DefaultProcessStep();
        process.setDescription(new SimpleInternationalString("Some process step."));
        process.setProcessingInformation(info);
        /*
         * XML marshalling.
         */
        final String xml = XML.marshal(process);
        assertFalse("Empty XML.", xml.isEmpty());
        assertXmlEquals(TestData.url(MetadataMarshallingTest.class, "ProcessStep.xml"),
                xml, "xmlns:*", "xsi:schemaLocation");
        /*
         * Validation tests.
         */
        final Object obj = XML.unmarshal(xml);
        assertTrue(obj instanceof DefaultProcessStep);
        assertEquals(process, obj);
    }

    /**
     * Tests the unmarshalling of a text group with a default {@code <gco:CharacterString>}
     * element. This test is somewhat a duplicate of {@link FreeTextMarshallingTest}, but
     * the context is more elaborated.
     *
     * @throws IOException   If an error occurred while reading the XML file.
     * @throws JAXBException If an error occurred during the creation of the JAXB context,
     *                       or during marshalling / unmarshalling processes.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-107">GEOTK-107</a>
     * @see FreeTextMarshallingTest
     *
     * @since 3.14
     */
    @Test
    public void testTextGroup() throws IOException, JAXBException {
        final String xml = TestData.readText(MetadataMarshallingTest.class, "PositionalAccuracy.xml");
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
        /*
         * Opportunist test. While it was not the purpose of this test, the above metadata
         * needs to contain a "result" element in order to pass XML validation test.
         */
        assertInstanceOf("Wrong value for <gmd:result>", DefaultConformanceResult.class,
                ((AbstractElement) obj).getResults().iterator().next());
        assertXmlEquals(xml, XML.marshal(obj), "xmlns:*", "xsi:schemaLocation", "xsi:type");
    }
}
