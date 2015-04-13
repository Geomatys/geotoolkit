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
package org.geotoolkit.process.vector.buffer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import javax.measure.unit.SI;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorProcessUtils;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.process.vector.buffer.BufferDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

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
 * @module pending
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
        final FeatureCollection inputFeatureList   = value(FEATURE_IN, inputParameters);
        final double inputDistance                          = value(DISTANCE_IN, inputParameters).doubleValue();
        Boolean inputLenient                                = value(LENIENT_TRANSFORM_IN, inputParameters);

        if (inputLenient == null) inputLenient = Boolean.TRUE;

        final FeatureCollection resultFeatureList =
                new BufferFeatureCollection(inputFeatureList, inputDistance, inputLenient);
        
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Apply buffer algorithm to the Feature geometry and store the resulting geometry
     * into a new Feature.
     * @param oldFeature
     * @param newType
     * @param distance
     * @param lenient
     * @return a Feature
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    static Feature makeBuffer(final Feature oldFeature, final FeatureType newType, final double distance, final Boolean lenient)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final CoordinateReferenceSystem originalCRS;

        if (oldFeature.getType().getCoordinateReferenceSystem() != null) {
            originalCRS = oldFeature.getType().getCoordinateReferenceSystem();
        } else {
            final Geometry geom = (Geometry) oldFeature.getDefaultGeometryProperty().getValue();
            originalCRS = JTS.findCoordinateReferenceSystem(geom);
        }

        final GeographicCRS longLatCRS = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform mtToLongLatCRS = CRS.findMathTransform(originalCRS, longLatCRS, lenient);

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());
        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //Geometry IN
                final Geometry originalGeometry = (Geometry) property.getValue();

                //convert geometry into WGS84
                final Geometry convertedGeometry = JTS.transform(originalGeometry, mtToLongLatCRS);
                final Envelope convertEnvelope = convertedGeometry.getEnvelopeInternal();

                //create custom projection for the geometry
                final MathTransform projection = VectorProcessUtils.changeProjection(convertEnvelope, longLatCRS, SI.METRE);

                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);

                //create buffer around the geometry
                Geometry bufferedGeometry = calculatedGeom.buffer(distance);

                //restor to original CRS
                bufferedGeometry = JTS.transform(bufferedGeometry, projection.inverse());
                bufferedGeometry = JTS.transform(bufferedGeometry, mtToLongLatCRS.inverse());
                JTS.setCRS(bufferedGeometry, originalCRS);
                resultFeature.getProperty(property.getName()).setValue(bufferedGeometry);

            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }
        return resultFeature;
    }
}
