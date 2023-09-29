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
package org.geotoolkit.processing.vector.nearest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.geotoolkit.storage.feature.query.Query;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Process return the nearest Feature(s) form a FeatureCollection to a geometry
 *
 * @author Quentin Boileau (Geomatys)
 */
public class NearestProcess extends AbstractProcess {

    private static final FilterFactory FF = FilterUtilities.FF;

    /**
     * Default constructor
     */
    public NearestProcess(final ParameterValueGroup input) {
        super(NearestDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        try {
            final FeatureSet inputFeatureList  = inputParameters.getValue(VectorDescriptor.FEATURESET_IN);
            final Geometry interGeom                  = inputParameters.getValue(NearestDescriptor.GEOMETRY_IN);
            final FeatureSet resultFeatureList = inputFeatureList.subset(nearestQuery(inputFeatureList, interGeom));

            outputParameters.getOrCreate(VectorDescriptor.FEATURESET_OUT).setValue(resultFeatureList);
        } catch (FactoryException | DataStoreException | TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Create a query to filter nearest feature to the geometry
     *
     * @return nearest query filter
     */
    private FeatureQuery nearestQuery(final FeatureSet original, final Geometry geom)
            throws FactoryException, MismatchedDimensionException, TransformException, DataStoreException
    {
        CoordinateReferenceSystem geomCrs = JTS.findCoordinateReferenceSystem(geom);
        if (geomCrs == null) {
            geomCrs = FeatureExt.getCRS(original.getType());
        }
        double dist = Double.POSITIVE_INFINITY;
        final Collection<Filter<Object>> listID = new ArrayList<>();
        try (final Stream<Feature> stream = original.features(false)) {
            Iterator<Feature> iter = stream.iterator();
            while (iter.hasNext()) {
                final Feature feature = iter.next();
                for (final PropertyType property : feature.getType().getProperties(true)) {
                    if (AttributeConvention.isGeometryAttribute(property)) {
                        Geometry featureGeom = (Geometry) feature.getPropertyValue(property.getName().toString());
                        final CoordinateReferenceSystem featureGeomCRS = FeatureExt.getCRS(property);

                        //re-project feature geometry into input geometry CRS
                        featureGeom = VectorProcessUtils.repojectGeometry(geomCrs, featureGeomCRS, featureGeom);
                        final double computedDist = geom.distance(featureGeom);
                        if (computedDist < dist) {
                            listID.clear();
                            dist = computedDist;
                            listID.add(FeatureExt.getId(feature));
                        } else {
                            if (computedDist == dist) {
                                listID.add(FeatureExt.getId(feature));
                            }
                        }
                    }
                }
            }
        }
        final FeatureQuery query = new FeatureQuery();
        query.setSelection(FF.or(listID));
        return query;
    }
}
