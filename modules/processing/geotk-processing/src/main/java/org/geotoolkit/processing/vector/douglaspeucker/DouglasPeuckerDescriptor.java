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
package org.geotoolkit.processing.vector.douglaspeucker;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;

import org.apache.sis.measure.Units;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 * Parameters description for DouglasPeucker process.
 * Simplify Feature geometry
 * Name of the process : "douglaspeucker"
 * Inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>ACCURACY_IN "accuracy_in" simplification accuracy in meters.</li>
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
public final class DouglasPeuckerDescriptor extends VectorDescriptor {

    /**Process name : douglaspeucker */
    public static final String NAME = "douglasPeucker";

    private static final Map ACCURACY_PROPERTIES = new HashMap();
    static{
        ACCURACY_PROPERTIES.put(IdentifiedObject.NAME_KEY, "accuracy_in");
        ACCURACY_PROPERTIES.put(IdentifiedObject.REMARKS_KEY, "Input simplification accuracy.");
    }

    /**
     * Mandatory - Simplification accuracy
     */
    public static final ParameterDescriptor<Double> ACCURACY_IN = new ParameterBuilder()
            .addName("accuracy_in")
            .setRemarks("Input simplification accuracy.")
            .setRequired(true)
            .create(1.0, Units.METRE);

     /**
     * Optional - Simplification behavior
     */
    public static final ParameterDescriptor<Boolean> DEL_SMALL_GEO_IN = new ParameterBuilder()
            .addName("del_small_geo_in")
            .setRemarks("Input boolean to set process behavior with small geometry")
            .setRequired(false)
            .create(Boolean.class, false);

    /**
     * Optional - Simplification behavior
     */
    public static final ParameterDescriptor<Boolean> LENIENT_TRANSFORM_IN = new ParameterBuilder()
            .addName("lenient_transform_in")
            .setRemarks("Input boolean to set accuracy CRS transformation")
            .setRequired(false)
            .create(Boolean.class, true);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, ACCURACY_IN, DEL_SMALL_GEO_IN, LENIENT_TRANSFORM_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

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
    public Process createProcess(final ParameterValueGroup input) {
        return new DouglasPeuckerProcess(input);
    }
}
