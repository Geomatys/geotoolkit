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
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.vector.VectorProcessUtils;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.process.vector.difference.DifferenceDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process to compute difference between two FeatureCollection
 * It is usually called "Spatial NOT", because it distracts the geometries from a FeatureCollection.
 * @author Quentin Boileau
 * @module pending
 */
public class DifferenceProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public DifferenceProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList           = value(FEATURE_IN, inputParameters);
        final FeatureCollection inputFeatureClippingList   = value(FEATURE_DIFF, inputParameters);

        final FeatureCollection resultFeatureList = new DifferenceFeatureCollection(inputFeatureList, inputFeatureClippingList);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
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
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection featureClippingList)
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
                final FeatureIterator clipIterator = featureClippingList.iterator();
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
