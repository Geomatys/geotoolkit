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
package org.geotoolkit.osmtms.model;

import java.awt.Dimension;
import java.util.Map;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.osmtms.GetTileRequest;
import org.geotoolkit.osmtms.OSMTileMapClient;
import org.geotoolkit.data.multires.DefaultPyramid;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSPyramidSet extends CachedPyramidSet{

    public static final double BASE_TILE_SIZE = 256d;
    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;
    static {
        try {
            GOOGLE_MERCATOR = CRS.forCode("EPSG:3857");

            //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E)
            //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
            MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
            ((GeneralEnvelope)MERCATOR_EXTEND).setRange(0, -20037508.342789244d, 20037508.342789244d);
            ((GeneralEnvelope)MERCATOR_EXTEND).setRange(1, -20037508.342789244d, 20037508.342789244d);

        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    public OSMTMSPyramidSet(final OSMTileMapClient server, final int maxScale, boolean cacheImage) {
        super(server,true,cacheImage);

        final DefaultPyramid pyramid = new DefaultPyramid(GOOGLE_MERCATOR);

        final int tileWidth = (int) BASE_TILE_SIZE;
        final int tileHeight = (int) BASE_TILE_SIZE;
        final Envelope extent = MERCATOR_EXTEND;

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(GOOGLE_MERCATOR);
        upperLeft.setOrdinate(0, extent.getMinimum(0));
        upperLeft.setOrdinate(1, extent.getMaximum(1));

        final double scale0Resolution = extent.getSpan(0) / BASE_TILE_SIZE;

        for(int i=0; i<=maxScale; i++){

            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;

            final OSMTMSMosaic mosaic = new OSMTMSMosaic(
                    this, pyramid, upperLeft,
                    new Dimension(size, size),
                    new Dimension(tileWidth,tileHeight),
                    scale,
                    i);

            pyramid.getMosaicsInternal().add(mosaic);
        }

        getPyramids().add(pyramid);
    }

    @Override
    protected OSMTileMapClient getServer() {
        return (OSMTileMapClient) server;
    }

    @Override
    public Request getTileRequest(Pyramid pyramid, Mosaic mosaic, int col, int row, Map hints) throws DataStoreException {
        final GetTileRequest request = getServer().createGetTile();
        request.setScaleLevel( ((OSMTMSMosaic)mosaic).getScaleLevel() );
        request.setTileCol(col);
        request.setTileRow(row);
        return request;
    }

}
