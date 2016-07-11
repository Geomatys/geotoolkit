/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.IndexColorModel;
import java.util.LinkedList;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PropertySource;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.measure.NumberRange;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.RenderedCoverage;
import org.geotoolkit.coverage.grid.ViewType;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.OutOfDomainOfValidityException;


/**
 * A set of utilities methods for the Grid Coverage package. Those methods are not really
 * rigorous; must of them should be seen as temporary implementations.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Simone Giannecchini (Geosolutions)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public final class CoverageUtilities extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private CoverageUtilities() {
    }

    /**
     * Returns a two-dimensional CRS for the given coverage. This method performs a
     * <cite>best effort</cite>; the returned CRS is not guaranteed to be the most
     * appropriate one.
     *
     * @param  coverage The coverage for which to obtains a two-dimensional CRS.
     * @return The two-dimensional CRS.
     * @throws TransformException if the CRS can't be reduced to two dimensions.
     */
    public static CoordinateReferenceSystem getCRS2D(final Coverage coverage)
            throws TransformException
    {
        if (coverage instanceof GridCoverage2D) {
            return ((GridCoverage2D) coverage).getCoordinateReferenceSystem2D();
        }
        if (coverage instanceof GridCoverage) {
            final GridGeometry2D geometry =
                    GridGeometry2D.castOrCopy(((GridCoverage) coverage).getGridGeometry());
            if (geometry.isDefined(GridGeometry2D.CRS)) {
                return geometry.getCoordinateReferenceSystem2D();
            } else try {
                return geometry.reduce(coverage.getCoordinateReferenceSystem());
            } catch (FactoryException exception) {
                // Ignore; we will fallback on the code below.
            }
        }
        return CRSUtilities.getCRS2D(coverage.getCoordinateReferenceSystem());
    }

    /**
     * Returns a two-dimensional envelope for the given coverage. This method performs a
     * <cite>best effort</cite>; the returned envelope is not guaranteed to be the most
     * appropriate one.
     *
     * @param  coverage The coverage for which to obtains a two-dimensional envelope.
     * @return The two-dimensional envelope.
     * @throws MismatchedDimensionException if the envelope can't be reduced to two dimensions.
     */
    public static Envelope2D getEnvelope2D(final Coverage coverage)
            throws MismatchedDimensionException
    {
        if (coverage instanceof GridCoverage2D) {
            return ((GridCoverage2D) coverage).getEnvelope2D();
        }
        if (coverage instanceof GridCoverage) {
            final GridGeometry2D geometry =
                    GridGeometry2D.castOrCopy(((GridCoverage) coverage).getGridGeometry());
            if (geometry.isDefined(GridGeometry2D.ENVELOPE)) {
                return geometry.getEnvelope2D();
            } else {
                return geometry.reduce(coverage.getEnvelope());
            }
        }
        // Following may thrown MismatchedDimensionException.
        return new Envelope2D(coverage.getEnvelope());
    }

    /**
     * Retrieves a best guess for the sample value to use for background,
     * inspecting the categories of the provided {@link GridCoverage2D}.
     *
     * @param coverage to use for guessing background values.
     * @return an array of double values to use as a background.
     */
    public static double[] getBackgroundValues(final GridCoverage coverage) {
        /*
         * Get the sample value to use for background. We will try to fetch this
         * value from one of "no data" categories. For geophysics images, it is
         * usually NaN. For non-geophysics images, it is usually 0.
         */
        final int numBands = coverage.getNumSampleDimensions();
        final double[] background = new double[numBands];
        for (int i=0; i<numBands; i++) {
            final SampleDimension band = coverage.getSampleDimension(i);
            if (band instanceof GridSampleDimension) {
                final NumberRange<?> range = ((GridSampleDimension) band).getBackground().getRange();
                final double min = range.getMinDouble();
                final double max = range.getMaxDouble();
                if (range.isMinIncluded()) {
                    background[i] = min;
                } else if (range.isMaxIncluded()) {
                    background[i] = max;
                } else {
                    background[i] = 0.5 * (min + max);
                }
            }
        }
        return background;
    }

    /**
     * Returns {@code true} if the provided {@link GridCoverage}
     * has {@link Category} objects with a real transformation.
     * <p>
     * Common use case for this method is understanding if a {@link GridCoverage} has an
     * accompanying Geophysics or non-Geophysics view, which means a dicotomy between the
     * coverage with the "real" data and the coverage with the rendered version of the original
     * data exists. An example is when you have raw data whose data type is float and you want
     * to render them using a palette. You usually do this by specifying a set of {@link Category}
     * object which will map some intervals of the raw data to some specific colors. The rendered
     * version that we will create using the method {@code GridCoverage2D.view(ViewType.RENDERED)}
     * will be backed by a RenderedImage with an IndexColorModel representing the colors provided
     * in the Categories.
     *
     * @param gridCoverage
     *          to check for the existence of categories with tranformations
     *          between original data and their rendered counterpart.
     * @return {@code false} if this coverage has only a single view associated with it,
     *         {@code true} otherwise.
     */
    public static boolean hasRenderingCategories(final GridCoverage gridCoverage) {
        // getting all the SampleDimensions of this coverage, if any exist
        final int numSampleDimensions = gridCoverage.getNumSampleDimensions();
        if (numSampleDimensions == 0) {
            return false;
        }
        final SampleDimension[] sampleDimensions = new SampleDimension[numSampleDimensions];
        for (int i=0; i<numSampleDimensions; i++) {
            sampleDimensions[i] = gridCoverage.getSampleDimension(i);
        }
        // do they have any transformation that is not the identity?
        return hasTransform(sampleDimensions);
    }

    /**
     * Returns {@code true} if at least one of the specified sample dimensions has a
     * {@linkplain SampleDimension#getSampleToGeophysics sample to geophysics} transform
     * which is not the identity transform.
     *
     * @param sampleDimensions The sample dimensions to check.
     * @return {@code true} if at least one sample dimension has defined a transform.
     */
    public static boolean hasTransform(final SampleDimension[] sampleDimensions) {
        for (int i=sampleDimensions.length; --i>=0;) {
            SampleDimension sd = sampleDimensions[i];
            if (sd instanceof GridSampleDimension) {
                sd = ((GridSampleDimension) sd).geophysics(false);
            }
            MathTransform1D tr = sd.getSampleToGeophysics();
            if (tr!=null && !tr.isIdentity()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified grid coverage or any of its source
     * uses the following image.
     *
     * @param  coverage The coverage to check for its sources.
     * @param  image The image which may be a source of the given coverage.
     * @return {@code true} if the coverage use the given image as a source.
     */
    public static boolean uses(final GridCoverage coverage, final RenderedImage image) {
        if (coverage != null) {
            if (coverage instanceof RenderedCoverage) {
                if (((RenderedCoverage) coverage).getRenderedImage() == image) {
                    return true;
                }
            }
            final Collection<GridCoverage> sources = coverage.getSources();
            if (sources != null) {
                for (final GridCoverage source : sources) {
                    if (uses(source, image)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the visible band in the specified {@link RenderedImage} or {@link PropertySource}.
     * This method fetch the {@code "GC_VisibleBand"} property. If this property is undefined,
     * then the visible band default to the first one.
     *
     * @param  image The image for which to fetch the visible band, or {@code null}.
     * @return The visible band.
     */
    public static int getVisibleBand(final Object image) {
        Object candidate = null;
        if (image instanceof RenderedImage) {
            candidate = ((RenderedImage) image).getProperty("GC_VisibleBand");
        } else if (image instanceof PropertySource) {
            candidate = ((PropertySource) image).getProperty("GC_VisibleBand");
        }
        if (candidate instanceof Integer) {
            return ((Integer) candidate).intValue();
        }
        return 0;
    }

    /**
     * General purpose method used in various operations for {@link GridCoverage2D} to help
     * with taking decisions on how to treat coverages with respect to their {@link ColorModel}.
     * <p>
     * The need for this method arose in consideration of the fact that applying most operations
     * on coverage whose {@link ColorModel} is an instance of {@link IndexColorModel} may lead to
     * unpredictable results depending on the applied {@link Interpolation} (think about applying
     * "Scale" with {@link InterpolationBilinear} on a non-geophysics {@link GridCoverage2D} with an
     * {@link IndexColorModel}) or more simply on the operation itself ("SubsampleAverage" cannot
     * be applied at all on a {@link GridCoverage2D} backed by an {@link IndexColorModel}).
     * <p>
     * This method suggests the actions to take depending on the structure of the provided
     * {@link GridCoverage2D}, the provided {@link Interpolation} and if the operation uses
     * a filter or not (this is useful for operations like SubsampleAverage or FilteredSubsample).
     * <p>
     * In general the idea is as follows: If the original coverage is backed by a
     * {@link RenderedImage} with an {@link IndexColorModel}, we have the following cases:
     * <p>
     * <ul>
     *  <li>if the interpolation is {@link InterpolationNearest} and there is no filter involved
     *      we can apply the operation on the {@link IndexColorModel}-backed coverage with nor
     *      problems.</li>
     *  <li>If the interpolations in of higher order or there is a filter to apply we have to
     *      options:
     *      <ul>
     *        <li>If the coverage has a twin geophysics view we need to go back to it and apply
     *            the operation there.</li>
     *        <li>If the coverage has no geophysics view (an orthophoto with an intrisic
     *            {@link IndexColorModel} view) we need to perform an RGB(A) color expansion
     *            before applying the operation.</li>
     *      </ul>
     *  </li>
     * </ul>
     * <p>
     * A special case is when we want to apply an operation on the geophysics view of a coverage
     * that does not involve high order interpolation or filters. In this case we suggest to apply
     * the operation on the non-geophysics view, which is usually much faster. Users may ignore
     * this advice.
     *
     * @param coverage The coverage to check for the action to take.
     * @param interpolation The interpolation to use for the action to take, or {@code null} if none.
     * @param hasFilter {@code true} if the operation we will apply is going to use a filter.
     * @param hints The hints to use when applying a certain operation.
     * @return {@link ViewType#SAME} if nothing has to be done on the provided coverage,
     *         {@link ViewType#PHOTOGRAPHIC} if a color expansion has to be provided,
     *         {@link ViewType#GEOPHYSICS} if we need to employ the geophysics view of
     *         the provided coverage,
     *         {@link ViewType#NATIVE} if we suggest to employ the native (usually packed) view
     *         of the provided coverage.
     *
     * @since 2.5
     *
     * @todo Move this method in {@link org.geotoolkit.coverage.processing.Operation2D}.
     */
    public static ViewType preferredViewForOperation(final GridCoverage2D coverage,
            final Interpolation interpolation, final boolean hasFilter, final RenderingHints hints)
    {
        /*
         * Checks if the user specified explicitly the view he wants to use for performing
         * the calculations.
         */
        if (hints != null) {
            final Object candidate = hints.get(Hints.COVERAGE_PROCESSING_VIEW);
            if (candidate instanceof ViewType) {
                return (ViewType) candidate;
            }
        }
        /*
         * Tries to infer automatically the view to use.  If there is no sample dimension with
         * a "sample to geophysics" transform, then we assume that the image has no geophysics
         * meaning and would better be handled as photographic.
         */
        final RenderedImage sourceImage = coverage.getRenderedImage();
        if (sourceImage.getColorModel() instanceof IndexColorModel) {
            if (!hasRenderingCategories(coverage)) {
                return ViewType.PHOTOGRAPHIC;
            }
            /*
             * If there is no filter and no interpolation, then we don't need to operate on
             * geophysics value. The packed view is usually faster. We could returns either
             * NATIVE, PACKED or SAME, which are equivalent in many cases:
             *
             *  - SAME is likely equivalent to PACKED because we checked that the color model is indexed.
             *  - NATIVE is likely equivalent to PACKED because data in NetCDF or HDF files are often packed.
             *
             * However those views differ in their behavior when the native data are geophysics
             * rather than packed (e.g. a NetCDF file with floating point values). In this case,
             * NATIVE is equivalent to GEOPHYSICS. The tradeoff of each views are:
             *
             *  - NATIVE is more accurate but slower when native data are geophysics
             *    (but as fast as other views when native data are packed).
             *
             *  - SAME is "as the user said" on the assumption that if he asked an operation on
             *    a packed view of a coverage rather than the geophysics view, he know what he
             *    is doing.
             */
            if (!hasFilter && (interpolation == null || interpolation instanceof InterpolationNearest)) {
                if (hints != null) {
                    final Object rendering = hints.get(RenderingHints.KEY_RENDERING);
                    if (RenderingHints.VALUE_RENDER_QUALITY.equals(rendering)) {
                        return ViewType.NATIVE;
                    }
                    if (RenderingHints.VALUE_RENDER_SPEED.equals(rendering)) {
                        return ViewType.SAME;
                    }
                }
                return ViewType.SAME; // Default value.
            }
            // In this case we need to go back the geophysics view of the source coverage.
            return ViewType.GEOPHYSICS;
        }
        /*
         * The operations are usually applied on floating-point values, in order
         * to gets maximal precision and to handle correctly the special case of
         * NaN values. However, we can apply some operation on integer values if
         * the interpolation type is "nearest neighbor", since this is not
         * really an interpolation.
         *
         * If this condition is met, then we verify if an "integer version" of
         * the image is available as a source of the source coverage (i.e. the
         * floating-point image is derived from the integer image, not the
         * converse).
         */
        if (!hasFilter && (interpolation == null || interpolation instanceof InterpolationNearest)) {
            final GridCoverage2D candidate = coverage.view(ViewType.NATIVE);
            if (candidate != coverage) {
                final List<RenderedImage> sources = coverage.getRenderedImage().getSources();
                if (sources != null && sources.contains(candidate.getRenderedImage())) {
                    return ViewType.NATIVE;
                }
            }
        }
        return ViewType.SAME;
    }

    /**
     * The preferred view in which to returns the coverage after the operation.
     * This method returns a view that match the current state of the given coverage.
     *
     * @param  coverage The source coverage <strong>before</strong> the operation.
     * @return The suggested view, or {@link ViewType#SAME} if this method doesn't
     *         have any suggestion.
     *
     * @since 2.5
     *
     * @deprecated This method duplicate functionalities defined in
     * {@link org.geotoolkit.coverage.processing.Operation2D}.
     */
    @Deprecated
    public static ViewType preferredViewAfterOperation(final GridCoverage2D coverage) {
        final Set<ViewType> views = coverage.getViewTypes();
        // Most restrictive views first, less restrictive last.
        if (views.contains(ViewType.GEOPHYSICS)) {
            return ViewType.GEOPHYSICS;
        }
        if (views.contains(ViewType.RENDERED)) {
            return ViewType.RENDERED;
        }
        if (views.contains(ViewType.PACKED)) {
            return ViewType.PACKED;
        }
        if (views.contains(ViewType.PHOTOGRAPHIC)) {
            return ViewType.PHOTOGRAPHIC;
        }
        return ViewType.SAME;
    }

    /**
     * Adapt input envelope to fit urn:ogc:def:wkss:OGC:1.0:GoogleCRS84Quad. Also give well known scales into the interval
     * given in parameter.
     *
     * As specified by WMTS standard v1.0.0 :
     * <p>
     *     [GoogleCRS84Quad] well-known scale set has been defined to allow quadtree pyramids in CRS84. Level
     * 0 allows representing the whole world in a single 256x256 pixels (where the first 64 and
     * last 64 lines of the tile are left blank). The next level represents the whole world in 2x2
     * tiles of 256x256 pixels and so on in powers of 2. Scale denominator is only accurate near
     * the equator.
     * </p>
     *
     * /!\ The well-known scales computed here have been designed for CRS:84 and Mercator projected CRS. Using it for
     * other coordinate reference systems can result in strange results.
     *
     * Note : only horizontal part of input envelope is analysed, so returned envelope will have same values as input one
     * for all additional dimension.
     *
     * @param envelope An envelope to adapt to well known scale quad-tree.
     * @param scaleLimit Minimum and maximum authorized scales. Edge inclusive. Unit must be input envelope horizontal
     *                    axis unit.
     * @return An entry with adapted envelope and its well known scales.
     */
    public static Map.Entry<Envelope, double[]> toWellKnownScale(final Envelope envelope, final NumberRange<Double> scaleLimit) throws TransformException, OutOfDomainOfValidityException {
        final CoordinateReferenceSystem targetCRS = CRS.getHorizontalComponent(envelope.getCoordinateReferenceSystem());
        if (targetCRS == null) {
            throw new IllegalArgumentException("Input envelope CRS has no defined horizontal component.");
        }

        /**
         * First, we retrieve total envelope of our Quad-tree. We try to use domain of validity of our input envelope
         * CRS. If we cannot, we'll take the world. After that, we'll perform consecutive divisions in order to find
         * minimal Quad-tree cell in which our envelope can be set. It will give us the result envelope. From this
         * envelope, we'll be able to build the final scale list.
         *
         * Note : final envelope can be the fusion of two neighbour Quad-Tree cells.
         */
        final Envelope tmpDomain = org.geotoolkit.geometry.Envelopes.getDomainOfValidity(targetCRS);
        final GeneralEnvelope quadTreeCell;
        if (tmpDomain == null) {
            final GeographicCRS crs84 = CommonCRS.defaultGeographic();
            final Envelope tmpWorld = org.geotoolkit.referencing.CRS.getEnvelope(crs84);
            quadTreeCell = new GeneralEnvelope(Envelopes.transform(tmpWorld, targetCRS));
        } else {
            quadTreeCell = new GeneralEnvelope(tmpDomain);
        }

        // We check we can perform divisions on computed domain.
        double min, max;
        for (int i = 0; i < quadTreeCell.getDimension(); i++) {
            min = quadTreeCell.getMinimum(i);
            max = quadTreeCell.getMaximum(i);
            if (Double.isNaN(min) || Double.isInfinite(min) ||
                    Double.isNaN(max) || Double.isInfinite(max)) {
                throw new OutOfDomainOfValidityException("Invalid world bounds " + quadTreeCell);
            }
        }

        GeneralEnvelope targetEnv = new GeneralEnvelope(envelope);

        GeneralEnvelope cellX = quadTreeCell.subEnvelope(0, 1);
        GeneralEnvelope cellY = quadTreeCell.subEnvelope(1, 2);

        final int xAxis = CRSUtilities.firstHorizontalAxis(envelope.getCoordinateReferenceSystem());
        final int yAxis = xAxis + 1;
        final GeneralEnvelope tmpInput = new GeneralEnvelope(envelope);
        GeneralEnvelope inputRangeX = tmpInput.subEnvelope(xAxis, xAxis+1);
        GeneralEnvelope inputRangeY = tmpInput.subEnvelope(yAxis, yAxis+1);

        double midQuadX, midQuadY;
        boolean containX = cellX.contains(inputRangeX);
        boolean containY = cellY.contains(inputRangeY);
        while (containX || containY) {
            // Resize on longitude
            if (containX) {
                targetEnv.setRange(xAxis, cellX.getMinimum(0), cellX.getMaximum(0));
                midQuadX = cellX.getLower(0) + (cellX.getSpan(0) / 2);
                if (inputRangeX.getMinimum(0) < midQuadX) {
                    // west side
                    cellX.setRange(0, cellX.getMinimum(0), midQuadX);
                } else {
                    // east side
                    cellX.setRange(0, midQuadX, cellX.getMaximum(0));
                }

                // Update envelope test
                containX = cellX.contains(inputRangeX);
            }

            // Resize on latitude
            if (containY) {
                targetEnv.setRange(yAxis, cellY.getMinimum(0), cellY.getMaximum(0));
                midQuadY = cellY.getLower(0) + (cellY.getSpan(0) / 2);
                if (inputRangeY.getMinimum(0) < midQuadY) {
                    // south side
                    cellY.setRange(0, cellY.getMinimum(0), midQuadY);
                } else {
                    // north side
                    cellY.setRange(0, midQuadY, cellY.getMaximum(0));
                }

                // Update envelope test
                containY = cellY.contains(inputRangeY);
            }
        }

        final double lowestResolution = Math.max(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());
        final double highestResolution = Math.min(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());
        // Go to lowest authorized resolution boundary : A single 256px side tile for output envelope.
        final List<Double> scalesList = new LinkedList<>();
        double minScale = targetEnv.getSpan(xAxis) / 256;
        scalesList.add(minScale);
        while (minScale > highestResolution) {
            minScale /= 2;
            //-- add after to get the last scale to, at least, reach data 1:1 resolution
            scalesList.add(minScale);
        }

        //-- cast
        final double[] scales = new double[scalesList.size()];
        for (int i = 0; i < scalesList.size(); i++) {
            scales[i] = scalesList.get(i);
        }
        return new AbstractMap.SimpleEntry<Envelope, double[]>(targetEnv, scales);
    }
}
