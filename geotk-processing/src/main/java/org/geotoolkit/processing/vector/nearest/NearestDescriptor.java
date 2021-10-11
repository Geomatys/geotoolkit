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
package org.geotoolkit.processing.vector.nearest;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.locationtech.jts.geom.Geometry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description for Nearest process.
 * name of the process : "nearest"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection</li>
 *     <li>GEOMETRY_IN "geometry_in" a geometry</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection intersected</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class NearestDescriptor extends VectorDescriptor {

    /**Process name : nearest */
    public static final String NAME = "vector:nearest";
    /**
     * Mandatory - Intersection geometry
     */
    public static final ParameterDescriptor<Geometry> GEOMETRY_IN = new ParameterBuilder()
            .addName("geometry_in")
            .setRemarks("Input geometry")
            .setRequired(true)
            .create(Geometry.class, null);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURESET_IN, GEOMETRY_IN);
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURESET_OUT);
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new NearestDescriptor();

    /**
     * Default constructor
     */
    private NearestDescriptor() {
        super(NAME, "Return the nearest Feature(s) in a FeatureCollection with the inputGeometry", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new NearestProcess(input);
    }
}
