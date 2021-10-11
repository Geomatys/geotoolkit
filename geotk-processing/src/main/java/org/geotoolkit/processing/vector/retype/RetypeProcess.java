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

import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.feature.FeatureType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

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
        final FeatureCollection inputFeatureList = inputParameters.getValue(VectorDescriptor.FEATURE_IN);
        final FeatureType mask = inputParameters.getValue(RetypeDescriptor.MASK_IN);
        final FeatureCollection resultFeatureList;
        if(mask instanceof ViewMapper){
            resultFeatureList = FeatureStreams.decorate(inputFeatureList, (ViewMapper) mask);
        }else{
            resultFeatureList = inputFeatureList;
        }

        outputParameters.getOrCreate(VectorDescriptor.FEATURE_OUT).setValue(resultFeatureList);
    }
}
