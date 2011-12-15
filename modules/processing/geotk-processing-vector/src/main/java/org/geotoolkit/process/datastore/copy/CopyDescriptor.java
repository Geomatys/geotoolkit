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

import org.geotoolkit.data.query.Query;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

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
     * Mandatory - create target datastore if it do not exist.
     */
    public static final ParameterDescriptor<Boolean> CREATE =
            new DefaultParameterDescriptor("create", "Create the datastore if it doesn't exist", 
            Boolean.class, false, true);
    
    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE =
            new DefaultParameterDescriptor("erase", "Erase type if it already exist before insertion.", 
            Boolean.class, false, true);
    
    /**
     * Mandatory - Feature type names to  copy. several names can be passed separated by commas.
     */
    public static final ParameterDescriptor<String> TYPE_NAME =
            new DefaultParameterDescriptor("type_name", "Name of the feature type to copy. '*' for all.", 
            String.class, "*", true);
    
    /**
     * Mandatory - Query to use to retrieve FeatureCollection during the process.
     */
    public static final ParameterDescriptor<Query> QUERY =
            new DefaultParameterDescriptor("query", "Query used to get the FeatureCollection during the process.", 
            Query.class, null, true);
    
    /** 
     * Input Parameters 
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{SOURCE_STORE_PARAMS, TARGET_STORE_PARAMS,CREATE,ERASE,TYPE_NAME, QUERY});

    /** 
     * Output Parameters 
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
    public Process createProcess(final ParameterValueGroup input) {
        return new Copy(input);
    }
}
