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
package org.geotoolkit.processing.vector.sort;

import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Sort a FeatureCollection.
 * @see org.geotoolkit.internal.data.GenericSortByFeatureIterator
 * @author Quentin Boileau
 * @module
 */
public class SortByProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public SortByProcess(final ParameterValueGroup input) {
        super(SortByDescriptor.INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList = inputParameters.getValue(VectorDescriptor.FEATURE_IN);
        final org.opengis.filter.sort.SortBy[] sorter = inputParameters.getValue(SortByDescriptor.SORTER_IN);
        final FeatureCollection resultFeatureList = FeatureStreams.sort(inputFeatureList, sorter);
        outputParameters.getOrCreate(VectorDescriptor.FEATURE_OUT).setValue(resultFeatureList);
    }
}
