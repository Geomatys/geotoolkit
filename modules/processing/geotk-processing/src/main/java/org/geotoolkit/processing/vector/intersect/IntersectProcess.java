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
package org.geotoolkit.processing.vector.intersect;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;

import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.parameter.Parameters.*;
/**
 * This process return all Features from a FeatureCollection that intersect a geometry.
 * @author Quentin Boileau
 * @module pending
 */
public class IntersectProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public IntersectProcess(final ParameterValueGroup input) {
        super(IntersectDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList   = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final Geometry interGeom                            = value(IntersectDescriptor.GEOMETRY_IN, inputParameters);

        final FeatureCollection resultFeatureList = new IntersectFeatureCollection(inputFeatureList, interGeom);

        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }
}
