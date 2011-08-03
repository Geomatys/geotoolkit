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

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.process.vector.VectorProcessUtils;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Process buffer, create a buffer around Feature's geometry and
 * store it into a result Feature. Inputs process parameters :
 * <ul>
 *      <li>A FeatureCollection </li>
 *      <li>Buffer distance</li>
 *      <li>Unit for the distance</li>
 *      <li>Boolean for the transformation from Geometry CRS to WGS84</li>
 * </ul>
 *
 * @author Quentin Boileau
 * @module pending
 */
public class Buffer extends AbstractProcess {

    /**
     * Default constructor
     */
    public Buffer() {
        super(BufferDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        fireStartEvent(new ProcessEvent(this,0,null,null));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(BufferDescriptor.FEATURE_IN, inputParameters);
        final double inputDistance = Parameters.value(BufferDescriptor.DISTANCE_IN, inputParameters).doubleValue();
        final Unit<Length> inputUnit = Parameters.value(BufferDescriptor.UNIT_IN, inputParameters);
        final boolean inputLenient = Parameters.value(BufferDescriptor.LENIENT_TRANSFORM_IN, inputParameters);

        final FeatureCollection resultFeatureList =
                new BufferFeatureCollection(inputFeatureList, inputDistance, inputUnit, inputLenient);

        final ParameterValueGroup result = getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        fireEndEvent(new ProcessEvent(this,100,null,null));
    }

    /**
     * Apply buffer algorithm to the Feature geometry and store the resulting geometry
     * into a new Feature
     * @param oldFeature
     * @param newType
     * @param distance
     * @param unit
     * @param lenient
     * @return a Feature
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    static Feature makeBuffer(final Feature oldFeature, final FeatureType newType, final double distance,
            final Unit<Length> unit, final Boolean lenient) throws FactoryException, MismatchedDimensionException, TransformException {

        final CoordinateReferenceSystem originalCRS;
        
        if (oldFeature.getType().getCoordinateReferenceSystem() != null) {
            originalCRS = oldFeature.getType().getCoordinateReferenceSystem();
        } else {
            final Geometry geom = (Geometry) oldFeature.getDefaultGeometryProperty().getValue();
            originalCRS = JTS.findCoordinateReferenceSystem(geom);
        }
        
        final GeographicCRS longLatCRS = DefaultGeographicCRS.WGS84;
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
                final MathTransform projection = VectorProcessUtils.changeProjection(convertEnvelope, longLatCRS, unit);

                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);

                //create buffer around the geometry
                Geometry bufferedGeometry = calculatedGeom.buffer(distance);

                //restor to original CRS
                bufferedGeometry = JTS.transform(bufferedGeometry, projection.inverse());
                bufferedGeometry = JTS.transform(bufferedGeometry, mtToLongLatCRS.inverse());
                resultFeature.getProperty(property.getName()).setValue(bufferedGeometry);

            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }
        return resultFeature;
    }
}
