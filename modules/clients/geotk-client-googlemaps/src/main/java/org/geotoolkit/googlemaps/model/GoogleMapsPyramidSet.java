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
package org.geotoolkit.googlemaps.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.DefaultPyramid;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.StaticGoogleMapsServer;
import org.geotoolkit.googlemaps.map.GoogleMapsUtilities;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsPyramidSet extends CachedPyramidSet{

    private final StaticGoogleMapsServer server;
    private final String mapType;
    
    public GoogleMapsPyramidSet(final StaticGoogleMapsServer server, final String mapType) {
        
        this.server = server;
        this.mapType = mapType;
        
        final int maxScale;        
        if (GetMapRequest.TYPE_HYBRID.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_ROADMAP.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_SATELLITE.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_TERRAIN.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else {
            throw new IllegalArgumentException("Unknowned google maps layer : " + mapType);
        }
        
        
        final DefaultPyramid pyramid = new DefaultPyramid(this, GoogleMapsUtilities.GOOGLE_MERCATOR);
        
        final int tileWidth = (int) GoogleMapsUtilities.BASE_TILE_SIZE;        
        final int tileHeight = (int) GoogleMapsUtilities.BASE_TILE_SIZE;        
        final Envelope extent = GoogleMapsUtilities.MERCATOR_EXTEND;
        final Point2D upperLeft = new Point2D.Double(extent.getMinimum(0), extent.getMaximum(1));        
        final double scale0Resolution = extent.getSpan(0) / GoogleMapsUtilities.BASE_TILE_SIZE;
        
        for(int i=0; i<=maxScale; i++){
            
            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;
            
            final GoogleMapsMosaic mosaic = new GoogleMapsMosaic(
                    pyramid, upperLeft, 
                    new Dimension(size,size),
                    new Dimension(tileHeight, tileWidth), 
                    scale,
                    i);
            
            pyramid.getMosaics().put(scale, mosaic);
        }
        
        getPyramids().add(pyramid);    
    }
    
    @Override
    protected InputStream download(GridMosaic mosaic, String mimeType, int col, int row) throws DataStoreException {
        final int zoom = ((GoogleMapsMosaic)mosaic).getScaleLevel();
        
        final GetMapRequest request = server.createGetMap();
        request.setFormat(mimeType);
        request.setMapType(mapType);
        request.setDimension(new Dimension(GoogleMapsUtilities.BASE_TILE_SIZE, GoogleMapsUtilities.BASE_TILE_SIZE));
        request.setZoom(zoom);

        final DirectPosition position = GoogleMapsUtilities.getCenter(zoom, col, row);
        request.setCenter(position);
        try {
            return request.getResponseStream();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
    
}
