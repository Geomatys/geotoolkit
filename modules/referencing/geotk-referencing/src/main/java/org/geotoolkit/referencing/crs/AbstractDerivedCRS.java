/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.crs;

import java.util.Map;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.*;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.operation.DefaultConversion;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.DefaultSingleOperation;
import org.apache.sis.referencing.operation.DefaultOperationMethod;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.apache.sis.internal.system.Semaphores;
import org.apache.sis.internal.referencing.WKTUtilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.io.wkt.Formatter;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.internal.referencing.WKTUtilities.toFormattable;


/**
 * A coordinate reference system that is defined by its coordinate
 * {@linkplain Conversion conversion} from another CRS (not by a {@linkplain Datum datum}).
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.1
 * @module
 */
@Immutable
public class AbstractDerivedCRS extends AbstractSingleCRS implements GeneralDerivedCRS {
    /**
     * Serial number for inter-operability with different versions.
     */
//  private static final long serialVersionUID = -175151161496419854L;

    /**
     * Key for the <code>{@value}</code> property to be given to the constructor.
     * The value should be one of
     * <code>{@linkplain PlanarProjection}.class</code>,
     * <code>{@linkplain CylindricalProjection}.class</code> or
     * <code>{@linkplain ConicProjection}.class</code>.
     * <p>
     * This is a Geotk specific property used as a hint for creating a
     * {@linkplain org.geotoolkit.referencing.operation.DefaultProjection projection} of proper type from a
     * {@linkplain DefiningConversion defining conversion}. In many cases, this hint is not needed
     * since Geotk is often capable to infer it. This hint is used mostly by advanced factories
     * like the {@linkplain org.geotoolkit.referencing.factory.epsg EPSG backed} one.
     *
     * @see DefaultConversion#create
     *
     * @since 2.4
     */
    public static final String CONVERSION_TYPE_KEY = "conversionType";

    /**
     * The base coordinate reference system.
     */
    protected final CoordinateReferenceSystem baseCRS;

    /**
     * The conversion from the {@linkplain #getBaseCRS base CRS} to this CRS.
     */
    protected final Conversion conversionFromBase;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractDerivedCRS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new derived CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    protected AbstractDerivedCRS(final GeneralDerivedCRS crs) {
        super(crs);
        baseCRS            = crs.getBaseCRS();
        conversionFromBase = crs.getConversionFromBase();
    }

    /**
     * Constructs a derived CRS from a {@linkplain DefiningConversion defining conversion}.
     * The properties are given unchanged to the
     * {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) base class constructor}.
     *
     * @param  properties Name and other properties to give to the new derived CRS object.
     * @param  conversionFromBase The {@linkplain DefiningConversion defining conversion}.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDerived} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     */
    protected AbstractDerivedCRS(final Map<String,?>       properties,
                                 final Conversion  conversionFromBase,
                                 final CoordinateReferenceSystem base,
                                 final MathTransform    baseToDerived,
                                 final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, CRSUtilities.getDatum(base), derivedCS);
        ensureNonNull("conversionFromBase", conversionFromBase);
        ensureNonNull("baseToDerived",      baseToDerived);
        this.baseCRS = base;
        checkDimensions(base, baseToDerived, derivedCS);
        CRSUtilities.checkDimensions(conversionFromBase.getMethod(), baseToDerived);
        final Class<?> c = (Class<?>) properties.get(CONVERSION_TYPE_KEY);
        Class<? extends Conversion> typeHint = getConversionType();
        if (c != null) {
            typeHint = c.asSubclass(typeHint);
        }
        this.conversionFromBase = DefaultConversion.create(
            /* definition */ conversionFromBase,
            /* sourceCRS  */ base,
            /* targetCRS  */ this,
            /* transform  */ baseToDerived,
            /* typeHints  */ typeHint);
    }

    /**
     * Constructs a derived CRS from a set of properties. A {@linkplain DefaultOperationMethod
     * default operation method} is inferred from the {@linkplain AbstractMathTransform math
     * transform}. This is a convenience constructor that is not guaranteed to work reliably for
     * non-Geotk implementations. Use the constructor expecting a {@linkplain DefiningConversion
     * defining conversion} for more determinist result.
     * <p>
     * The properties are given unchanged to the
     * {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) base class constructor}.
     * The following optional properties are also understood:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;<code>"conversion.name"</code>&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;<code>{@linkplain #getConversionFromBase}.getName()</code></td>
     *   </tr>
     * </table>
     * <p>
     * Additional properties for the {@link DefaultConversion} object to be created can be
     * specified with the {@code "conversion."} prefix added in front of property names
     * (example: {@code "conversion.remarks"}). The same applies for operation method,
     * using the {@code "method."} prefix.
     *
     * @param  properties Name and other properties to give to the new derived CRS object and to
     *         the underlying {@linkplain DefaultConversion conversion}.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDerived} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     *
     * @since 2.5
     */
    protected AbstractDerivedCRS(final Map<String,?>       properties,
                                 final CoordinateReferenceSystem base,
                                 final MathTransform    baseToDerived,
                                 final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, CRSUtilities.getDatum(base), derivedCS);
        ensureNonNull("baseToDerived", baseToDerived);
        this.baseCRS = base;
        /*
         * Makes sure that the source and target dimensions match. We do not check parameters
         * in current version of this implementation (we may add this check in a future version),
         * since the descriptors provided in this user-supplied OperationMethod may be more
         * accurate than the one inferred from the MathTransform.
         */
        checkDimensions(base, baseToDerived, derivedCS);
        final OperationMethod method = new DefaultOperationMethod(baseToDerived);
        CRSUtilities.checkDimensions(method, baseToDerived);
        this.conversionFromBase = (Conversion) DefaultSingleOperation.create(
            /* properties */ new UnprefixedMap(properties, "conversion."),
            /* sourceCRS  */ base,
            /* targetCRS  */ this,
            /* transform  */ baseToDerived,
            /* method     */ method,
            /* type       */ (this instanceof ProjectedCRS) ? Projection.class : Conversion.class);
    }

    /**
     * Checks consistency between the base CRS and the "base to derived" transform.
     */
    private static void checkDimensions(final CoordinateReferenceSystem base,
                                        final MathTransform    baseToDerived,
                                        final CoordinateSystem     derivedCS)
            throws MismatchedDimensionException
    {
        final int dimSource = baseToDerived.getSourceDimensions();
        final int dimTarget = baseToDerived.getTargetDimensions();
        int dim1, dim2;
        if ((dim1 = dimSource) != (dim2 = base.getCoordinateSystem().getDimension()) ||
            (dim1 = dimTarget) != (dim2 = derivedCS.getDimension()))
        {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, dim1, dim2));
        }
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The default implementation returns {@code GeneralDerivedCRS.class}.
     * Subclasses implementing a more specific GeoAPI interface shall override this method.
     *
     * @return The coordinate reference system interface implemented by this class.
     */
    @Override
    public Class<? extends GeneralDerivedCRS> getInterface() {
        return GeneralDerivedCRS.class;
    }

    /**
     * Returns the base coordinate reference system.
     *
     * @return The base coordinate reference system.
     */
    @Override
    public CoordinateReferenceSystem getBaseCRS() {
        return baseCRS;
    }

    /**
     * Returns the conversion from the {@linkplain #getBaseCRS base CRS} to this CRS.
     *
     * @return The conversion to this CRS.
     */
    @Override
    public Conversion getConversionFromBase() {
        return conversionFromBase;
    }

    /**
     * Returns the expected type of conversion.
     * {@link DefaultProjectedCRS} will override this type with {@link Projection}.
     */
    Class<? extends Conversion> getConversionType() {
        return Conversion.class;
    }

    /**
     * Compares this coordinate reference system with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            final boolean strict = (mode == ComparisonMode.STRICT);
            if (deepEquals(strict ? baseCRS : getBaseCRS(),
                           strict ? ((AbstractDerivedCRS) object).baseCRS
                                  :  ((GeneralDerivedCRS) object).getBaseCRS(), mode)) {
                /*
                 * Avoid never-ending recursivity: Conversion has a 'targetCRS' field (inherited from
                 * the AbstractCoordinateOperation super-class) that is set to this AbstractDerivedCRS.
                 */
                if (Semaphores.queryAndSet(Semaphores.COMPARING)) {
                    return true;
                }
                try {
                    return deepEquals(strict ? conversionFromBase : getConversionFromBase(),
                                      strict ? ((AbstractDerivedCRS) object).conversionFromBase
                                             :  ((GeneralDerivedCRS) object).getConversionFromBase(), mode);
                } finally {
                    Semaphores.clear(Semaphores.COMPARING);
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected long computeHashCode() {
        /*
         * Do not invoke 'conversionFromBase.hashCode()' in order to avoid a never-ending loop.
         * This is because Conversion inherits a 'sourceCRS' field from the CoordinateOperation
         * parent type, which is set to this DerivedCRS. Checking the OperationMethod does not
         * work neither for the reason documented inside the DefaultSingleOperation.equals(...)
         * method body. The MathTransform is our best discriminant.
         */
        return super.computeHashCode() + 31*baseCRS.hashCode() + conversionFromBase.getMathTransform().hashCode();
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#FITTED_CS"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The name of the WKT element type, which is {@code "FITTED_CS"}.
     */
    @Override
    public String formatTo(final Formatter formatter) { // TODO: should be protected.
        WKTUtilities.appendName(this, formatter, null);
        MathTransform inverse = conversionFromBase.getMathTransform();
        try {
            inverse = inverse.inverse();
        } catch (NoninvertibleTransformException exception) {
            // TODO: provide a more accurate error message.
            throw new IllegalStateException(exception.getLocalizedMessage(), exception);
        }
        formatter.newLine();
        formatter.append(inverse);
        formatter.newLine();
        formatter.append(toFormattable(baseCRS));
        return "FITTED_CS";
    }
}
