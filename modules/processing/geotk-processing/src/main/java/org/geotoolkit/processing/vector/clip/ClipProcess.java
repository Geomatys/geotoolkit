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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.processing.vector.clip.ClipDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;


/**
 * Process to clip a FeatureCollection using another FeatureCollection
 *
 * @author Quentin Boileau
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
        final FeatureCollection inputFeatureList         = value(FEATURE_IN, inputParameters);
        final FeatureCollection inputFeatureClippingList = value(FEATURE_CLIP, inputParameters);
        final FeatureCollection resultFeatureList = new ClipFeatureCollection(inputFeatureList,inputFeatureClippingList);
        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Clip a feature with the FeatureCollection's geometries
     *
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to clip
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection featureClippingList)
            throws FactoryException, MismatchedDimensionException, TransformException
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
                try (final FeatureIterator clipIterator = featureClippingList.iterator()) {
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
