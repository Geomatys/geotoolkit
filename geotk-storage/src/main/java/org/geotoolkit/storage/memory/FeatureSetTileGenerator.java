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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.util.Utilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.coordinatesequence.GridAlignedFilter;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.util.StringUtilities;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

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

    private static final FilterFactory FF = DefaultFilterFactory.forFeatures();
    private final FeatureSet source;
    private final CoordinateReferenceSystem sourceCrs;

    public FeatureSetTileGenerator(FeatureSet source) throws DataStoreException {
        this.source = source;
        sourceCrs = FeatureExt.getCRS(source.getType());
    }

    @Override
    protected boolean isEmpty(Tile tile) throws DataStoreException {
        Resource r = tile.getResource();
        FeatureSet fs = (FeatureSet) r;
        return FeatureStoreUtilities.getCount(fs) == 0l;
    }

    /**
     * Create the function responsible for geometry simplifications.
     * If the function returns a null, the feature will be discarded.
     *
     * @param tileGrid current tile being generated
     * @return function to simplify geometries.
     */
    public Function<Geometry,Geometry> buildSimplifier(GridGeometry tileGrid) {
        final double[] resolution = tileGrid.getResolution(true);
        final Envelope tileEnv = tileGrid.getEnvelope();
        final GridAlignedFilter geometryAligned = new GridAlignedFilter(tileEnv.getMinimum(0), tileEnv.getMinimum(1), resolution[0], resolution[1]);
        geometryAligned.setCreateEmpty(false);
        geometryAligned.setRemoveColinear(true);
        geometryAligned.setRemoveSpikes(true);
        return geometryAligned::alignAndSimplify;
    }

    /**
     * Apply an operation on the tile FeatureSet after it has been simplified.
     *
     * @param tileGrid current tile being generated
     * @param features tile FeatureSet
     * @return modified tile FeatureSet
     */
    public FeatureSet postSimplify(GridGeometry tileGrid, FeatureSet features) throws DataStoreException {
        return features;
    }

    @Override
    public Tile generateTile(WritableTileMatrixSet tileMatrixSet, WritableTileMatrix tileMatrix, long[] tileCoord) throws DataStoreException {

        final int[] tileSize = TileMatrices.getTileSize(tileMatrix);
        final GridGeometry tileGrid = tileMatrix.getTilingScheme().derive().subgrid(new GridExtent(null, tileCoord, tileCoord, true)).build().upsample(tileSize);
        final Envelope tileEnv = tileGrid.getEnvelope();
        final CoordinateReferenceSystem tileCrs = tileEnv.getCoordinateReferenceSystem();
        final Filter filter;
        try {
            filter = buildFilter(tileEnv);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        final Function<Geometry, Geometry> simplifier = buildSimplifier(tileGrid);

        final Polygon tileBound = JTS.toGeometry(tileEnv);

        final CoordinateReferenceSystem baseCrs = FeatureExt.getCRS(source.getType());
        final FeatureQuery query;
        if (!Utilities.equalsIgnoreMetadata(baseCrs, tileCrs)) {
            query = Query.reproject(source.getType(), tileCrs);
            query.setSelection(filter);
        } else {
            query = new FeatureQuery();
            query.setSelection(filter);
        }

        final FeatureSet subset = source.subset(query);
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
                geometry = simplifier.apply(geometry);

                if (geometry != null) {
                    //reset user data and srid
                    geometry.setSRID(srid);
                    geometry.setUserData(userData);

                    feature.setPropertyValue(AttributeConvention.GEOMETRY, geometry);
                    features.add(feature);
                }
            }
        }

        FeatureSet fs = new InMemoryFeatureSet(subset.getType(), features);
        fs = postSimplify(tileGrid, fs);
        return new InMemoryDeferredTile(tileCoord, fs);
    }

    private Filter buildFilter(Envelope env) throws DataStoreException, TransformException {
        if (sourceCrs != null) {
            //convert envelope to data crs, more efficient
            env = Envelopes.transform(env, sourceCrs);
        }
        return FF.bbox(FF.property(AttributeConvention.GEOMETRY), env);
    }

    @Override
    public String toString() {
        final List<String> elements = new ArrayList<>();
        elements.add("origin : " + source.toString());
        return StringUtilities.toStringTree(this.getClass().getSimpleName(), elements);
    }

}
