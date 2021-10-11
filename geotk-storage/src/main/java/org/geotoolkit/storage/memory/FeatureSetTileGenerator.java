/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.memory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.query.FeatureQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.util.StringUtilities;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 * Generate tiles with a FeatureSet resource.
 * <p>
 * The created features, will have geometries :
 * </p>
 * <ul>
 *   <li>transformed to the pyramid crs</li>
 *   <li>simplified to the mosaic resolution</li>
 *   <li>clipped to the tile bounds</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetTileGenerator extends AbstractTileGenerator {

    private static final FilterFactory2 FF = FilterUtilities.FF;
    private final FeatureSet source;
    private final CoordinateReferenceSystem sourceCrs;

    public FeatureSetTileGenerator(FeatureSet source) throws DataStoreException {
        this.source = source;
        sourceCrs = FeatureExt.getCRS(source.getType());
    }

    @Override
    protected boolean isEmpty(Tile tile) throws DataStoreException {
        Resource r = tile;
        if (r instanceof DeferredTile) {
            r = ((DeferredTile) r).open();
        }

        FeatureSet fs = (FeatureSet) r;
        return FeatureStoreUtilities.getCount(fs) == 0l;
    }

    @Override
    public Tile generateTile(TileMatrixSet pyramid, TileMatrix mosaic, Point tileCoord) throws DataStoreException {

        final Envelope tileEnv = TileMatrices.computeTileEnvelope(mosaic, tileCoord.x, tileCoord.y);
        final CoordinateReferenceSystem tileCrs = tileEnv.getCoordinateReferenceSystem();
        final Filter filter;
        try {
            filter = buildFilter(tileEnv);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        final Polygon tileBound = JTS.toGeometry(tileEnv);

        final FeatureQuery query = QueryBuilder.reproject(source.getType(), tileCrs);
        query.setSelection(filter);

        final FeatureSet subset = source.subset(query);
        final double scale = mosaic.getScale();

        final List<Feature> features = new ArrayList<>();

        try (Stream<Feature> stream = subset.features(false)) {
            final Iterator<Feature> iterator = stream.iterator();
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                feature = FeatureExt.deepCopy(feature);

                Geometry geometry = (Geometry) feature.getPropertyValue(AttributeConvention.GEOMETRY);

                //save geometry user data and srid
                final Object userData = geometry.getUserData();
                final int srid = geometry.getSRID();

                //clip
                geometry = geometry.intersection(tileBound);

                //simplify
                TopologyPreservingSimplifier simplifier = new TopologyPreservingSimplifier(geometry);
                simplifier.setDistanceTolerance(scale);
                geometry = simplifier.getResultGeometry();

                //reset user data and srid
                geometry.setSRID(srid);
                geometry.setUserData(userData);

                feature.setPropertyValue(AttributeConvention.GEOMETRY, geometry);
                features.add(feature);
            }
        }

        final InMemoryFeatureSet fs = new InMemoryFeatureSet(subset.getType(), features);
        return new InMemoryDeferredTile(tileCoord, fs);
    }

    private Filter buildFilter(Envelope env) throws DataStoreException, TransformException {
        if (sourceCrs != null) {
            //convert envelope to data crs, more efficient
            env = Envelopes.transform(env, sourceCrs);
        }
        return FF.intersects(FF.property(AttributeConvention.GEOMETRY), FF.literal(env));
    }

    @Override
    public String toString() {
        final List<String> elements = new ArrayList<>();
        elements.add("origin : " + source.toString());
        return StringUtilities.toStringTree(this.getClass().getSimpleName(), elements);
    }

}
