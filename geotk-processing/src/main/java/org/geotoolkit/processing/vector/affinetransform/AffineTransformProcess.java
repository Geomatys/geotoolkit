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
package org.geotoolkit.processing.vector.affinetransform;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.TransformMapper;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.processing.AbstractProcess;

import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Apply an affine transformation to all FeatureCollection geometries
 * @author Quentin Boileau
 * @module
 */
public class AffineTransformProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public AffineTransformProcess(final ParameterValueGroup input) {
        super(AffineTransformDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        final FeatureSet inputFeatureList = inputParameters.getValue(VectorDescriptor.FEATURE_IN);
        final java.awt.geom.AffineTransform transform = inputParameters.getValue(AffineTransformDescriptor.TRANSFORM_IN);
        final AffineTransformGeometryTransformer trs = new AffineTransformGeometryTransformer(transform);
        final TransformMapper ttype;
        try {
            ttype = new TransformMapper(inputFeatureList.getType(), trs);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        final FeatureSet resultFeatureList = FeatureStreams.decorate(inputFeatureList,ttype);
        outputParameters.getOrCreate(VectorDescriptor.FEATURE_OUT).setValue(resultFeatureList);
    }
}
