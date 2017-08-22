/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.image.io.plugin.TiffImageWriter;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class TimedCoverageStoreTest extends DirectoryBasedTest {

    private static final int DELAY = 100;

    @Test
    public void checkFactory() {
        DataStoreFactory factory = DataStores.getFactoryById(TimedCoverageFactory.NAME);
        Assert.assertNotNull("Factory not found for name "+TimedCoverageFactory.NAME, factory);
    }

    @Test
    public void createEmpty() throws DataStoreException {
        try (final TimedCoverageStore store = create()) {

            final GridCoverageReader reader = acquireReader(store);

            final GeneralGridGeometry gg = reader.getGridGeometry(0);
            Assert.assertNotNull("No grid geometry available", gg);

            final CoordinateReferenceSystem crs = gg.getCoordinateReferenceSystem();
            Assert.assertNotNull("No coordinate reference system available", crs);

            final TemporalCRS timeCRS = CRS.getTemporalComponent(crs);
            Assert.assertNotNull("No temporal dimension available", timeCRS);

            final Envelope envelope = gg.getEnvelope();
            Assert.assertNotNull("No envelope set", envelope);
            Assert.assertTrue("Datastore is empty : envelope should be too", new GeneralEnvelope(envelope).isEmpty());
        }
    }

    public void createWithData() throws DataStoreException, Exception {
        final GeographicCRS inputCRS = CommonCRS.SPHERE.normalizedGeographic();
        final Envelope inputEnvelope = new GeneralEnvelope(new double[]{-10, -10}, new double[]{-9, -9});
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setEnvelope(inputEnvelope);
        builder.setCoordinateReferenceSystem(inputCRS);
        final BufferedImage mockImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        builder.setRenderedImage(mockImage);
        GridCoverage cvg = builder.build();

        writeCoverage(dir.resolve("20170101.tif"), cvg);

        builder.reset();
        builder.setEnvelope(-2, -2, 1, 1);
        builder.setCoordinateReferenceSystem(inputCRS);
        builder.setRenderedImage(mockImage);
        cvg = builder.build();

        writeCoverage(dir.resolve("20170202.tif"), cvg);

        final long timestamp = toTimestamp(LocalDate.of(2017, Month.JANUARY, 1));
        final long secondTimestamp = toTimestamp(LocalDate.of(2017, Month.FEBRUARY, 2));

        DefaultCompoundCRS expectedCRS = new DefaultCompoundCRS(
                Collections.singletonMap("name", "Timed"),
                inputCRS,
                CommonCRS.Temporal.JAVA.crs()
        );

        // To avoid empty envelope, an offset of one milliseconds is added to the end of the interval.
        final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(expectedCRS);
        expectedEnvelope.setRange(0, -10, 1);
        expectedEnvelope.setRange(1, -10, 1);
        expectedEnvelope.setRange(2, timestamp, secondTimestamp + 1);
        final GridEnvelope twoSlices = new GeneralGridEnvelope(new int[]{0, 0, 0}, new int[]{16, 16, 2}, false);

        try (final TimedCoverageStore store = create()) {
            final GridCoverageReader reader = acquireReader(store);
            checkReferencing(reader.getGridGeometry(0), twoSlices, expectedEnvelope);
        }

        // We try a second time to ensure indexed data is consistent after reboot
        try (final TimedCoverageStore store = create()) {
            final GridCoverageReader reader = acquireReader(store);
            checkReferencing(reader.getGridGeometry(0), twoSlices, expectedEnvelope);
        }
    }

    @Test
    public void createEmptyThenAddData() throws Exception {
        try (final TimedCoverageStore store = create()) {

            final GeographicCRS inputCRS = CommonCRS.SPHERE.normalizedGeographic();
            final Envelope inputEnvelope = new GeneralEnvelope(new double[]{-10, -10}, new double[]{-9, -9});
            final GridCoverageBuilder builder = new GridCoverageBuilder();
            builder.setEnvelope(inputEnvelope);
            builder.setCoordinateReferenceSystem(inputCRS);
            final BufferedImage mockImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            builder.setRenderedImage(mockImage);
            GridCoverage cvg = builder.build();

            writeCoverage(dir.resolve("20170101.tif"), cvg);
            synchronized (this) {
                wait(DELAY * 4);
            }

            DefaultCompoundCRS expectedCRS = new DefaultCompoundCRS(
                    Collections.singletonMap("name", "Timed"),
                    inputCRS,
                    CommonCRS.Temporal.JAVA.crs()
            );
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(expectedCRS);
            expectedEnvelope.subEnvelope(0, 2).setEnvelope(inputEnvelope);

            // compute the timestamp corresponding to written coverage, to be able to check indexed time.
            final long timestamp = toTimestamp(LocalDate.of(2017, Month.JANUARY, 1));
            // To avoid empty envelope, an offset of one milliseconds is added to the end of the interval.
            expectedEnvelope.setRange(2, timestamp, timestamp);
            final GeneralGridEnvelope singleSlice = new GeneralGridEnvelope(new int[]{0, 0, 0}, new int[]{16, 16, 1}, false);

            final GridCoverageReader reader = acquireReader(store);
            checkReferencing(reader.getGridGeometry(0), singleSlice, expectedEnvelope);

            /* We check another image to ensure the available data grows and we can
         * select the two images separately.
             */
            builder.reset();
            builder.setEnvelope(-2, -2, 1, 1);
            builder.setCoordinateReferenceSystem(inputCRS);
            builder.setRenderedImage(mockImage);
            cvg = builder.build();

            writeCoverage(dir.resolve("20170202.tif"), cvg);
            synchronized (this) {
                wait(DELAY * 4);
            }

            // First, we check that our resource has expanded.
            expectedEnvelope.setRange(0, -10, 1);
            expectedEnvelope.setRange(1, -10, 1);
            final long secondTimestamp = toTimestamp(LocalDate.of(2017, Month.FEBRUARY, 2));

            /* As we've got two images, the envelope will be computed as if time
             * slices are thick, and so we'll do not find the inserted times,
             * but a wider envelope.
            */
            final long timeResolution = (secondTimestamp - timestamp) / 2;
            expectedEnvelope.setRange(2, timestamp - timeResolution, secondTimestamp + timeResolution);
            final GridEnvelope twoSlices = new GeneralGridEnvelope(new int[]{0, 0, 0}, new int[]{16, 16, 2}, false);
            checkReferencing(reader.getGridGeometry(0), twoSlices, expectedEnvelope);

            // Secondly, we verify we can acquire our data slice by slice.
            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setEnvelope(expectedEnvelope);
            final GridCoverage secondSlice = reader.read(0, param);
            Assert.assertTrue("Read coverage should be 2D slice of the data.", secondSlice instanceof GridCoverage2D);
            final GeneralEnvelope env2d = expectedEnvelope.subEnvelope(0, 2).clone();
            env2d.setRange(0, -2, 1);
            env2d.setRange(1, -2, 1);
            env2d.setCoordinateReferenceSystem(inputCRS);
            final GeneralGridEnvelope slice2d = new GeneralGridEnvelope(new int[]{0, 0}, new int[]{16, 16}, false);
            checkReferencing(((GridCoverage2D) secondSlice).getGridGeometry(), slice2d, env2d);
        }
    }

    private GridCoverageReader acquireReader(final TimedCoverageStore store) throws DataStoreException {
        final Resource root = store.getRootResource();
        Assert.assertNotNull("Data store resource should exist", root);
        Assert.assertTrue("Root resource is not a coverage", root instanceof CoverageResource);

        final GridCoverageReader reader = ((CoverageResource)root).acquireReader();
        Assert.assertNotNull("No reader available", reader);

        return reader;
    }

    private TimedCoverageStore create() throws DataStoreException {
        final Parameters params = Parameters.castOrWrap(TimedCoverageFactory.PARAMETERS.createValue());
        params.getOrCreate(TimedCoverageFactory.PATH).setValue(dir.toUri());
        params.getOrCreate(TimedCoverageFactory.NAME_PATTERN).setValue("([^_]+)(_.*)?\\.\\w+");
        params.getOrCreate(TimedCoverageFactory.TIME_INDEX).setValue(1);
        params.getOrCreate(TimedCoverageFactory.TIME_FORMAT).setValue("yyyyMMdd");
        params.getOrCreate(TimedCoverageFactory.DELAY).setValue(DELAY);
        return new TimedCoverageStore(params);
    }

    private static void writeCoverage(final Path destination, final GridCoverage cvg) throws Exception {
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        final TiffImageWriter imgWriter = new TiffImageWriter(new TiffImageWriter.Spi());
        try (final AutoCloseable cvgClose = () -> writer.dispose(); final AutoCloseable imgClose = () -> imgWriter.dispose()) {
            imgWriter.setOutput(destination);
            writer.setOutput(imgWriter);
            writer.write(cvg, new GridCoverageWriteParam());
        }
    }

    /**
     * Check that given Grid geometry describes the same location information that
     * input envelope and grid extent.
     *
     * Note : given envelope should be set with the CRS we expect to find in the
     * grid geometry. If not, an assertion error will be thrown.
     *
     * @param target The grid geometry to test.
     * @param expectedExtent The grid extent we want to find in the tested geometry.
     * @param expectedEnvelope The envelope we want to find in the tested geometry.
     * @throws CoverageStoreException
     */
    private static void checkReferencing(final GeneralGridGeometry target, final GridEnvelope expectedExtent, final Envelope expectedEnvelope) throws CoverageStoreException {
        CoordinateReferenceSystem targetCRS = target.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem expectedCRS = expectedEnvelope.getCoordinateReferenceSystem();
        Assert.assertTrue(
                String.format(
                        "Coordinate reference system has not been preserved at insertion.%nExpected: %s%nBut was: %s",
                        expectedCRS, targetCRS
                ),
                Utilities.equalsIgnoreMetadata(expectedCRS, targetCRS)
        );

        final GeneralEnvelope targetEnvelope = new GeneralEnvelope(target.getEnvelope());
        Assert.assertTrue(
                String.format(
                        "Datastore envelope is invalid.%nExpected: %s%nBut was: %s",
                        expectedEnvelope, targetEnvelope
                ),
                targetEnvelope.equals(expectedEnvelope, 1e-7, false)
        );

        GridEnvelope targetExtent = target.getExtent();
        Assert.assertEquals("Data grid extent has not been preserved.", expectedExtent, targetExtent);
    }

    private static long toTimestamp(final LocalDate date) {
        return date
                .atStartOfDay(ZoneId.of("Z"))
                .toInstant()
                .toEpochMilli();
    }
}
