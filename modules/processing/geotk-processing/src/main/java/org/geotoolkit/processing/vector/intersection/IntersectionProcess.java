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
package org.geotoolkit.processing.vector.intersection;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.processing.vector.intersection.IntersectionDescriptor.*;

/**
 * Generate a FeatureCollection where each Feature are the intersections of the two input
 * FeatureCollection's geometries.It is usually called "Spatial AND".
 * @author Quentin Boileau
 * @module
 */
public class IntersectionProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public IntersectionProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList               = inputParameters.getValue(FEATURE_IN);
        final FeatureCollection inputFeatureIntersectionList   = inputParameters.getValue(FEATURE_INTER);
        final String inputGeometryName                         = inputParameters.getValue(GEOMETRY_NAME);

        final FeatureCollection resultFeatureList = new IntersectionFeatureCollection(inputFeatureList, inputFeatureIntersectionList, inputGeometryName);

        outputParameters.getOrCreate(FEATURE_OUT).setValue(resultFeatureList);
    }

    /**
     * Intersection a feature with the FeatureCollection's geometries
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to clip
     * @return Feature
     */
    public static FeatureCollection intersetFeature(final Feature oldFeature, final FeatureType newType,
            final FeatureCollection featureClippingList, final String geometryName)
            throws FactoryException, MismatchedDimensionException, TransformException, ProcessException {

        return VectorProcessUtils.intersectionFeatureToColl(oldFeature, featureClippingList, geometryName);
    }
}
