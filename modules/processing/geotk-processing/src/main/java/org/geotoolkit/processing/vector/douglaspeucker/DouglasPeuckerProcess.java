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
package org.geotoolkit.processing.vector.douglaspeucker;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.util.Collections;
import javax.measure.unit.SI;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.processing.vector.douglaspeucker.DouglasPeuckerDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process to simplify geometry contained into a Features.
 * If the simplification accuracy is more than geometry envelope width or height
 * and the simplification behavior boolean is true, the returned geometry will be null.
 * The used unit for the accuracy is meters.
 *
 * @author Quentin Boileau
 * @module pending
 */
public class DouglasPeuckerProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public DouglasPeuckerProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList   = value(FEATURE_IN, inputParameters);
        final Double inputAccuracy                          = value(ACCURACY_IN, inputParameters);
        final Boolean inputBehavior                               = value(DEL_SMALL_GEO_IN, inputParameters) != null ?
                                                                    value(DEL_SMALL_GEO_IN, inputParameters) :
                                                                    DEL_SMALL_GEO_IN.getDefaultValue();

        final Boolean inputLenient                                = value(LENIENT_TRANSFORM_IN, inputParameters) != null ?
                                                                    value(LENIENT_TRANSFORM_IN, inputParameters) :
                                                                    LENIENT_TRANSFORM_IN.getDefaultValue();

        final FeatureCollection resultFeatureList =
                new DouglasPeuckerFeatureCollection(inputFeatureList, inputAccuracy, inputBehavior, inputLenient);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Simplify feature geometry using the DouglasPeucker Algorithm.
     * Geometries are transform into better projection and simplified
     * If the simplified accuracy is bigger than geometry envelope and the inputBehavior
     * is set to true, the result geometry will be <code>null</code>.
     * @param oldFeature
     * @param accuracy
     * @param behavior - boolean to set the process behavior with small geometries
     * @param lenient - boolean used to set the lenient parameter during CRS change
     * @return the simplified Feature
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    static Feature simplifyFeature(final Feature oldFeature, final Double accuracy, final boolean behavior, final boolean lenient)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final CoordinateReferenceSystem originalCRS = oldFeature.getType().getCoordinateReferenceSystem();
        final GeographicCRS longLatCRS = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform mtToLongLatCRS = CRS.findOperation(originalCRS, longLatCRS, null).getMathTransform();

        final Feature resultFeature = FeatureUtilities.defaultFeature(oldFeature.getType(), oldFeature.getIdentifier().getID());
        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //Geometry IN
                final Geometry originalGeometry = (Geometry) property.getValue();

                //convert geometry into WGS84
                final Geometry convertedGeometry = JTS.transform(originalGeometry, mtToLongLatCRS);
                Envelope convertEnvelope = convertedGeometry.getEnvelopeInternal();

                //create custom projection for the geometry
                final MathTransform projection = VectorProcessUtils.changeProjection(convertEnvelope, longLatCRS, SI.METRE);
                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);
                convertEnvelope = calculatedGeom.getEnvelopeInternal();

                 //We compare if the simplification accuracy is more than geometry envelope width or height
                if (convertEnvelope.getWidth() < accuracy && convertEnvelope.getHeight() < accuracy) {
                    //In this case, if behavior boolean is true, we return null for the feature
                    //else we set the geometry feature to null
                    if(behavior) {
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
