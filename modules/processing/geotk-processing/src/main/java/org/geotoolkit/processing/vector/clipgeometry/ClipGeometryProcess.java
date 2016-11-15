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
package org.geotoolkit.processing.vector.clipgeometry;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.processing.vector.clipgeometry.ClipGeometryDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;


/**
 * Process to clip a FeatureCollection using a geometry
 *
 * @author Quentin Boileau
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
        final FeatureCollection inputFeatureList  = value(FEATURE_IN, inputParameters);
        final Geometry inputClippingGeometry      = value(CLIP_GEOMETRY_IN, inputParameters);
        final FeatureCollection resultFeatureList = new ClipGeometryFeatureCollection(inputFeatureList,inputClippingGeometry);
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip the feature with the Geometry Clipping
     *
     * @param newType the new FeatureType for the Feature
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final Geometry clipGeometry) {
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (PropertyType property : oldFeature.getType().getProperties(true)) {
            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Geometry interGeometry = VectorProcessUtils.geometryIntersection((Geometry) value, clipGeometry);

                //test clipping
                if (interGeometry != null) {
                    resultFeature.setPropertyValue(name, interGeometry);
                } else {
                    return null;
                }
            } else if(property instanceof AttributeType && !(AttributeConvention.contains(property.getName()))){
                resultFeature.setPropertyValue(name, value);
            }
        }
        return resultFeature;
    }
}
