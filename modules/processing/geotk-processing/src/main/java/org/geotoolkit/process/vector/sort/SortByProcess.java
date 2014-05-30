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
package org.geotoolkit.process.vector.sort;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.geotoolkit.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.vector.sort.SortByDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Sort a FeatureCollection.
 * @see org.geotoolkit.data.memory.GenericSortByFeatureIterator
 * @author Quentin Boileau
 * @module pending
 */
public class SortByProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public SortByProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection<Feature> inputFeatureList   = Parameters.value(FEATURE_IN, inputParameters);
        final org.opengis.filter.sort.SortBy[] sorter       = Parameters.value(SORTER_IN, inputParameters);

        final FeatureCollection resultFeatureList = GenericSortByFeatureIterator.wrap(inputFeatureList, sorter);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
        outputParameters.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }
}
