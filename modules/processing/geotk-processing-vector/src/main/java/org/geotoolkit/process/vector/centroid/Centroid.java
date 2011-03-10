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
package org.geotoolkit.process.vector.centroid;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ListIterator;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
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
 * Process to extract geometry centroid from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class Centroid extends AbstractProcess {

    ParameterValueGroup result;

    public Centroid() {
        super(CentroidDescriptor.INSTANCE);
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
        final FeatureCollection<?> inputFeatureList = Parameters.value(CentroidDescriptor.FEATURE_IN, inputParameters);

        //Change the geometry feature type
        final FeatureType newType = changeFeatureType(inputFeatureList.getFeatureType());

        final FeatureCollection<Feature> resultFeatureList = DataUtilities.collection("", newType);
                
        for(Feature inputFeature : inputFeatureList){
           

            //create result feature based on the new feature type and th input feature
            final Feature resultFeature = FeatureUtilities.defaultFeature(newType, inputFeature.getIdentifier().getID());

            for (Property property : inputFeature.getProperties()) {
                //if the propperty is a geometry
                if (property.getDescriptor()instanceof GeometryDescriptor) {
                    final Geometry inputFeatureGeometry = (Geometry) property.getValue();
                    resultFeature.getProperty(property.getName()).setValue(inputFeatureGeometry.getCentroid());
                } else {
                    resultFeature.getProperty(property.getName()).setValue(property.getValue());
                }
            }
            resultFeatureList.add(resultFeature);   
        }
        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * Change the geometry descriptor to Point for centroids.
     * @param FeatureType
     * @return FeatureType
     */
    private FeatureType changeFeatureType(FeatureType oldFeatureType) {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.copy(oldFeatureType);

        final ListIterator<PropertyDescriptor> ite = ftb.getProperties().listIterator();

        while (ite.hasNext()) {

            final PropertyDescriptor desc = ite.next();
            if (desc instanceof GeometryDescriptor) {

                GeometryType type = (GeometryType) desc.getType();
                //if type bunding =! Point
                if (!Point.class.isAssignableFrom(type.getBinding())) {

                    final AttributeDescriptorBuilder descBuilder = new AttributeDescriptorBuilder();
                    final AttributeTypeBuilder typeBuilder = new AttributeTypeBuilder();
                    descBuilder.copy((AttributeDescriptor) desc);
                    typeBuilder.copy(type);
                    typeBuilder.setBinding(Point.class);
                    descBuilder.setType(typeBuilder.buildGeometryType());
                    final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                    ite.set(newDesc);
                }
            }
        }

        return ftb.buildFeatureType();
    }
}
