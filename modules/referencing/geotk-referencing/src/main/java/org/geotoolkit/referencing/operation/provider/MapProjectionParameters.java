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

import javax.measure.unit.Unit;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.parameter.FloatParameter;
import org.apache.sis.internal.referencing.Formulas;

import org.geotoolkit.parameter.AbstractParameterValue;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;
import static org.geotoolkit.referencing.operation.provider.MapProjectionDescriptor.ADD_EARTH_RADIUS;
import static org.geotoolkit.referencing.operation.provider.MapProjectionDescriptor.ADD_STANDARD_PARALLEL;


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
     * The earth radius parameter. This parameter is computed automatically from the
     * {@code "semi_major"} and {@code "semi_minor"}. When explicitely set, this parameter
     * value is also assigned to the {@code "semi_major"} and {@code "semi_minor"} axis lengths.
     *
     * @see org.geotoolkit.referencing.datum.DefaultEllipsoid#getAuthalicRadius()
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
            super(EARTH_RADIUS);
        }

        /**
         * Invoked when the parameter value is requested. Unconditionally computes
         * the authalic radius. If an Earth radius has been explicitely specified,
         * the result will be the same unless the user overwrote it with explicit
         * semi-major or semi-minor axis length.
         */
        @Override
        public double doubleValue() {
            return Formulas.getAuthalicRadius(get(SEMI_MAJOR), get(SEMI_MINOR));
        }

        /**
         * Invoked when a new parameter value is set. This method sets both axis length
         * to the given radius.
         */
        @Override
        public void setValue(final double value, final Unit<?> unit) {
            super.setValue(value, unit); // Perform argument check.
            set(SEMI_MAJOR, value, unit);
            set(SEMI_MINOR, value, unit);
        }
    }

    /**
     * The inverse flattening parameter. This parameter is computed automatically from the
     * {@code "semi_major"} and {@code "semi_minor"}. When explicitly set, this parameter
     * value is used for computing the semi-minor axis length.
     *
     * @see org.geotoolkit.referencing.datum.DefaultEllipsoid#getInverseFlattening()
     */
    private final class InverseFlattening extends FloatParameter implements ChangeListener {
        /**
         * For cross-version compatibility. Actually instances of this class
         * are not expected to be serialized, but we try to be a bit safer here.
         */
        private static final long serialVersionUID = 4490056024453509851L;

        /**
         * Creates a new parameter.
         */
        InverseFlattening() {
            super(INVERSE_FLATTENING);
        }

        /**
         * Returns the semi-major parameter, which is needed by this parameter value for
         * computing the semi-minor parameter.
         */
        private AbstractParameterValue<?> semiMajor() {
            return (AbstractParameterValue<?>) parameter("semi_major");
        }

        /**
         * Invoked when the parameter value is requested. Unconditionally computes the inverse
         * flattening from the ellipsoid axis lengths. Note that the result will be slightly
         * different than the specified value because of rounding errors.
         */
        @Override
        public double doubleValue() {
            final double semiMajorAxis = get(SEMI_MAJOR);
            final double semiMinorAxis = get(SEMI_MINOR);
            double ivf = semiMajorAxis / (semiMajorAxis - semiMinorAxis);
            if (Double.isNaN(ivf)) {
                ivf = super.doubleValue();
            }
            return ivf;
        }

        /**
         * Invoked when a new parameter value is set. This method computes the semi-minor
         * axis length from the given value. It will also register a listener in case the
         * semi-major axis length is updated after this method call.
         */
        @Override
        public void setValue(final double value, final Unit<?> unit) {
            final boolean wasNull = Double.isNaN(super.doubleValue());
            super.setValue(value, unit); // Perform argument check.
            if (Double.isNaN(value)) {
                if (!wasNull) {
                    semiMajor().removeChangeListener(this);
                }
            } else {
                if (wasNull) {
                    semiMajor().addChangeListener(this);
                }
                update(value);
            }
        }

        /**
         * Computes the semi-minor axis length from the given inverse flattening value.
         */
        private void update(final double value) {
            final ParameterValue<?> semiMajor;
            try {
                semiMajor = Parameters.getOrCreate(SEMI_MAJOR, MapProjectionParameters.this);
            } catch (IllegalStateException e) {
                // Semi-major axis is not yet defined.
                // Ignore - we will try to compute gain later.
                return;
            }
            set(SEMI_MINOR, semiMajor.doubleValue()*(1 - 1/value), semiMajor.getUnit());
        }

        /**
         * Invoked when the semi-major axis value changed. This method recomputes
         * the semi-minor axis length from the ellipsoid.
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            update(super.doubleValue());
        }
    }

    /**
     * The standard parallel parameter as an array of {@code double}. This parameter is computed
     * automatically from the {@code "standard_parallel_1"} and {@code "standard_parallel_1"}
     * parameters. When explicitely set, the parameter elements are given to the above-cited
     * parameters.
     */
    private final class StandardParallel extends Parameter<double[]> {
        /**
         * For cross-version compatibility. Actually instances of this class
         * are not expected to be serialized, but we try to be a bit safer here.
         */
        private static final long serialVersionUID = -1379566730374843040L;

        /**
         * Creates a new parameter.
         */
        StandardParallel() {
            super(STANDARD_PARALLEL);
        }

        /**
         * Invoked when the parameter value is requested. Unconditionally computes the array
         * from the {@code "standard_parallel_1"} and {@code "standard_parallel_1"} parameters.
         */
        @Override
        public double[] getValue() {
            final double standardParallel1 = get(STANDARD_PARALLEL_1);
            final double standardParallel2 = get(STANDARD_PARALLEL_2);
            if (Double.isNaN(standardParallel2)) {
                return Double.isNaN(standardParallel1) ? ArraysExt.EMPTY_DOUBLE : new double[] {standardParallel1};
            }
            return new double[] {standardParallel1, standardParallel2};
        }

        /**
         * Invoked when a new parameter value is set. This method assign the array elements
         * to @code "standard_parallel_1"} and {@code "standard_parallel_1"} parameters.
         */
        @Override
        @SuppressWarnings("fallthrough")
        protected void setSafeValue(final double[] value, final Unit<?> unit) {
            double standardParallel1 = Double.NaN;
            double standardParallel2 = Double.NaN;
            if (value != null) {
                switch (value.length) {
                    default: {
                        throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_1,
                                MapProjectionDescriptor.STANDARD_PARALLEL));
                    }
                    case 2: standardParallel2 = value[1]; // Fallthrough
                    case 1: standardParallel1 = value[0]; // Fallthrough
                    case 0: break;
                }
            }
            set(STANDARD_PARALLEL_1, standardParallel1, unit);
            set(STANDARD_PARALLEL_2, standardParallel2, unit);
        }
    }

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
    private transient ParameterValue<double[]> standardParallel;

    /**
     * Creates a new parameter value group. An instance of {@link MapProjectionDescriptor}
     * is mandatory, because some method in this class will need to cast the descriptor.
     */
    MapProjectionParameters(final MapProjectionDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * Returns the value associated to the given parameter descriptor.
     */
    final double get(final ParameterDescriptor<Double> parameter) {
        return Parameters.doubleValue(parameter, this);
    }

    /**
     * Sets the value associated to the given parameter descriptor.
     */
    final void set(final ParameterDescriptor<Double> parameter, final double value, final Unit<?> unit) {
        Parameters.getOrCreate(parameter, this).setValue(value, unit);
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
        final int supplement = ((MapProjectionDescriptor) getDescriptor()).supplement;
        if ((supplement & ADD_EARTH_RADIUS) != 0) {
            if (name.equalsIgnoreCase(MapProjectionDescriptor.EARTH_RADIUS)) {
                ParameterValue<?> value = earthRadius;
                if (value == null) {
                    value = earthRadius = new EarthRadius();
                }
                return value;
            }
            if (name.equalsIgnoreCase(MapProjectionDescriptor.INVERSE_FLATTENING)) {
                ParameterValue<?> value = inverseFlattening;
                if (value == null) {
                    value = inverseFlattening = new InverseFlattening();
                }
                return value;
            }
        }
        if ((supplement & ADD_STANDARD_PARALLEL) != 0) {
            if (name.equalsIgnoreCase(MapProjectionDescriptor.STANDARD_PARALLEL)) {
                ParameterValue<?> value = standardParallel;
                if (value == null) {
                    value = standardParallel = new StandardParallel();
                }
                return value;
            }
        }
        return super.parameter(name);
    }
}
