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
package org.geotoolkit.processing.vector.reproject;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Re-project a FeatureCollection into a target CoordinateReferenceSystem
 * @author Quentin Boileau
 * @module
 */
public class ReprojectProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public ReprojectProcess(final ParameterValueGroup input) {
        super(ReprojectDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        final FeatureSet inputFeatureList  = inputParameters.getValue(VectorDescriptor.FEATURE_IN);
        final CoordinateReferenceSystem targetCRS = inputParameters.getValue(ReprojectDescriptor.CRS_IN);

        final FeatureSet resultFeatureList;
        try {
            resultFeatureList = WrapFeatureSet.decorate(inputFeatureList,
                    new ReprojectMapper(inputFeatureList.getType(), targetCRS));
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        outputParameters.getOrCreate(VectorDescriptor.FEATURE_OUT).setValue(resultFeatureList);
    }
}
