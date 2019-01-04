package org.geotoolkit.processing.coverage.categorize;

import java.awt.image.RenderedImage;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.DismissProcessException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.processing.image.sampleclassifier.SampleClassifier;
import org.geotoolkit.processing.image.sampleclassifier.SampleClassifierDescriptor;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class Categorize extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.processing");
    private static final SampleClassifierDescriptor IMG_CAT_DESC = new SampleClassifierDescriptor();

    public Categorize() {
        super(new CategorizeDescriptor());
        inputParameters = Parameters.castOrWrap(CategorizeDescriptor.INPUT.createValue());
    }

    public Categorize(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final GridCoverageResource source = getSource();
        final GridCoverageResource destination = getDestination();

        final GridCoverageReader reader;
        try {
            reader = source.acquireReader();
        } catch (CoverageStoreException ex) {
            throw new ProcessException("Cannot access data source", this, ex);
        }

        final GridCoverageWriter writer;
        try {
            writer = destination.acquireWriter();
        } catch (CoverageStoreException ex) {
            throw new ProcessException("Cannot access data output", this, ex);
        }

        try (final UncheckedCloseable inClose = () -> source.recycle(reader);
                final UncheckedCloseable outClose = () -> destination.recycle(writer)) {
            final GeneralGridGeometry inputGG = reader.getGridGeometry(source.getImageIndex());

            final GeneralGridGeometry readGeom;
            Envelope env = getEnvelope();
            if (env == null) {
                env = inputGG.getEnvelope();
                readGeom = inputGG;
            } else {
                MathTransform gridToCRS = inputGG.getGridToCRS(PixelInCell.CELL_CORNER);
                GeographicBoundingBox bbox = null;
                try {
                    bbox = ReferencingUtilities.findGeographicBBox(source).orElse(null);
                } catch (DataStoreException e) {
                    /* This error is not directly related to data. It could be
                     * caused by malformed metadata. In which case, we just
                     * ignore it.
                     */
                    LOGGER.log(Level.FINE, "Cannot deduce geographic extent from metadata.", e);
                }

                if (env.getCoordinateReferenceSystem() != null) {
                    final CoordinateOperation op = CRS.findOperation(
                            inputGG.getCoordinateReferenceSystem(),
                            env.getCoordinateReferenceSystem(),
                            bbox
                    );

                    gridToCRS = MathTransforms.concatenate(gridToCRS, op.getMathTransform());
                    // Crop area of interest on source coverage area
                    final GeneralEnvelope sourceEnv;
                    try {
                        sourceEnv = Envelopes.transform(op, inputGG.getEnvelope());
                    } catch (TransformException ex) {
                        throw new ProcessException("Cannot check input envelope validity against source coverage.", this, ex);
                    }
                    sourceEnv.intersect(env);
                    env = sourceEnv;
                } else {
                    final GeneralEnvelope tmpEnv = new GeneralEnvelope(env);
                    tmpEnv.setCoordinateReferenceSystem(inputGG.getCoordinateReferenceSystem());
                    // Crop area of interest on source coverage area
                    tmpEnv.intersect(inputGG.getEnvelope());
                    env = tmpEnv;
                }

                readGeom = new GeneralGridGeometry(PixelInCell.CELL_CORNER, gridToCRS, env);
            }

            final GridGeometryIterator it = new GridGeometryIterator(readGeom);
            while (it.hasNext()) {
                final GridCoverageReadParam readParam = new GridCoverageReadParam();
                final GeneralGridGeometry sliceGeom = it.next();
                final GeneralEnvelope expectedSliceEnvelope = GeneralEnvelope.castOrCopy(sliceGeom.getEnvelope());
                readParam.setEnvelope(expectedSliceEnvelope);
                GridCoverage sourceCvg = reader.read(source.getImageIndex(), readParam);
                if (sourceCvg instanceof CoverageStack) {
                    // Try to unravel expected slice
                    final Optional<GridCoverage2D> slice = extractSlice((CoverageStack) sourceCvg, sliceGeom.getEnvelope());
                    if (slice.isPresent()) {
                        sourceCvg = slice.get();
                    }
                }

                if (!(sourceCvg instanceof GridCoverage2D)) {
                    throw new ProcessException("Cannot extract 2D slice from given data source", this);
                }

                // If the reader has not returned a coverage fitting queried
                // geometry, we have to resample input ourselves.
                GridCoverage2D source2D = (GridCoverage2D) sourceCvg;
                source2D = source2D.view(ViewType.GEOPHYSICS);
                final boolean compliantCrs = Utilities.equalsApproximatively(expectedSliceEnvelope.getCoordinateReferenceSystem(), source2D.getCoordinateReferenceSystem());
                final boolean compliantEnvelope = expectedSliceEnvelope.contains(source2D.getEnvelope(), true);
                if (!(compliantCrs && compliantEnvelope)) {
                    source2D = resample(source2D, sliceGeom);
                }

                final RenderedImage slice = categorize(source2D.getRenderedImage());

                final GridCoverageBuilder builder = new GridCoverageBuilder();
                builder.setSources(sourceCvg);
                builder.setGridGeometry(source2D.getGridGeometry());
                builder.setRenderedImage(slice);

                final GridCoverage resultCoverage = builder.build();

                final GridCoverageWriteParam writeParam = new GridCoverageWriteParam();
                writer.write(resultCoverage, writeParam);
            }
        } catch (TransformException ex) {
            throw new ProcessException("Cannot adapt input geometry", this, ex);
        } catch (FactoryException ex) {
            throw new ProcessException("Failure on EPSG database use", this, ex);
        } catch (CoverageStoreException ex) {
            throw new ProcessException("Cannot access either input or output data source", this, ex);
        } catch (CancellationException ex) {
            throw new DismissProcessException("Process cancelled", this, ex);
        }
    }

    /**
     * Try to find a 2D coverage matching input envelope in the given stack.
     * @param source A coverage stack to search for 2D slice into.
     * @param aoi The envelope of the 2D coverage to find.
     * @return If we find a 2D data contained in the given envelope, we return it.
     * Otherwise, we return nothing.
     */
    private static Optional<GridCoverage2D> extractSlice(final CoverageStack source, final Envelope aoi) {
        int stackSize = source.getStackSize();
        for (int i = 0; i < stackSize; i++) {
            final Coverage cvg = source.coverageAtIndex(i);
            final GeneralEnvelope subsetEnvelope = GeneralEnvelope.castOrCopy(cvg.getEnvelope());
            if (subsetEnvelope.contains(aoi, true)) {
                if (cvg instanceof GridCoverage2D) {
                    return Optional.of((GridCoverage2D) cvg);
                } else if (cvg instanceof CoverageStack) {
                    return extractSlice((CoverageStack) cvg, aoi);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Apply classification as parameterized in this process input on a single
     * image.
     * @param input The image to classify (lookup algorithm).
     * @return Classified image. It's a copy of the original one.
     * @throws ProcessException If an error happens while classifying.
     */
    private RenderedImage categorize(final RenderedImage input) throws ProcessException {
        final ParameterValueGroup values = IMG_CAT_DESC.getInputDescriptor().createValue();
        values.parameter(SampleClassifierDescriptor.IMAGE.getName().getCode()).setValue(input);
        values.parameter(SampleClassifierDescriptor.FILL_VALUE.getName().getCode()).setValue(getFillValue());
        final String catParamCode = SampleClassifierDescriptor.CATEGORIES.getName().getCode();
        inputParameters.groups(catParamCode).stream()
                .forEach(category -> Parameters.copy(category, values.addGroup(catParamCode)));

        final SampleClassifier classifier = new SampleClassifier(IMG_CAT_DESC, values);
        final Parameters result = Parameters.castOrWrap(classifier.call());
        return result.getMandatoryValue(SampleClassifierDescriptor.IMAGE);
    }

    /**
     * @implNote: The grid geometry of the returned coverage might not be the
     * same as the given one. Its grid envelope and grid to crs transform might
     * be differents. However, the envelope and resolution should match.
     * @param source The coverage to crop/subsample.
     * @param target Definition of the output data space.
     * @return The resampled data, never null.
     * @throws ProcessException
     */
    private GridCoverage2D resample(final GridCoverage2D source, final GeneralGridGeometry target) throws ProcessException, TransformException {
        if (!(target instanceof GridGeometry2D)) {
            throw new ProcessException("Subset cannot be done. Incompatible grid geometry.", this);
        }

        GridGeometry2D resampleGeometry = hack((GridGeometry2D) target);

        final ResampleProcess resample = new ResampleProcess(
                source,
                resampleGeometry.getCoordinateReferenceSystem(),
                resampleGeometry,
                InterpolationCase.NEIGHBOR,
                new double[]{getFillValue()}
        );

        return resample.executeNow();
    }

    public GridCoverageResource getSource() {
        return inputParameters.getMandatoryValue(CategorizeDescriptor.IN_COVERAGE);
    }

    public void setSource(final GridCoverageResource source) {
        inputParameters.getOrCreate(CategorizeDescriptor.IN_COVERAGE).setValue(source);
    }

    /**
     * Create a new category and add it to the categories to use for classification.
     * No collision test is done.
     *
     * @param inclusiveMin The lower boundary for category range. Inclusive.
     * @param exclusiveMax The upper boundary for category range. Exclusive.
     * @param classValue The class to associate to the created category.
     */
    public void addInterval(final float inclusiveMin, final float exclusiveMax, final byte classValue) {
        SampleClassifier.addInterval(inputParameters, inclusiveMin, exclusiveMax, classValue);
    }

    public GridCoverageResource getDestination() {
        return inputParameters.getMandatoryValue(CategorizeDescriptor.OUT_COVERAGE);
    }

    public void setDestination(final GridCoverageResource destination) {
        inputParameters.getOrCreate(CategorizeDescriptor.OUT_COVERAGE).setValue(destination);
    }

    public Byte getFillValue() {
        return inputParameters.getValue(SampleClassifierDescriptor.FILL_VALUE);
    }

    /**
     * Specify the value to use as a fallback for samples which does not fit in
     * any category.
     * @param fillValue The value to use as a fallback.
     */
    public void setFillValue(final byte fillValue) {
        inputParameters.getOrCreate(SampleClassifierDescriptor.FILL_VALUE).setValue(fillValue);
    }

    public Envelope getEnvelope() {
        return inputParameters.getValue(CategorizeDescriptor.ENVELOPE);
    }

    public void setEnvelope(final Envelope roi) {
        inputParameters.getOrCreate(CategorizeDescriptor.ENVELOPE).setValue(roi);
    }

    private static interface UncheckedCloseable extends AutoCloseable {

        @Override
        public void close();
    }

    /**
     * Apply dirty hacks needed to get a geometry usable for resampling. Two
     * problems are solved here :
     * <ul>
     * <li>Erase envelope, to ensure dimension coordinates (temporal, vertical)
     * are slices rightly centered. This is necessary due to the unbalanced
     * behavior induced by non-linear transforms on interpolation.</li>
     * <li>Ensures that geometry extent starts at 0 for horizontal components (ImageIO limitation).</li>
     * </ul>
     * @param source The grid geometry to adapt.
     * @return A new geometry, derived from source one.
     * @throws TransformException If there"s a problem while modiying data
     * envelope.
     */
    private static GridGeometry2D hack(final GridGeometry2D source) throws TransformException {
        final GridExtent sourceExtent = source.getExtent();
        final long minX = sourceExtent.getLow(source.gridDimensionX);
        final long minY = sourceExtent.getLow(source.gridDimensionY);
        if (minX == 0 && minY == 0 && source.getDimension() < 3) {
            return source;
        }

        /* Resampling does not support target geometry using non-zero grid origin.
         * This is due to an imageio limitation, which forbids building a BufferedImage
         * whose raster origin is not (0, 0). To bypass this problem, we simply
         * shift source extent, so its horizontal dimensions starts at 0.
         */
        final long[] low = GridGeometryIterator.getLow(sourceExtent);
        final long[] high = GridGeometryIterator.getHigh(sourceExtent);
        low[source.gridDimensionX] = 0;
        high[source.gridDimensionX] = high[source.gridDimensionX] - minX;
        low[source.gridDimensionY] = 0;
        high[source.gridDimensionY] = high[source.gridDimensionY] - minY;
        final GridExtent modifiedExtent = new GridExtent(null, low, high, true);

        /* Force empty span on dimensions. To be sure the result is correct,
         * coordinates must be reprojected from pixel centers. If not, shifts
         * will appear for non-linear dimensions (case for most Grib/NetCDF data).
         */
        final GeneralEnvelope flattenEnvelope;
        if (source.getDimension() > 2) {
            final GridExtent inExtent = source.getExtent();
            final GeneralEnvelope inGridEnv = new GeneralEnvelope(source.getDimension());
            for (int i = 0; i < source.getDimension(); i++) {
                if (inExtent.getSize(i) > 1 || i == source.gridDimensionX || i == source.gridDimensionY) {
                    inGridEnv.setRange(i, inExtent.getLow(i) - 0.5, inExtent.getHigh(i) + 0.5);
                } else {
                    inGridEnv.setRange(i, inExtent.getLow(i), inExtent.getHigh(i));
                }
            }

            flattenEnvelope = Envelopes.transform(source.getGridToCRS(PixelInCell.CELL_CENTER), inGridEnv);
            flattenEnvelope.setCoordinateReferenceSystem(source.getCoordinateReferenceSystem());
        } else {
            flattenEnvelope = new GeneralEnvelope(source.getEnvelope());
        }

        return new GridGeometry2D(modifiedExtent, flattenEnvelope);
    }
}
