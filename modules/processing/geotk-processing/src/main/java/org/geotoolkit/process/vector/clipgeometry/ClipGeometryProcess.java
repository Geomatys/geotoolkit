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
package org.geotoolkit.process.vector.clipgeometry;

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

import static org.geotoolkit.process.vector.clipgeometry.ClipGeometryDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * Process to clip a FeatureCollection using a geometry
 * @author Quentin Boileau
 * @module pending
 */
public class ClipGeometryProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public ClipGeometryProcess(final ParameterValueGroup input) {
        super(ClipGeometryDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection<Feature> inputFeatureList   = value(FEATURE_IN, inputParameters);
        final Geometry inputClippingGeometry                = value(CLIP_GEOMETRY_IN, inputParameters);

        final FeatureCollection resultFeatureList = new ClipGeometryFeatureCollection(inputFeatureList,inputClippingGeometry);
        
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip the feature with the Geometry Clipping
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @return Feature
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final Geometry clipGeometry) {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());

        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final Geometry interGeometry = VectorProcessUtils.geometryIntersection((Geometry) property.getValue(), clipGeometry);

                //test clipping
                if (interGeometry != null) {

                    resultFeature.getProperty(property.getName()).setValue(interGeometry);
                } else {
                    return null;
                }
            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }

        return resultFeature;
    }
}
