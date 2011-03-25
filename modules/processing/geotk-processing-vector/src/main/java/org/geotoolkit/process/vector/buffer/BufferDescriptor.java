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
package org.geotoolkit.process.vector.buffer;

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
 * Parameters description for Buffer process.
 * Make a geometry buffer around Features geometry
 * Name of the process : "buffer"
 * Inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>DISTANCE_IN "distance_in" buffer distance</li>
 *     <li>UNIT_IN "unit_in" simplification unit</li>
 *     <li>LENIENT_TRANSFORM_IN "lenient_transform_in" CRS transformation accuracy</li>
 * </ul>
 * Outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection buffered</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class BufferDescriptor extends VectorDescriptor {

    /**Process name : buffer */
    public static final String NAME = "buffer";
    /**
     * Mandatory - Buffer distance
     */
    public static final ParameterDescriptor<Double> DISTANCE_IN=
            new DefaultParameterDescriptor("distance_in", "Input buffer distance", Double.class, null, true);

    /**
     * Mandatory - Distance unit
     */
    public static final ParameterDescriptor<Unit> UNIT_IN=
            new DefaultParameterDescriptor("unit_in", "Input simplification unit", Unit.class, null, true);

  
    public static final ParameterDescriptor<Boolean> LENIENT_TRANSFORM_IN=
            new DefaultParameterDescriptor("lenient_transform_in", "Input boolean to set accuracy CRS transformation",
                                            Boolean.class, true, false);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, DISTANCE_IN, UNIT_IN, LENIENT_TRANSFORM_IN});

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new BufferDescriptor();

    /**
     * Default constructor
     */
    private BufferDescriptor() {
        super(NAME, "Apply buffer function to a FeatureCollection geometry", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new Buffer();
    }
}
