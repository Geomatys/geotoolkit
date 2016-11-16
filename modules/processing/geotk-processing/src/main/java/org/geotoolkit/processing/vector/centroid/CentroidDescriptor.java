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
package org.geotoolkit.processing.vector.centroid;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of centroid process.
 * name of the process : "centroid"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection clipped</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class CentroidDescriptor extends VectorDescriptor {

    /**Process name : centroid */
    public static final String NAME = "centroid";
    
    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CentroidDescriptor();

    private CentroidDescriptor() {
        super(NAME, "Return Feature centroid of all Feature in FeatureCollection", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CentroidProcess(input);
    }
}
