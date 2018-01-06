package org.geotoolkit.processing.image.sampleclassifier;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessDescriptor;

/**
 * Processing class for sample categorization. For more information about the
 * logic of this process, see {@link SampleClassifierDescriptor}.
 *
 * @author Alexis Manin (Geomatys)
 */
public class SampleClassifier extends AbstractProcess {

    public SampleClassifier() {
        super(new SampleClassifierDescriptor());
        inputParameters = Parameters.castOrWrap(SampleClassifierDescriptor.INPUT.createValue());
    }

    public SampleClassifier(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final RenderedImage input = getImage();
        if (input.getSampleModel().getNumBands() > 1) {
            throw new ProcessException("Input image must be single banded.", this);
        }

        final ClassMap baseMapping = buildClasses();

        final BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        final int minTileX = input.getMinTileX();
        final int minTileY = input.getMinTileY();
        final int numTiles = input.getNumXTiles() * input.getNumYTiles();
        final Point firstTile = new Point(minTileX, minTileY);
        if (numTiles < 2) {
            processTile(firstTile, input, output, baseMapping);
        } else {
            final int maxXTile = input.getNumXTiles() + minTileX - 1;
            final UnaryOperator<Point> nextTile = tile -> {
                final boolean nextLine = tile.x >= maxXTile;
                final int nextX = nextLine ? minTileX : tile.x + 1;
                final int nextY = nextLine ? tile.y+1 : tile.y;
                return new Point(nextX, nextY);
            };
            Stream.iterate(firstTile, nextTile)
                    .limit(numTiles)
                    .parallel()
                    .forEach(tile -> SampleClassifier.processTile(tile, input, output, baseMapping.clone()));
        }

        outputParameters.getOrCreate(SampleClassifierDescriptor.IMAGE).setValue(output);
    }

    private static void processTile(final Point tile, final RenderedImage input, final WritableRenderedImage output, final ClassMap mapping) {
        final Raster tileImage = input.getTile(tile.x, tile.y);
        // TODO : try to adapt iteration algorithm and try optimize it.
        // The aim would be to maximize lookalike value mapping optimisation done in ClassMap object.
        // TODO : Force iteration order to ensure consistency of iteration on both input and output.
        final PixelIterator.Builder pixBuilder = new PixelIterator.Builder();

        final PixelIterator tileIt = pixBuilder.create(tileImage);
        final Rectangle roi = new Rectangle(
                input.getTileGridXOffset() + tile.x * input.getTileWidth(),
                input.getTileGridYOffset() + tile.y * input.getTileHeight(),
                tileImage.getWidth(),
                tileImage.getHeight()
        );

        // TODO : replace with SIS writable iterator once available
        WritablePixelIterator outIt = pixBuilder
                .setRegionOfInterest(roi)
                .createWritable(output);

        while (tileIt.next() && outIt.next()) {
            final float sample = tileIt.getSampleFloat(0);
            final byte sampleClass = mapping.getClass(sample);
            outIt.setSample(0, sampleClass);
        }
    }

    /**
     *
     * @return The image currently configured as input for classification.
     */
    public RenderedImage getImage() {
        return inputParameters.getMandatoryValue(SampleClassifierDescriptor.IMAGE);
    }

    /**
     * Specify the image to classify.
     * @param input
     */
    public void setImage(final RenderedImage input) {
        inputParameters.getOrCreate(SampleClassifierDescriptor.IMAGE).setValue(input);
    }

//    public int getBand() {
//        return inputParameters.intValue(SampleClassifierDescriptor.BAND);
//    }
//
//    public void setBand(int bandIdx) {
//        inputParameters.getOrCreate(SampleClassifierDescriptor.BAND).setValue(bandIdx);
//    }

    private ClassMap buildClasses() throws ProcessException {
        final List<ParameterValueGroup> classes = inputParameters.groups(SampleClassifierDescriptor.CATEGORIES.getName().getCode());
        if (classes == null || classes.isEmpty()) {
            throw new ProcessException("No class provided", this);
        }

        final byte fillValue = getFillValue();

        final List<Map.Entry<NumberRange<Float>, Byte>> sortedClasses = classes.stream()
                .map(Parameters::castOrWrap)
                .map(p -> {
                    final NumberRange<Float> nr = new NumberRange<>(
                            Float.class,
                            p.getMandatoryValue(SampleClassifierDescriptor.MIN),
                            true,
                            p.getMandatoryValue(SampleClassifierDescriptor.MAX),
                            false
                    );

                    final Byte classValue = p.getMandatoryValue(SampleClassifierDescriptor.CLASS_VALUE);

                    return new AbstractMap.SimpleImmutableEntry<>(nr, classValue);
                })
                .sorted((o1, o2)
                        -> o1.getKey().getMinValue().compareTo(o2.getKey().getMinValue())
                )
                .collect(Collectors.toList());

        final List<Float> intervals = new ArrayList<>();
        final List<Byte> classValues = new ArrayList<>();
        float previousMax = Float.NEGATIVE_INFINITY;
        for (final Map.Entry<NumberRange<Float>, Byte> entry : sortedClasses) {
            final float minVal = entry.getKey().getMinValue();
            if (minVal > previousMax) {
                // Disjoint intervals. We fill the hole with a no-data category.
                intervals.add(previousMax);
                classValues.add(fillValue);
            }

            intervals.add(minVal);
            classValues.add(entry.getValue());

            previousMax = entry.getKey().getMaxValue();
        }

        intervals.add(previousMax);

        if (!Float.isInfinite(previousMax)) {
            intervals.add(Float.POSITIVE_INFINITY);
            classValues.add(fillValue);
        }

        final float[] pIntervals = new float[intervals.size()];
        for (int i = 0; i < pIntervals.length; i++) {
            pIntervals[i] = intervals.get(i);
        }

        final byte[] pClasses = new byte[classValues.size()];
        for (int i = 0; i < pClasses.length; i++) {
            pClasses[i] = classValues.get(i);
        }

        return new ClassMap(pIntervals, pClasses, fillValue);
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
        addInterval(inputParameters, inclusiveMin, exclusiveMax, classValue);
    }

    /**
     * Add a {@link SampleClassifierDescriptor#CATEGORIES} group into given
     * parameter group.
     *
     * @param parent The group to add a new category into.
     * @param inclusiveMin The lower boundary for created category. Inclusive.
     * @param exclusiveMax The upper boundary for created category. Exclusive.
     * @param classValue The class to associate to the created category.
     */
    public static void addInterval(final ParameterValueGroup parent, final float inclusiveMin, final float exclusiveMax, final byte classValue) {
        final ParameterValueGroup group = parent.addGroup(SampleClassifierDescriptor.CATEGORIES.getName().getCode());
        group.parameter(SampleClassifierDescriptor.MIN.getName().getCode()).setValue(inclusiveMin);
        group.parameter(SampleClassifierDescriptor.MAX.getName().getCode()).setValue(exclusiveMax);
        group.parameter(SampleClassifierDescriptor.CLASS_VALUE.getName().getCode()).setValue(classValue);
    }

    /**
     *
     * @return The fallback class value currently configured.
     */
    public byte getFillValue() {
        return inputParameters.getMandatoryValue(SampleClassifierDescriptor.FILL_VALUE);
    }

    /**
     * Specify the value to use as a fallback for samples which does not fit in
     * any category.
     * @param fillValue The value to use as a fallback.
     */
    public void setFillValue(final byte fillValue) {
        inputParameters.getOrCreate(SampleClassifierDescriptor.FILL_VALUE).setValue(fillValue);
    }

    /**
     * Component in charge of mapping sample values to class values.
     * @implNote : This component is not thread-safe. I.E : it has a state,
     * updated along usage, to allow minor performance optimization. However,
     * to use it with full-power in a multi-thread environment, you can use the
     * {@link #clone() } method to pop an equivalent mapping to use in a single
     * thread.
     */
    private static class ClassMap implements Cloneable {

        private final float[] intervals;
        private final byte[] classes;
        private final byte fillValue;

        /**
         * Minor optimisation : to speed up mapping on consecutive lookalike values,
         * we keep track of the last selected category, so we won't trigger a
         * binary search if not needed. The validity of this optimisation has
         * been tested using JMH, for a performance gain measured around 20-30%.
         */
        private final float[] previousRange = new float[2];
        private byte previousClass;

        public ClassMap(final float[] intervals, final byte[] classes, final byte fillValue) {
            this.intervals = intervals;
            this.classes = classes;
            this.fillValue = fillValue;

            updatePrevious(0);
        }

        public byte getClass(final float sampleValue) {
            if (sampleValue < previousRange[0] || sampleValue >= previousRange[1]) {
                int idx = Arrays.binarySearch(intervals, sampleValue);
                if (idx < 0) {
                    idx = - 2 - idx;
                }

                updatePrevious(idx);
            }
            return previousClass;
        }

        private void updatePrevious(final int classIdx) {
            if (classIdx < 0 || classes.length <= classIdx) {
                previousClass = fillValue;
                previousRange[0] = previousRange[1] = Float.NaN;
            } else {
                previousClass = classes[classIdx];
                previousRange[0] = intervals[classIdx];
                previousRange[1] = intervals[classIdx + 1];
            }
        }

        @Override
        protected ClassMap clone() {
            return new ClassMap(intervals, classes, fillValue);
        }
    }
}
