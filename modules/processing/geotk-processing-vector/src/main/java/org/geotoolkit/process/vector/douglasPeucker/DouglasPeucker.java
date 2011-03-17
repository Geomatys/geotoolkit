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
package org.geotoolkit.process.vector.douglasPeucker;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Process to simplify geometry contained into FeatureCollection's Features.
 * If the simplification accuracy is more than geometry envelope width or height
 * and the simplification behavior boolean is true, the returned geometry will be null.
 * @author Quentin Boileau
 * @module pending
 */
public class DouglasPeucker extends AbstractProcess {

    ParameterValueGroup result;
    
    /**
     * Default constructor
     */
    public DouglasPeucker() {
        super(DouglasPeuckerDescriptor.INSTANCE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterValueGroup getOutput() {
        return result;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(DouglasPeuckerDescriptor.FEATURE_IN, inputParameters);
        double inputAccuracy = Parameters.value(DouglasPeuckerDescriptor.ACCURACY_IN, inputParameters).doubleValue();
        Unit<Length> inputUnit = Parameters.value(DouglasPeuckerDescriptor.UNIT_IN, inputParameters);
        boolean inputBehavior = Parameters.value(DouglasPeuckerDescriptor.DEL_SMALL_GEO_IN, inputParameters);
        boolean inputLenient = Parameters.value(DouglasPeuckerDescriptor.LENIENT_TRANSFORM_IN, inputParameters);

        final DouglasPeuckerFeatureCollection resultFeatureList =
                new DouglasPeuckerFeatureCollection(inputFeatureList,inputAccuracy,inputUnit,inputBehavior,inputLenient);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * Simplify feature geometry using the DouglasPeucker Algorithm.
     * Geometries are transform into better projection and simplified
     * If the simplified accuracy is bigger than geometry envelope and the inputBehavior
     * is set to true, the result geometry will be <code>null</code>.
     * @param oldFeature
     * @param accuracy
     * @param unit
     * @param behavior
     * @param lenient
     * @return
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    static Feature simplifyFeature(final Feature oldFeature, double accuracy, Unit<Length> unit,boolean behavior, boolean lenient)
            throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {

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
                final MathTransform projection = DouglasPeucker.changeProjection(convertEnvelope, longLatCRS, unit);
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
                        resultFeature.getProperty(property.getName()).setValue(null);
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

    /**
     * Create a custom projection (Conic or Mercator) for the geometry using the
     * geometry envelope.
     * @param geomEnvelope Geometry bounding envelope
     * @param longLatCRS WGS84 projection
     * @param unit unit wanted for the geometry
     * @return MathTransform
     * @throws NoSuchIdentifierException
     * @throws FactoryException
     */
    private static MathTransform changeProjection(final Envelope geomEnvelope, final GeographicCRS longLatCRS,Unit<Length> unit) throws NoSuchIdentifierException, FactoryException {
        //collect data to create the projection
        final double centerMeridian = geomEnvelope.getWidth() / 2 + geomEnvelope.getMinX();
        final double centerParallal = geomEnvelope.getHeight() / 2 + geomEnvelope.getMinY();
        final double northParallal = geomEnvelope.getMaxY() - geomEnvelope.getHeight() / 3;
        final double southParallal = geomEnvelope.getMinY() + geomEnvelope.getHeight() / 3;

        boolean conicProjection = true;
        //if the geomery is near the equator we use the mercator projection
        if (geomEnvelope.getMaxY() > 0 && geomEnvelope.getMinY() < 0) {
            conicProjection = false;
        }
        //conicProjection = true;
        
        //create geometry lambert projection or mercator projection
        final Ellipsoid ellipsoid = longLatCRS.getDatum().getEllipsoid();
        double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        double semiMinorAxis = ellipsoid.getSemiMinorAxis();

        Unit<Length> projectionUnit = ellipsoid.getAxisUnit();
        //check for unit conversion
        if (unit != projectionUnit) {
            UnitConverter converter = projectionUnit.getConverterTo(unit);
            semiMajorAxis = converter.convert(semiMajorAxis);
            semiMinorAxis = converter.convert(semiMinorAxis);
        }

        final MathTransformFactory f = AuthorityFactoryFinder.getMathTransformFactory(null);
        ParameterValueGroup p;
        if (conicProjection) {
            
            p = f.getDefaultParameters("Albers_Conic_Equal_Area");
            p.parameter("semi_major").setValue(semiMajorAxis);
            p.parameter("semi_minor").setValue(semiMinorAxis);
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(northParallal);
            p.parameter("standard_parallel_2").setValue(southParallal);
        } else {

            p = f.getDefaultParameters("Mercator_2SP");
            p.parameter("semi_major").setValue(semiMajorAxis);
            p.parameter("semi_minor").setValue(semiMinorAxis);
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(centerParallal);
        }
        
        /*final MathTransformFactory f = AuthorityFactoryFinder.getMathTransformFactory(null);
        ParameterValueGroup p = f.getDefaultParameters("Stereographic");
        p.parameter("semi_major").setValue(semiMajorAxis);
        p.parameter("semi_minor").setValue(semiMajorAxis);//TODO use semiMinorAxis
        p.parameter("central_meridian").setValue(centerMeridian);
        p.parameter("latitude_of_origin").setValue(centerParallal);*/

        final MathTransform pTransform = f.createParameterizedTransform(p);
        return pTransform;
    }
}
