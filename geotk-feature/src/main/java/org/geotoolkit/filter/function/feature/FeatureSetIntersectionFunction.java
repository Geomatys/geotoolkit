/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.filter.function.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.measure.Quantities;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.function.AbstractFunction;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Expression;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Intersection of geometry with a FeatureSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetIntersectionFunction extends AbstractFunction {

    private static final Logger LOGGER = Logging.getLogger(FeatureSetIntersectionFunction.class);

    public FeatureSetIntersectionFunction(final Expression expr1, final Expression expr2) {
        super(FeatureFunctionFactory.DATA_INTERSECTION, expr1, expr2);
    }

    @Override
    public Object apply(final Object candidate) {

        final FilterFactory FF = DefaultFilterFactory.forFeatures();
        final Geometry geom = parameters.get(0).toValueType(Geometry.class).apply(candidate);
        final CoordinateReferenceSystem geomCrs = Geometries.wrap(geom).get().getCoordinateReferenceSystem();
        final Object identifier = parameters.get(1).apply(candidate);
        final Object resource = identifier;

        final List<Polygon> union = new ArrayList<>();

        if (resource instanceof FeatureSet fs) {
            try {
                final FeatureSet subset = fs.subset(createQuery(geom, fs));

                try (Stream<Feature> stream = subset.features(false)) {
                    final Iterator<Feature> iterator = stream.iterator();
                    while (iterator.hasNext()) {
                        final Feature feature = iterator.next();
                        Object igeom = feature.getValueOrFallback(AttributeConvention.GEOMETRY, null);
                        if (igeom != null) {
                            igeom = org.apache.sis.geometry.wrapper.jts.JTS.transform((Geometry) igeom, geomCrs);
                            try {
                                Geometry intersection = geom.intersection((Geometry) igeom);
                                if (!intersection.isEmpty()) {
                                    if (intersection instanceof Polygon polygon) {
                                        intersection.setUserData(geomCrs);
                                        union.add(polygon);
                                    } else if (intersection instanceof GeometryCollection gc) {
                                        for (int i = 0, n = gc.getNumGeometries(); i < n; i++) {
                                            Geometry cdt = gc.getGeometryN(i);
                                            if (cdt instanceof Polygon polygon) {
                                                cdt.setUserData(geomCrs);
                                                union.add(polygon);
                                            }
                                        }
                                    }
                                }
                            } catch (TopologyException ex) {
                                try {
                                    //try to clean the geometry
                                    igeom = ((Geometry) igeom).buffer(0);
                                    Geometry intersection = geom.intersection((Geometry) igeom);
                                    if (!intersection.isEmpty()) {
                                        if (intersection instanceof Polygon polygon) {
                                            intersection.setUserData(geomCrs);
                                            union.add(polygon);
                                        } else if (intersection instanceof GeometryCollection gc) {
                                            for (int i = 0, n = gc.getNumGeometries(); i < n; i++) {
                                                Geometry cdt = gc.getGeometryN(i);
                                                if (cdt instanceof Polygon polygon) {
                                                    cdt.setUserData(geomCrs);
                                                    union.add(polygon);
                                                }
                                            }
                                        }
                                    }
                                } catch (TopologyException e) {
                                    LOGGER.log(Level.WARNING, "FeatureSetIntersectionFunction caused a TopologyException, this warning is logged and will continue intersection tests on other features.");
                                }
                            }
                        }
                    }
                }
            } catch (DataStoreException | TransformException | FactoryException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        final MultiPolygon col = geom.getFactory().createMultiPolygon(union.toArray(Polygon[]::new));
        col.setUserData(geomCrs);
        return col;
    }

    static FeatureQuery createQuery(Geometry geom, FeatureSet target) {
        final FilterFactory ff = DefaultFilterFactory.forFeatures();

        final double resolution = geom.getFactory().getPrecisionModel().getScale();
        org.opengis.geometry.Envelope geomEnv = Geometries.wrap(geom).get().getEnvelope();
        final CoordinateReferenceSystem geomCrs = geomEnv.getCoordinateReferenceSystem();

        try {
            //try to pre transform envelope, to avoid every geometry transformation
            final FeatureType fsType = target.getType();
            CoordinateReferenceSystem fscrs = FeatureExt.getCRS(fsType);
            if (fscrs != null) {
                geomEnv = Envelopes.transform(geomEnv, fscrs);
            }
        } catch (DataStoreException | TransformException ex) {
            //do nothing
        }

        final FeatureQuery query = new FeatureQuery();
        query.setLinearResolution((Quantity<Length>) Quantities.create(resolution, geomCrs.getCoordinateSystem().getAxis(0).getUnit()));
        query.setSelection(ff.bbox(ff.property(AttributeConvention.GEOMETRY), geomEnv));
        return query;
    }
}
