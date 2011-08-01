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
package org.geotoolkit.process.datastore.copy;

import java.util.Map;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Parameters description of Copy process.
 * name of the process : "copy"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection source</li>
 *     <li>TRANSFORM_IN "transform_in" AffineTransform</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" resulting FeatureCollection</li>
 * </ul>
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class CopyDescriptor extends VectorDescriptor {

    /**
     * Process name : copy 
     */
    public static final String NAME = "copy";

    /**
     * Mandatory - Source datastore parameters
     */
    public static final ParameterDescriptor<Map> SOURCE_STORE_PARAMS =
            new DefaultParameterDescriptor("source_params", "The source datastore parameters", 
            Map.class, null, true);
    
    /**
     * Mandatory - Target datastore parameters
     */
    public static final ParameterDescriptor<Map> TARGET_STORE_PARAMS =
            new DefaultParameterDescriptor("target_params", "The target datastore parameters", 
            Map.class, null, true);
    

    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{SOURCE_STORE_PARAMS, TARGET_STORE_PARAMS});

    /** 
     * Ouput Parameters 
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{});
    
    public static final ProcessDescriptor INSTANCE = new CopyDescriptor();

    private CopyDescriptor() {
        super(NAME, "Copy features from one datastore to another.", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new Copy();
    }
}
