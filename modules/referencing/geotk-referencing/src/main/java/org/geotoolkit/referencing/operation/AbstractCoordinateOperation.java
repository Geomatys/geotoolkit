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
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.util.FactoryException;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.internal.referencing.WKTUtilities;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.DefaultTransformation;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;


/**
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
public class AbstractCoordinateOperation {
    private AbstractCoordinateOperation() {
    }

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
     * Returns a coordinate operation of the specified class. This method constructs an instance
     * of {@link Transformation}, {@link ConicProjection}, {@link CylindricalProjection},
     * {@link PlanarProjection}, {@link Projection} or {@link Conversion}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     * @param method     The operation method, or {@code null}.
     * @param type       The minimal type as <code>{@linkplain Conversion}.class</code>,
     *                   <code>{@linkplain Projection}.class</code>, <i>etc.</i>
     *                   This method may create an instance of a subclass of {@code type}.
     * @return A new coordinate operation, as an instance of the given type if possible.
     */
    public static CoordinateOperation create(final Map<String,?>            properties,
                                             final CoordinateReferenceSystem sourceCRS,
                                             final CoordinateReferenceSystem targetCRS,
                                             final MathTransform             transform,
                                                   OperationMethod           method,
                                             Class<? extends CoordinateOperation> type)
            throws FactoryException
    {
        if (Projection.class.isAssignableFrom(type)) {
            if (!(sourceCRS instanceof GeographicCRS) || !(targetCRS instanceof ProjectedCRS)) {
                type = Conversion.class;
            }
        }
        if (method == null) {
            // TODO: temporary patch (we need to avoid this situation).
            if (transform instanceof AbstractMathTransform) {
                final ParameterDescriptorGroup d = ((AbstractMathTransform) transform).getParameterDescriptors();
                if (d != null) {
                    final Identifier name = d.getName();
                    if (name != null) {
                        final String n = name.getCode();
                        for (final OperationMethod m : DefaultFactories.forBuildin(MathTransformFactory.class).getAvailableMethods(SingleOperation.class)) {
                            if (IdentifiedObjects.isHeuristicMatchForName(m, n)) {
                                method = m;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (method != null) {
            if (method instanceof MathTransformProvider) {
                final Class<? extends SingleOperation> candidate =
                        ((MathTransformProvider) method).getOperationType();
                if (candidate != null) {
                    if (type == null) {
                        type = candidate;
                    } else if (type.isAssignableFrom(candidate)) {
                        type = candidate.asSubclass(type);
                    }
                }
            }
            if (type != null) {
                if (Transformation.class.isAssignableFrom(type)) {
                    return new DefaultTransformation(properties, sourceCRS, targetCRS, null, method, transform);
                }
                if (!Conversion.class.isAssignableFrom(type)) {
                    type = Conversion.class;
                }
                return new DefaultConversion(properties, sourceCRS, targetCRS, null, method, transform)
                        .specialize((Class) type, sourceCRS, targetCRS, DefaultFactories.forBuildin(MathTransformFactory.class));
            }
        }
        throw new IllegalArgumentException();
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
        return org.apache.sis.referencing.operation.AbstractCoordinateOperation.castOrCopy(operation).getLinearAccuracy();
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
            final Set<Identifier> identifiers = object.getIdentifiers();
            final Map<String,Object> properties = new HashMap<>(4);
            properties.put(IdentifiedObject.NAME_KEY,        object.getName());
            properties.put(IdentifiedObject.IDENTIFIERS_KEY, identifiers.toArray(new Identifier[identifiers.size()]));
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
