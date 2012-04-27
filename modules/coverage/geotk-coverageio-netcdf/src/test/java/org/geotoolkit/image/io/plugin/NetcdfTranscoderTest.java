/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import javax.imageio.IIOException;
import ucar.nc2.NetcdfFile;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.temporal.Instant;
import org.opengis.wrapper.netcdf.NetcdfMetadataTest;

import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;

import org.junit.*;
import static org.opengis.test.Assert.*;
import static org.geotoolkit.test.Commons.getSingleton;


/**
 * Tests using the {@link NetcdfTranscoder} class. This class inherits the tests from the
 * {@code geoapi-netcdf} module, and adds a some additional assertions for attributes not
 * parsed by the GeoAPI demo code.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfTranscoderTest extends NetcdfMetadataTest {
    /**
     * {@code true} if this instance is running an integration test.
     * <p>
     * <ul>
     *   <li>If {@code false} (the usual value), the metadata will be constructed directly by a
     *       {@link NetcdfTranscoder} instance.</li>
     *   <li>If {@code true}, the metadata will be constructed by an {@link ImageCoverageReader},
     *       which will itself use a {@link NetcdfTranscoder} under the hood and will add more
     *       information.</li>
     * </ul>
     */
    private boolean integrationTest;

    /**
     * Wraps the given NetCDF file into the Metadata object to be tested.
     * This method is invoked by the tests inherited from the {@code geoapi-test} module.
     *
     * @param  file The NetCDF file to wrap.
     * @return A metadata implementation created from the attributes found in the given file.
     * @throws IOException If an error occurred while wrapping the given NetCDF file.
     */
    @Override
    protected Metadata wrap(final NetcdfFile file) throws IOException {
        if (integrationTest) try {
            final NetcdfImageReader imageReader = new NetcdfImageReader(null);
            imageReader.setInput(file);
            final ImageCoverageReader coverageReader = new ImageCoverageReader();
            coverageReader.setInput(imageReader);
            final Metadata metadata = coverageReader.getMetadata();
            coverageReader.dispose();
            return metadata;
        } catch (CoverageStoreException e) {
            throw new IIOException(e.getLocalizedMessage(), e);
        }
        // Bellow is the "normal" test.
        final NetcdfTranscoder ncISO = new NetcdfTranscoder(file, null);
        return ncISO.readMetadata();
    }

    /**
     * Verifies the hard-coded constants.
     */
    private static void verifyConstants(final Metadata metadata) {
        assertEquals("metadataStandardName", "ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data",
                metadata.getMetadataStandardName());
        assertEquals("metadataStandardVersion", "ISO 19115-2:2009(E)",
                metadata.getMetadataStandardVersion());
    }

    /**
     * Tests a file that contains THREDDS metadata. This method inherits the tests defined in
     * GeoAPI, and adds some additional tests for attributes parsed by Geotk but not GeoAPI.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testTHREDDS() throws IOException {
        super.testTHREDDS();
        verifyConstants(metadata);
        assertEquals("fileIdentifier", "crm_v1",
                metadata.getFileIdentifier());
        assertEquals("hierarchyLevel", new HashSet<>(Arrays.asList(ScopeCode.DATASET, ScopeCode.SERVICE)),
                metadata.getHierarchyLevels());
        /*
         * Metadata / Contact.
         */
        final ResponsibleParty contact = getSingleton(metadata.getContacts());
        assertEquals("identificationInfo.citation.citedResponsibleParty.individualName", "David Neufeld",
                contact.getIndividualName());
        assertEquals("identificationInfo.citation.citedResponsibleParty.contactInfo.address.electronicMailAddress", "xxxxx.xxxxxxx@noaa.gov",
                getSingleton(contact.getContactInfo().getAddress().getElectronicMailAddresses()));
        assertSame("identificationInfo.citation.citedResponsibleParty.role", Role.POINT_OF_CONTACT,
                contact.getRole());
        /*
         * Metadata / Data Identification.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        assertSame("identificationInfo.pointOfContact", contact,
                getSingleton(identification.getPointOfContacts()));
        /*
         * Metadata / Grid Spatial Representation.
         */
        final GridSpatialRepresentation spatial = (GridSpatialRepresentation) getSingleton(metadata.getSpatialRepresentationInfo());
        final List<? extends Dimension> axis = spatial.getAxisDimensionProperties();
        assertEquals(Integer.valueOf(2), spatial.getNumberOfDimensions());
        assertEquals(2, axis.size());
        assertEquals(Integer.valueOf(19201), axis.get(0).getDimensionSize());
        assertEquals(Integer.valueOf( 9601), axis.get(1).getDimensionSize());
        assertEquals(Double .valueOf(8.332899328159992E-4), axis.get(0).getResolution());
        assertEquals(Double .valueOf(8.332465368190813E-4), axis.get(1).getResolution());
        /*
         * Metadata / Quality / Lineage.
         */
        assertEquals("dataQualityInfo.lineage.statement", "xyz2grd -R-80/-64/40/48 -I3c -Gcrm_v1.grd",
                String.valueOf(getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement()));
    }

    /**
     * Tests a NetCDF binary file. This method inherits the tests defined in GeoAPI,
     * and adds some additional tests for attributes parsed by Geotk but not GeoAPI.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testNCEP() throws IOException {
        super.testNCEP();
        verifyConstants(metadata);
        assertSame("hierarchyLevel", ScopeCode.DATASET,
                getSingleton(metadata.getHierarchyLevels()));
        /*
         * Metadata / Contact
         */
        final ResponsibleParty contact = getSingleton(metadata.getContacts());
        assertEquals("identificationInfo.citation.citedResponsibleParty.individualName", "NOAA/NWS/NCEP",
                contact.getIndividualName());
        assertSame("identificationInfo.citation.citedResponsibleParty.role", Role.POINT_OF_CONTACT,
                contact.getRole());
        /*
         * Metadata / Data Identification.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        assertSame("identificationInfo.pointOfContact", contact,
                getSingleton(identification.getPointOfContacts()));
        assertEquals("identificationInfo.resourceConstraints.useLimitation", "Freely available",
                String.valueOf(getSingleton(getSingleton(identification.getResourceConstraints()).getUseLimitations())));
        /*
         * Metadata / Quality / Lineage.
         */
        assertEquals("2003-04-07 12:12:50 - created by gribtocdl              "
                   + "2005-09-26T21:50:00 - edavis - add attributes for dataset discovery",
                   String.valueOf(getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement()));
        /*
         * Metadata / Data Identification / Keywords.
         */
        final Keywords keywords = getSingleton(identification.getDescriptiveKeywords());
        assertSame("identificationInfo.descriptiveKeywords.type", KeywordType.THEME,
                keywords.getType());
        assertEquals("identificationInfo.descriptiveKeywords.thesaurusName", "GCMD Science Keywords",
                String.valueOf(keywords.getThesaurusName().getTitle()));
        assertEquals("identificationInfo.descriptiveKeywords.keyword",
                "EARTH SCIENCE > Oceans > Ocean Temperature > Sea Surface Temperature",
                String.valueOf(getSingleton(keywords.getKeywords())));
        /*
         * Metadata / Data Identification / Vertical Extent.
         */
        final Extent extent = getSingleton(identification.getExtents());
        final VerticalExtent vext = getSingleton(extent.getVerticalElements());
        assertEquals("Vertical min", 0, vext.getMinimumValue().doubleValue(), 0);
        assertEquals("Vertical max", 0, vext.getMaximumValue().doubleValue(), 0);
        /*
         * Metadata / Data Identification / Temporal Extent.
         */
        final TemporalExtent text = getSingleton(extent.getTemporalElements());
        final Instant instant = (Instant) text.getExtent();
        // Can not test at this time, since it requires the geotk-temporal module.
        /*
         * Metadata / Content information / Range dimension.
         */
        final Collection<? extends ContentInformation> content = metadata.getContentInfo();
        assertEquals("contentInfo.size", 2, content.size());
        final Iterator<? extends ContentInformation> it = content.iterator();
        assertTrue("contentInfo.hasNext", it.hasNext());
        RangeDimension band = getSingleton(((CoverageDescription) it.next()).getDimensions());
        assertEquals("contentInfo[0].dimension.sequenceIdentifier", "grid_number",
                String.valueOf(band.getSequenceIdentifier()));
        assertEquals("contentInfo[0].dimension.descriptor", "GRIB-1 catalogued grid numbers",
                String.valueOf(band.getDescriptor()));

        assertTrue("contentInfo.hasNext", it.hasNext());
        band = getSingleton(((CoverageDescription) it.next()).getDimensions());
        assertEquals("contentInfo[1].dimension.sequenceIdentifier", "SST",
                String.valueOf(band.getSequenceIdentifier()));
        assertEquals("contentInfo[1].dimension.descriptor", "Sea temperature",
                String.valueOf(band.getDescriptor()));
        assertFalse("contentInfo.hasNext", it.hasNext());
    }

    /**
     * Same test than {@link #testNCEP()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test, with some additional metadata which were added by the coverage
     * reader.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testIntegratedNCEP() throws IOException, CoverageStoreException {
        integrationTest = true;
        testNCEP();
        /*
         * Metadata / Grid Spatial Representation.
         */
        final GridSpatialRepresentation spatial = (GridSpatialRepresentation) getSingleton(metadata.getSpatialRepresentationInfo());
        final List<? extends Dimension> axis = spatial.getAxisDimensionProperties();
        assertEquals(Integer.valueOf(3), spatial.getNumberOfDimensions());
        assertEquals(3, axis.size());
        assertEquals(Integer.valueOf(73), axis.get(0).getDimensionSize());
        assertEquals(Integer.valueOf(73), axis.get(1).getDimensionSize());
        assertEquals(Integer.valueOf( 1), axis.get(2).getDimensionSize());
    }

    /**
     * Tests the Landsat file (binary format).
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testLandsat() throws IOException {
        super.testLandsat();
        verifyConstants(metadata);
        assertSame("hierarchyLevel", ScopeCode.DATASET,
                getSingleton(metadata.getHierarchyLevels()));
        /*
         * Metadata / Content information / Range dimension.
         */
        final ContentInformation content = getSingleton(metadata.getContentInfo());
        assertInstanceOf("ContentInformation", CoverageDescription.class, content);
        final RangeDimension band = getSingleton(((CoverageDescription) content).getDimensions());
        assertEquals("long_name attribute:", "GDAL Band Number 1", String.valueOf(band.getDescriptor()));
        assertEquals("NetCDF variable name:", "Band1", String.valueOf(band.getSequenceIdentifier()));
    }

    /**
     * Tests the "Current Icing Product" file (binary format).
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testCIP() throws IOException {
        super.testCIP();
        verifyConstants(metadata);
        assertSame("hierarchyLevel", ScopeCode.DATASET,
                getSingleton(metadata.getHierarchyLevels()));
        /*
         * Metadata / Contact.
         */
        final ResponsibleParty contact = getSingleton(metadata.getContacts());
            assertEquals("identificationInfo.citation.citedResponsibleParty.organisationName", "UCAR",
                    String.valueOf(contact.getOrganisationName()));
            assertEquals("identificationInfo.citation.citedResponsibleParty.role", Role.POINT_OF_CONTACT,
                    contact.getRole());
        /*
         * Metadata / Data Identification.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        assertSame  (contact, getSingleton(identification.getPointOfContacts()));
        /*
         * Metadata / Content information / Range dimension.
         */
        final ContentInformation content = getSingleton(metadata.getContentInfo());
        assertInstanceOf("ContentInformation", CoverageDescription.class, content);
        final RangeDimension band = getSingleton(((CoverageDescription) content).getDimensions());
        assertEquals("long_name attribute:", "Current Icing Product", String.valueOf(band.getDescriptor()));
        assertEquals("NetCDF variable name:", "CIP", String.valueOf(band.getSequenceIdentifier()));
        /*
         * Metadata / Quality / Lineage.
         */
        assertEquals("dataQualityInfo.lineage.statement", "U.S. National Weather Service - NCEP (WMC)",
                String.valueOf(getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement()));
    }

    /**
     * Same test than {@link #testCIP()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    @Ignore
    public void testIntegratedCIP() throws IOException, CoverageStoreException {
        integrationTest = true;
        testCIP();
    }
}
