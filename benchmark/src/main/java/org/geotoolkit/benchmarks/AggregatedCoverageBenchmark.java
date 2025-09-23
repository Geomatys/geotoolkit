package org.geotoolkit.benchmarks;

import java.awt.Dimension;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import static org.apache.sis.coverage.grid.PixelInCell.CELL_CENTER;
import static org.apache.sis.coverage.grid.PixelInCell.CELL_CORNER;

@Warmup(iterations = 2, time = 2)
@Fork(value = 2)
public class AggregatedCoverageBenchmark {

    private static final Object[] ASSERTION = { new Object() };
    private static final GridExtent OVERALL_EXTENT = new GridExtent(256, 256);
    private static final AffineTransform2D BASE_EXTENT = new AffineTransform2D(0.1, 0, 0, 0.1, 0, 0);
    private static final GridGeometry OVERALL_GRID = new GridGeometry(OVERALL_EXTENT, CELL_CENTER, BASE_EXTENT, CommonCRS.defaultGeographic());
    private static final List<SampleDimension> BASE_SAMPLES = Collections.singletonList(new SampleDimension.Builder().setName("data").build());

    public static abstract class Dataset {

        public AggregatedCoverageResource aggregation;
        public GridGeometry domain;
        public Dimension expectedDimension;

    }

    @State(Scope.Benchmark)
    public static class HomogeneousDataset extends Dataset {

        @Setup
        public void init() throws DataStoreException, TransformException {
            final GridGeometry g1 = subgrid(OVERALL_GRID, new GridExtent(64, 64).translate(32, 32));
            final GridGeometry g2 = subgrid(OVERALL_GRID, new GridExtent(128, 128).translate(64, 64));
            final GridGeometry g3 = subgrid(OVERALL_GRID, new GridExtent(64, 64).translate(64, 128));
            aggregation = new AggregatedCoverageResource();
            aggregation.add(new InMemoryGridCoverageResource(new BufferedGridCoverage(g1, BASE_SAMPLES, DataBuffer.TYPE_SHORT)));
            aggregation.add(new InMemoryGridCoverageResource(new BufferedGridCoverage(g2, BASE_SAMPLES, DataBuffer.TYPE_SHORT)));
            aggregation.add(new InMemoryGridCoverageResource(new BufferedGridCoverage(g3, BASE_SAMPLES, DataBuffer.TYPE_SHORT)));
            domain = OVERALL_GRID;
            expectedDimension = new Dimension(Math.toIntExact(OVERALL_EXTENT.getSize(0)), Math.toIntExact(OVERALL_EXTENT.getSize(1)));
        }
    }

    @State(Scope.Benchmark)
    public static class HeterogeneousDataset extends Dataset {
        @Param(value = {"3", "5"})
        int bufferType;

        @Setup
        public void init() throws DataStoreException, TransformException {
            final GridGeometry g1 = subgrid(OVERALL_GRID, new GridExtent(64, 64).translate(32, 32));
            final GridGeometry g2 = subgrid(OVERALL_GRID, new GridExtent(128, 128).translate(64, 64));
            final GridGeometry g3 = subgrid(OVERALL_GRID, new GridExtent(64, 64).translate(64, 128));
            final AggregatedCoverageResource.VirtualBand vb = new AggregatedCoverageResource.VirtualBand();
            final SampleDimension.Builder sdb = new SampleDimension.Builder();
            final List<SampleDimension> sampleDimensions = Arrays.asList(sdb.setName(0).build(), sdb.setName(1).build());
            GridCoverageResource s1 = new InMemoryGridCoverageResource(new BufferedGridCoverage(g1, sampleDimensions, DataBuffer.TYPE_SHORT));
            GridCoverageResource s2 = new InMemoryGridCoverageResource(new BufferedGridCoverage(g2, sampleDimensions, DataBuffer.TYPE_SHORT));
            GridCoverageResource s3 = new InMemoryGridCoverageResource(new BufferedGridCoverage(g3, sampleDimensions, DataBuffer.TYPE_SHORT));
            vb.setSources(
                    new AggregatedCoverageResource.Source(s1, 0),
                    new AggregatedCoverageResource.Source(s2, 0),
                    new AggregatedCoverageResource.Source(s3, 0)
            );
            aggregation = new AggregatedCoverageResource(Collections.singletonList(vb), AggregatedCoverageResource.Mode.ORDER, CommonCRS.defaultGeographic());

            aggregation.setDataType(bufferType);
            domain = OVERALL_GRID;
            expectedDimension = new Dimension(Math.toIntExact(OVERALL_EXTENT.getSize(0)), Math.toIntExact(OVERALL_EXTENT.getSize(1)));
        }
    }

    @Benchmark
    public void homogeneous(HomogeneousDataset input) throws DataStoreException {
        render(input);
    }

    @Benchmark
    public void heterogeneous(HeterogeneousDataset input) throws DataStoreException {
        render(input);
    }

    private void render(Dataset input) throws DataStoreException {
        final RenderedImage rendering = input.aggregation.read(input.domain).render(null);
        /*
         * Trick: branchless optimisation : instead of checking size with if statement, we simply expect size
         * substraction to be zero, and if not, an error wil be raised.
         * We're trying to impact benchmarck least possible, but still we'd like to check output.
         */
        final int idx = rendering.getWidth() - input.expectedDimension.width + rendering.getHeight() - input.expectedDimension.height;
        final Object assertion = ASSERTION[idx];
    }

    private static GridGeometry subgrid(GridGeometry source, GridExtent roi) throws TransformException {
        final MathTransform gridToCRS = source.getGridToCRS(CELL_CORNER);
        final GeneralEnvelope env = roi.toEnvelope(gridToCRS);
        return source.derive().subgrid(env).build();
    }
}
