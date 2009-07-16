/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import java.util.Collections;
import javax.measure.unit.SI;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;
import org.geotoolkit.internal.referencing.Identifiers;

import static org.geotoolkit.parameter.Parameters.*;


/**
 * The provider for "<cite>Geographic/geocentric conversions</cite>" (EPSG:9602). This provider
 * constructs transforms from {@linkplain GeographicCRS geographic} to {@linkplain GeocentricCRS
 * geocentric} coordinate reference systems.
 * <p>
 * <strong>WARNING:</strong> The EPSG code is the same than the {@link GeocentricToEllipsoid}
 * one. To avoid ambiguity, use the OGC name instead: {@code "Ellipsoid_To_Geocentric"}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GeocentricTransform
 *
 * @since 2.0
 * @module
 */
public class EllipsoidToGeocentric extends MathTransformProvider {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5690807111952562344L;

    /**
     * The operation parameter descriptor for the {@code "semi_major"} parameter value.
     * Valid values range from 0 to infinity. This parameter is mandatory and has no
     * default value.
     */
    public static final ParameterDescriptor<Double> SEMI_MAJOR = MapProjection.SEMI_MAJOR;

    /**
     * The operation parameter descriptor for the {@code "semi_minor"} parameter value.
     * Valid values range from 0 to infinity. This parameter is mandatory and has no
     * default value.
     */
    public static final ParameterDescriptor<Double> SEMI_MINOR = MapProjection.SEMI_MINOR;

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This is a Geotoolkit-specific argument. The default value is 3, which is the value
     * implied in OGC's WKT.
     */
    public static final ParameterDescriptor<Integer> DIM = DefaultParameterDescriptor.create(
                Collections.singletonMap(NAME_KEY,
                    new NamedIdentifier(Citations.GEOTOOLKIT, "dim")),
                3, 2, 3, false);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
                    "Ellipsoid_To_Geocentric",               // OGC name
                    "Geographic/geocentric conversions",     // EPSG name
                    "9602",                                  // EPSG identifier
                    Vocabulary.Keys.GEOCENTRIC_TRANSFORM);   // Geotoolkit name

    /**
     * Constructs the parameters group.
     */
    static ParameterDescriptorGroup createDescriptorGroup(final String ogc,
            final String epsgName, final String epsgCode, final int geotoolkit)
    {
        return Identifiers.createDescriptorGroup(new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,      ogc),
                new NamedIdentifier(Citations.EPSG,     epsgName),
                new NamedIdentifier(Citations.EPSG,     epsgCode),
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(geotoolkit))
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR, SEMI_MINOR, DIM
            });
    }

    /**
     * If this provider is for the 3D case, then {@code complement} is the provider for the 2D case.
     * Conversely if this provider is for the 2D case, then {@code complement} is the provider for
     * the 3D case.
     */
    private final EllipsoidToGeocentric complement;

    /**
     * Constructs a provider with default parameters.
     */
    public EllipsoidToGeocentric() {
        super(3, 3, PARAMETERS);
        complement = new EllipsoidToGeocentric(this);
    }

    /**
     * Constructs a provider for the 2-dimensional case.
     *
     * @param complement The provider for the 3D case.
     */
    private EllipsoidToGeocentric(final EllipsoidToGeocentric complement) {
        super(2, 3, PARAMETERS);
        this.complement = complement;
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Returns {@code 2} if the given parameter group contains a {@linkplain #DIM} parameter
     * having value 2. If the parameter value is 3 or if there is no parameter value, then
     * this method returns {@code 3}, which is consistent with the default value.
     *
     * @throws InvalidParameterValueException if the dimension parameter has an invalid value.
     */
    static int dimension(final ParameterValueGroup values) throws InvalidParameterValueException {
        final Integer dimension = integerValue(DIM, values);
        if (dimension != null) {
            switch (dimension) {
                case 2: // fall through;
                case 3: return dimension;
                default: {
                    final String name = DIM.getName().getCode();
                    throw new InvalidParameterValueException(Errors.format(Errors.Keys.
                            ILLEGAL_ARGUMENT_$2, name, dimension), name, dimension);
                }
            }
        }
        return 3;
    }

    /**
     * Creates a transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final double semiMajor = doubleValue(SEMI_MAJOR, values);
        final double semiMinor = doubleValue(SEMI_MINOR, values);
        final int    dimension = dimension(values);
        MathTransform transform = new GeocentricTransform(semiMajor, semiMinor, SI.METRE, dimension != 2);
        if (dimension != sourceDimension) {
            transform = new MathTransformDecorator(transform, complement);
        }
        return transform;
    }
}
