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

import java.util.Map;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;


/**
 * Map projection parameters, with special processing for alternative ways to express the
 * ellipsoid axis length and the standard parallels. Those alternative ways are non-standard;
 * when a value is set to such alternative parameter, the value is translated to standard
 * parameter values as soon as possible.
 * <p>
 * The non-standard parameters are:
 * <p>
 * <ul>
 *   <li>{@code "earth_radius"} and {@code "inverse_flattening"}, which are mapped to the
 *       {@link UniversalParameters#SEMI_MAJOR} and {@link UniversalParameters#SEMI_MINOR}
 *       parameters.</li>
 *   <li>{@code "standard_parallel"} with an array value of 1 or 2 elements, which is mapped to
 *       {@link UniversalParameters#STANDARD_PARALLEL_1} and
 *       {@link UniversalParameters#STANDARD_PARALLEL_2}</li>
 * </ul>
 * <p>
 * The main purpose of this class is to supported transparently the NetCDF ways to express
 * some parameter values.
 *
 * @author Martin Desruisseaux (Geomatys)
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
final class MapProjectionDescriptor extends DefaultParameterDescriptorGroup {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -9142116135803309453L;

    /**
     * The parameter name for the Earth radius.
     */
    static final String EARTH_RADIUS = "earth_radius";

    /**
     * The parameter name for inverse flattening.
     */
    static final String INVERSE_FLATTENING = "inverse_flattening";

    /**
     * The parameter name for the standard parallels.
     */
    static final String STANDARD_PARALLEL = "standard_parallel";

    /**
     * A constant for the {@linkplain UniversalParameters#createDescriptorGroup factory method}
     * method which indicate that the {@link #EARTH_RADIUS} parameter needs to be added.
     */
    static final int ADD_EARTH_RADIUS = 1;

    /**
     * A constant for the {@linkplain UniversalParameters#createDescriptorGroup factory method}
     * method which indicate that the {@link #STANDARD_PARALLEL} parameter needs to be added.
     */
    static final int ADD_STANDARD_PARALLEL = 2;

    /**
     * Bitwise combination of {@code ADD_*} constants indicating which dynamic parameters to add.
     */
    final int supplement;

    /**
     * Creates a new parameter descriptor from the given properties and parameters.
     */
    MapProjectionDescriptor(final Map<String,?> properties, final ParameterDescriptor<?>[] parameters, final int supplement) {
        super(properties, 1, 1, parameters);
        this.supplement = supplement;
    }

    /**
     * Returns the parameter descriptor for the given name. If the given name is one of the
     * "invisible" parameters, returns a descriptor for that parameter without adding it to
     * the list of parameter values.
     *
     * @param  name The case insensitive name of the parameter to search for.
     * @return The parameter for the given name.
     * @throws ParameterNotFoundException if there is no parameter for the given name.
     */
    @Override
    public GeneralParameterDescriptor descriptor(String name) throws ParameterNotFoundException {
        name = name.trim();
        if ((supplement & ADD_EARTH_RADIUS) != 0) {
            if (name.equalsIgnoreCase(EARTH_RADIUS)) {
                return UniversalParameters.EARTH_RADIUS;
            }
            if (name.equalsIgnoreCase(INVERSE_FLATTENING)) {
                return UniversalParameters.INVERSE_FLATTENING;
            }
        }
        if ((supplement & ADD_STANDARD_PARALLEL) != 0) {
            if (name.equalsIgnoreCase(STANDARD_PARALLEL)) {
                return UniversalParameters.STANDARD_PARALLEL;
            }
        }
        return super.descriptor(name);
    }

    /**
     * Returns the parameter group implementation which can handle the "invisible" parameters.
     */
    @Override
    public ParameterValueGroup createValue() {
        return new MapProjectionParameters(this);
    }
}
