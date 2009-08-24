/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import javax.measure.unit.SI;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;
import static org.geotoolkit.parameter.Parameters.*;


/**
 * The provider for "<cite>Geographic/geocentric conversions</cite>" (EPSG:9602). This provider
 * constructs transforms from {@linkplain GeocentricCRS geocentric} to {@linkplain GeographicCRS
 * geographic} coordinate reference systems.
 * <p>
 * <strong>WARNING:</strong> The EPSG code is the same than the {@link EllipsoidToGeocentric}
 * one. To avoid ambiguity, use the OGC name instead: {@code "Geocentric_To_Ellipsoid"}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GeocentricTransform
 *
 * @since 2.0
 * @module
 */
public class GeocentricToEllipsoid extends MathTransformProvider {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8459294628751497567L;

    /**
     * The operation parameter descriptor for the {@code "semi_major"} parameter value.
     * Valid values range from 0 to infinity.
     */
    public static final ParameterDescriptor<Double> SEMI_MAJOR = EllipsoidToGeocentric.SEMI_MAJOR;

    /**
     * The operation parameter descriptor for the {@code "semi_minor"} parameter value.
     * Valid values range from 0 to infinity.
     */
    public static final ParameterDescriptor<Double> SEMI_MINOR = EllipsoidToGeocentric.SEMI_MINOR;

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This is a Geotoolkit-specific argument. The default value is 3, which is the value
     * implied in OGC's WKT.
     */
    public static final ParameterDescriptor<Integer> DIM = EllipsoidToGeocentric.DIM;

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS =
            EllipsoidToGeocentric.createDescriptorGroup("Geocentric_To_Ellipsoid");

    /**
     * If this provider is for the 3D case, then {@code complement} is the provider for the 2D case.
     * Conversely if this provider is for the 2D case, then {@code complement} is the provider for
     * the 3D case.
     */
    private final GeocentricToEllipsoid complement;

    /**
     * Constructs a provider with default parameters.
     */
    public GeocentricToEllipsoid() {
        super(3, 3, PARAMETERS);
        complement = new GeocentricToEllipsoid(this);
    }

    /**
     * Constructs a provider for the given target dimension.
     *
     * @param complement The provider for the 3D case.
     */
    private GeocentricToEllipsoid(final GeocentricToEllipsoid complement) {
        super(3, 2, PARAMETERS);
        this.complement = complement;
    }

    /**
     * Creates a transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final double semiMajor = doubleValue(SEMI_MAJOR, values);
        final double semiMinor = doubleValue(SEMI_MINOR, values);
        final int    dimension = EllipsoidToGeocentric.dimension(values);
        MathTransform transform = new GeocentricTransform(semiMajor, semiMinor, SI.METRE, dimension != 2).inverse();
        if (dimension != targetDimension) {
            transform = new MathTransformDecorator(transform, complement);
        }
        return transform;
    }
}
