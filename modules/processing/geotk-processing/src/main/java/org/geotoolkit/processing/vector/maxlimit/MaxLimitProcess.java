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
package org.geotoolkit.processing.vector.maxlimit;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.processing.AbstractProcess;

import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.vector.maxlimit.MaxLimitDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Limit a FeatureCollection returns to a maximum
 * @author Quentin Boileau
 * @module
 */
public class MaxLimitProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public MaxLimitProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList   = value(FEATURE_IN, inputParameters);
        final int max                                       = value(MAX_IN, inputParameters);

        final FeatureCollection resultFeatureList = GenericMaxFeatureIterator.wrap(inputFeatureList, max);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
