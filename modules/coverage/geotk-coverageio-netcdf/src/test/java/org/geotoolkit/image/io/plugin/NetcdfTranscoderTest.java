/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import ucar.nc2.NetcdfFile;
import ucar.nc2.ncml.NcMLReader;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.temporal.Instant;

import org.geotoolkit.test.TestData;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.getSingleton;


/**
 * Tests using the {@link NetcdfTranscoder} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfTranscoderTest extends LocaleDependantTestBase {
    /**
     * The THREDDS XML test file.
     */
    private static final String THREDDS_FILE = "thredds.ncml";

    /**
     * The binary NetCDF test file.
     */
    private static final String BINARY_FILE = "2005092200_sst_21-24.en.nc";

    /**
     * Tolerance factor for floating point comparison.
     * We actually require an exact match.
     */
    private static final double EPS = 0;

    /**
     * Tests a file that contains THREDDS metadata.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    public void testTHREDDS() throws IOException {
        final Metadata metadata;
        try (InputStream in = TestData.openStream(NetcdfImageReader.class, THREDDS_FILE)) {
            final NetcdfFile file = NcMLReader.readNcML(in, null);
            final NetcdfTranscoder ncISO = new NetcdfTranscoder(file, null);
            metadata = ncISO.readMetadata();
            file.close();
        }
        assertEquals("crm_v1", metadata.getFileIdentifier());
        /*
         * Metadata / Responsibly party.
         */
        final ResponsibleParty party = getSingleton(metadata.getContacts());
        assertEquals("David Neufeld", party.getIndividualName());
        assertEquals("xxxxx.xxxxxxx@noaa.gov", getSingleton(party.getContactInfo().getAddress().getElectronicMailAddresses()));
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
         * Metadata / Data Identification / Geographic Bounding Box.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        final GeographicBoundingBox bbox = (GeographicBoundingBox) getSingleton(getSingleton(identification.getExtents()).getGeographicElements());
        assertEquals("West Bound Longitude", -80, bbox.getWestBoundLongitude(), EPS);
        assertEquals("East Bound Longitude", -64, bbox.getEastBoundLongitude(), EPS);
        assertEquals("South Bound Latitude",  40, bbox.getSouthBoundLatitude(), EPS);
        assertEquals("North Bound Latitude",  48, bbox.getNorthBoundLatitude(), EPS);
        /*
         * Metadata / Quality.
         */
        assertEquals("xyz2grd -R-80/-64/40/48 -I3c -Gcrm_v1.grd",
                getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement().toString());
    }

    /**
     * Tests a NetCDF binary file.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    public void testNC() throws IOException {
        final File file = TestData.file(NetcdfImageReader.class, BINARY_FILE);
        final NetcdfTranscoder ncISO = new NetcdfTranscoder(NetcdfFile.open(file.getPath()), null);
        final Metadata metadata = ncISO.readMetadata();
        ncISO.file.close();
        checkSST(metadata);
    }

    /**
     * Checks the metadata from the {@value #BINARY_FILE} file.
     */
    private static void checkSST(final Metadata metadata) {
        /*
         * Metadata / Data Identification.
         */
        assertEquals("NCEP/SST/Global_5x2p5deg/SST_Global_5x2p5deg_20050922_0000.nc", metadata.getFileIdentifier());
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        assertSame(SpatialRepresentationType.GRID, getSingleton(identification.getSpatialRepresentationTypes()));
        assertEquals("NCEP SST Global 5.0 x 2.5 degree model data", identification.getAbstract().toString());
        assertEquals("Freely available", getSingleton(getSingleton(identification.getResourceConstraints()).getUseLimitations()).toString());
        /*
         * Metadata / Quality.
         */
        assertEquals("2003-04-07 12:12:50 - created by gribtocdl              "
                   + "2005-09-26T21:50:00 - edavis - add attributes for dataset discovery",
                   getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement().toString());
        /*
         * Metadata / Responsibly party.
         */
        final ResponsibleParty party = getSingleton(metadata.getContacts());
        assertEquals("NOAA/NWS/NCEP", party.getIndividualName());
        assertEquals(Role.ORIGINATOR, party.getRole());
        /*
         * Metadata / Data Identification / Citation.
         */
        final Citation citation = identification.getCitation();
        final Identifier identifier = getSingleton(citation.getIdentifiers());
        final CitationDate date = getSingleton(citation.getDates());
        assertEquals("Sea Surface Temperature Analysis Model", citation.getTitle().toString());
        assertEquals("edu.ucar.unidata", identifier.getAuthority().getTitle().toString());
        assertEquals("NCEP/SST/Global_5x2p5deg/SST_Global_5x2p5deg_20050922_0000.nc", identifier.getCode());
        assertEquals("Expected 2005-09-22T00:00", 1127340000000L, date.getDate().getTime());
        assertSame(DateType.CREATION, date.getDateType());
        /*
         * Metadata / Data Identification / Keywords.
         */
        final Keywords keywords = getSingleton(identification.getDescriptiveKeywords());
        assertEquals("GCMD Science Keywords", keywords.getThesaurusName().getTitle().toString());
        assertEquals("EARTH SCIENCE > Oceans > Ocean Temperature > Sea Surface Temperature", getSingleton(keywords.getKeywords()).toString());
        /*
         * Metadata / Data Identification / Geographic Bounding Box.
         */
        final Extent extent = getSingleton(identification.getExtents());
        final GeographicBoundingBox bbox = (GeographicBoundingBox) getSingleton(extent.getGeographicElements());
        assertEquals("West Bound Longitude", -180, bbox.getWestBoundLongitude(), 0);
        assertEquals("East Bound Longitude", +180, bbox.getEastBoundLongitude(), 0);
        assertEquals("South Bound Latitude",  -90, bbox.getSouthBoundLatitude(), 0);
        assertEquals("North Bound Latitude",  +90, bbox.getNorthBoundLatitude(), 0);
        /*
         * Metadata / Data Identification / Vertical Extent.
         */
        final VerticalExtent vext = getSingleton(extent.getVerticalElements());
        assertEquals("Vertical min", 0, vext.getMinimumValue().doubleValue(), 0);
        assertEquals("Vertical max", 0, vext.getMaximumValue().doubleValue(), 0);
        /*
         * Metadata / Data Identification / Temporal Extent.
         */
        final TemporalExtent text = getSingleton(extent.getTemporalElements());
        final Instant instant = (Instant) text.getExtent();
        // Can not test at this time, since it requires the geotk-temporal module.
    }

    /**
     * Same test than {@link #testNC()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testGridCoverageReader() throws IOException, CoverageStoreException {
        final NetcdfImageReader imageReader = new NetcdfImageReader(null);
        imageReader.setInput(TestData.file(NetcdfImageReader.class, BINARY_FILE));
        final ImageCoverageReader coverageReader = new ImageCoverageReader();
        coverageReader.setInput(imageReader);
        final Metadata metadata = coverageReader.getMetadata();
        coverageReader.dispose();
        checkSST(metadata);
    }
}
