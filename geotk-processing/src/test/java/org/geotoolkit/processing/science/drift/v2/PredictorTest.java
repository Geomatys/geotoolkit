/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.image.DataBufferInt;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.storage.base.MemoryGridResource;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessListenerAdapter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.NoSuchIdentifierException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.opengis.metadata.spatial.DimensionNameType.COLUMN;
import static org.opengis.metadata.spatial.DimensionNameType.ROW;
import static org.opengis.metadata.spatial.DimensionNameType.TIME;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class PredictorTest {

    private static final PredictorDescriptor DESCRIPTOR = new PredictorDescriptor(GeotkProcessingRegistry.IDENTIFICATION);

    private static GridCoverage MOCK_UV_DATA;

    @BeforeClass
    public static void setup() throws Exception {
        MOCK_UV_DATA = createMockUVData();
    }

    /**
     * Create a test data that represents UV data source.
     * <ul>
     *  <li>The dataset has 2 bands:
     *      <ol>
     *          <li>U: eastward velocity component (m/s)</li>
     *          <li>V: northward velocity component (m/s)</li>
     *     </ol>
     *  </li>
     *  <li>All values are equal to 2, which simulates a constant move to the north-east.</li>
     *  <li>Output data axes are, in order: longitude, latitude, time</li>
     *  <li>Temporal CRS is seconds since UTC epoch</li>
     *  <li>Created dataset contains 8 temporal slices</li>
     * </ul>
     */
    private static GridCoverage createMockUVData() throws Exception {
        final CoordinateReferenceSystem dataCrs = CRS.compound(
                CommonCRS.defaultGeographic(),
                CommonCRS.Temporal.UNIX.crs()
        );
        final GridExtent dataGrid = new GridExtent(new DimensionNameType[]{COLUMN, ROW, TIME}, new long[3], new long[]{16, 16, 8}, false);
        final double scaleX = 360d / dataGrid.getSize(0);
        final double scaleY = -180d / dataGrid.getSize(1);
        final MathTransform dataGrid2Crs = MathTransforms.compound(
                new AffineTransform2D(scaleX, 0, 0, scaleY, -180+scaleX/2d, 90+scaleY/2d),
                MathTransforms.linear(2, 0)
        );
        final GridGeometry mockGeom = new GridGeometry(dataGrid, PixelInCell.CELL_CENTER, dataGrid2Crs, dataCrs);

        final SampleDimension.Builder builder = new SampleDimension.Builder();
        final List<SampleDimension> mockSamples = Arrays.asList(
                builder.setName("u")
                        .addQuantitative("speed", -10, 10, Units.METRES_PER_SECOND)
                        .build(),
                builder.setName("v").build()
        );

        int[] values = new int[8 * 16 * 16 * 2]; // 8 time slices, 16 rows, 16 columns, 2 bands
        Arrays.fill(values, 2);
        return new BufferedGridCoverage(mockGeom, mockSamples, new DataBufferInt(values, values.length));
    }

    @Test
    public void discovery() throws NoSuchIdentifierException {
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                GeotkProcessingRegistry.NAME, PredictorDescriptor.NAME
        );
        assertNotNull("Drift prediction descriptor", desc);
        assertTrue("Unexpected descriptor instance", desc instanceof PredictorDescriptor);
    }

    @Test
    public void sampleDrift() throws Exception {
        final Parameters input = Parameters.castOrWrap(DESCRIPTOR.getInputDescriptor().createValue());
        input.getOrCreate(PredictorDescriptor.MAX_POINTS).setValue(200);
        addWeight(input, 0.5f, 0.5f, 0.5f);
        addWeight(input, 0.2f, 0.8f, 0.8f);
        input.getOrCreate(PredictorDescriptor.TARGET_RESOLUTION).setValue(1);
        input.getOrCreate(PredictorDescriptor.TARGET_WIDTH).setValue(256);
        input.getOrCreate(PredictorDescriptor.TARGET_HEIGHT).setValue(256);
        input.getOrCreate(PredictorDescriptor.TIMESTEP).setValue(1);
        input.getOrCreate(PredictorDescriptor.START_TIMESTAMP).setValue(0);
        final long expectedEndTime = 7000;
        input.getOrCreate(PredictorDescriptor.END_TIMESTAMP).setValue(expectedEndTime);
        input.getOrCreate(PredictorDescriptor.START_POINT)
                .setValue(new DirectPosition2D(CommonCRS.defaultGeographic(), 0.1, 0.2));
        input.getOrCreate(PredictorDescriptor.WIND_RESOURCE).setValue(new MemoryGridResource(null, null, MOCK_UV_DATA, null));
        input.getOrCreate(PredictorDescriptor.CURRENT_RESOURCE).setValue(new MemoryGridResource(null, null, MOCK_UV_DATA, null));

        final Predictor predictor = new Predictor(DESCRIPTOR, input);
        predictor.addListener(new ProcessListenerAdapter() {
            @Override
            public void progressing(ProcessEvent event) {
                final Exception error = event.getException();
                if (error != null) {
                    Utilities.LOGGER.log(Level.WARNING, event.getTask().toString(), error);
                } else {
                    Utilities.LOGGER.log(Level.INFO, event.getTask().toString());
                }
            }
        });

        Utilities.LOGGER.info("Starting drift processing");
        final Parameters output = CompletableFuture.supplyAsync(() -> {
            try {
                return predictor.call();
            } catch (ProcessException ex) {
                throw new RuntimeException(ex);
            }
        })
                .thenApply(Parameters::castOrWrap)
                .get(30, TimeUnit.SECONDS);

        final long outTime = output.getMandatoryValue(PredictorDescriptor.ACTUAL_END_TIMESTAMP);
        final Path netcdf = output.getMandatoryValue(PredictorDescriptor.OUTPUT_DATA);

        try {
            assertEquals("Expected time of ending", expectedEndTime, outTime);
            assertNotNull("Output file path", netcdf);
            assertTrue("Output file is not readable", Files.isRegularFile(netcdf));

            checkContent(netcdf);
        } finally {
            Files.delete(netcdf);
        }
    }

    private void checkContent(final Path netcdf) throws DataStoreException {
        final StorageConnector connector = new StorageConnector(netcdf);
        try (final DataStore myStore = DataStores.open(connector)) {
            final Map<String, GridCoverageResource> resources = org.geotoolkit.storage.DataStores.flatten(myStore, true, GridCoverageResource.class)
                    .stream()
                    .collect(Collectors.toMap(
                            r -> { try {
                                return r.getIdentifier()
                                        .map(name -> name.tip().toString())
                                        .orElseThrow(() -> new AssertionError("No identifier available"));
                            } catch (Exception e) {
                                throw new AssertionError("Resource identifier not accessible", e);
                            } }, Function.identity()));
            assertEquals("Number of coverage resources", 2, resources.size());
            final GridCoverageResource prob_per_day = resources.get("prob_per_day");
            final GridGeometry gg = prob_per_day.getGridGeometry();
            assertNotNull(CRS.getTemporalComponent(gg.getCoordinateReferenceSystem()));
            // Sources cause a constant move at 2 meters per second. With a resolution at 1 meter over 8 seconds.
            // Even with random noise, weights and probabilities, we should at least have an image 10 pixel wide.
            assertTrue(gg.getExtent().getSize(0) >= 10);
            assertTrue(gg.getExtent().getSize(1) >= 10);
            assertEquals("Temporal dimension: one day expected", 1, gg.getExtent().getSize(2));
        }
    }

    private static void addWeight(final ParameterValueGroup processInput, final float wind, final float current, final float proba) {
        Parameters group = Parameters.castOrWrap(
                processInput.addGroup(PredictorDescriptor.WEIGHTS.getName().getCode())
        );
        group.getOrCreate(PredictorDescriptor.WIND_WEIGHT).setValue(wind);
        group.getOrCreate(PredictorDescriptor.CURRENT_WEIGHT).setValue(current);
        group.getOrCreate(PredictorDescriptor.WEIGHT_PROBABILITY).setValue(proba);
    }
}
