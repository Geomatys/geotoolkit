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
package org.geotoolkit.process.vector.spatialjoin;

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
 * Parameters description for SpatialJoin process.
 * name of the process : "spatialjoin"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" Source FeatureCollection</li>
 *     <li>FEATURE_TARGET "feature_target" Target FeatureCollection </li>
 *     <li>INTERSECT "intersect" Method used. true => Intersection, false => Nearest</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection intersected</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class SpatialJoinDescriptor extends VectorDescriptor {

    /**Process name : spatialjoin */
    public static final String NAME = "spatialjoin";

    /**
     * Mandatory - Target FeatureCollection
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_TARGET =
            new DefaultParameterDescriptor("feature_target", "Target Features", FeatureCollection.class, null, true);

     /**
     * Optional - Method used. true => Intersection, false => Nearest
     */
    public static final ParameterDescriptor<Boolean> INTERSECT =
            new DefaultParameterDescriptor("intersect", "Method used, intersect or nearest", Boolean.class, true, false);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, FEATURE_TARGET,INTERSECT});

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new SpatialJoinDescriptor();

    /**
     * Default constructor
     */
    private SpatialJoinDescriptor() {
        super(NAME, "Return the target FeatureCollection with source FeatureCollection attributes."
                + "The link between target and source depend of method used (Intersect or Nearest)", INPUT_DESC, OUTPUT_DESC);
    }
    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new SpatialJoin();
    }
}
