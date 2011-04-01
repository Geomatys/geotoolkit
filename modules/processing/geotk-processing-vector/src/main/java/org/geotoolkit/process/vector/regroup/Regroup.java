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
package org.geotoolkit.process.vector.regroup;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Regroup features and there geometries on a specify attribute name. Each different values of this
 * attribute generate a Feature.
 * @author Quentin Boileau
 * @module pending
 */
public class Regroup extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public Regroup() {
        super(RegroupDescriptor.INSTANCE);
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
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(RegroupDescriptor.FEATURE_IN, inputParameters);
        final String inputAttributeName = Parameters.value(RegroupDescriptor.REGROUP_ATTRIBUTE, inputParameters);
        final String inputGeometryName = Parameters.value(RegroupDescriptor.GEOMETRY_NAME, inputParameters);

        final RegroupFeatureCollection resultFeatureList = new RegroupFeatureCollection(inputFeatureList, inputAttributeName, inputGeometryName);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }

    /**
     * Create a new FeatureType with only the attribute and the geometry specified in process input.
     * @param oldFeatureType
     * @param geometryName - if null we use the default Feature geometry
     * @param regroupAttribute
     * @return a FeatureType
     */
    static FeatureType regroupFeatureType(FeatureType oldFeatureType, String geometryName, String regroupAttribute) {

        AttributeDescriptorBuilder descBuilder;
        AttributeTypeBuilder typeBuilder;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.copy(oldFeatureType);

        //if keepedGeometry is null we use the default Geometry
        if (geometryName == null) {
            geometryName = oldFeatureType.getGeometryDescriptor().getName().getLocalPart();
        }

        final Collection<String> listToRemove = new ArrayList<String>();

        final ListIterator<PropertyDescriptor> ite = ftb.getProperties().listIterator();
        while (ite.hasNext()) {

            final PropertyDescriptor desc = ite.next();
            //geometry property
            if (desc instanceof GeometryDescriptor) {

                final GeometryType type = (GeometryType) desc.getType();

                if (desc.getName().getLocalPart().equals(geometryName)) {
                    descBuilder = new AttributeDescriptorBuilder();
                    typeBuilder = new AttributeTypeBuilder();
                    descBuilder.copy((AttributeDescriptor) desc);
                    typeBuilder.copy(type);
                    typeBuilder.setBinding(Geometry.class);
                    descBuilder.setType(typeBuilder.buildGeometryType());
                    final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                    ite.set(newDesc);
                } else {
                    listToRemove.add(desc.getName().getLocalPart());
                }
            } else {//other properties
                //if it's a different property than we wanted
                if (!(desc.getName().getLocalPart().equals(regroupAttribute))) {
                    listToRemove.add(desc.getName().getLocalPart());
                }
            }
        }

        ftb.setDefaultGeometry(geometryName);
        for (String delPropertyDesc : listToRemove) {
            ftb.remove(delPropertyDesc);
        }


        return ftb.buildFeatureType();

    }

    /**
     * Create a Feature with one of attribute values and an union of all features geometry with the
     * same attribute value.
     * @param regroupAttribute - attribute specified in process input
     * @param attrubuteValue - one value of the specified attribute
     * @param newFeatureType - the new FeatureTYpe
     * @param geometryName - if null we use the default Feature geometry
     * @param filtredList - the input FeatureCollection filtered on attribute value
     * @return a Feature
     */
    static Feature regroupFeature(final String regroupAttribute, final Object attrubuteValue,
            final FeatureType newFeatureType, String geometryName, final FeatureCollection<Feature> filtredList) {

        Geometry regroupGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);

        FeatureIterator<Feature> featureIter = filtredList.iterator();
        try {
            while (featureIter.hasNext()) {
                final Feature feature = featureIter.next();
                if (geometryName == null) {
                    geometryName = feature.getDefaultGeometryProperty().getName().getLocalPart();
                }
                for (Property property : feature.getProperties()) {
                    //if property is a geometry
                    if (property.getDescriptor() instanceof GeometryDescriptor) {
                        //if it's the property we needed
                        if (property.getName().getLocalPart().equals(geometryName)) {
                            regroupGeometry = regroupGeometry.union((Geometry) property.getValue());
                        }
                    }
                }
            }
        } finally {
            featureIter.close();
        }

        //result feature
        final Feature resultFeature = FeatureUtilities.defaultFeature(newFeatureType, regroupAttribute + "-" + attrubuteValue);
        resultFeature.getProperty(regroupAttribute).setValue(attrubuteValue);
        resultFeature.getProperty(geometryName).setValue(regroupGeometry);

        return resultFeature;
    }

    /**
     * Browse in input FeatureCollection all different values of the specified attribute
     * @param regroupAttribute
     * @param featureList
     * @return a collection of Objects
     */
    static Collection<Object> getAttributeValues(final String regroupAttribute, final FeatureCollection<Feature> featureList) {

        final Collection<Object> values = new ArrayList<Object>();

        FeatureIterator<Feature> featureIter = featureList.iterator();
        try {
            while (featureIter.hasNext()) {
                final Feature feature = featureIter.next();

                for (Property property : feature.getProperties()) {
                    //if property is not a geometry
                    if (!(property.getDescriptor() instanceof GeometryDescriptor)) {
                        //it's the property we needed
                        if (property.getName().getLocalPart().equals(regroupAttribute)) {
                            //if property value isn't already in our collection, we add it.
                            if (!values.contains(property.getValue())) {
                                values.add(property.getValue());
                            }
                        }
                    }
                }
            }
        } finally {
            featureIter.close();
        }

        return values;
    }
}
