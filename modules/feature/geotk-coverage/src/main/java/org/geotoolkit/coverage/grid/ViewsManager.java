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
package org.geotoolkit.coverage.grid;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.FormatDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.internal.util.CollectionsExt;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArraysExt;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.color.ColorUtilities;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;


/**
 * Holds the different views of a {@link GridCoverage2D}. Those views are handled in a separated
 * class because the same instance may be shared by more than one {@link GridCoverage2D}. Because
 * views are associated with potentially big images, sharing them when possible is a big memory
 * and CPU saver.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class ViewsManager {
    /**
     * Slight number for rounding errors in floating point comparison.
     */
    private static final float EPS = 1E-5f;
    /**
     * The logging level for reporting coverage operations.
     * This level is equals or slightly lower than {@link Level#INFO}.
     */
    public static final Level OPERATION = new LogLevel("OPERATION", 780);

    /**
     * The grid coverage logging level type.
     */
    private static final class LogLevel extends Level {
        private static final long serialVersionUID = -2944283575307061508L;
        protected LogLevel(final String name, final int level) {
            super(name, level);
        }
    }

    /**
     * The views. The coverage that created this {@code ViewsManager} must be stored under
     * the {@link ViewType#NATIVE} key.
     *
     * /!\ Valid views for the coverage are determined at built, and identified using image properties. If user asks for
     * a non-managed type of view, an attempt will be done to satisfy him, but in ABSOLUTELY no case queried view type
     * will be added to the managed types. It would violate immutability and coherence of underlying data.
     */
    private final Map<ViewType,GridCoverage2D> views;

    /**
     * List of coherent view which can be extracted from source data.
     */
    final Set<ViewType> viewTypes;

    /**
     * Constructs a map of views.
     *
     * @param coverage The coverage that created this {@code ViewsManager}.
     */
    private ViewsManager(final GridCoverage2D coverage) {
        views = new EnumMap<>(ViewType.class);
        boolean geophysics   = true; // 'true' only if all bands are geophysics.
        boolean photographic = true; // 'true' only if no band have category.
        boolean rendered     = true; // 'true' only if all bands are on unsigned samples, and we are not in geophysics mode.
        final List<SampleDimension> dims = coverage.getSampleDimensions();
scan:   for (int i=0,n=dims.size(); i<n; i++) {
            final SampleDimension band = dims.get(i);
            if (band != null) {
                if (isNullOrEmpty(band.getCategories())) {
                    // No category. The image is treated as photographic.
                    continue;
                }
                photographic = false;
                final SampleDimension packed = band.forConvertedValues(false);
                if (band != packed) {
                    for (final Category category : packed.getCategories()) {
                        if (category.isQuantitative()) {
                            // Preserves the geophysics value if at least one category
                            // is quantitative. Otherwise it will be set to 'false'.
                            continue scan;
                        }
                    }
                }
                geophysics = false;
                if (SampleDimensionUtils.isRangeSigned(band)) {
                    rendered = false;
                }
            }
        }
        final ViewType type;
        if (photographic) {
            // Must be tested first because 'geophysics' is 'true' as well in this case.
            type = ViewType.PHOTOGRAPHIC;
        } else if (geophysics) {
            type = ViewType.GEOPHYSICS;
            rendered = false;
        } else {
            type = ViewType.PACKED;
        }
        views.put(type, coverage);
        if (rendered) {
            views.put(ViewType.RENDERED, coverage);
        }
        views.put(ViewType.NATIVE, coverage.getNativeView());
        viewTypes = Collections.unmodifiableSet(views.keySet());
    }

    /**
     * Returns a shared map of views <em>or</em> constructs a new one. In order to check if we can
     * share the views with one of the sources, the source must have identical image, geometry and
     * sample dimensions. As a safety, we do not allow views sharing for arbitrary classes of
     * {@link Calculator2D} (this is checked by {@code viewClass}).
     *
     * @param coverage The coverage that wants to create a {@code ViewsManager}.
     */
    static ViewsManager create(final GridCoverage2D coverage) {
        final Class<? extends GridCoverage2D> viewClass = coverage.getViewClass();
        if (viewClass != null) {
            Collection<GridCoverage> sources = coverage.getSources();
            while (sources != null) {
                Collection<GridCoverage> next = null;
                for (final GridCoverage source : sources) {
                    if (source instanceof GridCoverage2D) {
                        final GridCoverage2D candidate = (GridCoverage2D) source;
                        if (Objects.equals(coverage.image,                 candidate.image)            &&
                            Objects.equals(coverage.getGridGeometry(),     candidate.getGridGeometry())     &&
                            Objects.equals(coverage.getSampleDimensions(), candidate.getSampleDimensions()) &&
                            candidate.getViewClass() == viewClass)
                            // The CRS is checked with the GridGeometry2D.
                        {
                            return candidate.copyViewsTo(coverage);
                        }
                    }
                    final Collection<GridCoverage> more = source.getSources();
                    if (!isNullOrEmpty(more)) {
                        if (next == null) {
                            next = new LinkedHashSet<>(more);
                        } else {
                            next.addAll(more);
                        }
                    }
                }
                sources = next;
            }
        }
        return new ViewsManager(coverage);
    }

    /**
     * Invoked by {@linkplain GridCoverage2D#view} for getting a view.
     * <p>
     * <strong>NOTE:</strong> {@link GridCoverage2D#toString()} requires that this method is
     * synchronized on {@code this}.
     */
    public synchronized GridCoverage2D get(final GridCoverage2D caller, final ViewType type,
                                           final Hints userHints)
    {
        GridCoverage2D coverage = views.get(type);
        if (coverage != null) {
            return coverage;
        }
        coverage = views.get(ViewType.NATIVE);
        if (coverage == null) {
            // TODO: localize.
            throw new IllegalStateException("This coverage has been disposed.");
        }
        switch (type) {
            case RENDERED:     coverage = rendered(caller,            userHints); break;
            case PACKED:       coverage = geophysics(coverage, false, userHints); break;
            case GEOPHYSICS:   coverage = geophysics(coverage, true,  userHints); break;
            case PHOTOGRAPHIC: coverage = photographic(coverage,      userHints); break;
            default: {
                /*
                 * We don't want a case for:
                 *  - SAME   because it should be handled by "GridCoverage2D.view(...)"
                 *  - NATIVE because it should be handled by "views.get(type)".
                 *
                 * Getting there with one of the above type is an error.
                 */
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.IllegalArgument_2, "type", type));
            }
        }
        coverage = caller.specialize(coverage);
        if (caller.copyViewsTo(coverage) != this) {
            throw new AssertionError(); // Should never happen.
        }
        views.put(type, coverage);
        return coverage;
    }

    /**
     * Invoked by {@link #create} when a photographic view needs to be created. This method
     * reformats the {@linkplain ColorModel color model} to a {@linkplain ComponentColorModel
     * component color model} preserving transparency. The new color model is typically backed
     * by an RGB {@linkplain ColorSpace color space}, but not necessarily. It could be YMCB as well.
     */
    @SuppressWarnings("fallthrough")
    private static GridCoverage2D photographic(final GridCoverage2D coverage, final Hints userHints) {
        final RenderedImage image = coverage.getRenderedImage();
        final ColorModel cm = image.getColorModel();
        /*
         * If the image already use a component color model (not necessarily backed by
         * RGB color space - it could be CYMB as well), then there is nothing to do.
         */
        if (cm instanceof ComponentColorModel) {
            return coverage;
        }
        final int dataType;
        final ColorSpace cs;
        final LookupTableJAI lookup;
        /*
         * If the color model is indexed. Converts to RGB or gray scale using a single "Lookup"
         * operation. Color space will be RGB or GRAY, and type will be DataBuffer.TYPE_BYTE.
         */
        if (cm instanceof IndexColorModel) {
            final IndexColorModel icm = (IndexColorModel) cm;
            final int mapSize = icm.getMapSize();
            final byte[][] data;
            if (ColorUtilities.isGrayPalette(icm, false)) {
                final byte[] gray = new byte[mapSize];
                icm.getGreens(gray);
                if (icm.hasAlpha()) {
                    final byte[] alpha = new byte[mapSize];
                    icm.getAlphas(alpha);
                    data = new byte[][] { gray, alpha };
                } else {
                    data = new byte[][] { gray };
                }
                cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            } else {
                data = new byte[cm.getNumComponents()][mapSize];
                switch (data.length) {
                    default: // Should not occurs, but keep as a paranoiac check.
                    case 4:  icm.getAlphas(data[3]);
                    case 3:  icm.getBlues (data[2]);
                    case 2:  icm.getGreens(data[1]);
                    case 1:  icm.getReds  (data[0]);
                    case 0:  break;
                }
                cs = icm.getColorSpace();
            }
            dataType = DataBuffer.TYPE_BYTE;
            lookup = new LookupTableJAI(data);
        } else {
            lookup = null;
            cs = cm.getColorSpace();
            dataType = (cm instanceof DirectColorModel) ? DataBuffer.TYPE_BYTE :
                        image.getSampleModel().getTransferType();
        }
        /*
         * Gets the rendering hints to be given to the image operation.
         */
        final ColorModel  targetCM;
        final SampleModel targetSM;
        targetCM = new ComponentColorModel(cs,
                cm.hasAlpha(),               // If true, supports transparency.
                cm.isAlphaPremultiplied(),   // If true, alpha is premultiplied.
                cm.getTransparency(),        // What alpha values can be represented.
                dataType);                   // Type of primitive array used to represent pixel.
        targetSM = targetCM.createCompatibleSampleModel(image.getWidth(), image.getHeight());
        RenderingHints hints = ImageUtilities.getRenderingHints(image);
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        ImageLayout layout = (ImageLayout) hints.get(JAI.KEY_IMAGE_LAYOUT);
        if (layout == null) {
            layout = new ImageLayout();
        }
        layout.setColorModel (targetCM);
        layout.setSampleModel(targetSM);
        hints.put(JAI.KEY_IMAGE_LAYOUT, layout);
        /*
         * Creates the image, than the coverage.
         */
        final RenderedOp view;
        if (lookup != null) {
            view = LookupDescriptor.create(image, lookup, hints);
        } else {
            view = FormatDescriptor.create(image, dataType, hints);
        }
        assert view.getColorModel() instanceof ComponentColorModel;
        return createView(coverage, view, null, 2, userHints);
    }

    /**
     * Invoked by {@link #create} when a geophysics or packed view needs to be created.
     *
     * {@note <b>Historic</b>: In JDK 4 and 5, images using a <code>ComponentSampleModel</code> -
     * as created by <code>IndexColorModel</code> - were much slower at rendering time than images
     * using a <code>PixelInterleavedSampleModel</code> - as created by <code>BufferedImage</code>
     * for type <code>TYPE_BYTE_INDEXED</code>.  Consequently the Geotk code had a patch replacing
     * the former color model by the later one. This patch has been removed in later Geotk version
     * since a micro-benchmarking with JDK6 and 7 didn't show any performance difference anymore.}
     *
     * @todo The "Piecewise" operation is disabled because javac 1.4.1_01 generate illegal
     *       bytecode. This bug is fixed in javac 1.4.2-beta. However, we still have an
     *       ArrayIndexOutOfBoundsException in JAI code...
     */
    private static GridCoverage2D geophysics(final GridCoverage2D coverage, final boolean toGeo,
                                             final Hints userHints)
    {
        /*
         * STEP 1 - Gets the source image and prepare the target bands (sample dimensions).
         *          As a slight optimization, we skip the "Null" operation since such image
         *          may be the result of some operation (e.g. "Colormap").
         */
        RenderedImage image = coverage.image;
        while (image instanceof NullOpImage) {
            final NullOpImage op = (NullOpImage) image;
            if (op.getNumSources() != 1) {
                break;
            }
            image = op.getSourceImage(0);
        }
        final SampleModel           sourceModel = image.getSampleModel();
        final int                      numBands = sourceModel.getNumBands();
        final SampleDimension[] sourceBands = CollectionsExt.toArray(coverage.getSampleDimensions(), SampleDimension.class);
        final SampleDimension[] targetBands = sourceBands.clone();
        assert targetBands.length == numBands : targetBands.length;
        for (int i=0; i<targetBands.length; i++) {
            targetBands[i] = targetBands[i].forConvertedValues(toGeo);
        }
        /*
         * If the target bands are equal to the source bands, then there is nothing to do.
         * Otherwise, we call "nativeBands" the ones that are not geophysics (i.e. the ones
         * that may contain a MathTransform1D different than the identity transform).
         */
        if (Arrays.equals(sourceBands, targetBands)) {
            return coverage;
        }
        final int visibleBand = CoverageUtilities.getVisibleBand(image);
        final SampleDimension[] nativeBands = toGeo ? sourceBands : targetBands;
        /*
         * Computes immediately the "geophysics to native" transforms.  If all transforms are the
         * identity one, then we will return the coverage unchanged. The transforms that can't be
         * obtained will be set to null, which is understood by LookupTableFactory.create(...) as
         * "Lookup operation not allowed".
         */
        boolean isIdentity = true;
        MathTransform1D[] transforms = new MathTransform1D[numBands];
        for (int i=0; i<numBands; i++) {
            MathTransform1D transform = nativeBands[i].getTransferFunction().orElse(null);
            if (transform!=null && !toGeo) try {
                transform = transform.inverse(); // We want the geophysics to native transform.
            } catch (NoninvertibleTransformException e) {
                transform = null;
                isIdentity = false;
            }
            transforms[i] = transform;
            isIdentity &= transform.isIdentity();
        }
        if (isIdentity) {
            return coverage;
        }
        /*
         * STEP 2 - Computes the layout for the destination RenderedImage. We will use the same
         *          layout than the parent image, except for tile size if the parent image had
         *          only one big tile, and for the color model and sample model  (since we are
         *          reformating data in the process of this operation).
         */
        ImageLayout layout = ImageUtilities.getImageLayout(image);
        final ColorModel  colors      = SampleDimensionUtils.getColorModel(targetBands[visibleBand], visibleBand, numBands);
        final SampleModel targetModel = colors.createCompatibleSampleModel(
                layout.getTileWidth(image), layout.getTileHeight(image));
        layout = layout.setSampleModel(targetModel).setColorModel(colors);
        ParameterBlock param = new ParameterBlock().addSource(image);
        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        hints.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        hints.put(JAI.KEY_TRANSFORM_ON_COLORMAP,     Boolean.FALSE);
        String operation = null; // Will be set in step 3 or 4.
        /*
         * STEP 3 - Checks if the transcoding could be done with the JAI's "Lookup" operation. This
         *          is probably the fastest operation available for going to the geophysics view.
         *          Note that the transforms array may contains null elements, which will cause
         *          LookupTableFactory.create(...) to returns null.
         */
        if (transforms != null) try {
            final int sourceType = sourceModel.getDataType();
            final int targetType = targetModel.getDataType();
            LookupTableJAI table = LookupTableFactory.create(sourceType, targetType, transforms);
            if (table != null) {
                operation = "Lookup";
                param = param.add(table);
            }
        } catch (TransformException exception) {
            /*
             * A value can't be transformed. Fallback on "Rescale" or "Piecewise" operations. We
             * don't log yet because the more general operations are likely to fail for the same
             * reason and we don't want to log the same TransformException twice.
             */
        }
        /*
         * STEP 4 - Check if the transcoding could be done with a JAI's "Rescale" or "Piecewise"
         *          operations. The "Rescale" operation requires a completely linear relationship
         *          between the source and the destination sample values. The "Piecewise" operation
         *          is less strict: piecewise breakpoints are very similar to categories, but the
         *          transformation for all categories still have to be linear.
         */
        if (operation == null) try {
            boolean     canRescale   = true; // 'true' if the "Rescale"   operation can be applied.
            boolean     canPiecewise = true; // 'true' if the "Piecewise" operation can be applied.
            boolean     conditional  = false;// 'true' if isZeroExcluded(...) needs to be invoked.
            double[]    scales       = null; // The first  argument for "Rescale".
            double[]    offsets      = null; // The second argument for "Rescale".
            float[][][] breakpoints  = null; // The only   argument for "Piecewise".
testLinear: for (int i=0; i<numBands; i++) {
                final List<Category> sources = sourceBands[i].getCategories();
                final int      numCategories = sources.size();
                float[]    sourceBreakpoints = null;
                float[]    targetBreakpoints = null;
                double        expectedSource = NaN;
                double        expectedTarget = NaN;
                int jbp = 0; // Break point index (vary with j)
                for (int j=0; j<numCategories; j++) {
                    final Category sourceCategory = sources.get(j);
                    final Category packedCategory = sourceCategory; // TODO .forConvertedValues(false);
                    MathTransform1D transform = packedCategory.getTransferFunction().orElse(null);
                    final double offset, scale;
                    if (transform == null) {
                        /*
                         * A qualitative category was found. Those categories maps NaN values,
                         * which need the special processing performed by our "SampleTranscode"
                         * operation. However there is a few special cases where JAI operations
                         * could still fit:
                         *
                         * - In "packed to geophysics" transform, we can still use "Piecewise"
                         *   if the minimum and maximum target value are equals (usually NaN).
                         *
                         * - In "geophysics to packed" transform, we can still use "Rescale"
                         *   if the NaN value maps to 0.
                         */
                        if (toGeo) {
                            canRescale = false;
//                          final NumberRange<?> target = sourceCategory.getMeasurementRange().get();
//                          offset = target.getMinDouble();
//                          if (doubleToRawLongBits(offset) != doubleToRawLongBits(target.getMaxDouble())) {
                                canPiecewise = false;
                                break testLinear;
//                          }
//                          scale = 0;
                        } else {
                            canPiecewise = false;
                            assert !packedCategory.equals(sourceCategory) : packedCategory;
                            final NumberRange<?> range = packedCategory.getSampleRange();
                            if (range.getMinDouble(true) == 0 && range.getMaxDouble(true) == 0) {
                                assert isNaN(sourceCategory.getSampleRange().getMinDouble()) : sourceCategory;
                                conditional = true;
                                continue;
                            }
                            canRescale = false;
                            break testLinear;
                        }
                    } else {
                        if (!toGeo) {
                            // We are going to convert geophysics values to packed ones.
                            transform = transform.inverse();
                        }
                        offset = transform.transform(0);
                        scale  = transform.derivative(NaN);
                        if (isNaN(scale) || isNaN(offset)) {
                            // One category doesn't use a linear transformation. We can't deal with
                            // that with "Rescale" or "Piecewise". Fallback on our "SampleTranscode".
                            canRescale   = false;
                            canPiecewise = false;
                            break testLinear;
                        }
                    }
                    // Allocates arrays the first time the loop is run up to this point.
                    // Store scale and offset, and check if they still the same.
                    if (j == 0) {
                        if (i == 0) {
                            scales      = new double[numBands];
                            offsets     = new double[numBands];
                            breakpoints = new float [numBands][][];
                        }
                        sourceBreakpoints = new float[numCategories * 2];
                        targetBreakpoints = new float[numCategories * 2];
                        breakpoints[i] = new float[][] {sourceBreakpoints, targetBreakpoints};
                        offsets    [i] = offset;
                        scales     [i] = scale;
                    }
                    if (offset!=offsets[i] || scale!=scales[i]) {
                        canRescale = false;
                    }
                    // Computes breakpoints.
                    final NumberRange<?> range = sourceCategory.getSampleRange();
                    final double  minimum = range.getMinDouble(true);
                    final double  maximum = range.getMaxDouble(true);
                    final float sourceMin = (float) minimum;
                    final float sourceMax = (float) maximum;
                    final float targetMin = (float)(minimum * scale + offset);
                    final float targetMax = (float)(maximum * scale + offset);
                    assert sourceMin <= sourceMax : range;
                    if (Math.abs(minimum - expectedSource) <= EPS) {
                        if (Math.abs(targetMin - expectedTarget) <= EPS || isNaN(expectedTarget)) {
                            /*
                             * This breakpoint is identical to the previous one. Do not duplicate;
                             * overwrites the previous breakpoint since the later is likely to be
                             * more accurate. Note that we accept NaN in expected (not calculated)
                             * target values but not in source values, because "Piecewise" performs
                             * its search on source values, wich must be monotonically increasing.
                             */
                            jbp--;
                        } else {
                            // Found a discontinuity!!! The "piecewise" operation is not really
                            // designed for such case. The behavior between the last breakpoint
                            // and the current one may not be what the user expected.
                            assert sourceBreakpoints[jbp-1] < sourceMin : expectedSource;
                            canPiecewise = false;
                        }
                    } else if (j != 0) {
                        // Found a gap between the last category and the current one. But the
                        // piecewise operation still work as expected for values not in the gap.
                        assert !(expectedSource > sourceMin) : expectedSource;
                    }
                    sourceBreakpoints[jbp  ] = sourceMin;
                    sourceBreakpoints[jbp+1] = sourceMax;
                    targetBreakpoints[jbp  ] = targetMin;
                    targetBreakpoints[jbp+1] = targetMax;
                    jbp += 2;
                    expectedSource = range.getMaxDouble(false);
                    expectedTarget = expectedSource * scale + offset;
                }
                breakpoints[i][0] = sourceBreakpoints = ArraysExt.resize(sourceBreakpoints, jbp);
                breakpoints[i][1] = targetBreakpoints = ArraysExt.resize(targetBreakpoints, jbp);
                assert ArraysExt.isSorted(sourceBreakpoints, false);
            }
            if (canRescale && scales!=null && (!conditional || isZeroExcluded(image, scales, offsets))) {
                /*
                 * TODO: JAI BUG? If the type is TYPE_USHORT, its look like that JAI interpret
                 * 256 as 0 (and same for all value above 256). In order to check if this issue
                 * still exists, see SampleTranscoderTest.testTypeUShort().
                 */
                if (sourceModel.getDataType() != DataBuffer.TYPE_USHORT) {
                    operation = "Rescale";
                    param = param.add(scales).add(offsets);
                }
            } else if (canPiecewise && breakpoints!=null) {
//                operation = "Piecewise";
//                param = param.add(breakpoints);
            }
        } catch (TransformException exception) {
            /*
             * At least one category doesn't use a linear relation. Ignores the exception and
             * fallback on the next case. We log a message at Level.FINE rather than WARNING
             * because this exception may be normal. We pretend that the log come from
             * GridCoverage2D.view, which is the public method that invoked this one.
             */
            Logging.recoverableException(null, GridCoverage2D.class, "view", exception);
        }
        /*
         * STEP 5 - Transcode the image sample values. The "SampleTranscode" operation is
         *          registered in the org.geotoolkit.coverage package in the SampleDimension
         *          class.
         */
        if (operation == null) {
            param = param.add(sourceBands);
            operation = "org.geotoolkit.SampleTranscode";
        }
        final RenderedOp view = JAI.create(operation, param, hints);
        return createView(coverage, view, targetBands, toGeo ? 1 : 0, userHints);
    }

    /**
     * Invoked by {@link #create} when a rendered view needs to be created.
     *
     * @todo Not yet implemented. For now we use the packed view as a close match. Future version
     *       will needs to make sure that we returns the same instance than PACKED when suitable.
     */
    private GridCoverage2D rendered(final GridCoverage2D coverage, final Hints userHints) {
        return get(coverage, ViewType.PACKED, userHints);
    }

    /**
     * Creates the view and logs a record.
     */
    private static GridCoverage2D createView(final GridCoverage2D coverage, final RenderedOp view,
            final SampleDimension[] targetBands, final int code, final Hints userHints)
    {
        final InternationalString name = coverage.getName();
        if (GridCoverage2D.LOGGER.isLoggable(OPERATION)) {
            // Logs a message using the same level than grid coverage processor.
            final String operation = view.getOperationName();
            final String shortName = operation.substring(operation.lastIndexOf('.') + 1);
            final Locale    locale = coverage.getLocale();
            final LogRecord record = Loggings.getResources(locale).getLogRecord(
                    OPERATION, Loggings.Keys.SampleTranscode_3, new Object[] {
                        (name != null) ? name.toString(locale) :
                            Vocabulary.getResources(locale).getString(Vocabulary.Keys.Untitled),
                        code, shortName
                    });
            record.setSourceClassName(GridCoverage2D.class.getName());
            record.setSourceMethodName("geophysics");
            final Logger logger = GridCoverage2D.LOGGER;
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
        final GridCoverage[] sources = new GridCoverage[] {coverage};
        return new GridCoverage2D(name, view, coverage.getGridGeometry(), targetBands, sources, null, userHints);
    }

    /**
     * Returns {@code true} if rescaling every pixels in the specified image (excluding NaN) would
     * not produce zero value. In case of doubt, this method conservatively returns {@code false}.
     * <p>
     * <b>Why this method exists</b><br>
     * When a {@link org.geotoolkit.coverage.SampleDimension} describes exactly one linear relationship with one NaN value
     * mapping exactly to the index value 0, then the "<cite>geophysics to native</cite>" transform
     * can be optimized to the {@code "Rescale"} operation because {@link Float#NaN} casted to the
     * {@code int} primitive type equals 0. This case is very common, which make this optimization
     * a useful one. Unfortunately there is nothing in {@code "Rescale"} preventing some real number
     * (not NaN) to maps to 0 through the normal linear relationship. We need to make sure that the
     * range of transformed values doesn't contains 0.
     */
    private static boolean isZeroExcluded(final RenderedImage image,
            final double[] scales, final double[] offsets)
    {
        /*
         * We can't do any guarantee if pixel values are modifiable.
         */
        if (image instanceof WritableRenderedImage) {
            return false;
        }
        /*
         * If an "Extrema" operation is used somewhere in the image chain, ensure that it was
         * applied on an image with the same pixel values than the image we want to analyze.
         * Ensure also that no ROI was defined for the "Extrema" operation.
         */
        Object parent = image;
        while (parent instanceof PlanarImage) {
            final PlanarImage planar = (PlanarImage) image;
            if (parent instanceof RenderedOp) {
                final RenderedOp op = (RenderedOp) parent;
                final String name = op.getOperationName();
                if (name.equalsIgnoreCase("Extrema")) {
                    final int n = op.getNumParameters();
                    for (int i=0; i<n; i++) {
                        if (op.getObjectParameter(i) instanceof ROI) {
                            return false;
                        }
                    }
                    break;
                }
                if (!name.equalsIgnoreCase("Null") && !name.equalsIgnoreCase("Histogram")) {
                    return false;
                }
            }
            final int n = planar.getNumSources();
            if (n >= 2) return false;
            if (n == 0) break;
            parent = planar.getSourceObject(0);
        }
        /*
         * Apparently, there is nothing preventing us to query the "extrema" property. Note that
         * the above test did not guarantee that this property is defined - only that if defined,
         * it looks like suitable. Now ensure that the range after conversion does not includes 0.
         */
        final Object property = image.getProperty("extrema");
        if (!(property instanceof double[][])) {
            return false;
        }
        final double[][] extrema = (double[][]) property;
        if (extrema.length != 2) {
            return false;
        }
        for (int i=0; i<scales.length; i++) {
            final double scale  = scales [i];
            final double offset = offsets[i];
            double maximum = extrema[0][i] * scale + offset;
            double minimum = extrema[1][i] * scale + offset;
            if (minimum > maximum) {
                final double tmp = minimum;
                minimum = maximum;
                maximum = tmp;
            }
            if (!(minimum > 0 || maximum < 0)) { // Use '!' for catching NaN.
                return false;
            }
        }
        return true;
    }

    /**
     * Disposes all views and returns the remaining ones. Disposed views are removed from
     * this {@code ViewsManager}, but may be recreated if the user asks them again.
     * <p>
     * This method is invoked by {@link GridCoverage2D#dispose} method only.
     */
    public synchronized Collection<GridCoverage2D> dispose(final boolean force) {
        /*
         * Following loop will be executed as long as we have been able to dispose at least one
         * view. This is because some coverages can be disposed only after their dependency have
         * been disposed first. Since we don't know the dependency order, we just try the loop
         * again and again. The amount of values (5) is small enough to keep the cost small.
         */
        int disposed;
        do {
            disposed = 0;
            for (final Iterator<GridCoverage2D> it=views.values().iterator(); it.hasNext();) {
                final GridCoverage2D coverage = it.next();
                it.remove();
                disposed++;
            }
        } while (disposed != 0);
        return views.values();
    }
}
