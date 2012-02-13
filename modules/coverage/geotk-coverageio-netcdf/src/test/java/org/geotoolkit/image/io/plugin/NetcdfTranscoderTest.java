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
import java.util.Collection;
import java.io.IOException;
import javax.imageio.IIOException;
import ucar.nc2.NetcdfFile;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.VerticalExtent;
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
     * Tests a file that contains THREDDS metadata. This method inherits the tests defined in
     * GeoAPI, and adds some additional tests for attributes parsed by Geotk but not GeoAPI.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testTHREDDS() throws IOException {
        super.testTHREDDS();
        assertEquals("crm_v1", metadata.getFileIdentifier());
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
         * Metadata / Quality.
         */
        assertEquals("xyz2grd -R-80/-64/40/48 -I3c -Gcrm_v1.grd",
                getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement().toString());
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
        /*
         * Metadata / Data Identification.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        assertSame(SpatialRepresentationType.GRID, getSingleton(identification.getSpatialRepresentationTypes()));
        assertEquals("Freely available", getSingleton(getSingleton(identification.getResourceConstraints()).getUseLimitations()).toString());
        /*
         * Metadata / Quality.
         */
        assertEquals("2003-04-07 12:12:50 - created by gribtocdl              "
                   + "2005-09-26T21:50:00 - edavis - add attributes for dataset discovery",
                   getSingleton(metadata.getDataQualityInfo()).getLineage().getStatement().toString());
        /*
         * Metadata / Data Identification / Keywords.
         */
        final Keywords keywords = getSingleton(identification.getDescriptiveKeywords());
        assertEquals("GCMD Science Keywords", keywords.getThesaurusName().getTitle().toString());
        assertEquals("EARTH SCIENCE > Oceans > Ocean Temperature > Sea Surface Temperature", getSingleton(keywords.getKeywords()).toString());
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
        final ContentInformation content = getSingleton(metadata.getContentInfo());
        assertInstanceOf("ContentInformation", CoverageDescription.class, content);
        Collection<? extends RangeDimension> dimensions = ((CoverageDescription) content).getDimensions();
        assertEquals("Dimensions", 2, dimensions.size());
        // TODO: should have only one band!!
    }

    /**
     * Same test than {@link #testNCEP()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testGridCoverageReader() throws IOException, CoverageStoreException {
        integrationTest = true;
        testNCEP();
    }
}
