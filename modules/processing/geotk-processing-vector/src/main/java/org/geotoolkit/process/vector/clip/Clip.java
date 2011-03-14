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
package org.geotoolkit.process.vector.clip;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.ListIterator;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
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
 * Process to clip a FeatureCollection using another FeatureCollection
 * @author Quentin Boileau
 * @module pending
 */
public class Clip extends AbstractProcess {

    ParameterValueGroup result;
    static FeatureCollection<Feature>  inputFeatureClippingList;

    /**
     * Default constructor
     */
    public Clip() {
        super(ClipDescriptor.INSTANCE);
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
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(ClipDescriptor.FEATURE_IN, inputParameters);
        inputFeatureClippingList = Parameters.value(ClipDescriptor.FEATURE_CLIP, inputParameters);

        final ClipFeatureCollection resultFeatureList = new ClipFeatureCollection(inputFeatureList);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * Change the geometry descriptor to GeometryCollection.
     * @param FeatureType
     * @return FeatureType
     */
    public static FeatureType changeFeatureType(FeatureType oldFeatureType) {

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
                    typeBuilder.setBinding(GeometryCollection.class);
                    descBuilder.setType(typeBuilder.buildGeometryType());

                    final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                    ite.set(newDesc);
                }
            }
        }

        return ftb.buildFeatureType();
    }

    /**
     * Clip a feature with the FeatureCollection's geometries
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @return Feature
     */
    public static Feature clipFeature(Feature oldFeature, FeatureType newType) {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());
        final GeometryFactory GF = new GeometryFactory();

        for (Property property : oldFeature.getProperties()) {
            
            //for each Geometry in the oldFeature
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                final ArrayList<Geometry> bufferInterGeometries = new ArrayList<Geometry>();
                final FeatureIterator<Feature> clipIterator = inputFeatureClippingList.iterator();
                try{
                    while(clipIterator.hasNext()){
                        Feature clipFeature = clipIterator.next();
                        for (Property clipFeatureProperty : clipFeature.getProperties()) {
                            if (clipFeatureProperty.getDescriptor() instanceof GeometryDescriptor) {
                                final Geometry interGeometry = testClipping((Geometry) property.getValue(), 
                                                                            (Geometry) clipFeatureProperty.getValue());

                                //if an intersection geometry exist, store it into a buffer Collection
                                if (interGeometry != null) {
                                    bufferInterGeometries.add(interGeometry);
                                }                                 
                            }
                        }
                    }  
                }
                finally{
                    clipIterator.close();
                }
               
                //if the feature intersect one of the feature clipping list
                if (bufferInterGeometries.size() > 0) {
                    final Geometry[] bufferArray = (bufferInterGeometries.toArray(new Geometry[bufferInterGeometries.size()]));
                    
                    //create a GeometryCollection with all the intersections
                    final GeometryCollection resultGeometry = GF.createGeometryCollection(bufferArray);

                    resultFeature.getProperty(property.getName()).setValue(resultGeometry);
                } else {
                    return null;
                }
            } else {
                //others properties (no geometry)
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }
        return resultFeature;
    }

     /**
     * Test clipping between the feature's geometry and the clipping geometry
     * @param featureGeometry Geometry
     * @param clippingGeometry Geometry
     * @return Geometry
     */
    public static Geometry testClipping(Geometry featureGeometry, Geometry clippingGeometry) {

        Geometry intersectGeometry;

        if (!featureGeometry.intersects(clippingGeometry)) {
            return null;
        } else {
            intersectGeometry = featureGeometry.intersection(clippingGeometry);
        }

        return intersectGeometry;
    }
}
