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
package org.geotoolkit.tms.model;

import java.awt.Dimension;
import java.util.Map;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedTileMatrixSets;
import org.geotoolkit.storage.multires.DefaultTileMatrixSet;
import org.geotoolkit.tms.GetTileRequest;
import org.geotoolkit.tms.TileMapClient;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSTileMatrixSets extends CachedTileMatrixSets {

    public static final double BASE_TILE_SIZE = 256d;
    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;

    static {
        try {
            GOOGLE_MERCATOR = CRS.forCode("EPSG:3857");

            //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E)
            //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
            MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
            ((GeneralEnvelope) MERCATOR_EXTEND).setRange(0, -20037508.342789244d, 20037508.342789244d);
            ((GeneralEnvelope) MERCATOR_EXTEND).setRange(1, -20037508.342789244d, 20037508.342789244d);

        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    public TMSTileMatrixSets(final TileMapClient server, final int maxScale, boolean cacheImage) {
        super(server, true, cacheImage);

        final DefaultTileMatrixSet pyramid = new DefaultTileMatrixSet(GOOGLE_MERCATOR);

        final int tileWidth = (int) BASE_TILE_SIZE;
        final int tileHeight = (int) BASE_TILE_SIZE;
        final Envelope extent = MERCATOR_EXTEND;

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(GOOGLE_MERCATOR);
        upperLeft.setOrdinate(0, extent.getMinimum(0));
        upperLeft.setOrdinate(1, extent.getMaximum(1));

        final double scale0Resolution = extent.getSpan(0) / BASE_TILE_SIZE;

        for (int i = 0; i <= maxScale; i++) {

            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;

            final TMSTileMatrix mosaic = new TMSTileMatrix(
                    this, pyramid, upperLeft,
                    new Dimension(size, size),
                    new int[]{tileWidth, tileHeight},
                    scale,
                    i);

            pyramid.getMosaicsInternal().insertByScale(mosaic);
        }

        getTileMatrixSets().add(pyramid);
    }

    @Override
    protected TileMapClient getServer() {
        return (TileMapClient) server;
    }

    @Override
    public Request getTileRequest(TileMatrixSet pyramid, TileMatrix mosaic, long[] indices, Map hints) throws DataStoreException {
        final GetTileRequest request = getServer().createGetTile();
        request.setScaleLevel(((TMSTileMatrix) mosaic).getScaleLevel());
        request.setTileCol(indices[0]);
        request.setTileRow(indices[1]);
        return request;
    }

}
