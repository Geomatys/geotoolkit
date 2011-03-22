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
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process to extract geometry centroid from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class Centroid extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default Constructor
     */
    public Centroid() {
        super(CentroidDescriptor.INSTANCE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterValueGroup getOutput() {
        return result;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(CentroidDescriptor.FEATURE_IN, inputParameters);

        final CentroidFeatureCollection resultFeatureList = new CentroidFeatureCollection(inputFeatureList);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * Create a new Feature with centroid
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @return Feature
     */
    public static Feature changeFeature(Feature oldFeature, FeatureType newType) {

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
