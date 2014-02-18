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
package org.geotoolkit.coverage.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.awt.geom.Rectangle2D;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link LayerCoverageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.10 (derived from Seagis)
 */
@DependsOn(CoverageDatabaseTest.class)
public final strictfp class LayerCoverageReaderTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public LayerCoverageReaderTest() {
        super(LayerCoverageReader.class);
    }

    /**
     * The coverage database.
     */
    private static CoverageDatabase database;

    /**
     * Creates the database when first needed.
     */
    private static synchronized CoverageDatabase getCoverageDatabase() {
        if (database == null) {
            database = new CoverageDatabase((TableFactory) getDatabase());
        }
        return database;
    }

    /**
     * Disposes the database.
     */
    @AfterClass
    public static synchronized void dispose() {
        if (database != null) {
            database.dispose();
            database = null;
        }
    }

    /**
     * Tests loading an image of temperature data in WGS84 CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testTemperature() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.TEMPERATURE);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.TEMPERATURE, layer.getName());

        SpatialMetadata metadata = reader.getStreamMetadata();
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "└───DiscoveryMetadata\n" +
            "    └───Extent\n" +
            "        └───GeographicElement\n" +
            "            ├───westBoundLongitude=“-180.0”\n" +
            "            ├───eastBoundLongitude=“180.0”\n" +
            "            ├───southBoundLatitude=“-90.0”\n" +
            "            ├───northBoundLatitude=“90.0”\n" +
            "            └───inclusion=“true”\n"), metadata.toString());
        metadata = reader.getCoverageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "├───ImageDescription\n" +
            "│   └───Dimensions\n" +
            "│       └───Dimension\n" +
            "│           ├───descriptor=“SST [-3 … 32.25°C]”\n" +
            "│           ├───minValue=“-2.85”\n" +
            "│           ├───maxValue=“35.25”\n" +
            "│           ├───units=“Cel”\n" +
            "│           ├───validSampleValues=“[1 … 255]”\n" +
            "│           ├───fillSampleValues=“0”\n" +
            "│           ├───scaleFactor=“0.15”\n" +
            "│           ├───offset=“-3.0”\n" +
            "│           └───transferFunctionType=“linear”\n" +
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-180.0 90.0 6431.0”\n" +
            "│   ├───Limits\n" +
            "│   │   ├───low=“0 0 0”\n" +
            "│   │   └───high=“4095 2047 6”\n" +
            "│   └───OffsetVectors\n" +
            "│       ├───OffsetVector\n" +
            "│       │   └───values=“0.087890625 0.0 0.0”\n" +
            "│       ├───OffsetVector\n" +
            "│       │   └───values=“0.0 -0.087890625 0.0”\n" +
            "│       └───OffsetVector\n" +
            "│           └───values=“0.0 0.0 8.0”\n" +
            "└───SpatialRepresentation\n" +
            "    ├───numberOfDimensions=“3”\n" +
            "    ├───centerPoint=“0.0 0.0 6459.0”\n" +
            "    └───pointInPixel=“upperLeft”\n"), metadata.toString());

        final CoverageEnvelope envelope = layer.getEnvelope(null, null);
        envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);
        param.setResolution(0.703125, 0.703125, 0, 0); // Image of size (512, 256).

        requireImageData();
        GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(512, coverage.getRenderedImage().getWidth());
        assertEquals(256, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);

        show(coverage.view(ViewType.RENDERED));

        // Read one more time, in order to ensure that recycling LayerCoverageReader work.
        // Before we fixed this test, we got an "Input not set" exception in such situation.
        param.setResolution(1.40625, 1.40625, 0, 0);
        coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(256, coverage.getRenderedImage().getWidth());
        assertEquals(128, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);
    }

    /**
     * Tests loading an image of temperature data in Mercator CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testCoriolis() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.NETCDF);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.NETCDF, layer.getName());

        final CoverageEnvelope envelope = layer.getEnvelope(null, 100);
        envelope.setHorizontalRange(new Rectangle2D.Double(-40, -40, 80, 80));

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);

        requireImageData();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(160, coverage.getRenderedImage().getWidth());
        assertEquals(175, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkCoriolisCoverage(coverage);

        show(coverage.view(ViewType.RENDERED));
    }

    /**
     * Tests loading a tiled image.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testBluemarble() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.BLUEMARBLE);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.BLUEMARBLE, layer.getName());

        SpatialMetadata metadata = reader.getStreamMetadata();
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "└───DiscoveryMetadata\n" +
            "    └───Extent\n" +
            "        └───GeographicElement\n" +
            "            ├───westBoundLongitude=“-180.0”\n" +
            "            ├───eastBoundLongitude=“180.0”\n" +
            "            ├───southBoundLatitude=“-90.0”\n" +
            "            ├───northBoundLatitude=“90.0”\n" +
            "            └───inclusion=“true”\n"), metadata.toString());
        metadata = reader.getCoverageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-180.0 90.0”\n" +
            "│   ├───Limits\n" +
            "│   │   ├───low=“0 0”\n" +
            "│   │   └───high=“2879 1439”\n" +
            "│   └───OffsetVectors\n" +
            "│       ├───OffsetVector\n" +
            "│       │   └───values=“0.125 0.0”\n" +
            "│       └───OffsetVector\n" +
            "│           └───values=“0.0 -0.125”\n" +
            "└───SpatialRepresentation\n" +
            "    ├───numberOfDimensions=“2”\n" +
            "    ├───centerPoint=“0.0 0.0”\n" +
            "    └───pointInPixel=“upperLeft”\n"), metadata.toString());

        final CoverageEnvelope envelope = layer.getEnvelope(null, 100);
        envelope.setHorizontalRange(new Rectangle2D.Double(-40, -40, 80, 80));

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);

        requireImageData();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(640, coverage.getRenderedImage().getWidth());
        assertEquals(640, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkBluemarbleCoverage(coverage);

        show(coverage.view(ViewType.RENDERED));
    }
}
