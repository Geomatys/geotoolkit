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
package org.geotoolkit.processing.vector.buffer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import org.apache.sis.measure.Units;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.processing.vector.buffer.BufferDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;


/**
 * Process buffer, create a buffer around Feature's geometry and
 * store it into a result Feature. Inputs process parameters :
 * <ul>
 *      <li>A FeatureCollection </li>
 *      <li>Buffer distance in meters</li>
 *      <li>Boolean for the transformation from Geometry CRS to WGS84</li>
 * </ul>
 *
 * @author Quentin Boileau
 */
public class BufferProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public BufferProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList = value(FEATURE_IN, inputParameters);
        final double inputDistance               = value(DISTANCE_IN, inputParameters).doubleValue();
        Boolean inputLenient                     = value(LENIENT_TRANSFORM_IN, inputParameters);
        if (inputLenient == null) {
            inputLenient = Boolean.TRUE;
        }
        final FeatureCollection resultFeatureList =
                new BufferFeatureCollection(inputFeatureList, inputDistance, inputLenient);
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Apply buffer algorithm to the Feature geometry and store the resulting geometry
     * into a new Feature.
     */
    static Feature makeBuffer(final Feature oldFeature, final FeatureType newType, final double distance, final Boolean lenient)
            throws FactoryException, MismatchedDimensionException, TransformException
    {
        CoordinateReferenceSystem originalCRS = FeatureExt.getCRS(oldFeature.getType());
        if (originalCRS == null) {
            final Geometry geom = (Geometry) oldFeature.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
            originalCRS = JTS.findCoordinateReferenceSystem(geom);
        }
        final GeographicCRS longLatCRS = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform mtToLongLatCRS = CRS.findOperation(originalCRS, longLatCRS, null).getMathTransform();
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (final PropertyType property : oldFeature.getType().getProperties(true)) {
            if(!(property instanceof AttributeType)) continue;

            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);

            if (AttributeConvention.isGeometryAttribute(property)) {

                //convert geometry into WGS84
                final Geometry convertedGeometry = JTS.transform((Geometry) value, mtToLongLatCRS);
                final Envelope convertEnvelope = convertedGeometry.getEnvelopeInternal();

                //create custom projection for the geometry
                final MathTransform projection = VectorProcessUtils.changeProjection(convertEnvelope, longLatCRS, Units.METRE);

                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);

                //create buffer around the geometry
                Geometry bufferedGeometry = calculatedGeom.buffer(distance);

                //restor to original CRS
                bufferedGeometry = JTS.transform(bufferedGeometry, projection.inverse());
                bufferedGeometry = JTS.transform(bufferedGeometry, mtToLongLatCRS.inverse());
                JTS.setCRS(bufferedGeometry, originalCRS);
                resultFeature.setPropertyValue(name, bufferedGeometry);
            } else {
                resultFeature.setPropertyValue(name, value);
            }
        }
        return resultFeature;
    }
}
