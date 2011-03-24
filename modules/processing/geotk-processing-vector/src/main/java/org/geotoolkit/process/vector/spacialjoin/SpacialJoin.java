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
package org.geotoolkit.process.vector.spacialjoin;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Iterator;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.process.vector.VectorProcessFactory;
import org.geotoolkit.process.vector.intersect.IntersectDescriptor;
import org.geotoolkit.process.vector.nearest.NearestDescriptor;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process return the target FeatureCollection with source FeatureCollection attributes.
 * The link between target and source depend of method used (Intersect or Nearest).
 * @author Quentin Boileau
 * @module pending
 */
public class SpacialJoin extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public SpacialJoin() {
        super(SpacialJoinDescriptor.INSTANCE);
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
        final FeatureCollection<Feature> sourceFeatureList = Parameters.value(SpacialJoinDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection<Feature> targetFeatureList = Parameters.value(SpacialJoinDescriptor.FEATURE_TARGET, inputParameters);
        final boolean method = Parameters.value(SpacialJoinDescriptor.INTERSECT, inputParameters);

        final SpacialJoinFeatureCollection resultFeatureList =
                new SpacialJoinFeatureCollection(sourceFeatureList, targetFeatureList, method);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * THis function join target Feature with another Feature form source FeatureCollection.
     * If method is true, the method used is Intersect, else it's Nearest.
     * If there is no Feature which Intersect the target Geometry, the return Feature
     * will have "joined attributes" set to null.
     * If there is more than one result for Nearest method
     * (many Feature at the same distance), we use the first returned.
     * If there is more than one result for Intersect method , we use the Feature
     * with the biggest intersection area with target Geometry.
     * @param target the target Feature
     * @param newType the concatenated FeatureType
     * @param sourceFC the source FeatureCollection
     * @param method the used method
     * @return the joined feature
     */
    static Feature join(final Feature target, final FeatureType newType,
            final FeatureCollection<Feature> sourceFC, final boolean method) {

        Feature resultFeature = FeatureUtilities.defaultFeature(newType, target.getIdentifier().getID());

        //copy target Feature
        for (Property targetProperty : target.getProperties()) {
            resultFeature.getProperty(targetProperty.getName()).setValue(targetProperty.getValue());
        }

        ProcessDescriptor desc;
        org.geotoolkit.process.Process proc;
        ParameterValueGroup in;
        ArrayList<Feature> featureOutArray;

        //for each feature geometry
        for (Property property : target.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final Geometry targetGeometry = (Geometry) property.getValue();

                //use intersect method
                if (method) {
                    desc = ProcessFinder.getProcessDescriptor(VectorProcessFactory.NAME, IntersectDescriptor.NAME);
                    proc = desc.createProcess();
                    in = desc.getInputDescriptor().createValue();
                    in.parameter(IntersectDescriptor.FEATURE_IN.getName().getCode()).setValue(sourceFC);
                    in.parameter(IntersectDescriptor.GEOMETRY_IN.getName().getCode()).setValue(targetGeometry);

                } else {//use nearest method
                    desc = ProcessFinder.getProcessDescriptor(VectorProcessFactory.NAME, NearestDescriptor.NAME);
                    proc = desc.createProcess();
                    in = desc.getInputDescriptor().createValue();
                    in.parameter(NearestDescriptor.FEATURE_IN.getName().getCode()).setValue(sourceFC);
                    in.parameter(NearestDescriptor.GEOMETRY_IN.getName().getCode()).setValue(targetGeometry);
                }

                //init process and run it
                proc.setInput(in);
                proc.run();

                final FeatureCollection<Feature> featureOut =
                        (FeatureCollection<Feature>) proc.getOutput().parameter("feature_out").getValue();

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

        final Feature resultFeature = FeatureUtilities.defaultFeature(concatType, target.getIdentifier().getID());

        //copy target Feature
        for (Property targetProperty : target.getProperties()) {
            resultFeature.getProperty(targetProperty.getName()).setValue(targetProperty.getValue());
        }

        //copy source Feature except geometry descriptor
        for (Property sourceProperty : source.getProperties()) {
            if (!(sourceProperty.getDescriptor() instanceof GeometryDescriptor)) {

                resultFeature.getProperty(sourceProperty.getName().getLocalPart() + "_" + source.getType().getName().getLocalPart()).setValue(sourceProperty.getValue());
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
    static Feature biggestIntersection(final FeatureCollection<Feature> outFC, final Geometry intersectGeometry) {
        double area = 0.0;

        final ArrayList<Feature> listID = new ArrayList<Feature>();

        final FeatureIterator<Feature> iter = outFC.iterator(null);
        while (iter.hasNext()) {
            final Feature feature = iter.next();
            for (Property property : feature.getProperties()) {
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


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.copy(targetType);
        ftb.setName(targetType.getName().getLocalPart() + "+" + sourceType.getName().getLocalPart());
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
                    final String newName = sourceDesc.getName().getLocalPart() + "_" + sourceType.getName().getLocalPart();
                    typeBuilder.setName(newName);
                    descBuilder.setName(newName);
                }
                descBuilder.setNillable(true);
                descBuilder.setType(typeBuilder.buildGeometryType());
                ftb.add(descBuilder.buildDescriptor());
            }
        }
        return ftb.buildFeatureType();
    }
}
