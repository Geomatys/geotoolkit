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
package org.geotoolkit.processing.vector.clip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import static org.geotoolkit.processing.vector.clip.ClipDescriptor.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Process to clip a FeatureCollection using another FeatureCollection
 *
 * @author Quentin Boileau
 */
public class ClipProcess extends AbstractProcess {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

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
    protected void execute() throws ProcessException {
        final FeatureSet inputFeatureList         = inputParameters.getValue(FEATURE_IN);
        final FeatureSet inputFeatureClippingList = inputParameters.getValue(FEATURE_CLIP);
        final FeatureSet resultFeatureList;
        try {
            resultFeatureList = new ClipFeatureCollection(inputFeatureList,inputFeatureClippingList);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        outputParameters.getOrCreate(FEATURE_OUT).setValue(resultFeatureList);
    }

    /**
     * Clip a feature with the FeatureCollection's geometries
     *
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to clip
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureSet featureClippingList)
            throws FactoryException, MismatchedDimensionException, TransformException, DataStoreException
    {
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (final PropertyType property : oldFeature.getType().getProperties(true)) {
            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);

            //for each Geometry in the oldFeature
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Geometry inputGeom = (Geometry) value;
                final CoordinateReferenceSystem inputGeomCRS = FeatureExt.getCRS(property);

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                final List<Geometry> bufferInterGeometries = new ArrayList<>();
                try (Stream<Feature> stream = featureClippingList.features(false)) {
                    final Iterator<Feature> clipIterator = stream.iterator();
                    while(clipIterator.hasNext()) {
                        final Feature clipFeature = clipIterator.next();
                        for (PropertyType clipFeatureProperty : clipFeature.getType().getProperties(true)) {
                            if (AttributeConvention.isGeometryAttribute(clipFeatureProperty)) {
                                Geometry clipGeom = (Geometry) clipFeature.getPropertyValue(clipFeatureProperty.getName().toString());
                                final CoordinateReferenceSystem clipGeomCRS = FeatureExt.getCRS(clipFeatureProperty);

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

                //if the feature intersect one of the feature clipping list
                final int size = bufferInterGeometries.size();
                if (size == 1) {
                    resultFeature.setPropertyValue(name, bufferInterGeometries.get(0));
                } else if (size > 1) {
                    final Geometry[] bufferArray = bufferInterGeometries.toArray(new Geometry[bufferInterGeometries.size()]);

                    //create a GeometryCollection with all the intersections
                    final GeometryCollection resultGeometry = GF.createGeometryCollection(bufferArray);
                    resultFeature.setPropertyValue(name, resultGeometry);
                } else {
                    return null;
                }
            } else if(property instanceof AttributeType && !(AttributeConvention.contains(property.getName()))){
                //others properties (no geometry)
                resultFeature.setPropertyValue(name, value);
            }
        }
        return resultFeature;
    }
}
