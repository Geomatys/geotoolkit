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
package org.geotoolkit.process.vector.union;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Parameters description of Union process.
 * name of the process : "union"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection source </li>
 *     <li>FEATURE_UNION "feature_union" FeatureCollection used for compute the difference</li>
 *     <li>INPUT_GEOMETRY_NAME "input_geometry_name" String attribute name of input geometry used by union process</li>
 *     <li>UNION_GEOMETRY_NAME "union_geometry_name" String attribute name of union geometry used by union process</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" the result FeatureCollection </li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class UnionDescriptor extends VectorDescriptor {

    /**Process name : union */
    public static final String NAME = "union";
    /**
     * Mandatory - Union Feature Collection
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_UNION =
            new DefaultParameterDescriptor("feature_union", "Input FeatureCollection used for Union process", FeatureCollection.class, null, true);
    /**
     * Optional - Input geometry property name. Refer to the geometry form FEATURE_IN used for the union process
     */
    public static final ParameterDescriptor<String> INPUT_GEOMETRY_NAME =
            new DefaultParameterDescriptor("input_geometry_name", "Input geometry property name", String.class, null, false);
    /**
     * Optional - Union geometry property name. Refer to the geometry from FEATURE_UNION used for the union process
     */
    public static final ParameterDescriptor<String> UNION_GEOMETRY_NAME =
            new DefaultParameterDescriptor("union_geometry_name", "Union geometry property name", String.class, null, false);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, FEATURE_UNION, INPUT_GEOMETRY_NAME, UNION_GEOMETRY_NAME});
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new UnionDescriptor();

    /**
     * Default constructor
     */
    private UnionDescriptor() {
        super(NAME, "Return the result FeatureCollection of Union process", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new Union();
    }
}
