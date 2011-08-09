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
package org.geotoolkit.process.vector.douglaspeucker;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.util.Collections;
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
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Process to simplify geometry contained into a Features.
 * If the simplification accuracy is more than geometry envelope width or height
 * and the simplification behavior boolean is true, the returned geometry will be null.
 * @author Quentin Boileau
 * @module pending
 */
public class DouglasPeucker extends AbstractProcess {
    
    /**
     * Default constructor
     */
    public DouglasPeucker(final ParameterValueGroup input) {
        super(DouglasPeuckerDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public ParameterValueGroup call() {
        fireStartEvent(new ProcessEvent(this));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(DouglasPeuckerDescriptor.FEATURE_IN, inputParameters);
        final double inputAccuracy = Parameters.value(DouglasPeuckerDescriptor.ACCURACY_IN, inputParameters).doubleValue();
        final Unit<Length> inputUnit = Parameters.value(DouglasPeuckerDescriptor.UNIT_IN, inputParameters);
        final boolean inputBehavior = Parameters.value(DouglasPeuckerDescriptor.DEL_SMALL_GEO_IN, inputParameters);
        final boolean inputLenient = Parameters.value(DouglasPeuckerDescriptor.LENIENT_TRANSFORM_IN, inputParameters);

        final FeatureCollection resultFeatureList =
                new DouglasPeuckerFeatureCollection(inputFeatureList,inputAccuracy,inputUnit,inputBehavior,inputLenient);

        outputParameters.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        fireEndEvent(new ProcessEvent(this,null,100));
        return outputParameters;
    }

    /**
     * Simplify feature geometry using the DouglasPeucker Algorithm.
     * Geometries are transform into better projection and simplified
     * If the simplified accuracy is bigger than geometry envelope and the inputBehavior
     * is set to true, the result geometry will be <code>null</code>.
     * @param oldFeature
     * @param accuracy
     * @param unit
     * @param behavior - boolean to set the process behavior with small geometries
     * @param lenient - boolean used to set the lenient parameter during CRS change
     * @return the simplified Feature
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    static Feature simplifyFeature(final Feature oldFeature, final double accuracy, final Unit<Length> unit,
            final boolean behavior, final boolean lenient)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final CoordinateReferenceSystem originalCRS = oldFeature.getType().getCoordinateReferenceSystem();
        final GeographicCRS longLatCRS = DefaultGeographicCRS.WGS84;
        final MathTransform mtToLongLatCRS = CRS.findMathTransform(originalCRS, longLatCRS, lenient);

        final Feature resultFeature = FeatureUtilities.defaultFeature(oldFeature.getType(), oldFeature.getIdentifier().getID());
        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //Geometry IN
                final Geometry originalGeometry = (Geometry) property.getValue();

                //convert geometry into WGS84
                final Geometry convertedGeometry = JTS.transform(originalGeometry, mtToLongLatCRS);
                Envelope convertEnvelope = convertedGeometry.getEnvelopeInternal();

                //create custom projection for the geometry
                final MathTransform projection = VectorProcessUtils.changeProjection(convertEnvelope, longLatCRS, unit);
                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);
                convertEnvelope = calculatedGeom.getEnvelopeInternal();

                 //We compare if the simplification accuracy is more than geometry envelope width or height
                if (convertEnvelope.getWidth() < accuracy && convertEnvelope.getHeight() < accuracy) {
                    //In this case, if behavior boolean is true, we return null for the feature
                    //else we set the geometry feature to null
                    if(behavior){
                        return null;
                    }else{
                        resultFeature.getProperty(property.getName()).setValue(new GeometryFactory().buildGeometry(Collections.EMPTY_LIST));
                    }
                } else {
                     //simplify geometry
                    Geometry simplifiedGeometry = DouglasPeuckerSimplifier.simplify(calculatedGeom, accuracy);

                    //restor to original CRS
                    simplifiedGeometry = JTS.transform(simplifiedGeometry, projection.inverse());
                    simplifiedGeometry = JTS.transform(simplifiedGeometry, mtToLongLatCRS.inverse());
                    resultFeature.getProperty(property.getName()).setValue(simplifiedGeometry);
                }
            } else {
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }
        return resultFeature;
    }

}
