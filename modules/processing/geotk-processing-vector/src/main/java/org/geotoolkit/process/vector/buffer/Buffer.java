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

import java.util.ListIterator;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

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

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public Buffer() {
        super(BufferDescriptor.INSTANCE);
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
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(BufferDescriptor.FEATURE_IN, inputParameters);
        final double inputDistance = Parameters.value(BufferDescriptor.DISTANCE_IN, inputParameters).doubleValue();
        final Unit<Length> inputUnit = Parameters.value(BufferDescriptor.UNIT_IN, inputParameters);
        final boolean inputLenient = Parameters.value(BufferDescriptor.LENIENT_TRANSFORM_IN, inputParameters);

        final BufferFeatureCollection resultFeatureList =
                new BufferFeatureCollection(inputFeatureList, inputDistance, inputUnit, inputLenient);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
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

        final CoordinateReferenceSystem originalCRS = oldFeature.getType().getCoordinateReferenceSystem();
        final GeographicCRS longLatCRS = DefaultGeographicCRS.WGS84;
        final MathTransform mtToLongLatCRS = CRS.findMathTransform(originalCRS, longLatCRS, lenient);

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());
        for (Property property : oldFeature.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //Geometry IN
                final Geometry originalGeometry = (Geometry) property.getValue();

                //convert geometry into WGS84
                final Geometry convertedGeometry = JTS.transform(originalGeometry, mtToLongLatCRS);
                Envelope convertEnvelope = convertedGeometry.getEnvelopeInternal();

                //create custom projection for the geometry
                final MathTransform projection = Buffer.changeProjection(convertEnvelope, longLatCRS, unit);

                //Apply the custom projection to geometry
                final Geometry calculatedGeom = JTS.transform(convertedGeometry, projection);
                convertEnvelope = calculatedGeom.getEnvelopeInternal();

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

     /**
     * Change the geometry descriptor to Geometry.
     * @param oldFeatureType FeatureType
     * @return newFeatureType FeatureType
     */
    public static FeatureType changeFeatureType(final FeatureType oldFeatureType) {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.copy(oldFeatureType);

        final ListIterator<PropertyDescriptor> ite = ftb.getProperties().listIterator();

        while (ite.hasNext()) {

            final PropertyDescriptor desc = ite.next();
            if (desc instanceof GeometryDescriptor) {

                GeometryType type = (GeometryType) desc.getType();

                final AttributeDescriptorBuilder descBuilder = new AttributeDescriptorBuilder();
                final AttributeTypeBuilder typeBuilder = new AttributeTypeBuilder();
                descBuilder.copy((AttributeDescriptor) desc);
                typeBuilder.copy(type);
                typeBuilder.setBinding(Geometry.class);
                descBuilder.setType(typeBuilder.buildGeometryType());
                final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                ite.set(newDesc);
            }
        }

        return ftb.buildFeatureType();
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
    private static MathTransform changeProjection(final Envelope geomEnvelope, final GeographicCRS longLatCRS,
            final Unit<Length> unit) throws NoSuchIdentifierException, FactoryException {

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

        return f.createParameterizedTransform(p);
    }
}
