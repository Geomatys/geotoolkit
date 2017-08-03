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
package org.geotoolkit.processing.vector.centroid;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.processing.vector.centroid.CentroidDescriptor.*;
import org.opengis.feature.AttributeType;


/**
 * Process to extract geometry centroid from a FeatureCollection.
 *
 * @author Quentin Boileau
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
        final FeatureCollection inputFeatureList = inputParameters.getValue(FEATURE_IN);
        final FeatureCollection resultFeatureList = new CentroidFeatureCollection(inputFeatureList);
        outputParameters.getOrCreate(FEATURE_OUT).setValue(resultFeatureList);
    }

    /**
     * Create a new Feature with centroid
     *
     * @param newType the new FeatureType for the Feature
     */
    public static Feature changeFeature(final Feature oldFeature, final FeatureType newType) {

        //create result feature based on the new feature type and th input feature
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (final PropertyType property : oldFeature.getType().getProperties(true)) {
            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);

            //if the propperty is a geometry
            if (AttributeConvention.isGeometryAttribute(property)) {
                resultFeature.setPropertyValue(name, ((Geometry) value).getCentroid());
            } else if(property instanceof AttributeType && !(AttributeConvention.contains(property.getName()))){
                resultFeature.setPropertyValue(name, value);
            }
        }
        return resultFeature;
    }
}
