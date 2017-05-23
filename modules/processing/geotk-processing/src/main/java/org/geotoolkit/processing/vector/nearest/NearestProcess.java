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

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.VectorProcessUtils;
import org.apache.sis.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.parameter.Parameters.*;


/**
 * Process return the nearest Feature(s) form a FeatureCollection to a geometry
 *
 * @author Quentin Boileau
 */
public class NearestProcess extends AbstractProcess {

    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
            new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

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
            final FeatureCollection inputFeatureList  = value(VectorDescriptor.FEATURE_IN, inputParameters);
            final Geometry interGeom                  = value(NearestDescriptor.GEOMETRY_IN, inputParameters);
            final FeatureCollection resultFeatureList =
                    new NearestFeatureCollection(inputFeatureList.subCollection(nearestQuery(inputFeatureList, interGeom)));

            getOrCreate(VectorDescriptor.FEATURE_OUT, outputParameters).setValue(resultFeatureList);
        } catch (FactoryException | DataStoreException | TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Create a query to filter nearest feature to the geometry
     *
     * @return nearest query filter
     */
    private Query nearestQuery(final FeatureCollection original, final Geometry geom)
            throws FactoryException, MismatchedDimensionException, TransformException
    {
        CoordinateReferenceSystem geomCrs = JTS.findCoordinateReferenceSystem(geom);
        if (geomCrs == null) {
            geomCrs = FeatureExt.getCRS(original.getFeatureType());
        }
        double dist = Double.POSITIVE_INFINITY;
        final Collection<Identifier> listID = new ArrayList<>();
        try (final FeatureIterator iter = original.iterator(null)) {
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
        final Filter filter = FF.id(new HashSet<>(listID));
        return QueryBuilder.filtered("nearest", filter);
    }
}
