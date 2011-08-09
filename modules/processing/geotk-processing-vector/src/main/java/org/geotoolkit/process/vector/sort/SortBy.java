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
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Sort a FeatureCollection.
 * @see org.geotoolkit.data.memory.GenericSortByFeatureIterator
 * @author Quentin Boileau
 * @module pending
 */
public class SortBy extends AbstractProcess {

    /**
     * Default constructor
     */
    public SortBy(final ParameterValueGroup input) {
        super(SortByDescriptor.INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public ParameterValueGroup call() {
        fireStartEvent(new ProcessEvent(this));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(SortByDescriptor.FEATURE_IN, inputParameters);
        final org.opengis.filter.sort.SortBy[] sorter = Parameters.value(SortByDescriptor.SORTER_IN, inputParameters);
        
        final FeatureCollection resultFeatureList = GenericSortByFeatureIterator.wrap(inputFeatureList, sorter);

        outputParameters.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        fireEndEvent(new ProcessEvent(this,null,100));
        return outputParameters;
    }
}
