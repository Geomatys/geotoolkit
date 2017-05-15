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
package org.geotoolkit.processing.vector.union;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.apache.sis.referencing.CommonCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;


/**
 * Process compute union between two FeatureCollection
 * It is usually called "Spatial OR".
 * The returned Features will have both attributes from the two FeatureType of FeatureCollection.
 * But only one Geometry (intersection Geometry between two Features from each Collection)
 * Feature ID will be formed with the two Features ID which intersects. Or if the Feature Geometry haven't
 * intersection with the other FeatureCollection Geometry, he keep all of his attributes.
 *
 * @author Quentin Boileau
 * @module
 */
public class UnionProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public UnionProcess(final ParameterValueGroup input) {
        super(UnionDescriptor.INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList  = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection unionFeatureList  = value(UnionDescriptor.FEATURE_UNION, inputParameters);
        final String inputGeometryName            = value(UnionDescriptor.INPUT_GEOMETRY_NAME, inputParameters);
        final String unionGeometryName            = value(UnionDescriptor.UNION_GEOMETRY_NAME, inputParameters);
        final FeatureCollection resultFeatureList = new UnionFeatureCollection(inputFeatureList, unionFeatureList, inputGeometryName, unionGeometryName);
        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Generate an union FeatureCollection comparing a Feature to a FeatureCollection.
     * During the second pass, we remove duplicates Features
     *
     * @param newFeatureType the new FeatureType
     * @param unionFC union FeatureCollection
     * @param inputGeomName attribute name of the used Geometry from inputFeature
     * @param unionGeomName attribute name of the used Geometry from unionFC
     * @param featureList Set of already created Features (it's used in order to remove duplicate Features)
     * @return the result FeatureCollection of an union between a Feature and a FeatureCollection
     */
    static FeatureCollection unionFeatureToFC(final Feature inputFeature, final FeatureType newFeatureType, final FeatureCollection unionFC,
            final String inputGeomName, final String unionGeomName, final boolean firstPass, final Set<String> featureList)
            throws TransformException, FactoryException
    {
        final FeatureCollection resultFeatureList =
                FeatureStoreUtilities.collection(FeatureExt.getId(inputFeature).getID(), newFeatureType);
        /*
         * In order to get all part of Feature, add a second pass with the diffenrence between the FeatureGeometry
         * and united intersections. if return nothing we have all the geometry feature, else we add the difference
         */
        Geometry inputGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        for (final PropertyType inputProperty : inputFeature.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(inputProperty)) {
                final String name = inputProperty.getName().toString();
                if (name.equals(inputGeomName)) {
                    inputGeometry = (Geometry) inputFeature.getPropertyValue(name);
                }
            }
        }

        Geometry remainingGeometry = inputGeometry;
        boolean isIntersected = false;
        //Check if each union Features intersect inputFeature. if yes, create a new Feature which is union of both
        try (final FeatureIterator unionIter = unionFC.iterator()) {
            while (unionIter.hasNext()) {
                final Feature unionFeature = unionIter.next();
                final String featureID;

                //Invert ID order for the second pass (firstpass "inputID U unionID", second pass "unionID U inputID")
                if (firstPass) {
                    featureID = FeatureExt.getId(inputFeature).getID() + "-" + FeatureExt.getId(unionFeature).getID();
                } else {
                    featureID = FeatureExt.getId(unionFeature).getID() + "-" + FeatureExt.getId(inputFeature).getID();
                }

                final Feature resultFeature = unionFeatureToFeature(inputFeature, unionFeature, newFeatureType,
                        inputGeomName, unionGeomName, featureID, firstPass);

                //If resultFeature is null, mean there is no intersection
                //Else we add the resutl Feature to resultFeatureList
                if (resultFeature != null) {
                    isIntersected = true;
                    resultFeatureList.add(resultFeature);
                    Geometry intersectGeom =  (Geometry) resultFeature.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
                    remainingGeometry = remainingGeometry.difference(intersectGeom);
                }
            }
        }

        //If remaining Geometry is empty and isIntersecting boolean is false, mean the geometry
        if (remainingGeometry.isEmpty() && !isIntersected) {
            final Feature remainingFeature = newFeatureType.newInstance();
            FeatureExt.setId(remainingFeature, FeatureExt.getId(inputFeature));
            //Copy none Geometry attributes
            for (final PropertyType inputProperty : inputFeature.getType().getProperties(true)) {
                if(!(inputProperty instanceof AttributeType) || AttributeConvention.contains(inputProperty.getName())) continue;
                if (!AttributeConvention.isGeometryAttribute(inputProperty)) {
                    final String name = inputProperty.getName().toString();
                    remainingFeature.setPropertyValue(name, inputFeature.getPropertyValue(name));
                }
            }
            if (firstPass) {
                remainingFeature.setPropertyValue(inputGeomName, inputGeometry);
            } else {
                remainingFeature.setPropertyValue(unionGeomName, inputGeometry);
            }
            resultFeatureList.add(remainingFeature);

        }

        //Create a remaining Feature with the inputGeometry
        if (!(remainingGeometry.isEmpty())) {
            if(remainingGeometry.equalsTopo(inputGeometry)) {
                remainingGeometry = inputGeometry;
            }

            final Feature remainingFeature = newFeatureType.newInstance();
            FeatureExt.setId(remainingFeature, FeatureExt.getId(inputFeature));
            //Copy none Geometry attributes
            for (final PropertyType inputProperty : inputFeature.getType().getProperties(true)) {
                if(!(inputProperty instanceof AttributeType) || AttributeConvention.contains(inputProperty.getName())) continue;
                if (!AttributeConvention.isGeometryAttribute(inputProperty)) {
                    final String name = inputProperty.getName().toString();
                    remainingFeature.setPropertyValue(name, inputFeature.getPropertyValue(name));
                }
            }
            //System.out.println("LOG Empty remaining Geometry");
            if (firstPass) {
                remainingFeature.setPropertyValue(inputGeomName, remainingGeometry);
            } else {
                remainingFeature.setPropertyValue(unionGeomName, remainingGeometry);
            }
            resultFeatureList.add(remainingFeature);
        }
        final Collection<Feature> featureToRemove = new ArrayList<>();
        /* Check if created features are already present in featureList.
         * If yes, we delete them from the returning FeatureCollection
         * else we add them into the featureList
         */
        for (final Feature createdFeature : resultFeatureList) {
            final String createdFeatureID = FeatureExt.getId(createdFeature).getID();
            if (featureList.contains(createdFeatureID)) {
                featureToRemove.add(createdFeature);
            } else {
                featureList.add(createdFeatureID);
            }
        }
        //remove existing feature
        resultFeatureList.removeAll(featureToRemove);
        return resultFeatureList;
    }

    /**
     * Create a Feature which is the union between two Features. If there is no intersection,
     * function will return null.
     *
     * @param inputGeomName attribute name of the used Geometry in inputFeature
     * @param unionGeomName attribute name of the used Geometry in unionFeature
     * @param featureID ID of the new Feature
     * @param firstPass boolean to set which Geometry is use for the created Feature
     * @return the union Feature with intersection as Geometry. Return null if there is no intersection.
     */
    private static Feature unionFeatureToFeature(final Feature inputFeature, final Feature unionFeature, final FeatureType newFeatureType,
            final String inputGeomName, final String unionGeomName, final String featureID, final boolean firstPass) throws FactoryException,
            TransformException
    {
        final Geometry intersectGeometry = VectorProcessUtils.intersectionFeatureToFeature(inputFeature, unionFeature, inputGeomName, unionGeomName);
        if (!intersectGeometry.isEmpty()) {
            final Feature resultFeature = newFeatureType.newInstance();
            resultFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), featureID);

            //copy none Geometry attributes
            for (final PropertyType unionProperty : unionFeature.getType().getProperties(true)) {
                if(!(unionProperty instanceof AttributeType) || AttributeConvention.contains(unionProperty.getName())) continue;
                if (!AttributeConvention.isGeometryAttribute(unionProperty)) {
                    final String name = unionProperty.getName().toString();
                    resultFeature.setPropertyValue(name, unionFeature.getPropertyValue(name));
                }
            }
            for (final PropertyType inputProperty : inputFeature.getType().getProperties(true)) {
                if(!(inputProperty instanceof AttributeType) || AttributeConvention.contains(inputProperty.getName())) continue;
                if (!AttributeConvention.isGeometryAttribute(inputProperty)) {
                    final String name = inputProperty.getName().toString();
                    resultFeature.setPropertyValue(name, inputFeature.getPropertyValue(name));
                }
            }
            if (firstPass) {
                resultFeature.setPropertyValue(inputGeomName, intersectGeometry);
            } else {
                resultFeature.setPropertyValue(unionGeomName, intersectGeometry);
            }
            return resultFeature;
        } else {
            return null;
        }
    }

    /**
     * Create a new FeatureType merging two inputs FeatureType with only one Geometry attribute.
     * By default the geometry CRS is set to WGS84
     * @param type1
     * @param type2
     * @param geometryName - Name of the union Geometry
     * @param geometryCRS - CRS of the union Geometry
     * @return the new FeatureType
     */
    static FeatureType mergeType(final FeatureType type1, final FeatureType type2, final String geometryName,
            CoordinateReferenceSystem geometryCRS) {

        //use WGS84 CRS if geometryCRS is null
        if (geometryCRS == null) {
            geometryCRS = CommonCRS.WGS84.normalizedGeographic();
        }

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        //add identifier
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);

        // Name of the new FeatureType.
        ftb.setName(type1.getName().tip().toString() + "-" + type2.getName().tip().toString());

        //Copy all properties from type1
        for (final PropertyType sourceDesc : type1.getProperties(true)) {
            if(AttributeConvention.contains(sourceDesc.getName())) continue;
            //add all descriptors but geometry
            if (!AttributeConvention.isGeometryAttribute(sourceDesc)) {
                ftb.addProperty(sourceDesc);
            }
        }

        // Copy all properties from the type2 without duplicate
        for (final PropertyType targetDesc : type2.getProperties(true)) {
            if(AttributeConvention.contains(targetDesc.getName())) continue;

            if (!AttributeConvention.isGeometryAttribute(targetDesc)) {
                boolean isExistDesc = false;
                //search if target descriptor name already exist into source descriptors
                for (final PropertyType sourceDesc : type1.getProperties(true)) {
                    if (!AttributeConvention.isGeometryAttribute(sourceDesc)) {
                        //if attribute descriptor name isn't found into ftb we add it
                        if ((targetDesc.getName().equals(sourceDesc.getName()))) {
                            isExistDesc = true;
                            break;
                        }
                    }
                }
                if (!isExistDesc) {
                    ftb.addProperty(targetDesc);
                }
            }
        }
        //add geometry
        ftb.addAttribute(Geometry.class).setName(geometryName).setCRS(geometryCRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }
}
