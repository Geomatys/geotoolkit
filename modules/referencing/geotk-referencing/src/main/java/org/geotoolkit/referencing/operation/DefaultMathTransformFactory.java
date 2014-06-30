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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;
import java.text.ParseException;
import javax.measure.unit.Unit;
import javax.measure.quantity.Length;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.io.wkt.Symbols;
import org.geotoolkit.io.wkt.MathTransformParser;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.geotoolkit.referencing.factory.ReferencingFactory;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;
import org.geotoolkit.internal.referencing.ParameterizedAffine;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArraysExt;

import static org.apache.sis.util.iso.DefaultNameSpace.DEFAULT_SEPARATOR;


/**
 * Low level factory for creating {@linkplain MathTransform math transforms}. Many high
 * level GIS applications will never need to use this factory directly; they can use a
 * {@linkplain CoordinateOperationFactory coordinate operation factory} instead. However,
 * the {@code MathTransformFactory} interface can be used directly by applications that wish
 * to transform other types of coordinates (e.g. color coordinates, or image pixel coordinates).
 * <p>
 * A {@linkplain MathTransform math transform} is an object that actually does the work of
 * applying formulae to coordinate values. The math transform does not know or care how the
 * coordinates relate to positions in the real world. This lack of semantics makes implementing
 * {@code MathTransformFactory} significantly easier than it would be otherwise. For example the
 * affine transform applies a matrix to the coordinates without knowing how what it is doing
 * relates to the real world. So if the matrix scales <var>Z</var> values by a factor of 1000,
 * then it could be converting meters into millimeters, or it could be converting kilometers
 * into meters.
 * <p>
 * Because math transforms have low semantic value (but high mathematical value), programmers
 * who do not have much knowledge of how GIS applications use coordinate systems, or how those
 * coordinate systems relate to the real world can implement {@code MathTransformFactory}. The
 * low semantic content of math transforms also means that they will be useful in applications
 * that have nothing to do with GIS coordinates. For example, a math transform could be used to
 * map color coordinates between different color spaces, such as converting (red, green, blue)
 * colors into (hue, light, saturation) colors.
 * <p>
 * Since a math transform does not know what its source and target coordinate systems mean, it
 * is not necessary or desirable for a math transform object to keep information on its source
 * and target coordinate systems.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 1.2
 * @module
 */
@ThreadSafe
public class DefaultMathTransformFactory extends ReferencingFactory implements MathTransformFactory {
    /**
     * The hints to provide to math transform providers. Null for now, but may be non-null
     * in some future version.
     */
    private static final Hints HINTS = null;

    /**
     * Thread-local variables used by {@link DefaultMathTransformFactory}.
     */
    private static final class Variables {
        /**
         * The object to use for parsing <cite>Well-Known Text</cite> (WKT) strings.
         */
        MathTransformParser parser;

        /**
         * The code which was used for requesting {@link #lastProvider}.
         */
        String lastCode;

        /**
         * The last value returned by {@link #getProvider}. Stored as an
         * optimization since the same provider is often asked many times.
         */
        MathTransformProvider lastProvider;

        /**
         * The operation method for the last transform created.
         * This is often, but not necessarily, the same than {@link #lastProvider}.
         */
        OperationMethod lastMethod;
    }

    /**
     * A set of thread-local variables.
     */
    private final ThreadLocal<Variables> variables;

    /**
     * A pool of math transform. This pool is used in order to
     * returns instance of existing math transforms when possible.
     */
    private final WeakHashSet<MathTransform> pool;

    /**
     * The service registry for finding {@link MathTransformProvider} implementations.
     */
    private final FactoryRegistry registry;

    /**
     * The available providers extracted from the {@linkplain #registry}.
     * Will be reset to {@code null} when {@link #scanForPlugins()} is invoked.
     */
    private volatile MathTransformProvider[] providers;

    /**
     * Constructs a default {@link MathTransform math transform} factory.
     */
    public DefaultMathTransformFactory() {
        registry  = new FactoryRegistry(MathTransformProvider.class);
        pool      = new WeakHashSet<>(MathTransform.class);
        variables = new ThreadLocal<>();
    }

    /**
     * Returns the vendor responsible for creating this factory implementation. Many implementations
     * may be available for the same factory interface. The default implementation returns
     * {@linkplain Citations#GEOTOOLKIT Geotoolkit.org}.
     *
     * @return The vendor for this factory implementation.
     */
    @Override
    public Citation getVendor() {
        return Citations.GEOTOOLKIT;
    }

    /**
     * Returns all available providers. <strong>Do not modify the returned array</strong>,
     * because this method does not clone it.
     */
    private MathTransformProvider[] getAvailableMethods() {
        MathTransformProvider[] methods = providers;
        if (methods == null) {
            // Double-checked locking. We a deprecated technic prior Java 5.
            // Okay since Java 5 provided that the variable is volatile.
            synchronized (registry) {
                methods = providers;
                if (methods == null) {
                    final Iterator<MathTransformProvider> it = registry.getServiceProviders(
                            MathTransformProvider.class, null, HINTS, null);
                    int count = 0;
                    methods = new MathTransformProvider[64];
                    while (it.hasNext()) {
                        if (count == methods.length) {
                            methods = Arrays.copyOf(methods, count*2);
                        }
                        methods[count++] = it.next();
                    }
                    providers = methods = ArraysExt.resize(methods, count);
                }
            }
        }
        return methods;
    }

    /**
     * Returns a set of available methods for {@linkplain MathTransform math transforms}. For
     * each element in this set, the {@linkplain OperationMethod#getName operation method name}
     * must be known to the {@link #getDefaultParameters} method in this factory.
     * The set of available methods is implementation dependent.
     *
     * @param  type <code>{@linkplain SingleOperation}.class</code> for fetching all operation methods,
     *         or <code>{@linkplain Projection}.class</code> for fetching only map projection
     *         methods.
     * @return All {@linkplain MathTransform math transform} methods available in this factory.
     *
     * @see #getDefaultParameters
     * @see #createParameterizedTransform
     */
    @Override
    public Set<OperationMethod> getAvailableMethods(final Class<? extends SingleOperation> type) {
        return new OperationMethodSet(getAvailableMethods(), type);
    }

    /**
     * Returns the operation method used for the latest call to
     * {@link #createParameterizedTransform createParameterizedTransform}
     * in the currently running thread. Returns {@code null} if not applicable.
     *
     * @see #createParameterizedTransform
     *
     * @since 2.5
     */
    @Override
    public OperationMethod getLastMethodUsed() {
        final Variables localVariables = variables.get();
        return (localVariables != null) ? localVariables.lastMethod : null;
    }

    /**
     * Returns the operation method for the specified name.
     *
     * @param  name The case insensitive {@linkplain Identifier#getCode identifier code}
     *         of the operation method to search for (e.g. {@code "Transverse_Mercator"}).
     * @return The operation method.
     * @throws NoSuchIdentifierException if there is no operation method registered for the
     *         specified name.
     *
     * @since 2.2
     */
    public OperationMethod getOperationMethod(String name) throws NoSuchIdentifierException {
        return getProvider(name);
    }

    /**
     * Returns the math transform provider for the specified operation method.
     * This provider can be used in order to query parameter for a method name
     * (e.g. {@code getProvider("Transverse_Mercator").getParameters()}), or
     * any of the alias in a given locale.
     *
     * @param  method The case insensitive {@linkplain Identifier#getCode identifier code}
     *         of the operation method to search for (e.g. {@code "Transverse_Mercator"}).
     * @return The math transform provider.
     * @throws NoSuchIdentifierException if there is no provider registered for the specified
     *         method.
     */
    private MathTransformProvider getProvider(final String method) throws NoSuchIdentifierException {
        Variables localVariables = variables.get();
        if (localVariables != null && method.equals(localVariables.lastCode)) {
            return localVariables.lastProvider;
        }
        MathTransformProvider provider = null;
        final MathTransformProvider[] providers = getAvailableMethods();
        for (int i=0; i<providers.length; i++) {
            final MathTransformProvider candidate = providers[i];
            if (candidate.isHeuristicMatchForName(method)) {
                provider = candidate;
                if (!isDeprecated(candidate, method)) {
                    break;
                }
            }
        }
        if (provider == null) {
            /*
             * No matching name found. If the given method is of the form EPSG:9624, searches among
             * the identifiers. This is not the usual way to use this class (we rather use the names),
             * but is required by the EPSG factory since EPSG operation names can be ambiguous.
             */
            for (int s=method.indexOf(DEFAULT_SEPARATOR); s>=0; s=method.indexOf(DEFAULT_SEPARATOR, s)) {
                final String codespace = method.substring(0, s).trim();
                final String code = method.substring(++s).trim();
                for (int i=0; i<providers.length; i++) {
                    final MathTransformProvider candidate = providers[i];
                    if (candidate.identifierMatches(codespace, code)) {
                        provider = candidate;
                        break;
                    }
                }
            }
            if (provider == null) {
                throw new NoSuchIdentifierException(Errors.format(
                        Errors.Keys.NO_TRANSFORM_FOR_CLASSIFICATION_1, method), method);
            }
        }
        /*
         * Remember the provider we just found, for faster check next time.
         */
        if (localVariables == null) {
            variables.set(localVariables = new Variables());
        }
        localVariables.lastCode = method;
        localVariables.lastProvider = provider;
        return provider;
    }

    /**
     * Returns the local variables, instantiating them if necessary.
     */
    private Variables getLocalVariables() {
        Variables localVariables = variables.get();
        if (localVariables == null) {
            variables.set(localVariables = new Variables());
        }
        return localVariables;
    }

    /**
     * Returns {@code true} if the given operation method is deprecated.
     *
     * @param  method The method to test for deprecation.
     * @param  name   The name which was used for finding the method.
     * @return {@code true} if the given operation method is deprecated.
     *
     * @since 3.16
     */
    static boolean isDeprecated(final OperationMethod method, final String name) {
        for (final GenericName id : method.getAlias()) {
            if (id instanceof ImmutableIdentifier) {
                final ImmutableIdentifier df = (ImmutableIdentifier) id;
                if (name.equals(df.getCode())) {
                    return df.isDeprecated();
                }
            }
        }
        return false;
    }

    /**
     * Returns the default parameter values for a math transform using the given method.
     * The method argument is the name of any operation method returned by the
     * {@link #getAvailableMethods} method. A typical example is
     * <code>"<A HREF="http://www.remotesensing.org/geotiff/proj_list/transverse_mercator.html">Transverse_Mercator</A>"</code>).
     * <p>
     * This method creates new parameter instances at every call. It is intended to be modified
     * by the user before to be passed to <code>{@linkplain #createParameterizedTransform
     * createParameterizedTransform}(parameters)</code>.
     *
     * @param  method The case insensitive name of the method to search for.
     * @return The default parameter values.
     * @throws NoSuchIdentifierException if there is no transform registered for the specified
     *         method.
     *
     * @see #getAvailableMethods
     * @see #createParameterizedTransform
     * @see org.apache.sis.referencing.operation.transform.AbstractMathTransform#getParameterValues()
     */
    @Override
    public ParameterValueGroup getDefaultParameters(final String method)
            throws NoSuchIdentifierException
    {
        return getProvider(method).getParameters().createValue();
    }

    /**
     * Creates a {@linkplain #createParameterizedTransform parameterized transform} from a base
     * CRS to a derived CS. If the {@code "semi_major"} and {@code "semi_minor"} parameters are
     * not explicitly specified, they will be inferred from the {@linkplain Ellipsoid ellipsoid}
     * and added to {@code parameters}. In addition, this method performs axis switch as needed.
     * <p>
     * The {@linkplain OperationMethod operation method} used can be obtained by a call to
     * {@link #getLastMethodUsed}.
     *
     * @param  baseCRS The source coordinate reference system.
     * @param  parameters The parameter values for the transform.
     * @param  derivedCS the target coordinate system.
     * @return The parameterized transform.
     * @throws NoSuchIdentifierException if there is no transform registered for the method.
     * @throws FactoryException if the object creation failed. This exception is thrown
     *         if some required parameter has not been supplied, or has illegal value.
     */
    @Override
    public MathTransform createBaseToDerived(final CoordinateReferenceSystem baseCRS,
                                             final ParameterValueGroup       parameters,
                                             final CoordinateSystem          derivedCS)
            throws NoSuchIdentifierException, FactoryException
    {
        /*
         * If the user's parameter do not contains semi-major and semi-minor axis length, infers
         * them from the ellipsoid. This is a convenience service since the user often omit those
         * parameters (because they duplicate datum information).
         */
        final Ellipsoid ellipsoid = CRSUtilities.getHeadGeoEllipsoid(baseCRS);
        if (ellipsoid != null) try {
            final Unit<Length> axisUnit = ellipsoid.getAxisUnit();
            Parameters.ensureSet(parameters, "semi_major", ellipsoid.getSemiMajorAxis(), axisUnit, false);
            Parameters.ensureSet(parameters, "semi_minor", ellipsoid.getSemiMinorAxis(), axisUnit, false);
        } catch (ParameterNotFoundException | InvalidParameterTypeException e) {
            /*
             * Parameter not found. This exception should not occurs for map projections.
             * If it occurs, we will not try to set the parameter here, but the same
             * exception is likely to occurs at MathTransform creation time. The later
             * is the expected place for this exception, so we will let it happen there.
             *
             * This exception may happen for Molodenski or similar operations. Ignoring
             * this exception for such operations is correct. The actual parameter names
             * are "src_semi_major" and "src_semi_minor", but we don't try to set them
             * since we have no way to set the corresponding "tgt_semi_major" and
             * "tgt_semi_minor" parameters anyway.
             */
        }
        MathTransform baseToDerived = createParameterizedTransform(parameters);
        final Variables localVariables = getLocalVariables();
        final OperationMethod method = localVariables.lastMethod;
        baseToDerived = createBaseToDerived(baseCRS, baseToDerived, derivedCS);
        localVariables.lastMethod = method;
        return baseToDerived;
    }

    /**
     * Creates a transform from a base CRS to a derived CS. This method expects a "raw"
     * transform without unit conversion or axis switch, typically a map projection
     * working on (<cite>longitude</cite>, <cite>latitude</cite>) axes in degrees and
     * (<cite>x</cite>, <cite>y</cite>) axes in metres. This method inspects the coordinate
     * systems and prepend or append the unit conversions and axis switches automatically.
     *
     * @param  baseCRS The source coordinate reference system.
     * @param  projection The "raw" <cite>base to derived</cite> transform.
     * @param  derivedCS the target coordinate system.
     * @return The parameterized transform.
     * @throws FactoryException if the object creation failed. This exception is thrown
     *         if some required parameter has not been supplied, or has illegal value.
     *
     * @since 2.5
     */
    public MathTransform createBaseToDerived(final CoordinateReferenceSystem baseCRS,
                                             final MathTransform          projection,
                                             final CoordinateSystem        derivedCS)
            throws FactoryException
    {
        /*
         * Computes matrix for swapping axis and performing units conversion.
         * There is one matrix to apply before projection on (longitude,latitude)
         * coordinates, and one matrix to apply after projection on (easting,northing)
         * coordinates.
         */
        final CoordinateSystem sourceCS = baseCRS.getCoordinateSystem();
        final Matrix swap1, swap3;
        try {
            swap1 = AbstractCS.swapAndScaleAxis(sourceCS, AbstractCS.standard(sourceCS));
            swap3 = AbstractCS.swapAndScaleAxis(AbstractCS.standard(derivedCS), derivedCS);
        } catch (IllegalArgumentException | ConversionException cause) {
            throw new FactoryException(cause);
        }
        /*
         * Prepares the concatenation of the matrix computed above and the projection.
         * Note that at this stage, the dimensions between each step may not be compatible.
         * For example the projection (step2) is usually two-dimensional while the source
         * coordinate system (step1) may be three-dimensional if it has a height.
         */
        MathTransform step1 = createAffineTransform(swap1);
        MathTransform step3 = createAffineTransform(swap3);
        MathTransform step2 = projection;
        /*
         * If the target coordinate system has a height, instructs the projection to pass
         * the height unchanged from the base CRS to the target CRS. After this block, the
         * dimensions of 'step2' and 'step3' should match.
         */
        final int numTrailingOrdinates = step3.getSourceDimensions() - step2.getTargetDimensions();
        if (numTrailingOrdinates > 0) {
            step2 = createPassThroughTransform(0, step2, numTrailingOrdinates);
        }
        /*
         * If the source CS has a height but the target CS doesn't, drops the extra coordinates.
         * After this block, the dimensions of 'step1' and 'step2' should match.
         */
        final int sourceDim = step1.getTargetDimensions();
        final int targetDim = step2.getSourceDimensions();
        if (sourceDim > targetDim) {
            final Matrix drop = Matrices.create(targetDim+1, sourceDim+1);
            drop.setElement(targetDim, sourceDim, 1);
            step1 = createConcatenatedTransform(createAffineTransform(drop), step1);
        }
        MathTransform mt = createConcatenatedTransform(createConcatenatedTransform(step1, step2), step3);
        if (projection instanceof ParameterizedAffine) {
            mt = ((ParameterizedAffine) projection).using(mt);
        }
        return mt;
    }

    /**
     * Creates a transform from a group of parameters. The method name is inferred
     * from the {@linkplain ParameterDescriptorGroup#getName parameter group name}.
     * Example:
     *
     * {@preformat java
     *     ParameterValueGroup p = factory.getDefaultParameters("Transverse_Mercator");
     *     p.parameter("semi_major").setValue(6378137.000);
     *     p.parameter("semi_minor").setValue(6356752.314);
     *     MathTransform mt = factory.createParameterizedTransform(p);
     * }
     *
     * @param  parameters The parameter values.
     * @return The parameterized transform.
     * @throws NoSuchIdentifierException if there is no transform registered for the method.
     * @throws FactoryException if the object creation failed. This exception is thrown
     *         if some required parameter has not been supplied, or has illegal value.
     *
     * @see #getDefaultParameters
     * @see #getAvailableMethods
     * @see #getLastMethodUsed
     */
    @Override
    public MathTransform createParameterizedTransform(ParameterValueGroup parameters)
            throws NoSuchIdentifierException, FactoryException
    {
        MathTransform transform;
        OperationMethod method = null;
        try {
            final String classification = parameters.getDescriptor().getName().getCode();
            final MathTransformProvider provider = getProvider(classification);
            method = provider;
            try {
                parameters = provider.ensureValidValues(parameters);
                transform  = provider.createMathTransform(parameters);
            } catch (IllegalArgumentException | IllegalStateException exception) {
                /*
                 * Catch only exceptions which may be the result of improper parameter
                 * usage (e.g. a value out of range). Do not catch exception caused by
                 * programming errors (e.g. null pointer exception).
                 */
                throw new FactoryException(exception);
            }
            if (transform instanceof MathTransformDecorator) {
                final MathTransformDecorator delegate = (MathTransformDecorator) transform;
                method    = delegate.method;
                transform = delegate.transform;
            }
            transform = pool.unique(transform);
        } finally {
            getLocalVariables().lastMethod = method; // May be null in case of failure, which is intended.
        }
        return transform;
    }

    /**
     * Creates an affine transform from a matrix. If the transform input dimension is {@code M},
     * and output dimension is {@code N}, then the matrix will have size {@code [N+1][M+1]}. The
     * +1 in the matrix dimensions allows the matrix to do a shift, as well as a rotation. The
     * {@code [M][j]} element of the matrix will be the j'th ordinate of the moved origin. The
     * {@code [i][N]} element of the matrix will be 0 for <var>i</var> less than {@code M}, and 1
     * for <var>i</var> equals {@code M}.
     *
     * @param  matrix The matrix used to define the affine transform.
     * @return The affine transform.
     * @throws FactoryException if the object creation failed.
     *
     * @see MathTransforms#linear(Matrix)
     */
    @Override
    public MathTransform createAffineTransform(final Matrix matrix)
            throws FactoryException
    {
        final Variables localVariables = variables.get();
        if (localVariables != null) {
            localVariables.lastMethod = null; // To be strict, we should set the ProjectiveTransform provider
        }
        return pool.unique(MathTransforms.linear(matrix));
    }

    /**
     * Creates a transform by concatenating two existing transforms.
     * A concatenated transform acts in the same way as applying two
     * transforms, one after the other.
     * <p>
     * The dimension of the output space of the first transform must match
     * the dimension of the input space in the second transform. In order
     * to concatenate more than two transforms, use this method repeatedly.
     *
     * @param  transform1 The first transform to apply to points.
     * @param  transform2 The second transform to apply to points.
     * @return The concatenated transform.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public MathTransform createConcatenatedTransform(final MathTransform transform1,
                                                     final MathTransform transform2)
            throws FactoryException
    {
        MathTransform tr;
        try {
            tr = MathTransforms.concatenate(transform1, transform2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        tr = pool.unique(tr);
        return tr;
    }

    /**
     * Creates a transform which passes through a subset of ordinates to another transform.
     * This allows transforms to operate on a subset of ordinates. For example giving
     * (<var>latitude</var>, <var>longitude</var>, <var>height</var>) coordinates, a pass
     * through transform can convert the height values from meters to feet without affecting
     * the (<var>latitude</var>, <var>longitude</var>) values.
     * <p>
     * The resulting transform will have the following dimensions:
     *
     * {@preformat text
     *     Source: firstAffectedOrdinate + subTransform.getSourceDimensions() + numTrailingOrdinates
     *     Target: firstAffectedOrdinate + subTransform.getTargetDimensions() + numTrailingOrdinates
     * }
     *
     * @param  firstAffectedOrdinate The lowest index of the affected ordinates.
     * @param  subTransform Transform to use for affected ordinates.
     * @param  numTrailingOrdinates Number of trailing ordinates to pass through.
     *         Affected ordinates will range from {@code firstAffectedOrdinate}
     *         inclusive to {@code dimTarget-numTrailingOrdinates} exclusive.
     * @return A pass through transform.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public MathTransform createPassThroughTransform(final int firstAffectedOrdinate,
                                                    final MathTransform subTransform,
                                                    final int numTrailingOrdinates)
            throws FactoryException
    {
        MathTransform tr;
        try {
            tr = PassThroughTransform.create(firstAffectedOrdinate, subTransform, numTrailingOrdinates);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        tr = pool.unique(tr);
        return tr;
    }

    /**
     * Creates a math transform object from a XML string. The default implementation
     * always throws an exception, since this method is not yet implemented.
     *
     * @param  xml Math transform encoded in XML format.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public MathTransform createFromXML(String xml) throws FactoryException {
        throw new FactoryException("Not yet implemented.");
    }

    /**
     * Creates a math transform object from a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A>.
     *
     * @param  text Math transform encoded in Well-Known Text format.
     * @return The math transform (never {@code null}).
     * @throws FactoryException if the Well-Known Text can't be parsed,
     *         or if the math transform creation failed from some other reason.
     */
    @Override
    public MathTransform createFromWKT(final String text) throws FactoryException {
        final Variables localVariables = getLocalVariables();
        MathTransformParser parser = localVariables.parser;
        if (parser == null) {
            parser = new MathTransformParser(Symbols.getDefault(), this);
            localVariables.parser = parser;
        }
        try {
            return parser.parseMathTransform(text);
        } catch (ParseException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof FactoryException) {
                throw (FactoryException) cause;
            }
            throw new FactoryException(exception);
        }
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is needed because the
     * application class path can theoretically change, or additional plug-ins may become available.
     * Rather than re-scanning the classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this method to prompt a re-scan.
     * Thus this method need only be invoked by sophisticated applications which dynamically make
     * new plug-ins available at runtime.
     *
     * @level advanced
     */
    public void scanForPlugins() {
        synchronized (registry) {
            providers = null;
            registry.scanForPlugins();
        }
    }
}
