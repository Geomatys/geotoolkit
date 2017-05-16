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
package org.geotoolkit.processing.vector.differencegeometry;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;


/**
 * Process to clip the difference with a FeatureCollection using a geometry
 *
 * @author Quentin Boileau
 */
public class DifferenceGeometryProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public DifferenceGeometryProcess(final ParameterValueGroup input) {
        super(DifferenceGeometryDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList  = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final Geometry inputDifferenceGeometry    = value(DifferenceGeometryDescriptor.DIFF_GEOMETRY_IN, inputParameters);
        final FeatureCollection resultFeatureList =
                new DifferenceGeometryFeatureCollection(inputFeatureList,inputDifferenceGeometry);
        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip difference the feature with the Geometry
     *
     * @param newType the new FeatureType for the Feature
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final Geometry geometry) {
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (final PropertyType property : oldFeature.getType().getProperties(true)) {
            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Geometry diffGeometry = VectorProcessUtils.geometryDifference((Geometry) value, geometry);
                if (diffGeometry != null) {
                    resultFeature.setPropertyValue(name, diffGeometry);
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
