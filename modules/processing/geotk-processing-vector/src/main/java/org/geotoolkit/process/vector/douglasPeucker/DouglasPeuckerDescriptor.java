/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector.douglaspeucker;

import javax.measure.unit.Unit;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Description of clip by geometry process.
 * Clip a FeatureCollection with a geometry and return a resulting FeatureCollection.
 * Name of the process : "douglaspeucker"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>ACCURACY_IN "accuracy_in" simplification accuracy</li>
 *     <li>UNIT_IN "unit_in" simplification unit</li>
 *     <li>DEL_SMALL_GEO_IN "del_small_geo_in" Simplification behavior with small geometry</li>
 *     <li>LENIENT_TRANSFORM_IN "lenient_transform_in" CRS transformation accuracy</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection simplified</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
final public class DouglasPeuckerDescriptor extends VectorDescriptor {

    /**Process name : douglaspeucker */
    public static final String NAME = "douglasPeucker";
    /**
     * Mandatory - Simplification accuracy
     */
    public static final ParameterDescriptor<Double> ACCURACY_IN=
            new DefaultParameterDescriptor("accuracy_in", "Input simplification accuracy", Double.class, null, true);

    /**
     * Mandatory - Simplification unit
     */
    public static final ParameterDescriptor<Unit> UNIT_IN=
            new DefaultParameterDescriptor("unit_in", "Input simplification unit", Unit.class, null, true);

     /**
     * Optional - Simplification behavior
     */
    public static final ParameterDescriptor<Boolean> DEL_SMALL_GEO_IN=
            new DefaultParameterDescriptor("del_small_geo_in", "Input boolean to set process behavior with small geometry",
                                            Boolean.class, null, false);

    /**
     * Optional - Simplification behavior
     */
    public static final ParameterDescriptor<Boolean> LENIENT_TRANSFORM_IN=
            new DefaultParameterDescriptor("lenient_transform_in", "Input boolean to set accuracy CRS transformation",
                                            Boolean.class, null, false);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, ACCURACY_IN, UNIT_IN, DEL_SMALL_GEO_IN, LENIENT_TRANSFORM_IN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new DouglasPeuckerDescriptor();

    /**
     * Default constructor
     */
    private DouglasPeuckerDescriptor() {
        super(NAME, "Simplify a FeatureCollection geometry with Douglas Pucker algorithm", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new DouglasPeucker();
    }
}
