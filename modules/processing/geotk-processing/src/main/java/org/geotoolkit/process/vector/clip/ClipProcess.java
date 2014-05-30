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

import java.util.ArrayList;
import java.util.List;

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

import static org.geotoolkit.process.vector.clip.ClipDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Process to clip a FeatureCollection using another FeatureCollection
 * @author Quentin Boileau
 * @module pending
 */
public class ClipProcess extends AbstractProcess {

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Default constructor
     */
    public ClipProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection<Feature> inputFeatureList           = value(FEATURE_IN, inputParameters);
        final FeatureCollection<Feature> inputFeatureClippingList   = value(FEATURE_CLIP, inputParameters);

        final FeatureCollection resultFeatureList = new ClipFeatureCollection(inputFeatureList,inputFeatureClippingList);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip a feature with the FeatureCollection's geometries
     * @param oldFeature Feature
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to clip
     * @return Feature
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection<Feature> featureClippingList)
            throws FactoryException, MismatchedDimensionException, TransformException {

        final Feature resultFeature = FeatureUtilities.defaultFeature(newType, oldFeature.getIdentifier().getID());

        for (Property property : oldFeature.getProperties()) {

            //for each Geometry in the oldFeature
            if (property.getDescriptor() instanceof GeometryDescriptor) {

                final Geometry inputGeom = (Geometry) property.getValue();
                final GeometryDescriptor inputGeomDesc = (GeometryDescriptor) property.getDescriptor();
                final CoordinateReferenceSystem inputGeomCRS = inputGeomDesc.getCoordinateReferenceSystem();

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                final List<Geometry> bufferInterGeometries = new ArrayList<Geometry>();
                final FeatureIterator<Feature> clipIterator = featureClippingList.iterator();
                try{
                    while(clipIterator.hasNext()) {
                        final Feature clipFeature = clipIterator.next();
                        for (Property clipFeatureProperty : clipFeature.getProperties()) {
                            if (clipFeatureProperty.getDescriptor() instanceof GeometryDescriptor) {

                                Geometry clipGeom = (Geometry) clipFeatureProperty.getValue();
                                final GeometryDescriptor clipGeomDesc = (GeometryDescriptor) clipFeatureProperty.getDescriptor();
                                final CoordinateReferenceSystem clipGeomCRS = clipGeomDesc.getCoordinateReferenceSystem();

                                //re-project clipping geometry into input Feature geometry CRS
                                clipGeom = VectorProcessUtils.repojectGeometry(inputGeomCRS, clipGeomCRS, clipGeom);

                                final Geometry interGeometry = VectorProcessUtils.geometryIntersection(inputGeom, clipGeom);

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
                final int size = bufferInterGeometries.size();

                if (size == 1) {
                    resultFeature.getProperty(property.getName()).setValue(bufferInterGeometries.get(0));
                }else if (size > 1) {
                    final Geometry[] bufferArray = bufferInterGeometries.toArray(new Geometry[bufferInterGeometries.size()]);

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

}
