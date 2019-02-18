/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessListenerAdapter;
import org.geotoolkit.processing.science.drift.DriftPredictionDescriptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.GenericName;
import org.opengis.util.NoSuchIdentifierException;

import static org.opengis.metadata.spatial.DimensionNameType.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class PredictorTest {

    static final CoordinateReferenceSystem DATA_CRS = new DefaultCompoundCRS(
            Collections.singletonMap("name", "utm0+seconds"),
            CommonCRS.defaultGeographic(),
            CommonCRS.Temporal.UNIX.crs()
    );

    static final GridExtent DATA_GRID = new GridExtent(new DimensionNameType[]{COLUMN, ROW, TIME}, new long[3], new long[]{16, 16, 8}, false);
    static final MathTransform DATA_GRID2CRS = MathTransforms.compound(
            new AffineTransform2D(360d/DATA_GRID.getSize(0), 0, 0, -180d/DATA_GRID.getSize(1), -180, 90),
            MathTransforms.linear(2, 0)
    );

    static final GridGeometry DATA_GEOM = new GridGeometry(DATA_GRID, PixelInCell.CELL_CORNER, DATA_GRID2CRS, DATA_CRS);

    static final PredictorDescriptor DESCRIPTOR = new PredictorDescriptor(GeotkProcessingRegistry.IDENTIFICATION);

    static BufferedImage MOCK_IMAGE;

    static List<SampleDimension> MOCK_DIMENSIONS;

    @BeforeClass
    public static void setupImage() {
        MOCK_IMAGE = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        ImageUtilities.fill(MOCK_IMAGE, 2); // This will cause constant north-east movement.
    }

    @BeforeClass
    public static void setupSampleDimensions() {
        final SampleDimension.Builder builder = new SampleDimension.Builder();
        MOCK_DIMENSIONS = Arrays.asList(
                builder.setName("u")
                        .addQuantitative("speed", -10, 10, Units.METRES_PER_SECOND)
                        .build(),
                builder.setName("v").build(),
                builder.setName("Not used").build()
        );
    }

    @Test
    public void discovery() throws NoSuchIdentifierException {
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                GeotkProcessingRegistry.NAME, PredictorDescriptor.NAME
        );
        Assert.assertNotNull("Drift prediction descriptor", desc);
        Assert.assertTrue("Unexpected descriptor instance", desc instanceof PredictorDescriptor);
    }

    @Test
    public void sampleDrift() throws Exception {
        final Parameters input = Parameters.castOrWrap(DESCRIPTOR.getInputDescriptor().createValue());
        input.getOrCreate(PredictorDescriptor.MAX_POINTS).setValue(200);
        addWeight(input, 0.5f, 0.5f, 0.5f);
        addWeight(input, 0.2f, 0.8f, 0.8f);
        //input.getOrCreate(PredictorDescriptor.TARGET_RESOLUTION).setValue(1);
        input.getOrCreate(PredictorDescriptor.TARGET_WIDTH).setValue(256);
        input.getOrCreate(PredictorDescriptor.TARGET_HEIGHT).setValue(256);
        input.getOrCreate(PredictorDescriptor.TIMESTEP).setValue(1);
        input.getOrCreate(DriftPredictionDescriptor.START_TIMESTAMP).setValue(0);
        final long expectedEndTime = 7000;
        input.getOrCreate(DriftPredictionDescriptor.END_TIMESTAMP).setValue(expectedEndTime);
        input.getOrCreate(DriftPredictionDescriptor.START_POINT)
                .setValue(new DirectPosition2D(CommonCRS.defaultGeographic(), 0.1, 0.2));
        input.getOrCreate(PredictorDescriptor.WIND_RESOURCE).setValue(new MockCoverageResource("wind"));
        input.getOrCreate(PredictorDescriptor.CURRENT_RESOURCE).setValue(new MockCoverageResource("current"));

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

        final long outTime = output.getMandatoryValue(DriftPredictionDescriptor.ACTUAL_END_TIMESTAMP);
        final Path netcdf = output.getMandatoryValue(DriftPredictionDescriptor.OUTPUT_DATA);

        Assert.assertEquals("Expected time of ending", expectedEndTime, outTime);
        Assert.assertNotNull("Output file path", netcdf);
        Assert.assertTrue("Output file is not readable", Files.isRegularFile(netcdf));

        // TODO : check output value
    }

    private static void addWeight(final ParameterValueGroup processInput, final float wind, final float current, final float proba) {
        Parameters group = Parameters.castOrWrap(
                processInput.addGroup(PredictorDescriptor.WEIGHTS.getName().getCode())
        );
        group.getOrCreate(PredictorDescriptor.WIND_WEIGHT).setValue(wind);
        group.getOrCreate(PredictorDescriptor.CURRENT_WEIGHT).setValue(current);
        group.getOrCreate(PredictorDescriptor.WEIGHT_PROBABILITY).setValue(proba);
    }

    private static class MockCoverageResource implements GridCoverageResource {

        final GenericName name;

        MockCoverageResource(String name) {
            this.name = Names.createLocalName(null, ":", name);
        }

        @Override
        public GridGeometry getGridGeometry() throws DataStoreException {
            return DATA_GEOM;
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException {
            return MOCK_DIMENSIONS;
        }

        @Override
        public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
            final List<SampleDimension> samples;
            final List<SampleDimension> brutSamples = getSampleDimensions();
            final boolean noBandSelection = range == null || range.length < 1;
            if (noBandSelection) {
                samples = brutSamples;
            } else {
                samples = IntStream.of(range)
                        .mapToObj(brutSamples::get)
                        .collect(Collectors.toList());
            }

            return new GridCoverage(domain, samples) {
                @Override
                public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
                    return MOCK_IMAGE; // TODO: band selection
                }
            };
        }

        @Override
        public Envelope getEnvelope() throws DataStoreException {
            return DATA_GEOM.getEnvelope();
        }

        @Override
        public GenericName getIdentifier() throws DataStoreException {
            return name;
        }

        @Override
        public Metadata getMetadata() throws DataStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
