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
package org.geotoolkit.process.vector.centroid;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.process.AbstractProcess;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.vector.centroid.CentroidDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process to extract geometry centroid from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class CentroidProcess extends AbstractProcess {

    /**
     * Default Constructor
     */
    public CentroidProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList = value(FEATURE_IN, inputParameters);

        final FeatureCollection resultFeatureList = new CentroidFeatureCollection(inputFeatureList);
        
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Create a new Feature with centroid
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @return Feature
     */
    public static Feature changeFeature(final Feature oldFeature, final FeatureType newType) {

        //create result feature based on the new feature type and th input feature
        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());

        for (Property property : oldFeature.getProperties()) {
            //if the propperty is a geometry
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final Geometry inputFeatureGeometry = (Geometry) property.getValue();
                resultFeature.getProperty(property.getName()).setValue(inputFeatureGeometry.getCentroid());
            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }

        return resultFeature;
    }
}
