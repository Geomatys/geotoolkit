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
package org.geotoolkit.process.vector.difference;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process to compute difference between two FeatureCollection
 * It is usually called "Spatial NOT", because it distracts the geometries from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class Difference extends AbstractProcess {
    
    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public Difference() {
        super(DifferenceDescriptor.INSTANCE);
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
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(DifferenceDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection<Feature> inputFeatureClippingList = Parameters.value(DifferenceDescriptor.FEATURE_DIFF, inputParameters);

        final DifferenceFeatureCollection resultFeatureList = new DifferenceFeatureCollection(inputFeatureList,inputFeatureClippingList);

        result = super.getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
    }

    /**
     * Compute difference between a feature and FeatureCollection's geometries
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to compute the difference
     * @return Feature
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection<Feature> featureClippingList) {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());

        for (Property property : oldFeature.getProperties()) {
            
            //for each Geometry in the oldFeature
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                //final List<Geometry> bufferInterGeometries = new ArrayList<Geometry>();
                Geometry resultGeometry = (Geometry) property.getValue();
                final FeatureIterator<Feature> clipIterator = featureClippingList.iterator();
                try{
                    while(clipIterator.hasNext()){
                        final Feature clipFeature = clipIterator.next();
                        for (Property clipFeatureProperty : clipFeature.getProperties()) {
                            if (clipFeatureProperty.getDescriptor() instanceof GeometryDescriptor) {
                                final Geometry diffGeometry = 
                                        VectorProcessUtils.difference(resultGeometry,(Geometry) clipFeatureProperty.getValue());

                                /*
                                 * If diffGeometry return null, it's because the result geomerty
                                 * is contained into another Geometry. So we stop the loop and return null.
                                 */
                                if (diffGeometry != null) {
                                   resultGeometry = diffGeometry;
                                }else{
                                    return null;
                                }
                            }
                        }
                    }  
                }
                finally{
                    clipIterator.close();
                }
              
                resultFeature.getProperty(property.getName()).setValue(resultGeometry);
                
            } else {
                //others properties (no geometry)
                resultFeature.getProperty(property.getName()).setValue(property.getValue());
            }
        }
        return resultFeature;
    }
}
