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
package org.geotoolkit.processing.vector.retype;

import org.geotoolkit.feature.ViewFeatureType;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.FeatureStreams;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.feature.FeatureType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.parameter.Parameters.*;

/**
 * Apply a mask to a FeatureCollection FeatureType.
 * @author Quentin Boileau
 * @module
 */
public class RetypeProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public RetypeProcess(final ParameterValueGroup input) {
        super(RetypeDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final FeatureType mask = value(RetypeDescriptor.MASK_IN, inputParameters);
        final FeatureCollection resultFeatureList;
        if(mask instanceof ViewFeatureType){
            resultFeatureList = FeatureStreams.decorate(inputFeatureList, (ViewFeatureType) mask);
        }else{
            resultFeatureList = inputFeatureList;
        }

        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
