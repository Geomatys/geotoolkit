/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;

import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.internal.referencing.CRSUtilities;

import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;


/**
 * Map projection parameters, with special processing for alternative ways to express the
 * ellipsoid axis length and the standard parallels. See {@link MapProjectionDescriptor}
 * for more information about those non-standard parameters.
 * <p>
 * The main purpose of this class is to supported transparently the NetCDF ways to express
 * some parameter values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/reference/StandardCoordinateTransforms.html">NetCDF projection parameters</a>
 *
 * @since 3.20
 * @module
 */
final class MapProjectionParameters extends ParameterGroup {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6801091012335717139L;

    /**
     * The earth radius parameter.
     */
    private final class EarthRadius extends FloatParameter {
        /**
         * For cross-version compatibility. Actually instances of this class
         * are not expected to be serialized, but we try to be a bit safer here.
         */
        private static final long serialVersionUID = 5848432458976184182L;

        /**
         * Creates a new parameter.
         */
        EarthRadius() {
            super(UniversalParameters.EARTH_RADIUS);
        }

        /**
         * Invoked when the parameter value is requested. If the earth radius has not been
         * explicitely defined, returns the authalic radius.
         */
        @Override
        public double doubleValue() {
            double value = super.doubleValue();
            if (Double.isNaN(value)) {
                value = CRSUtilities.getAuthalicRadius(get(SEMI_MAJOR), get(SEMI_MINOR));
            }
            return value;
        }

        /**
         * Invoked when a new parameter value is set. This method set both axis length
         * to the given radius.
         */
        @Override
        public void setValue(final double value) {
            super.setValue(value); // Also perform argument check.
            set(SEMI_MAJOR, value);
            set(SEMI_MINOR, value);
        }
    }

    /**
     * {@code true} if the {@code "standard_parallel"} parameter name is present.
     */
    private final boolean hasStandardParallel;

    /**
     * The {@link EarthRadius} parameter instance, created when first needed.
     */
    private transient ParameterValue<Double> earthRadius;

    /**
     * The {@link InverseFlattening} parameter instance, created when first needed.
     */
    private transient ParameterValue<Double> inverseFlattening;

    /**
     * The {@link StandardParallel} parameter instance, created when first needed.
     */
    private transient ParameterValue<Double> standardParallel;

    /**
     * Creates a new parameter value group.
     */
    MapProjectionParameters(final MapProjectionDescriptor descriptor) {
        super(descriptor);
        hasStandardParallel = descriptor.hasStandardParallel;
    }

    /**
     * Returns the value associated to the given parameter descriptor.
     */
    double get(final ParameterDescriptor<Double> parameter) {
        return Parameters.doubleValue(parameter, this);
    }

    /**
     * Sets the value associated to the given parameter descriptor.
     */
    void set(final ParameterDescriptor<Double> parameter, final double value) {
        Parameters.getOrCreate(parameter, this).setValue(value);
    }

    /**
     * Returns the value in this group for the specified name. If the given name is one of the
     * "invisible" parameters, returns a dynamic parameter view without adding it to the list of
     * real parameter values.
     *
     * @param  name The case insensitive name of the parameter to search for.
     * @return The parameter value for the given name.
     * @throws ParameterNotFoundException if there is no parameter value for the given name.
     */
    @Override
    public ParameterValue<?> parameter(String name) throws ParameterNotFoundException {
        name = name.trim();
        ParameterValue<?> value;
        if (name.equalsIgnoreCase(MapProjectionDescriptor.EARTH_RADIUS)) {
            value = earthRadius;
            if (value == null) {
                value = earthRadius = new EarthRadius();
            }
        } else if (name.equalsIgnoreCase(MapProjectionDescriptor.INVERSE_FLATTENING)) {
            value = inverseFlattening;
            if (value == null) {
                // TODO
            }
        } else if (!hasStandardParallel && name.equalsIgnoreCase(MapProjectionDescriptor.STANDARD_PARALLEL)) {
            value = standardParallel;
            if (value == null) {
                // TODO
            }
        } else {
            value = super.parameter(name);
        }
        return value;
    }
}
