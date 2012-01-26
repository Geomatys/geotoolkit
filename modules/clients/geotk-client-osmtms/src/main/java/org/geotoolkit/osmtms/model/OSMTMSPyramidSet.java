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
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.DefaultPyramid;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.osmtms.GetTileRequest;
import org.geotoolkit.osmtms.OSMTileMapServer;
import org.geotoolkit.osmtms.map.OSMTMSUtilities;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSPyramidSet extends CachedPyramidSet{

    private final OSMTileMapServer server;
    
    public OSMTMSPyramidSet(final OSMTileMapServer server, final int maxScale) {
        this.server = server;
        
        final DefaultPyramid pyramid = new DefaultPyramid(this,OSMTMSUtilities.GOOGLE_MERCATOR);
        
        final int tileWidth = (int) OSMTMSUtilities.BASE_TILE_SIZE;        
        final int tileHeight = (int) OSMTMSUtilities.BASE_TILE_SIZE;        
        final Envelope extent = OSMTMSUtilities.MERCATOR_EXTEND;
                
        final Point2D upperLeft = new Point2D.Double(extent.getMinimum(0), extent.getMaximum(1));    
        
        final double scale0Resolution = extent.getSpan(0) / OSMTMSUtilities.BASE_TILE_SIZE;
        
        for(int i=0; i<=maxScale; i++){
            
            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;
            
            final OSMTMSMosaic mosaic = new OSMTMSMosaic(
                    pyramid, upperLeft, 
                    new Dimension(size, size), 
                    new Dimension(tileWidth,tileHeight),
                    scale,
                    i);
            
            pyramid.getMosaics().put(scale, mosaic);
        }
        
        getPyramids().add(pyramid);        
    }

    @Override
    protected InputStream download(GridMosaic mosaic, String mimeType, int col, int row) throws DataStoreException {
        final GetTileRequest request = server.createGetTile();
        request.setScaleLevel( ((OSMTMSMosaic)mosaic).getScaleLevel() );
        request.setTileCol(col);
        request.setTileRow(row);        
        try {
            return request.getResponseStream();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

}
