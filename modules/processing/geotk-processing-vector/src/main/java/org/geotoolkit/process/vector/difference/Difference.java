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
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Process to compute difference between two FeatureCollection
 * It is usually called "Spatial NOT", because it distracts the geometries from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class Difference extends AbstractProcess {

    /**
     * Default constructor
     */
    public Difference(final ParameterValueGroup input) {
        super(DifferenceDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public ParameterValueGroup call() {
        fireStartEvent(new ProcessEvent(this));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(DifferenceDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection<Feature> inputFeatureClippingList = Parameters.value(DifferenceDescriptor.FEATURE_DIFF, inputParameters);

        final FeatureCollection resultFeatureList = new DifferenceFeatureCollection(inputFeatureList, inputFeatureClippingList);

        outputParameters.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        fireEndEvent(new ProcessEvent(this, null, 100));
        return outputParameters;
    }

    /**
     * Compute difference between a feature and FeatureCollection's geometries
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to compute the difference
     * @return Feature
     * @throws MismatchedDimensionException
     * @throws TransformException
     * @throws FactoryException
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection<Feature> featureClippingList)
            throws MismatchedDimensionException, TransformException, FactoryException {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());

        for (Property property : oldFeature.getProperties()) {

            //for each Geometry in the oldFeature
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                final GeometryDescriptor inputGeomDesc = (GeometryDescriptor) property.getDescriptor();
                final CoordinateReferenceSystem inputGeomCRS = inputGeomDesc.getCoordinateReferenceSystem();

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                Geometry resultGeometry = (Geometry) property.getValue();
                final FeatureIterator<Feature> clipIterator = featureClippingList.iterator();
                try {
                    while (clipIterator.hasNext()) {
                        final Feature clipFeature = clipIterator.next();
                        for (Property clipFeatureProperty : clipFeature.getProperties()) {
                            if (clipFeatureProperty.getDescriptor() instanceof GeometryDescriptor) {

                                Geometry diffGeom = (Geometry) clipFeatureProperty.getValue();
                                final GeometryDescriptor diffGeomDesc = (GeometryDescriptor) clipFeatureProperty.getDescriptor();
                                final CoordinateReferenceSystem diffGeomCRS = diffGeomDesc.getCoordinateReferenceSystem();

                                //re-project clipping geometry into input Feature geometry CRS
                                diffGeom = VectorProcessUtils.repojectGeometry(inputGeomCRS, diffGeomCRS, diffGeom);
                                
                                final Geometry diffGeometry =
                                        VectorProcessUtils.geometryDifference(resultGeometry, diffGeom);

                                /*
                                 * If diffGeometry return null, it's because the result geomerty
                                 * is contained into another Geometry. So we stop the loop and return null.
                                 */
                                if (diffGeometry != null) {
                                    resultGeometry = diffGeometry;
                                } else {
                                    return null;
                                }
                            }
                        }
                    }
                } finally {
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
