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
package org.geotoolkit.processing.vector.startoffset;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.parameter.Parameters.*;

/**
 * Start FeatureCollection iteration at given offset
 * @see org.geotoolkit.internal.data.GenericSortByFeatureIterator
 * @author Quentin Boileau
 * @module
 */
public class StartOffsetProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public StartOffsetProcess(final ParameterValueGroup input) {
        super(StartOffsetDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList   = Parameters.value(VectorDescriptor.FEATURE_IN, inputParameters);
        final int offset                                    = Parameters.value(StartOffsetDescriptor.OFFSET_IN, inputParameters);

        final FeatureCollection resultFeatureList = FeatureStreams.skip(inputFeatureList, offset);

        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
