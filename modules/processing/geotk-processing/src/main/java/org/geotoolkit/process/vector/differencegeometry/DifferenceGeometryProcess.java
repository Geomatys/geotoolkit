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
package org.geotoolkit.process.vector.differencegeometry;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.vector.differencegeometry.DifferenceGeometryDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process to clip the difference with a FeatureCollection using a geometry
 * @author Quentin Boileau
 * @module pending
 */
public class DifferenceGeometryProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public DifferenceGeometryProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList   = value(FEATURE_IN, inputParameters);
        final Geometry inputDifferenceGeometry              = value(DIFF_GEOMETRY_IN, inputParameters);

        final FeatureCollection resultFeatureList =
                new DifferenceGeometryFeatureCollection(inputFeatureList,inputDifferenceGeometry);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip difference the feature with the Geometry
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @param geometry th geometry
     * @return Feature
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final Geometry geometry) {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());


        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final Geometry diffGeometry = VectorProcessUtils.geometryDifference((Geometry) property.getValue(), geometry);

                if(diffGeometry != null) {
                    resultFeature.getProperty(property.getName()).setValue(diffGeometry);
                }else{
                    return null;
                }
            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }

        return resultFeature;
    }
}
