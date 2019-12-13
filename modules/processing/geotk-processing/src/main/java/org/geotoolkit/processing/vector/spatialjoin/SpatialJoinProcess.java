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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.geotoolkit.processing.vector.intersect.IntersectDescriptor;
import org.geotoolkit.processing.vector.nearest.NearestDescriptor;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;



/**
 * Process return the target FeatureCollection with source FeatureCollection attributes.
 * The link between target and source depend of method used (Intersect or Nearest).
 *
 * @author Quentin Boileau
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
    protected void execute() throws ProcessException {
        final FeatureSet sourceFeatureList = inputParameters.getValue(VectorDescriptor.FEATURESET_IN);
        final FeatureSet targetFeatureList = inputParameters.getValue(SpatialJoinDescriptor.FEATURE_TARGET);
        final boolean method = inputParameters.getValue(SpatialJoinDescriptor.INTERSECT);

        final FeatureSet resultFeatureList;
        try {
            resultFeatureList = new SpatialJoinFeatureCollection(sourceFeatureList, targetFeatureList, method);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this);
        }

        outputParameters.getOrCreate(VectorDescriptor.FEATURESET_OUT).setValue(resultFeatureList);
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
            final FeatureSet sourceFC, final boolean method) throws DataStoreException {

        Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(target));

        //copy target Feature
        for (final PropertyType targetProperty : target.getType().getProperties(true)) {
            if(targetProperty instanceof AttributeType && !AttributeConvention.contains(targetProperty.getName())){
                final String name = targetProperty.getName().toString();
                resultFeature.setPropertyValue(name, target.getPropertyValue(name));
            }
        }

        ProcessDescriptor desc;
        org.geotoolkit.process.Process proc;
        Parameters in;
        ArrayList<Feature> featureOutArray;

        //for each target feature geometry
        for (final PropertyType property : target.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Geometry targetGeometry = (Geometry) target.getPropertyValue(property.getName().toString());
                final CoordinateReferenceSystem geomCRS = FeatureExt.getCRS(property);

                JTS.setCRS(targetGeometry, geomCRS);        //add CRS to the used data geometry
                //use intersect method
                if (method) {
                    desc = IntersectDescriptor.INSTANCE;
                    in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
                    in.getOrCreate(IntersectDescriptor.FEATURESET_IN ).setValue(sourceFC);
                    in.getOrCreate(IntersectDescriptor.GEOMETRY_IN).setValue(targetGeometry);
                    proc = desc.createProcess(in);

                } else {    //use nearest method
                    desc = NearestDescriptor.INSTANCE;
                    in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
                    in.getOrCreate(NearestDescriptor.FEATURESET_IN ).setValue(sourceFC);
                    in.getOrCreate(NearestDescriptor.GEOMETRY_IN).setValue(targetGeometry);
                    proc = desc.createProcess(in);
                }

                //run it
                final FeatureSet featureOut;
                try {
                    featureOut = (FeatureSet) proc.call().parameter("feature_out").getValue();
                } catch (ProcessException ex) {
                    Logging.getLogger("org.geotoolkit.processing.vector.spatialjoin").log(Level.WARNING, null, ex);
                    return null;
                }

                featureOutArray = new ArrayList<>(featureOut.features(false).collect(Collectors.toList()));

                if (method) {   //intersect method

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
                } else {    //nearest method
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
        final Feature resultFeature = concatType.newInstance();
        resultFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),
                FeatureExt.getId(target).getID() + "_" + FeatureExt.getId(source).getID());

        //copy target Feature
        for (final PropertyType targetProperty : target.getType().getProperties(true)) {
            if(targetProperty instanceof AttributeType && !AttributeConvention.contains(targetProperty.getName())){
                final String name = targetProperty.getName().toString();
                resultFeature.setPropertyValue(name, target.getPropertyValue(name));
            }
        }

        //copy source Feature except geometry descriptor
        for (final PropertyType sourceProperty : source.getType().getProperties(true)) {
            if(sourceProperty instanceof AttributeType && !AttributeConvention.contains(sourceProperty.getName())){
                if (!AttributeConvention.isGeometryAttribute(sourceProperty)) {
                    final String name = sourceProperty.getName().tip().toString();
                    final String rename = name + "_" + source.getType().getName().tip().toString();

                    try {
                        resultFeature.setPropertyValue(rename, source.getPropertyValue(name));
                    } catch (IllegalArgumentException ex) {
                        resultFeature.setPropertyValue(name, source.getPropertyValue(name));
                    }
                }
            }
        }
        return resultFeature;
    }

    /**
     * Return the Feature with the biggest intersection area with the geometry.
     * If there is many Feature with the same area, the function return the first founded.
     * @param outFC
     * @param intersectGeometry
     * @return the Feature
     */
    static Feature biggestIntersection(final FeatureSet outFC, final Geometry intersectGeometry) throws DataStoreException {
        double area = 0.0;
        final ArrayList<Feature> listID = new ArrayList<>();
        try (Stream<Feature> stream = outFC.features(false)) {
            final Iterator<Feature> iter = stream.iterator();
            while (iter.hasNext()) {
                final Feature feature = iter.next();
                for (final PropertyType property : feature.getType().getProperties(true)) {
                    if (AttributeConvention.isGeometryAttribute(property)) {
                        final Geometry geom = (Geometry) feature.getPropertyValue(property.getName().toString());
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
        }
        return listID.get(0);
    }

    /**
     * Create a new FeatureType with the target FeatureType and adding
     * source attributes except the geometry descriptor.
     *
     * @param targetType target FeatureType
     * @param sourceType source FeatureType
     * @return the new FeatureType
     */
    static FeatureType concatType(final FeatureType targetType, final FeatureType sourceType) {

        //copy targetType into a FeatureTypeBuilder and change Name to targetName+sourceName
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(targetType);
        ftb.setName(targetType.getName().tip().toString() + "_" + sourceType.getName().tip().toString());

        //each source descriptor
        for (final PropertyType sourceDesc : sourceType.getProperties(true)) {
            if(AttributeConvention.contains(sourceDesc.getName()) || !(sourceDesc instanceof AttributeType)) continue;

            //add all descriptors but geometry
            if (!AttributeConvention.isGeometryAttribute(sourceDesc)) {
                SingleAttributeTypeBuilder typeBuilder = new SingleAttributeTypeBuilder();
                typeBuilder.copy((AttributeType<?>) sourceDesc);

                //test if exist in targetType
                boolean isSameName = false;
                for (final PropertyType targetDesc : targetType.getProperties(true)) {
                    if (targetDesc.getName() == sourceDesc.getName()) {
                        isSameName = true;
                        break;
                    }
                }
                if (isSameName) {
                    final String newName = sourceDesc.getName().tip().toString() + "_" + sourceType.getName().tip().toString();
                    typeBuilder.setName(newName);
                }
                typeBuilder.setMinimumOccurs(0);
                ftb.addProperty(typeBuilder.build());
            }
        }
        return ftb.build();
    }
}
