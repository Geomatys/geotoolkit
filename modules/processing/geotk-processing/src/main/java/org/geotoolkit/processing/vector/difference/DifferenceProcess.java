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
package org.geotoolkit.processing.vector.difference;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorProcessUtils;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.feature.AttributeType;

/**
 * Process to compute difference between two FeatureCollection
 * It is usually called "Spatial NOT", because it distracts the geometries from a FeatureCollection.
 *
 * @author Quentin Boileau
 */
public class DifferenceProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public DifferenceProcess(final ParameterValueGroup input) {
        super(DifferenceDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList         = value(VectorDescriptor.FEATURE_IN, inputParameters);
        final FeatureCollection inputFeatureClippingList = value(DifferenceDescriptor.FEATURE_DIFF, inputParameters);
        final FeatureCollection resultFeatureList = new DifferenceFeatureCollection(inputFeatureList, inputFeatureClippingList);
        getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
     * Compute difference between a feature and FeatureCollection's geometries
     *
     * @param newType the new FeatureType for the Feature
     * @param featureClippingList FeatureCollection used to compute the difference
     */
    public static Feature clipFeature(final Feature oldFeature, final FeatureType newType, final FeatureCollection featureClippingList)
            throws MismatchedDimensionException, TransformException, FactoryException
    {
        final Feature resultFeature = newType.newInstance();
        FeatureExt.setId(resultFeature, FeatureExt.getId(oldFeature));
        for (final PropertyType property : oldFeature.getType().getProperties(true)) {
            final String name = property.getName().toString();
            final Object value = oldFeature.getPropertyValue(name);

            //for each Geometry in the oldFeature
            if (AttributeConvention.isGeometryAttribute(property)) {
                final CoordinateReferenceSystem inputGeomCRS = FeatureExt.getCRS(property);

                //loop and test intersection between each geometry of each clipping feature from
                //clipping FeatureCollection
                Geometry resultGeometry = (Geometry) value;
                try (final FeatureIterator clipIterator = featureClippingList.iterator()) {
                    while (clipIterator.hasNext()) {
                        final Feature clipFeature = clipIterator.next();
                        for (PropertyType clipFeatureProperty : clipFeature.getType().getProperties(true)) {
                            if (AttributeConvention.isGeometryAttribute(clipFeatureProperty)) {
                                Geometry diffGeom = (Geometry) clipFeature.getPropertyValue(clipFeatureProperty.getName().toString());
                                final CoordinateReferenceSystem diffGeomCRS = FeatureExt.getCRS(clipFeatureProperty);

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
                }
                resultFeature.setPropertyValue(name, resultGeometry);
            } else if(property instanceof AttributeType && !(AttributeConvention.contains(property.getName()))){
                //others properties (no geometry)
                resultFeature.setPropertyValue(name, value);
            }
        }
        return resultFeature;
    }
}
