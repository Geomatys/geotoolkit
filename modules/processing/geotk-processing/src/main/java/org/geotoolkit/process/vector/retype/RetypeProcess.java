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
package org.geotoolkit.process.vector.retype;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.vector.retype.RetypeDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Apply a mask to a FeatureCollection FeatureType.
 * @author Quentin Boileau
 * @module pending
 */
public class RetypeProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public RetypeProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList = value(FEATURE_IN, inputParameters);
        final FeatureType mask = value(MASK_IN, inputParameters);

        final FeatureCollection resultFeatureList = GenericRetypeFeatureIterator.wrap(inputFeatureList, mask);
        
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
