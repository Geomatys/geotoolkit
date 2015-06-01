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
package org.geotoolkit.processing.vector.extendfeature;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.GenericExtendFeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.vector.extendfeature.ExtendFeatureDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Adding on the fly attributes of Feature contents.
 * @author Quentin Boileau
 * @module pending
 */
public class ExtendFeatureProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public ExtendFeatureProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList           = value(FEATURE_IN, inputParameters);
        final GenericExtendFeatureIterator.FeatureExtend extension  = value(EXTEND_IN, inputParameters);
        final Hints hints                                           = value(HINTS_IN, inputParameters);

        final FeatureCollection resultFeatureList = GenericExtendFeatureIterator.wrap(inputFeatureList, extension, hints);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
