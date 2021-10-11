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
package org.geotoolkit.processing.vector.differencegeometry;

import org.locationtech.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of difference clipping by geometry process.
 * Compute difference between a FeatureCollection and a geometry. Return a FeatureCollection.
 * name of the process : "diffGeometry"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection</li>
 *     <li>DIFF_GEOMETRY_IN "diff_geometry_in" Geometry used to compute difference</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class DifferenceGeometryDescriptor extends VectorDescriptor {

    /**Process name : diffGeometry */
    public static final String NAME = "vector:diffGeometry";
    /**
     * Mandatory - Difference Geometry
     */
    public static final ParameterDescriptor<Geometry> DIFF_GEOMETRY_IN =new ParameterBuilder()
            .addName("diff_geometry_in")
            .setRemarks("Inpute clip geometry")
            .setRequired(true)
            .create(Geometry.class, null);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, DIFF_GEOMETRY_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new DifferenceGeometryDescriptor();

    /**
     * Default constructor
     */
    private DifferenceGeometryDescriptor() {
        super(NAME, "Compute difference between a FeatureCollection and a geometry. Return a FeatureCollection.", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new DifferenceGeometryProcess(input);
    }
}
