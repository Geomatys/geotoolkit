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
package org.geotoolkit.processing.vector.spatialjoin;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.logging.Level;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.intersect.IntersectDescriptor;
import org.geotoolkit.processing.vector.nearest.NearestDescriptor;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.util.logging.Logging;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process return the target FeatureCollection with source FeatureCollection attributes.
 * The link between target and source depend of method used (Intersect or Nearest).
 * @author Quentin Boileau
 * @module pending
 */
public class SpatialJoinProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public SpatialJoinProcess(final ParameterValueGroup input) {
        super(SpatialJoinDescriptor.INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection sourceFeatureList = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection targetFeatureList = value(SpatialJoinDescriptor.FEATURE_TARGET, inputParameters);
        final boolean method = value(SpatialJoinDescriptor.INTERSECT, inputParameters);

        final FeatureCollection resultFeatureList =
                new SpatialJoinFeatureCollection(sourceFeatureList, targetFeatureList, method);

        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * This function join target Feature with another Feature form a source FeatureCollection.
     *
     * If boolean <code>method</code> is true, the method used is Intersect, else it's Nearest.
     *
     * If there is no Feature which Intersect the target Geometry, the return Feature
     * will have "joined attributes" set to null.
     *
     * If there is more than one result for Nearest method
     * (many Feature at the same distance), we use the first returned.
     *
     * If there is more than one result for Intersect method , we use the Feature
     * with the biggest intersection area with target Geometry.
     *
     * @param target the target Feature
     * @param newType the concatenated FeatureType
     * @param sourceFC the source FeatureCollection
     * @param method the used method. True -> Intersect, False -> Nearest
     * @return the joined feature
     */
    static Feature join(final Feature target, final FeatureType newType,
            final FeatureCollection sourceFC, final boolean method) {

        Feature resultFeature = FeatureUtilities.defaultFeature(newType, target.getIdentifier().getID());

        //copy target Feature
        for (final Property targetProperty : target.getProperties()) {
            resultFeature.getProperty(targetProperty.getName()).setValue(targetProperty.getValue());
        }

        ProcessDescriptor desc;
        org.geotoolkit.process.Process proc;
        ParameterValueGroup in;
        ArrayList<Feature> featureOutArray;

        //for each target feature geometry
        for (final Property property : target.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final Geometry targetGeometry = (Geometry) property.getValue();
                final GeometryDescriptor geomDesc = (GeometryDescriptor) property.getDescriptor();
                final CoordinateReferenceSystem geomCRS = geomDesc.getCoordinateReferenceSystem();

                JTS.setCRS(targetGeometry, geomCRS);//add CRS to the used data geometry
                //use intersect method
                if (method) {
                    desc = IntersectDescriptor.INSTANCE;
                    in = desc.getInputDescriptor().createValue();
                    in.parameter(IntersectDescriptor.FEATURE_IN.getName().getCode()).setValue(sourceFC);
                    in.parameter(IntersectDescriptor.GEOMETRY_IN.getName().getCode()).setValue(targetGeometry);
                    proc = desc.createProcess(in);

                } else {//use nearest method
                    desc = NearestDescriptor.INSTANCE;
                    in = desc.getInputDescriptor().createValue();
                    in.parameter(NearestDescriptor.FEATURE_IN.getName().getCode()).setValue(sourceFC);
                    in.parameter(NearestDescriptor.GEOMETRY_IN.getName().getCode()).setValue(targetGeometry);
                    proc = desc.createProcess(in);
                }

                //run it
                final FeatureCollection featureOut;
                try {
                    featureOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();
                } catch (ProcessException ex) {
                    Logging.getLogger("org.geotoolkit.processing.vector.spatialjoin").log(Level.WARNING, null, ex);
                    return null;
                }

                featureOutArray = new ArrayList<Feature>(featureOut);

                if (method) {//intersect method

                    if (featureOutArray.isEmpty()) { //no intersection
                        return resultFeature;
                    } else {

                        if (featureOutArray.size() > 1) { //more than one intersection
                            final Feature biggestFeature = biggestIntersection(featureOut, targetGeometry);
                            resultFeature = copyAttributes(target, biggestFeature, newType);
                        } else {// only one intersection
                            resultFeature = copyAttributes(target, featureOutArray.get(0), newType);
                        }
                    }

                } else {//nearest method
                    if (featureOutArray.isEmpty()) {
                        return resultFeature;
                    } else {
                        resultFeature = copyAttributes(target, featureOutArray.get(0), newType);
                    }
                }
            }
        }

        return resultFeature;
    }

    /**
     * This function copy attributes from source to target Feature except geometry descriptor.
     * The copied attributes name will be "attributeName_sourceFeatureTypeName".
     * @param target targetFeature
     * @param source source Feature
     * @param concatType concatenated FeatureType
     * @return the resulting Feature
     */
    static Feature copyAttributes(final Feature target, final Feature source, final FeatureType concatType) {

        final Feature resultFeature = FeatureUtilities.defaultFeature(concatType, target.getIdentifier().getID()+"_"+
                source.getIdentifier().getID());

        //copy target Feature
        for (final Property targetProperty : target.getProperties()) {
            resultFeature.getProperty(targetProperty.getName()).setValue(targetProperty.getValue());
        }

        //copy source Feature except geometry descriptor
        for (final Property sourceProperty : source.getProperties()) {
            if (!(sourceProperty.getDescriptor() instanceof GeometryDescriptor)) {
                resultFeature.getProperty(sourceProperty.getName().tip().toString() + "_"
                        + source.getType().getName().tip().toString()).setValue(sourceProperty.getValue());
            }
        }

        return resultFeature;
    }

    /**
     * Return the Feature with the biggest intersection area with the geometry.
     * If there is many Feature with the same area, the function return th first founded.
     * @param outFC
     * @param intersectGeometry
     * @return the Feature
     */
    static Feature biggestIntersection(final FeatureCollection outFC, final Geometry intersectGeometry) {
        double area = 0.0;

        final ArrayList<Feature> listID = new ArrayList<Feature>();

        final FeatureIterator iter = outFC.iterator(null);
        try{
            while (iter.hasNext()) {
                final Feature feature = iter.next();
                for (final Property property : feature.getProperties()) {
                    if (property.getDescriptor() instanceof GeometryDescriptor) {

                        final Geometry geom = (Geometry) property.getValue();

                        final double computeArea = intersectGeometry.intersection(geom).getArea();
                        if (computeArea > area) {
                            listID.clear();
                            area = computeArea;
                            listID.add(feature);

                        } else {
                            if (computeArea == area) {
                                listID.add(feature);
                            }
                        }
                    }
                }
            }
        }finally{
            iter.close();
        }
        return listID.get(0);
    }

    /**
     * Create a new FeatureType with the target FeatureType and adding
     * source attributes except the GeometryDescriptor
     * @param targetType target FeatureType
     * @param sourceType source FeatureType
     * @return the new FeatureType
     */
    static FeatureType concatType(final FeatureType targetType, final FeatureType sourceType) {

        boolean isSameName = false;
        AttributeType property;
        AttributeDescriptorBuilder descBuilder;
        AttributeTypeBuilder typeBuilder;


        //copy targetType into a FeatureTypeBuilder and change Name to targetName+sourceName
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.copy(targetType);
        ftb.setName(targetType.getName().tip().toString() + "_" + sourceType.getName().tip().toString());

        //each source descriptor
        final Iterator<PropertyDescriptor> iteSource = sourceType.getDescriptors().iterator();
        while (iteSource.hasNext()) {

            final PropertyDescriptor sourceDesc = iteSource.next();

            //add all descriptors but geometry
            if (!(sourceDesc instanceof GeometryDescriptor)) {

                property = (AttributeType) sourceDesc.getType();

                descBuilder = new AttributeDescriptorBuilder();
                typeBuilder = new AttributeTypeBuilder();
                descBuilder.copy((AttributeDescriptor) sourceDesc);
                typeBuilder.copy(property);

                //test if exist in targetType
                final Iterator<PropertyDescriptor> iteTarget = sourceType.getDescriptors().iterator();
                while (iteTarget.hasNext()) {
                    final PropertyDescriptor targetDesc = iteTarget.next();
                    if (targetDesc.getName() == sourceDesc.getName()) {
                        isSameName = true;
                    }
                }
                if (isSameName) {
                    final String newName = sourceDesc.getName().tip().toString() + "_" + sourceType.getName().tip().toString();
                    typeBuilder.setName(newName);
                    descBuilder.setName(newName);
                }
                descBuilder.setNillable(true);
                descBuilder.setType(typeBuilder.buildType());
                ftb.add(descBuilder.buildDescriptor());
            }
        }
        return ftb.buildFeatureType();
    }
}
