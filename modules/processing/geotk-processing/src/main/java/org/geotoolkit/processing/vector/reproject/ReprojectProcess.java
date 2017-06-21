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

import org.geotoolkit.feature.ReprojectFeatureType;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.parameter.Parameters.*;

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
    protected void execute() {
        final FeatureCollection inputFeatureList   = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final CoordinateReferenceSystem targetCRS           = value(ReprojectDescriptor.CRS_IN, inputParameters);

        final FeatureCollection resultFeatureList = FeatureStreams.decorate(inputFeatureList,
                new ReprojectFeatureType(inputFeatureList.getFeatureType(), targetCRS));

        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
