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
package org.geotoolkit.process.vector.union;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.process.vector.VectorProcessUtils;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Process compute union between two FeatureCollection
 * It is usually called "Spatial OR".
 * The returned Features will have both attributes from the two FeatureType of FeatureCollection.
 * But only one Geometry (intersection Geometry between two Features from each Collection)
 * Feature ID will be formed with the two Features ID which intersects. Or if the Feature Geometry haven't
 * intersection with the other FeatureCollection Geometry, he keep all of his attributes.
 * 
 * @author Quentin Boileau
 * @module pending
 */
public class Union extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public Union() {
        super(UnionDescriptor.INSTANCE);
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
        getMonitor().started(new ProcessEvent(this, 0, null, null));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(UnionDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection<Feature> inputFeatureUnionList = Parameters.value(UnionDescriptor.FEATURE_UNION, inputParameters);
        final String inputGeometryName = Parameters.value(UnionDescriptor.INPUT_GEOMETRY_NAME, inputParameters);
        final String unionGeometryName = Parameters.value(UnionDescriptor.UNION_GEOMETRY_NAME, inputParameters);

        final FeatureCollection resultFeatureList = new UnionFeatureCollection(inputFeatureList, inputFeatureUnionList, inputGeometryName, unionGeometryName);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }

    /**
     * Generate an union FeatureCollection comparing a Feature to a FeatureCollection.
     * During the second pass, we remove duplicates Features
     * @param inputFeature
     * @param newFeatureType - the new FeatureType
     * @param unionFC - union FeatureCollection
     * @param inputGeomName - attribute name of the used Geometry from inputFeature
     * @param unionGeomName - attribute name of the used Geometry from unionFC
     * @param firstPass
     * @param featureList - Set of already created Features (it's used in order to remove duplicate Features)
     * @return the result FeatureCollection of an union between a Feature and a FeatureCollection
     * @throws TransformException
     * @throws FactoryException
     */
    static FeatureCollection unionFeatureToFC(final Feature inputFeature, final FeatureType newFeatureType, final FeatureCollection unionFC,
            final String inputGeomName, final String unionGeomName, final boolean firstPass, Set<String> featureList)
            throws TransformException, FactoryException {

        final FeatureCollection<Feature> resultFeatureList =
                DataUtilities.collection(inputFeature.getIdentifier().getID(), newFeatureType);



        //Check if each union Features intersect inputFeature. if yes, create a new Feature which is union of both
        Geometry remainingGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        final FeatureIterator<Feature> unionIter = unionFC.iterator();
        try {
            while (unionIter.hasNext()) {
                final Feature unionFeature = unionIter.next();

                String featureID = null;
                //Invert ID order for the second pass
                if (firstPass) {
                    featureID = inputFeature.getIdentifier().getID() + " U " + unionFeature.getIdentifier().getID();
                } else {
                    featureID = unionFeature.getIdentifier().getID() + " U " + inputFeature.getIdentifier().getID();
                }


                final Feature resultFeature = unionFeatureToFeature(inputFeature, unionFeature, newFeatureType,
                        inputGeomName, unionGeomName, featureID, firstPass);

                //If resultFeature is null, that's mean there is no intersection
                if (resultFeature != null) {
                    resultFeatureList.add(resultFeature);
                    remainingGeometry = remainingGeometry.union((Geometry) resultFeature.getDefaultGeometryProperty().getValue());
                }
            }
        } finally {
            unionIter.close();
        }

        /*
         * In order to get all part of Feature, add a second pass with the diffenrence between the FeatureGeometry
         * and united intersections. if return nothing we have all the geometry feature, else we add the difference
         */
        Geometry inputGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        for (Property inputProperty : inputFeature.getProperties()) {
            if (inputProperty.getDescriptor() instanceof GeometryDescriptor) {
                if (inputProperty.getName().getLocalPart().equals(inputGeomName)) {
                    inputGeometry = (Geometry) inputProperty.getValue();
                }
            }
        }

        //Create the remaining Feature with the remaining geometry if different of input Geometry
        if (!(inputGeometry.equals(remainingGeometry))) {

            final Feature remainingFeature = FeatureUtilities.defaultFeature(newFeatureType, inputFeature.getIdentifier().getID());
            //Copy none Geometry attributes
            for (Property inputProperty : inputFeature.getProperties()) {
                if (!(inputProperty.getDescriptor() instanceof GeometryDescriptor)) {
                    remainingFeature.getProperty(inputProperty.getName()).setValue(inputProperty.getValue());
                }
            }

            if (firstPass) {
                remainingFeature.getProperty(inputGeomName).setValue(inputGeometry.difference(remainingGeometry));
            } else {
                remainingFeature.getProperty(unionGeomName).setValue(inputGeometry.difference(remainingGeometry));
            }
            resultFeatureList.add(remainingFeature);
        }

        final Collection<Feature> featureToRemove = new ArrayList<Feature>();
        /* Check if created features are already present in featureList.
         * If yes, we delete them from the returning FeatureCollection
         * else we add them into the featureList
         */
        for (Feature createdFeature : resultFeatureList) {
            final String createdFeatureID = createdFeature.getIdentifier().getID();
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
     * @param inputFeature
     * @param unionFeature
     * @param newFeatureType
     * @param inputGeomName- attribute name of the used Geometry in inputFeature
     * @param unionGeomName - attribute name of the used Geometry in unionFeature
     * @param featureID - ID of the new Feature
     * @param firstPass - boolean to set which Geometry is use for the created Feature
     * @return the union Feature with intersection as Geometry. Return null if there is no intersection.
     * @throws FactoryException
     * @throws TransformException
     */
    private static Feature unionFeatureToFeature(final Feature inputFeature, final Feature unionFeature, final FeatureType newFeatureType,
            final String inputGeomName, final String unionGeomName, final String featureID, final boolean firstPass) throws FactoryException,
            TransformException {

        final Geometry intersectGeometry = VectorProcessUtils.intersect(inputFeature, unionFeature, inputGeomName, unionGeomName);
        if (!intersectGeometry.isEmpty()) {

            final Feature resultFeature = FeatureUtilities.defaultFeature(newFeatureType, featureID);

            //copy none Geometry attributes
            for (Property unionProperty : unionFeature.getProperties()) {
                if (!(unionProperty.getDescriptor() instanceof GeometryDescriptor)) {
                    resultFeature.getProperty(unionProperty.getName()).setValue(unionProperty.getValue());
                }
            }

            for (Property inputProperty : inputFeature.getProperties()) {
                if (!(inputProperty.getDescriptor() instanceof GeometryDescriptor)) {
                    resultFeature.getProperty(inputProperty.getName()).setValue(inputProperty.getValue());
                }
            }

            if (firstPass) {
                resultFeature.getProperty(inputGeomName).setValue(intersectGeometry);
            } else {
                resultFeature.getProperty(unionGeomName).setValue(intersectGeometry);
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
            geometryCRS = DefaultGeographicCRS.WGS84;
        }

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        // Name of the new FeatureType.
        ftb.setName(type1.getName().getLocalPart() + " U " + type2.getName().getLocalPart());

        //Copy all properties from type1
        Iterator<PropertyDescriptor> iteSource = type1.getDescriptors().iterator();
        while (iteSource.hasNext()) {
            final PropertyDescriptor sourceDesc = iteSource.next();

            //add all descriptors but geometry
            if (!(sourceDesc instanceof GeometryDescriptor)) {
                ftb.add(sourceDesc);
            }
        }

        // Copy all properties from the type2 without duplicate
        final Iterator<PropertyDescriptor> iteTarget = type2.getDescriptors().iterator();
        while (iteTarget.hasNext()) {
            final PropertyDescriptor targetDesc = iteTarget.next();

            if (!(targetDesc instanceof GeometryDescriptor)) {

                iteSource = type1.getDescriptors().iterator();
                while (iteSource.hasNext()) {
                    final PropertyDescriptor sourceDesc = iteSource.next();

                    if (!(sourceDesc instanceof GeometryDescriptor)) {
                        //if attribute descriptor name isn't found into ftb we add it
                        if (!(targetDesc.getName().equals(sourceDesc.getName()))) {
                            ftb.add(targetDesc);
                        }
                    }
                }
            }
        }
        //add geometry
        ftb.add(geometryName, Geometry.class, geometryCRS);
        ftb.setDefaultGeometry(geometryName);

        return ftb.buildFeatureType();
    }
}
