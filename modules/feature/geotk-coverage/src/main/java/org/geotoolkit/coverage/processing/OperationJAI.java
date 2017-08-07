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
package org.geotoolkit.coverage.processing;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.Locale;
import java.util.Collections;
import java.io.Serializable;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import javax.measure.Unit;

import javax.media.jai.JAI;
import javax.media.jai.ImageLayout;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.processing.OperationNotFoundException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.InvalidGridGeometryException;
import org.geotoolkit.coverage.parameter.ImagingParameters;
import org.geotoolkit.coverage.parameter.ImagingParameterDescriptors;
import org.geotoolkit.referencing.operation.transform.DimensionFilter;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.iso.AbstractInternationalString;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.image.internal.ImageUtilities;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;


/**
 *
 * Wraps a JAI's {@link OperationDescriptor} for inter-operability with
 * <A HREF="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</A>.
 * This class help to leverage the rich set of JAI operators in an GeoAPI framework.
 * {@code OperationJAI} inherits operation name and argument types from {@link OperationDescriptor},
 * except the source argument type (usually <code>{@linkplain RenderedImage}.class</code>) which is
 * set to <code>{@linkplain GridCoverage2D}.class</code>. If there is only one source argument, it
 * will be renamed {@code "Source"} for better compliance with OpenGIS usage.
 * <p>
 * The entry point for applying an operation is the usual {@link #doOperation doOperation} method.
 * The default implementation forward the call to other methods for different bits of tasks,
 * resulting in the following chain of calls:
 * <p>
 * <blockquote><table>
 *   <tr><td>{@link #doOperation doOperation}:&nbsp;</td>
 *       <td>the entry point.</td></tr>
 *   <tr><td>{@link #resampleToCommonGeometry resampleToCommonGeometry}:&nbsp;</td>
 *       <td>reprojects all sources to the same coordinate reference system.</td></tr>
 *   <tr><td>{@link #deriveGridCoverage deriveGridCoverage}:&nbsp;</td>
 *       <td>gets the destination properties.</td></tr>
 *   <tr><td>{@link #deriveSampleDimension deriveSampleDimension}:&nbsp;</td>
 *       <td>gets the destination sample dimensions.</td></tr>
 *   <tr><td>{@link #deriveCategory deriveCategory}:&nbsp;</td>
 *       <td>gets the destination categories.</td></tr>
 *   <tr><td>{@link #deriveRange deriveRange}:&nbsp;</td>
 *       <td>gets the expected range of values.</td></tr>
 *   <tr><td>{@link #deriveUnit deriveUnit}:&nbsp;</td>
 *       <td>gets the destination units.</td></tr>
 *   <tr><td>{@link #createRenderedImage createRenderedImage}:&nbsp;</td>
 *       <td>the actual call to {@link JAI#createNS JAI.createNS}.</td></tr>
 * </table></blockquote>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @version 3.00
 *
 * @since 1.2
 * @module
 *
 * @deprecated The API of this class will change in a future Geotk release. Do not rely on it.
 */
@Deprecated
public class OperationJAI extends Operation2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5974520239347639965L;

    /**
     * The rendered mode for JAI operation, which is {@value}.
     *
     * @see RenderedRegistryMode#MODE_NAME
     */
    protected static final String RENDERED_MODE = RenderedRegistryMode.MODE_NAME;

    /**
     * The JAI's operation descriptor.
     */
    protected final OperationDescriptor operation;

    /**
     * Constructs a grid coverage operation from a JAI operation name. This convenience
     * constructor fetches the {@link OperationDescriptor} from the specified operation
     * name using the default {@link JAI} instance.
     *
     * @param operation JAI operation name (e.g. {@code "GradientMagnitude"}).
     * @throws OperationNotFoundException if no JAI descriptor was found for the given name.
     */
    public OperationJAI(final String operation) throws OperationNotFoundException {
        this(getOperationDescriptor(operation));
    }

    /**
     * Constructs a grid coverage operation backed by a JAI operation. The operation descriptor
     * must support the {@value #RENDERED_MODE} mode (which is the case for most JAI operations).
     *
     * @param operation The JAI operation descriptor.
     */
    public OperationJAI(final OperationDescriptor operation) {
        this(operation, new ImagingParameterDescriptors(operation));
    }

    /**
     * Constructs a grid coverage operation backed by a JAI operation. The operation descriptor
     * must supports the {@value #RENDERED_MODE} mode (which is the case for most JAI operations).
     *
     * @param operation  The JAI operation descriptor.
     * @param descriptor The OGC parameters descriptor.
     */
    protected OperationJAI(final OperationDescriptor operation,
                           final ParameterDescriptorGroup descriptor)
    {
        super(descriptor);
        this.operation = operation;
        ensureNonNull("operation", operation);
        ensureRenderedImage(operation.getDestClass(RENDERED_MODE));
        final Class<?>[] sourceClasses = operation.getSourceClasses(RENDERED_MODE);
        if (sourceClasses != null) {
            final int length = sourceClasses.length;
            assert length == operation.getNumSources();
            for (int i=0; i<length; i++) {
                ensureRenderedImage(sourceClasses[i]);
            }
        }
        assert super.getNumSources() == operation.getNumSources();
    }

    /**
     * Returns the operation descriptor for the specified JAI operation name. This method
     * uses the default {@link JAI} instance and looks for the {@value #RENDERED_MODE} mode.
     *
     * @param  name The operation name.
     * @return The operation descriptor for the given name.
     * @throws OperationNotFoundException if no JAI descriptor was found for the given name.
     *
     * @since 2.4
     */
    protected static OperationDescriptor getOperationDescriptor(final String name)
            throws OperationNotFoundException
    {
        final OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        OperationDescriptor operation = (OperationDescriptor) registry.getDescriptor(RENDERED_MODE, name);
        if (operation != null) {
            return operation;
        }
        if (name.startsWith("org.geotoolkit.") && registry.getDescriptor(RENDERED_MODE, "org.geotoolkit.Combine") == null) {
            try {
                // Try to register our operations.
                Registry.registerGeotoolkitServices(registry);
            } catch (RuntimeException e) {
                Logging.unexpectedException(AbstractCoverageProcessor.LOGGER,
                        OperationJAI.class, "getOperationDescriptor", e);
            }
            // Try to get it again.
            operation = (OperationDescriptor) registry.getDescriptor(RENDERED_MODE, name);
            if (operation != null) {
                return operation;
            }
        }
        throw new OperationNotFoundException(Errors.format(Errors.Keys.NoSuchOperation_1, name));
    }

    /**
     * Ensures that the specified class is assignable to {@link RenderedImage}.
     */
    private static void ensureRenderedImage(final Class<?> classe) throws IllegalArgumentException {
        if (!RenderedImage.class.isAssignableFrom(classe)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.IllegalClass_2, classe, RenderedImage.class));
        }
    }

    /**
     * Copies parameter values from the specified {@link ParameterValueGroup} to the
     * {@link ParameterBlockJAI}, except the sources.
     *
     * {@note It would be possible to use <code>ImagingParameters.parameters(...)</code> directly
     *        in some occasions. However, we perform an unconditional copy instead because some
     *        operations (e.g. <code>"GradientMagnitude"</code>) may change the values.}
     *
     * @param parameters The {@link ParameterValueGroup} to be copied.
     * @return A copy of the provided {@link ParameterValueGroup} as a JAI block.
     *
     * @see ImagingParameters#parameters
     *
     * @since 2.4
     */
    protected ParameterBlockJAI prepareParameters(final ParameterValueGroup parameters) {
        final ImagingParameters copy = (ImagingParameters) descriptor.createValue();
        final ParameterBlockJAI block = (ParameterBlockJAI) copy.parameters;
        org.apache.sis.parameter.Parameters.copy(parameters, copy);
        return block;
    }

    /**
     * Applies a process operation to a grid coverage.
     * The default implementation performs the following steps:
     * <p>
     * <ol>
     *   <li>Converts source Grid Coverages to their {@linkplain #getComputationView
     *       computation view} (typically {@linkplain ViewType#GEOPHYSICS geophysics}).</li>
     *
     *   <li>Converts every sources Coverages to the same {@linkplain CoordinateReferenceSystem
     *       Coordinate Reference System} (at least for the two-dimensional part) with the same
     *       "{@linkplain GridGeometry2D#getGridToCRS2D grid to CRS}" relationship.</li>
     *
     *   <li>Invokes {@link #deriveGridCoverage deriveGridCoverage}.
     *       The sources in the {@code ParameterBlock} are {@link RenderedImage} objects
     *       obtained from {@link GridCoverage2D#getRenderedImage()}.</li>
     *
     *   <li>If a change of view has been performed at step 1, converts the result back to the
     *       original view.</li>
     * </ol>
     *
     * @param  parameters List of name value pairs for the parameters required for the operation.
     * @param  hints A set of rendering hints, or {@code null} if none.
     * @return The result as a grid coverage.
     * @throws CoverageProcessingException If an error occurred while applying the operation.
     *
     * @see #deriveGridCoverage
     */
    @Override
    protected Coverage doOperation(final ParameterValueGroup parameters, final Hints hints)
            throws CoverageProcessingException
    {
        final ParameterBlockJAI block = prepareParameters(parameters);
        /*
         * Extracts the source grid coverages now as an array. The sources will be set in the
         * ParameterBlockJAI (as RenderedImages) later, after the reprojection performed in the
         * next block.
         */
        final String[] sourceNames = operation.getSourceNames();
        final GridCoverage2D[] sources = new GridCoverage2D[sourceNames.length];
        ViewType primarySourceType = extractSources(parameters, sourceNames, sources);
        /*
         * Ensures that all coverages use the same CRS and has the same 'gridToCRS' relationship.
         * After the reprojection, the method still checks all CRS in case the user overridden the
         * {@link #resampleToCommonGeometry} method.
         */
        resampleToCommonGeometry(sources, null, null, hints);
        GridCoverage2D coverage = sources[PRIMARY_SOURCE_INDEX];
        final CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        for (int i=0; i<sources.length; i++) {
            final GridCoverage2D source = sources[i];
            if (!Utilities.equalsIgnoreMetadata(crs, source.getCoordinateReferenceSystem2D()) ||
                !Utilities.equalsIgnoreMetadata(gridToCRS, source.getGridGeometry().getGridToCRS2D()))
            {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.IncompatibleGridGeometry));
            }
            block.setSource(sourceNames[i], source.getRenderedImage());
        }
        /*
         * Applies the operation. This delegates the work to the chain of 'deriveXXX' methods.
         */
        coverage = deriveGridCoverage(sources, new Parameters(crs, gridToCRS, block, hints));
        if (primarySourceType != null) {
            coverage = coverage.view(primarySourceType);
        }
        return coverage;
    }

    /**
     * Returns an error message for an unsupported CRS.
     */
    private static String unsupported(final CoordinateReferenceSystem crs) {
        return Errors.format(Errors.Keys.UnsupportedCrs_1, crs.getName().getCode());
    }

    /**
     * Returns a sub-coordinate reference system for the specified dimension range.
     * This method is for internal use by {@link #resampleToCommonGeometry}.
     *
     * @param  crs   The coordinate reference system to decompose.
     * @param  lower The first dimension to keep, inclusive.
     * @param  upper The last  dimension to keep, exclusive.
     * @return The sub-coordinate system, or {@code null} if {@code lower} is equals to {@code upper}.
     * @throws InvalidGridGeometryException if the CRS can't be separated.
     */
    private static CoordinateReferenceSystem getSubCRS(final CoordinateReferenceSystem crs,
                                                       final int lower, final int upper)
            throws InvalidGridGeometryException
    {
        if (lower == upper) {
            return null;
        }
        final CoordinateReferenceSystem candidate = org.apache.sis.referencing.CRS.getComponentAt(crs, lower, upper);
        if (candidate == null) {
            throw new InvalidGridGeometryException(unsupported(crs));
        }
        return candidate;
    }

    /**
     * Ensures that the source and target dimensions are the same. This method is for internal
     * use by {@link #resampleToCommonGeometry}.
     */
    private static void ensureStableDimensions(final DimensionFilter filter)
            throws InvalidGridGeometryException
    {
        final int[] source = filter.getSourceDimensions(); Arrays.sort(source);
        final int[] target = filter.getTargetDimensions(); Arrays.sort(target);
        if (!Arrays.equals(source, target)) {
            throw new InvalidGridGeometryException(Errors.format(Errors.Keys.UnsupportedTransform));
        }
    }

    /**
     * Resamples all sources grid coverages to the same {@linkplain GridGeometry2D two-dimensional
     * geometry} before to apply the {@linkplain #operation}. This method is invoked automatically
     * by the {@link #doOperation doOperation} method.
     * <p>
     * Only the two-dimensional part is reprojected (usually the spatial component of a CRS).
     * Extra dimensions (if any) are left unchanged. Extra dimensions are typically time axis
     * or depth. Note that extra dimensions are <strong>not</strong> forced to a common geometry;
     * only the two dimensions that apply to a {@link javax.media.jai.PlanarImage} are. This is
     * because the extra dimensions don't need to be compatible for all operations. For example
     * if a source image is a slice in a time series, a second source image could be a slice in
     * the frequency representation of this time series.
     * <p>
     * Subclasses should override this method if they want to specify target
     * {@linkplain GridGeometry2D Grid Geometry} and
     * {@linkplain CoordinateReferenceSystem Coordinate Reference System} different than the
     * default ones. For example if a subclass wants to force all images to be referenced in a
     * {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS 84} CRS, then
     * it may overrides this method as below:
     *
     * {@preformat java
     *     protected void resampleToCommonGeometry(...) {
     *        crs2D = CommonCRS.WGS84.normalizedGeographic();
     *        super.resampleToCommonGeometry(sources, crs2D, gridToCrs2D, hints);
     *     }
     * }
     *
     * @param sources     The source grid coverages to resample. This array is updated in-place as
     *                    needed (for example if a grid coverage is replaced by a projected one).
     * @param crs2D       The target coordinate reference system to use, or {@code null} for a
     *                    default one.
     * @param gridToCrs2D The target "<cite>grid to CRS"</cite> transform, or {@code null}
     *                    for a default one.
     * @param hints       The rendering hints, or {@code null} if none.
     *
     * @throws InvalidGridGeometryException If a source coverage has an unsupported grid geometry.
     * @throws CannotReprojectException If a grid coverage can't be resampled for some other reason.
     */
    protected void resampleToCommonGeometry(final GridCoverage2D[] sources,
                                            CoordinateReferenceSystem crs2D,
                                            MathTransform2D gridToCrs2D,
                                            final Hints hints)
            throws InvalidGridGeometryException, CannotReprojectException
    {
        if (sources == null || sources.length == 0) {
            return; // Nothing to reproject.
        }
        /*
         * Ensures that the target CRS is two-dimensional. If no target CRS were specified,
         * uses the CRS of the primary source. The math transform must be 2D too, but this
         * is ensured by the interface type (MathTransform2D).
         */
        final GridCoverage2D primarySource = sources[PRIMARY_SOURCE_INDEX];
        if (crs2D == null) {
            if (gridToCrs2D == null && sources.length == 1) {
                return; // No need to reproject.
            }
            crs2D = primarySource.getCoordinateReferenceSystem2D();
        } else try {
            crs2D = CRSUtilities.getCRS2D(crs2D);
        } catch (TransformException exception) {
            throw new CannotReprojectException(unsupported(crs2D), exception);
        }
        if (gridToCrs2D == null) {
            gridToCrs2D = primarySource.getGridGeometry().getGridToCRS2D();
        }
        /*
         * 'crs2D' is the two dimensional part of the target CRS. Now for each source coverages,
         * substitute their two-dimensional CRS by this 'crs2D'. A source may have more than two
         * dimensions. For example it may have a time or a depth axis. In such case, their "head"
         * and "tail" CRS will be preserved before and after 'crs2D'.
         */
        final AbstractCoverageProcessor processor = getProcessor(hints);
        for (int i=0; i<sources.length; i++) {
            final GridCoverage2D            source    = sources[i];
            final GridGeometry2D            geometry  = source.getGridGeometry();
            final CoordinateReferenceSystem srcCrs2D  = source.getCoordinateReferenceSystem2D();
            final CoordinateReferenceSystem sourceCRS = source.getCoordinateReferenceSystem();
            final CoordinateReferenceSystem targetCRS;
            if (Utilities.equalsIgnoreMetadata(crs2D, srcCrs2D)) {
                targetCRS = sourceCRS; // No reprojection needed for this source coverage.
            } else {
                /*
                 * Replaces the 2D part in the source CRS, while preserving the leading and
                 * trailing CRS (if any). Leading and trailing CRS are typically time axis or
                 * depth axis. Current implementation requires that the 2D part appears in two
                 * consecutive dimensions. Those dimensions are (0,1) in the majority of cases.
                 */
                final int  lowerDim = Math.min(geometry.axisDimensionX, geometry.axisDimensionY);
                final int  upperDim = Math.max(geometry.axisDimensionX, geometry.axisDimensionY)+1;
                final int sourceDim = sourceCRS.getCoordinateSystem().getDimension();
                if (upperDim-lowerDim != srcCrs2D.getCoordinateSystem().getDimension()) {
                    throw new InvalidGridGeometryException(unsupported(sourceCRS));
                }
                final CoordinateReferenceSystem headCRS = getSubCRS(sourceCRS, 0, lowerDim);
                final CoordinateReferenceSystem tailCRS = getSubCRS(sourceCRS, upperDim, sourceDim);
                CoordinateReferenceSystem[]  components = new CoordinateReferenceSystem[3];
                int count = 0;
                if (headCRS != null) components[count++] = headCRS;
                                     components[count++] = crs2D;
                if (tailCRS != null) components[count++] = tailCRS;
                components = ArraysExt.resize(components, count);
                if (count == 1) {
                    targetCRS = components[0];
                } else try {
                    targetCRS = FactoryFinder.getCRSFactory(hints).createCompoundCRS(
                                Collections.singletonMap(IdentifiedObject.NAME_KEY,
                                crs2D.getName().getCode()), components);
                } catch (FactoryException exception) {
                    throw new CannotReprojectException(exception);
                }
            }
            /*
             * Constructs the 'gridToCRS' transform in the same way than the CRS:
             * leading and trailing dimensions (if any) are preserved.
             */
            final MathTransform toSource2D = geometry.getGridToCRS2D();
            final MathTransform toSource   = geometry.getGridToCRS();
            MathTransform toTarget;
            if (Utilities.equalsIgnoreMetadata(gridToCrs2D, toSource2D)) {
                toTarget  = toSource;
            } else {
                /*
                 * Replaces the 2D part in the source MT, while preserving the leading and
                 * trailing MT (if any). This is similar to the 'lowerDim' and 'upperDim'
                 * variables in the CRS case above, except that we operate on "grid" space
                 * rather than "axis" spaces. The index are usually the same, but not always.
                 */
                final int  lowerDim = Math.min(geometry.gridDimensionX, geometry.gridDimensionY);
                final int  upperDim = Math.max(geometry.gridDimensionX, geometry.gridDimensionY)+1;
                final int sourceDim = toSource.getSourceDimensions();
                if (upperDim-lowerDim != toSource2D.getSourceDimensions()) {
                    throw new InvalidGridGeometryException(Errors.format(Errors.Keys.UnsupportedTransform));
                }
                final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(hints);
                final DimensionFilter       filter = new DimensionFilter(toSource, factory);
                toTarget = gridToCrs2D;
                try {
                    if (lowerDim != 0) {
                        filter.addSourceDimensionRange(0, lowerDim);
                        MathTransform step = filter.separate();
                        ensureStableDimensions(filter);
                        step = factory.createPassThroughTransform(0, step, sourceDim-lowerDim);
                        toTarget = factory.createConcatenatedTransform(step, toTarget);
                    }
                    if (upperDim != sourceDim) {
                        filter.clear();
                        filter.addSourceDimensionRange(upperDim, sourceDim);
                        MathTransform step = filter.separate();
                        ensureStableDimensions(filter);
                        step = factory.createPassThroughTransform(upperDim, step, 0);
                        toTarget = factory.createConcatenatedTransform(toTarget, step);
                    }
                } catch (FactoryException exception) {
                    throw new CannotReprojectException(Errors.format(
                            Errors.Keys.CantReprojectCoverage_1, source.getName()), exception);
                }
            }
            final GridGeometry2D targetGeom = new GridGeometry2D(null, toTarget, targetCRS);
            final ParameterValueGroup param = processor.getOperation("Resample").getParameters();
            param.parameter("Source")                   .setValue(source);
            param.parameter("GridGeometry")             .setValue(targetGeom);
            param.parameter("CoordinateReferenceSystem").setValue(targetCRS);
            sources[i] = (GridCoverage2D) processor.doOperation(param);
        }
    }

    /**
     * Applies a JAI operation to a grid coverage. This method is invoked automatically by
     * the {@link #doOperation doOperation} method. The default implementation performs the
     * following steps:
     * <p>
     * <ul>
     *   <li>Gets the {@linkplain GridSampleDimension Sample Dimensions} for the target images
     *       by invoking the {@link #deriveSampleDimension deriveSampleDimension} method.</li>
     *   <li>Applies the JAI operation using {@link #createRenderedImage createRenderedImage}.</li>
     *   <li>Wraps the result in a {@link GridCoverage2D} object.</li>
     * </ul>
     *
     * @param  sources The source coverages.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return The result as a grid coverage.
     *
     * @see #doOperation
     * @see #deriveSampleDimension
     * @see JAI#createNS
     */
    protected GridCoverage2D deriveGridCoverage(
            final GridCoverage2D[] sources, final Parameters parameters)
    {
        GridCoverage2D primarySource = sources[PRIMARY_SOURCE_INDEX];
        /*
         * Gets the target SampleDimensions. If they are identical to the SampleDimensions of
         * one of the source GridCoverage2D, then this GridCoverage2D will be used at the primary
         * source. It will affect the target GridCoverage2D's name and the visible band. Then,
         * a new color model will be constructed from the new SampleDimensions, taking in
         * account the visible band.
         */
        final GridSampleDimension[][] list = new GridSampleDimension[sources.length][];
        for (int i=0; i<list.length; i++) {
            list[i] = sources[i].getSampleDimensions();
        }
        final GridSampleDimension[] sampleDims = deriveSampleDimension(list, parameters);
        int primarySourceIndex = -1;
        for (int i=0; i<list.length; i++) {
            if (Arrays.equals(sampleDims, list[i])) {
                primarySource = sources[i];
                primarySourceIndex = i;
                break;
            }
        }
        /*
         * Sets the rendering hints image layout. Only the following properties will be set:
         *
         *     - Color model
         */
        RenderingHints hints = ImageUtilities.getRenderingHints(parameters.getSource());
        ImageLayout   layout = (hints!=null) ? (ImageLayout)hints.get(JAI.KEY_IMAGE_LAYOUT) : null;
        if (layout==null || !layout.isValid(ImageLayout.COLOR_MODEL_MASK)) {
            if (sampleDims!=null && sampleDims.length!=0) {
                int visibleBand = CoverageUtilities.getVisibleBand(primarySource.getRenderedImage());
                if (visibleBand >= sampleDims.length) {
                    visibleBand = 0;
                }
                final ColorModel colors;
                colors = sampleDims[visibleBand].getColorModel(visibleBand, sampleDims.length);
                if (colors != null) {
                    if (layout == null) {
                        layout = new ImageLayout();
                    }
                    layout = layout.setColorModel(colors);
                }
            }
        }
        if (layout != null) {
            if (hints == null) {
                hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
            } else {
                hints.put(JAI.KEY_IMAGE_LAYOUT, layout);
            }
        }
        if (parameters.hints != null) {
            if (hints != null) {
                hints.add(parameters.hints); // May overwrite the image layout we have just set.
            } else {
                hints = parameters.hints;
            }
        }
        /*
         * Performs the operation using JAI and construct the new grid coverage.
         * Uses the coordinate system from the main source coverage in order to
         * preserve the extra dimensions (if any). The first two dimensions should
         * be equal to the coordinate system set in the 'parameters' block.
         */
        final InternationalString      name = deriveName(sources, primarySourceIndex, parameters);
        final CoordinateReferenceSystem crs = primarySource.getCoordinateReferenceSystem();
        final MathTransform           toCRS = primarySource.getGridGeometry().getGridToCRS();
        final RenderedImage            data = createRenderedImage(parameters.parameters, hints);
        final Map<String,?>      properties = getProperties(data,crs,name,toCRS,sources,parameters);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(name);
        gcb.setRenderedImage(data);
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(toCRS);
        gcb.setSampleDimensions(sampleDims);
        gcb.setSources(sources);
        gcb.setProperties(properties);
        return gcb.getGridCoverage2D();
    }

    /**
     * Prepares the properties to be given to the coverage created by the
     * {@link #deriveGridCoverage deriveGridCoverage} method. The default
     * implementation returns {@code null}.
     *
     * @param data       The {@link RenderedImage} created by this operation.
     * @param crs        The coordinate reference system assigned to the coverage this
     *                   {@code OperationJAI} will produce.
     * @param name       The name assigned to the coverage this {@code OperationJAI} will produce.
     * @param gridToCRS  The {@linkplain MathTransform transform} from grid to {@code crs} to be
     *                   assigned to the coverage this {@link OperationJAI} will produce.
     * @param sources    The sources to be assigned to the coverage this {@link OperationJAI} will
     *                   produce.
     * @param parameters The parameters that were used by this {@link OperationJAI}.
     * @return A {@code Map} with the properties generated by this {@link OperationJAI},
     *         or null if we haven't any.
     *
     * @since 2.4
     */
    protected Map<String,?> getProperties(RenderedImage data, CoordinateReferenceSystem crs,
                                          InternationalString name, MathTransform gridToCRS,
                                          GridCoverage2D[] sources, Parameters parameters)
    {
        return null;
    }

    /**
     * Returns the index of the quantitative category, providing that there is
     * one and only one quantitative category. If {@code categories} contains 0,
     * 2 or more quantitative category, then this method returns {@code -1}.
     *
     * @param categories The categories to test.
     * @return The index of the quantitative category, or {@code -1} if none can be chosen.
     *
     * @since 2.4
     */
    protected static int getQuantitative(final Category[] categories) {
        int index = -1;
        for (int i=0; i<categories.length; i++) {
            if (categories[i].isQuantitative()) {
                if (index >= 0) {
                    return -1;
                }
                index = i;
            }
        }
        return index;
    }

    /**
     * Returns the {@linkplain GridSampleDimension sample dimensions} for the target
     * {@linkplain GridCoverage2D grid coverage}. This method is invoked automatically by
     * {@link #deriveGridCoverage deriveGridCoverage} with a {@code bandLists} argument
     * initialized as below:
     * <p>
     * <ul>
     *   <li>The {@code bandLists} array length is equals to the number of source coverages.</li>
     *   <li>The <code>bandLists[<var>i</var>]</code> array length is equals to the number of
     *       sample dimensions in the source coverage <var>i</var>.</li>
     *   <li>The sample dimension for a band at index <var>band</var> in the source at index
     *       <var>source</var> is {@code bandLists[source][band]}.</li>
     * </ul>
     * <p>
     * This method shall return an array with a length equals to the number of bands in the target
     * image. If the sample dimensions can't be determined, then this method is allowed to returns
     * {@code null}.
     * <p>
     * The default implementation iterates among all bands and invokes the {@link #deriveCategory
     * deriveCategory} and {@link #deriveUnit deriveUnit} methods for each of them. Subclasses
     * should override this method if they know a more accurate algorithm for determining sample
     * dimensions.
     *
     * @param  bandLists The set of sample dimensions for each source {@link GridCoverage2D}s.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return The sample dimensions for each band in the destination image, or {@code null}
     *         if unknown.
     *
     * @see #deriveCategory
     * @see #deriveUnit
     */
    protected GridSampleDimension[] deriveSampleDimension(
            final GridSampleDimension[][] bandLists, final Parameters parameters)
    {
        /*
         * Computes the number of bands. Sources with only 1 band are treated as a special case:
         * their unique band is applied to all bands in other sources. If sources don't have the
         * same number of bands, then this method returns {@code null} since we don't know how to
         * handle those cases.
         */
        int numBands = 1;
        for (int i=0; i<bandLists.length; i++) {
            final int nb = bandLists[i].length;
            if (nb != 1) {
                if (numBands!=1 && nb!=numBands) {
                    return null;
                }
                numBands = nb;
            }
        }
        /*
         * Iterates among all bands. The 'result' array will contains SampleDimensions created
         * during the iteration for each individual band. The 'XS' suffix designates temporary
         * arrays of categories and units accross all sources for one particular band.
         */
        final GridSampleDimension[] result = new GridSampleDimension[numBands];
        final Category[]        categoryXS = new Category[bandLists.length];
        final Unit<?>[]             unitXS = new Unit<?>[bandLists.length];
        while (--numBands >= 0) {
            GridSampleDimension sampleDim = null;
            Category[]      categoryArray = null;
            int       indexOfQuantitative = 0;
            assert PRIMARY_SOURCE_INDEX == 0; // See comment below.
            for (int i=bandLists.length; --i>=0;) {
                /*
                 * Iterates among all sources (i) for the current band. We iterate
                 * sources in reverse order because the primary source MUST be the
                 * last one iterated, in order to have proper values for variables
                 * 'sampleDim', 'categoryArray' and 'indexOfQuantitative' after the
                 * loop.
                 */
                final GridSampleDimension[] allBands = bandLists[i];
                sampleDim = allBands[allBands.length == 1 ? 0 : numBands];
                final List<Category> categories = sampleDim.getCategories();
                // GridSampleDimension may contain no categories
                if (categories == null) {
                    result[numBands] = sampleDim;
                    continue;
                }
                categoryArray       = (Category[]) categories.toArray(); // NOSONAR
                indexOfQuantitative = getQuantitative(categoryArray);
                if (indexOfQuantitative < 0) {
                    return null;
                }
                unitXS    [i] = sampleDim.getUnits();
                categoryXS[i] = categoryArray[indexOfQuantitative];
            }
            if (categoryArray == null) {
                continue;
            }
            final Category oldCategory = categoryArray[indexOfQuantitative];
            final Unit<?>  oldUnit     = sampleDim.getUnits();
            final Category newCategory = deriveCategory(categoryXS, parameters);
            final Unit<?>  newUnit     = deriveUnit(unitXS, parameters);
            if (newCategory == null) {
                return null;
            }
            if (!oldCategory.equals(newCategory) || !Objects.equals(oldUnit, newUnit)) {
                /*
                 * Create a new sample dimension. Note that we use a null title, not the same
                 * title than the original sample dimension, because the new sample dimension
                 * may be quite different. For example the original sample dimension may be
                 * about "Temperature" in °C units, and the new one about "Gradient magnitude
                 * of Temperature" in °C/km units. The GridSampleDimension constructor will
                 * infers the title from what looks like the "main" category.
                 */
                final CharSequence title = null;
                categoryArray[indexOfQuantitative] = newCategory;
                result[numBands] = new GridSampleDimension(title, categoryArray, newUnit);
            } else {
                // Reuse the category list from the primary source.
                result[numBands] = sampleDim;
            }
        }
        return result;
    }

    /**
     * Returns the quantitative category for a single {@linkplain GridSampleDimension sample dimension}
     * in the target {@linkplain GridCoverage2D grid coverage}. This method is invoked automatically
     * by the {@link #deriveSampleDimension deriveSampleDimension} method for each band in the
     * target image. The default implementation creates a default category from the target range
     * of values returned by {@link #deriveRange deriveRange}.
     *
     * @param  categories The quantitative categories from every sources. For unary operations
     *         like {@code "GradientMagnitude"}, this array has a length of 1. For binary
     *         operations like {@code "add"} and {@code "multiply"}, this array has a length of 2.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return The quantitative category to use in the destination image, or {@code null} if unknown.
     */
    protected Category deriveCategory(final Category[] categories, final Parameters parameters) {
        final NumberRange<?>[] ranges = new NumberRange<?>[categories.length];
        for (int i=0; i<ranges.length; i++) {
            ranges[i] = categories[i].getRange();
        }
        final NumberRange<?> range = deriveRange(ranges, parameters);
        if (range != null) {
            final Category category = categories[PRIMARY_SOURCE_INDEX];
            return new Category(category.getName(), category.getColors(),
                    category.geophysics(false).getRange(), range).geophysics(true);
        }
        return null;
    }

    /**
     * Returns the range of value for a single {@linkplain GridSampleDimension sample dimension}
     * in the target {@linkplain GridCoverage2D grid coverage}. This method is invoked automatically
     * by the {@link #deriveCategory deriveCategory} method for each band in the target image.
     * Subclasses should override this method in order to compute the target range of values.
     * For example, the {@code "add"} operation may implements this method as below:
     *
     * {@preformat java
     *     double min = ranges[0].getMinimum() + ranges[1].getMinimum();
     *     double max = ranges[0].getMaximum() + ranges[1].getMaximum();
     *     return NumberRange.create(min, max);
     * }
     *
     * @param  ranges The range of values from every sources. For unary operations like
     *         {@code "GradientMagnitude"}, this array has a length of 1. For binary operations
     *         like {@code "add"} and {@code "multiply"}, this array has a length of 2.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return The range of values to use in the destination image, or {@code null} if unknown.
     */
    protected NumberRange<?> deriveRange(final NumberRange<?>[] ranges, final Parameters parameters) {
        return null;
    }

    /**
     * Returns the unit of data for a single {@linkplain GridSampleDimension sample dimension} in the
     * target {@linkplain GridCoverage2D grid coverage}. This method is invoked automatically by
     * the {@link #deriveSampleDimension deriveSampleDimension} method for each band in the target
     * image. Subclasses should override this method in order to compute the target units from the
     * source units. For example a {@code "multiply"} operation may implement this method as below:
     *
     * {@preformat java
     *     if (units[0] != null && units[1] != null) {
     *         return units[0].times(units[1]);
     *     } else {
     *         return super.deriveUnit(units, cs, parameters);
     *     }
     * }
     *
     * @param  units The units from every sources. For unary operations like
     *         {@code "GradientMagnitude"}, this array has a length of 1. For binary operations
     *         like {@code "add"} and {@code "multiply"}, this array has a length of 2.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return The unit of data in the destination image, or {@code null} if unknown.
     */
    protected Unit<?> deriveUnit(final Unit<?>[] units, final Parameters parameters) {
        return null;
    }

    /**
     * Returns a name for the target {@linkplain GridCoverage2D grid coverage} based on the given
     * sources. This method is invoked once by the {@link #deriveGridCoverage deriveGridCoverage}
     * method. The default implementation returns the operation name followed by the source name
     * between parenthesis, for example "<cite>GradientMagnitude(Sea Surface Temperature)</cite>".
     *
     * @param  sources The sources grid coverage.
     * @param  primarySourceIndex The index of what seems to be the primary source, or {@code -1}
     *         if none of unknown.
     * @param  parameters Parameters, rendering hints and coordinate reference system to use.
     * @return A name for the target grid coverage.
     */
    protected InternationalString deriveName(final GridCoverage2D[] sources,
            final int primarySourceIndex, final Parameters parameters)
    {
        final InternationalString[] names;
        if (primarySourceIndex >= 0) {
            names = new InternationalString[] {
                sources[primarySourceIndex].getName()
            };
        } else {
            names = new InternationalString[sources.length];
            for (int i=0; i<names.length; i++) {
                names[i] = sources[i].getName();
            }
        }
        return new Name(getName(), names);
    }

    /**
     * A localized name for the default implementation of {@link OperationJAI#deriveName}.
     */
    private static final class Name extends AbstractInternationalString implements Serializable {
        /** Serial number for cross-versions compatibility. */
        private static final long serialVersionUID = -8096255331549347383L;

        /** The operation name. */
        private final String operation;

        /** Names of source grid coverages. */
        private final InternationalString[] sources;

        /** Constructs a name from the given source names. */
        public Name(final String operation, final InternationalString[] sources) {
            this.operation = operation;
            this.sources   = sources;
        }

        /** Returns a string localized in the given locale. */
        @Override public String toString(final Locale locale) {
            final StringBuilder buffer = new StringBuilder(operation);
            buffer.append('(');
            for (int i=0; i<sources.length; i++) {
                if (i != 0) {
                    buffer.append(", ");
                }
                buffer.append(sources[i].toString(locale));
            }
            return buffer.append(')').toString();
        }
    }

    /**
     * Applies the JAI operation. The operation name can be fetch from {@link #operation}.
     * The JAI instance to use can be fetch from {@link #getJAI}. The default implementation
     * returns the following:
     *
     * {@preformat java
     *     getJAI(hints).createNS(operation.getName(), parameters, hints)
     * }
     *
     * Subclasses may override this method in order to invokes a different JAI operation
     * according the parameters.
     *
     * @param parameters The parameters to be given to JAI.
     * @param hints The rendering hints to be given to JAI.
     * @return The result of JAI operation using the given parameters and hints.
     */
    protected RenderedImage createRenderedImage(final ParameterBlockJAI parameters,
                                                final RenderingHints    hints)
    {
        return getJAI(hints).createNS(operation.getName(), parameters, hints);
    }

    /**
     * Returns the {@link JAI} instance to use for operations on {@link RenderedImage}.
     * If no JAI instance is defined for the {@link Hints#JAI_INSTANCE} key, then the
     * default instance is returned.
     *
     * @param  hints The rendering hints, or {@code null} if none.
     * @return The JAI instance to use (never {@code null}).
     */
    public static JAI getJAI(final RenderingHints hints) {
        if (hints != null) {
            final Object value = hints.get(Hints.JAI_INSTANCE);
            if (value instanceof JAI) {
                return (JAI) value;
            }
        }
        return JAI.getDefaultInstance();
    }

    /**
     * Compares the specified object with this operation for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final OperationJAI that = (OperationJAI) object;
            return Objects.equals(this.operation, that.operation);
        }
        return false;
    }

    /**
     * A block of parameters for a {@link GridCoverage2D} processed by a {@link OperationJAI}.
     * This parameter is given to the following methods:
     * <p>
     * <ul>
     *   <li>{@link OperationJAI#deriveSampleDimension deriveSampleDimension}</li>
     *   <li>{@link OperationJAI#deriveCategory deriveCategory}</li>
     *   <li>{@link OperationJAI#deriveUnit deriveUnit}</li>
     * </ul>
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.2
     * @module
     */
    protected static final class Parameters {
        /**
         * The two dimensional coordinate reference system for all sources and the
         * destination {@link GridCoverage2D}. Sources coverages will be projected in
         * this CRS as needed.
         */
        public final CoordinateReferenceSystem crs;

        /**
         * The "<cite>grid to CRS"</cite> transform common to all source grid coverages.
         */
        public final MathTransform2D gridToCRS;

        /**
         * The parameters to be given to the {@link JAI#createNS} method.
         */
        public final ParameterBlockJAI parameters;

        /**
         * The rendering hints to be given to the {@link JAI#createNS} method.
         * The {@link JAI} instance to use for the {@code createNS} call will
         * be fetch from the {@link Hints#JAI_INSTANCE} key.
         */
        public final Hints hints;

        /**
         * Constructs a new parameter block with the specified values.
         */
        Parameters(final CoordinateReferenceSystem crs,
                   final MathTransform2D     gridToCRS,
                   final ParameterBlockJAI   parameters,
                   final Hints               hints)
        {
            this.crs        = crs;
            this.gridToCRS  = gridToCRS;
            this.parameters = parameters;
            this.hints      = hints;
        }

        /**
         * Returns the first source image, or {@code null} if none.
         */
        final RenderedImage getSource() {
            final int n = parameters.getNumSources();
            for (int i=0; i<n; i++) {
                final Object source = parameters.getSource(i);
                if (source instanceof RenderedImage) {
                    return (RenderedImage) source;
                }
            }
            return null;
        }
    }
}
