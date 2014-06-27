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
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.quantity.Length;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.QuantitativeResult;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*; // We really use most of them.
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.InternationalString;
import org.opengis.util.Record;

import org.apache.sis.util.iso.Types;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.wkt.Formattable;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy;
import org.apache.sis.internal.referencing.Semaphores;
import org.apache.sis.measure.Units;

import static org.apache.sis.util.Utilities.deepEquals;
import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.internal.InternalUtilities.nonEmptySet;
import org.apache.sis.internal.referencing.WKTUtilities;
import static org.apache.sis.util.collection.Containers.property;


/**
 * Establishes an association between a source and a target
 * {@linkplain CoordinateReferenceSystem coordinate reference system}, and provides a
 * {@linkplain MathTransform transform} for transforming coordinates in the source CRS
 * to coordinates in the target CRS. Many but not all coordinate operations (from CRS
 * <var>A</var> to CRS <var>B</var>) also uniquely define the inverse operation (from
 * CRS <var>B</var> to CRS <var>A</var>). In some cases, the operation method algorithm
 * for the inverse operation is the same as for the forward algorithm, but the signs of
 * some operation parameter values must be reversed. In other cases, different algorithms
 * are required for the forward and inverse operations, but the same operation parameter
 * values are used. If (some) entirely different parameter values are needed, a different
 * coordinate operation shall be defined.
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 1.2
 * @module
 */
@Immutable
public class AbstractCoordinateOperation extends AbstractIdentifiedObject implements CoordinateOperation, Formattable {
    /**
     * Serial number for inter-operability with different versions.
     */
//  private static final long serialVersionUID = 1237358357729193885L;

    /**
     * An empty array of positional accuracy. This is useful for fetching accuracies as an array,
     * using the following idiom:
     *
     * {@preformat java
     *     getCoordinateOperationAccuracy().toArray(EMPTY_ACCURACY_ARRAY);
     * }
     *
     * @see #getCoordinateOperationAccuracy()
     */
    public static final PositionalAccuracy[] EMPTY_ACCURACY_ARRAY = new PositionalAccuracy[0];

    /**
     * The source CRS, or {@code null} if not available.
     */
    protected final CoordinateReferenceSystem sourceCRS;

    /**
     * The target CRS, or {@code null} if not available.
     */
    protected final CoordinateReferenceSystem targetCRS;

    /**
     * Version of the coordinate transformation
     * (i.e., instantiation due to the stochastic nature of the parameters).
     */
    final String operationVersion;

    /**
     * Estimate(s) of the impact of this operation on point accuracy, or {@code null}
     * if none.
     */
    private final Collection<PositionalAccuracy> coordinateOperationAccuracy;

    /**
     * Area in which this operation is valid, or {@code null} if not available.
     */
    protected final Extent domainOfValidity;

    /**
     * Description of domain of usage, or limitations of usage, for which this operation is valid.
     */
    private final InternationalString scope;

    /**
     * Transform from positions in the {@linkplain #getSourceCRS source coordinate reference system}
     * to positions in the {@linkplain #getTargetCRS target coordinate reference system}.
     */
    protected final MathTransform transform;

    /**
     * Constructs a new coordinate operation with the same values than the specified
     * defining conversion, together with the specified source and target CRS. This
     * constructor is used by {@link DefaultConversion} only.
     */
    AbstractCoordinateOperation(final Conversion               definition,
                                final CoordinateReferenceSystem sourceCRS,
                                final CoordinateReferenceSystem targetCRS,
                                final MathTransform             transform)
    {
        super(definition);
        this.sourceCRS                   = sourceCRS;
        this.targetCRS                   = targetCRS;
        this.operationVersion            = definition.getOperationVersion();
        this.coordinateOperationAccuracy = definition.getCoordinateOperationAccuracy();
        this.domainOfValidity            = definition.getDomainOfValidity();
        this.scope                       = definition.getScope();
        this.transform                   = transform;
    }

    /**
     * Constructs a coordinate operation from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     * Additionally, the following properties are understood by this construtor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.operation.CoordinateOperation#OPERATION_VERSION_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getOperationVersion}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.operation.CoordinateOperation#COORDINATE_OPERATION_ACCURACY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;<code>{@linkplain PositionalAccuracy} (singleton or array)</code>&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getCoordinateOperationAccuracy}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.operation.CoordinateOperation#DOMAIN_OF_VALIDITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Extent}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getDomainOfValidity}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.operation.CoordinateOperation#SCOPE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getScope}</td>
     *   </tr>
     * </table>
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     */
    public AbstractCoordinateOperation(final Map<String,?>             properties,
                                       final CoordinateReferenceSystem sourceCRS,
                                       final CoordinateReferenceSystem targetCRS,
                                       final MathTransform             transform)
    {
        super(properties);
        Object positionalAccuracy;
        domainOfValidity   = property(properties, DOMAIN_OF_VALIDITY_KEY, Extent.class);
        scope              = Types.toInternationalString(properties, SCOPE_KEY);
        operationVersion   = property(properties, OPERATION_VERSION_KEY, String.class);
        positionalAccuracy = properties.get(COORDINATE_OPERATION_ACCURACY_KEY);
        if (positionalAccuracy instanceof PositionalAccuracy[]) {
            final PositionalAccuracy[] accuracies = ((PositionalAccuracy[]) positionalAccuracy).clone();
            for (int i=0; i<accuracies.length; i++) {
                ensureNonNull(COORDINATE_OPERATION_ACCURACY_KEY, i, accuracies);
            }
            coordinateOperationAccuracy = nonEmptySet(accuracies);
        } else {
            coordinateOperationAccuracy = (positionalAccuracy == null)
                    ? Collections.<PositionalAccuracy>emptySet()
                    : Collections.singleton((PositionalAccuracy) positionalAccuracy);
        }
        this.sourceCRS = sourceCRS;
        this.targetCRS = targetCRS;
        this.transform = transform;
        validate();
    }

    /**
     * Checks the validity of this operation. This method is invoked by the constructor after
     * every fields have been assigned. It can be overridden by subclasses if different rules
     * should be applied.
     * <p>
     * {@link DefaultConversion} overrides this method in order to allow null values, providing
     * that all of {@code transform}, {@code sourceCRS} and {@code targetCRS} are null together.
     * Note that null values are not allowed for transformations, so {@link DefaultTransformation}
     * does not override this method.
     *
     * @throws IllegalArgumentException if at least one of {@code transform}, {@code sourceCRS}
     *         or {@code targetCRS} is invalid. We throw this kind of exception rather than
     *         {@link IllegalStateException} because this method is invoked by the constructor
     *         for checking argument validity.
     */
    void validate() throws IllegalArgumentException {
        ensureNonNull ("sourceCRS", sourceCRS);
        ensureNonNull ("targetCRS", targetCRS);
        ensureNonNull ("transform", transform);
        checkDimension("sourceCRS", sourceCRS, transform.getSourceDimensions());
        checkDimension("targetCRS", targetCRS, transform.getTargetDimensions());
    }

    /**
     * Checks if a reference coordinate system has the expected number of dimensions.
     *
     * @param name     The argument name.
     * @param crs      The coordinate reference system to check.
     * @param expected The expected number of dimensions.
     */
    private static void checkDimension(final String name,
                                       final CoordinateReferenceSystem crs,
                                       final int expected)
    {
        final int actual = crs.getCoordinateSystem().getDimension();
        if (actual != expected) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_3, name, actual, expected));
        }
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The default implementation returns {@code CoordinateOperation.class}.
     * Subclasses implementing a more specific GeoAPI interface shall override this method.
     *
     * @return The coordinate operation interface implemented by this class.
     */
    @Override
    public Class<? extends CoordinateOperation> getInterface() {
        return CoordinateOperation.class;
    }

    /**
     * Returns the source CRS.
     */
    @Override
    public CoordinateReferenceSystem getSourceCRS() {
        return sourceCRS;
    }

    /**
     * Returns the target CRS.
     */
    @Override
    public CoordinateReferenceSystem getTargetCRS() {
        return targetCRS;
    }

    /**
     * Version of the coordinate transformation (i.e., instantiation due to the stochastic
     * nature of the parameters). Mandatory when describing a transformation, and should not
     * be supplied for a conversion.
     *
     * @return The coordinate operation version, or {@code null} in none.
     */
    @Override
    public String getOperationVersion() {
        return operationVersion;
    }

    /**
     * Estimate(s) of the impact of this operation on point accuracy. Gives
     * position error estimates for target coordinates of this coordinate
     * operation, assuming no errors in source coordinates.
     *
     * @return The position error estimates, or an empty collection if not available.
     *
     * @see #getAccuracy()
     *
     * @since 2.4
     */
    @Override
    public Collection<PositionalAccuracy> getCoordinateOperationAccuracy() {
        if (coordinateOperationAccuracy == null) {
            return Collections.emptySet();
        }
        return coordinateOperationAccuracy;
    }

    /**
     * Convenience method returning the accuracy in meters. The default implementation delegates
     * to <code>{@linkplain #getAccuracy(CoordinateOperation) getAccuracy}(this)</code>. Subclasses
     * should override this method if they can provide a more accurate algorithm.
     *
     * @return The accuracy in meters, or NaN if unknown.
     *
     * @since 2.2
     */
    public double getAccuracy() {
        return accuracy(this);
    }

    /**
     * Convenience method returning the accuracy in meters for the specified operation. This method
     * try each of the following procedures and returns the first successful one:
     * <p>
     * <ul>
     *   <li>If a {@linkplain QuantitativeResult quantitative} positional accuracy is found with a
     *       linear unit, then this accuracy estimate is converted to {@linkplain SI#METRE metres}
     *       and returned.</li>
     *
     *   <li>Otherwise, if the operation is a {@linkplain Conversion conversion}, then returns
     *       0 since a conversion is by definition accurates up to rounding errors.</li>
     *
     *   <li>Otherwise, if the operation is a {@linkplain Transformation transformation}, then
     *       checks if the datum shift were applied with the help of Bursa-Wolf parameters.
     *       This procedure looks for Geotk-specific
     *       {@link AbstractPositionalAccuracy#DATUM_SHIFT_APPLIED DATUM_SHIFT_APPLIED} and
     *       {@link AbstractPositionalAccuracy#DATUM_SHIFT_OMITTED DATUM_SHIFT_OMITTED} metadata.
     *       If a datum shift has been applied, returns 25 meters. If a datum shift should have
     *       been applied but has been omitted, returns 1000 meters. The 1000 meters value is
     *       higher than the highest value (999 meters) found in the EPSG database version 6.7.
     *       The 25 meters value is the next highest value found in the EPSG database for a
     *       significant number of transformations.
     *
     *   <li>Otherwise, if the operation is a {@linkplain ConcatenatedOperation concatenated one},
     *       returns the sum of the accuracy of all components. This is a conservative scenario
     *       where we assume that errors cumulate linearly. Note that this is not necessarily
     *       the "worst case" scenario since the accuracy could be worst if the math transforms
     *       are highly non-linear.</li>
     * </ul>
     *
     * @param  operation The operation to inspect for accuracy.
     * @return The accuracy estimate (always in meters), or NaN if unknown.
     *
     * @since 2.2
     */
    public static double getAccuracy(final CoordinateOperation operation) {
        if (operation instanceof AbstractCoordinateOperation) {
            // Maybe the user overridden this method...
            return ((AbstractCoordinateOperation) operation).getAccuracy();
        }
        return accuracy(operation);
    }

    /**
     * Implementation of {@code getAccuracy} methods, both the ordinary and the
     * static member variants. The {@link #getAccuracy()} method can't invoke
     * {@link #getAccuracy(CoordinateOperation)} directly since it would cause
     * never-ending recursive calls.
     */
    private static double accuracy(final CoordinateOperation operation) {
        final Collection<PositionalAccuracy> accuracies = operation.getCoordinateOperationAccuracy();
        if (accuracies != null) for (final PositionalAccuracy accuracy : accuracies) {
            if (accuracy != null) for (final Result result : accuracy.getResults()) {
                if (result instanceof QuantitativeResult) {
                    final QuantitativeResult quantity = (QuantitativeResult) result;
                    final Collection<? extends Record> records = quantity.getValues();
                    if (records != null) {
                        final Unit<?> unit = quantity.getValueUnit();
                        if (Units.isLinear(unit)) {
                            final Unit<Length> unitOfLength = unit.asType(Length.class);
                            for (final Record record : records) {
                                for (final Object value : record.getAttributes().values()) {
                                    if (value instanceof Number) {
                                        double v = ((Number) value).doubleValue();
                                        v = unitOfLength.getConverterTo(SI.METRE).convert(v);
                                        return v;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
         * No quantitative, linear accuracy were found. If the coordinate operation is actually
         * a conversion, the accuracy is up to rounding error (i.e. conceptually 0) by definition.
         */
        if (operation instanceof Conversion) {
            return 0;
        }
        /*
         * If the coordinate operation is actually a transformation, checks if Bursa-Wolf
         * parameters were available for the datum shift. This is Geotk-specific.
         * See javadoc for a rational about the return values chosen.
         */
        if (operation instanceof Transformation) {
            if (!accuracies.contains(AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED)) {
                if (accuracies.contains(AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED)) {
                    return 25;
                }
            }
            return 1000;
        }
        /*
         * If the coordinate operation is a compound of other coordinate operations, returns
         * the sum of their accuracy, skipping unknown ones. Making the sum is a conservative
         * approach (not exactly the "worst case" scenario, since it could be worst if the
         * transforms are highly non-linear).
         */
        double accuracy = Double.NaN;
        if (operation instanceof ConcatenatedOperation) {
            for (final SingleOperation op : ((ConcatenatedOperation) operation).getOperations()) {
                final double candidate = Math.abs(getAccuracy(op));
                if (!Double.isNaN(candidate)) {
                    if (Double.isNaN(accuracy)) {
                        accuracy = candidate;
                    } else {
                        accuracy += candidate;
                    }
                }
            }
        }
        return accuracy;
    }

    /**
     * Area or region or timeframe in which this coordinate operation is valid.
     * Returns {@code null} if not available.
     *
     * @since 2.4
     */
    @Override
    public Extent getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * Description of domain of usage, or limitations of usage, for which this operation is valid.
     */
    @Override
    public InternationalString getScope() {
        return scope;
    }

    /**
     * Gets the math transform. The math transform will transform positions in the
     * {@linkplain #getSourceCRS source coordinate reference system} into positions
     * in the {@linkplain #getTargetCRS target coordinate reference system}.
     */
    @Override
    public MathTransform getMathTransform() {
        return transform;
    }

    /**
     * Returns the most specific {@link CoordinateOperation} interface implemented by the
     * specified operation. Special cases:
     * <p>
     * <ul>
     *   <li>If the operation implements the {@link Transformation} interface,
     *       then this method returns {@code Transformation.class}. Transformation
     *       has precedence over any other interface implemented by the operation.</li>
     *   <li>Otherwise if the operation implements the {@link Conversion} interface,
     *       then this method returns the most specific {@code Conversion} sub-interface.</li>
     *   <li>Otherwise if the operation implements the {@link SingleOperation} interface,
     *       then this method returns {@code SingleOperation.class}.</li>
     *   <li>Otherwise if the operation implements the {@link ConcatenatedOperation} interface,
     *       then this method returns {@code ConcatenatedOperation.class}.</li>
     *   <li>Otherwise this method returns {@code CoordinateOperation.class}.</li>
     * </ul>
     *
     * @param  operation A coordinate operation.
     * @return The most specific GeoAPI interface implemented by the given operation.
     */
    public static Class<? extends CoordinateOperation> getType(final CoordinateOperation operation) {
        if (operation instanceof        Transformation) return        Transformation.class;
        if (operation instanceof       ConicProjection) return       ConicProjection.class;
        if (operation instanceof CylindricalProjection) return CylindricalProjection.class;
        if (operation instanceof      PlanarProjection) return      PlanarProjection.class;
        if (operation instanceof            Projection) return            Projection.class;
        if (operation instanceof            Conversion) return            Conversion.class;
        if (operation instanceof       SingleOperation) return       SingleOperation.class;
        if (operation instanceof ConcatenatedOperation) return ConcatenatedOperation.class;
        return CoordinateOperation.class;
    }

    /**
     * Compares this coordinate operation with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties are
     * compared including the {@linkplain #getDomainOfValidity() domain of validity} and
     * the {@linkplain #getScope scope}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                // Do not test targetCRS now - it will be tested later in this method.
                // See comment in DefaultSingleOperation.equals(...) about why we compare MathTransform.
                case STRICT: {
                    final AbstractCoordinateOperation that = (AbstractCoordinateOperation) object;
                    if (!Objects.equals(sourceCRS,                   that.sourceCRS)        ||
                        !Objects.equals(transform,                   that.transform)        ||
                        !Objects.equals(domainOfValidity,            that.domainOfValidity) ||
                        !Objects.equals(scope,                       that.scope)            ||
                        !Objects.equals(coordinateOperationAccuracy, that.coordinateOperationAccuracy))
                    {
                        return false;
                    }
                    break;
                }
                case BY_CONTRACT: {
                    final CoordinateOperation that = (CoordinateOperation) object;
                    if (!deepEquals(getScope(),                       that.getScope(), mode) ||
                        !deepEquals(getDomainOfValidity(),            that.getDomainOfValidity(), mode) ||
                        !deepEquals(getCoordinateOperationAccuracy(), that.getCoordinateOperationAccuracy(), mode))
                    {
                        return false;
                    }
                    // Fall through
                }
                default: {
                    final CoordinateOperation that = (CoordinateOperation) object;
                    if (!deepEquals(getSourceCRS(),     that.getSourceCRS(), mode) ||
                        !deepEquals(getMathTransform(), that.getMathTransform(), mode))
                    {
                        return false;
                    }
                    break;
                }
            }
            /*
             * Avoid never-ending recursivity: AbstractDerivedCRS has a 'conversionFromBase'
             * field that is set to this AbstractCoordinateOperation.
             */
            if (Semaphores.queryAndSet(Semaphores.COMPARING)) {
                return true;
            }
            try {
                if (mode == ComparisonMode.STRICT) {
                    return Objects.equals(targetCRS, ((AbstractCoordinateOperation) object).targetCRS);
                } else {
                    return deepEquals(getTargetCRS(), ((CoordinateOperation) object).getTargetCRS(), mode);
                }
            } finally {
                Semaphores.clear(Semaphores.COMPARING);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected long computeHashCode() {
        return super.computeHashCode() + Objects.hash(sourceCRS, targetCRS, transform);
    }

    /**
     * Formats this operation as a pseudo-WKT format. No WKT format were defined for coordinate
     * operation at the time this method was written. This method may change in any future version
     * until a standard format is found.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        append(formatter, sourceCRS, "SOURCE");
        append(formatter, targetCRS, "TARGET");
        return super.formatTo(formatter);
    }

    /**
     * Appends the identifier for the specified object name (possibly {@code null}) to the specified
     * formatter.
     *
     * @param formatter The formatter where to append the object name.
     * @param object    The object to append, or {@code null} if none.
     * @param type      The label to put in front of the object name.
     */
    @SuppressWarnings("serial")
    static void append(final Formatter formatter, final IdentifiedObject object, final String type) {
        if (object != null) {
            final Set<ReferenceIdentifier> identifiers = object.getIdentifiers();
            final Map<String,Object> properties = new HashMap<>(4);
            properties.put(IdentifiedObject.NAME_KEY,        object.getName());
            properties.put(IdentifiedObject.IDENTIFIERS_KEY, identifiers.toArray(new ReferenceIdentifier[identifiers.size()]));
            formatter.newLine();
            formatter.append(new AbstractIdentifiedObject(properties) {
                @Override
                protected String formatTo(final Formatter formatter) {
                    WKTUtilities.appendName(this, formatter, null);
                    return type;
                }
            });
        }
    }
}
