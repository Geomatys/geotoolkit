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
package org.geotoolkit.googlemaps.map;

import java.awt.Dimension;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractPyramidGraphic;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.model.GoogleMapsMosaic;
import org.geotoolkit.googlemaps.model.GoogleMapsPyramidSet;
import org.opengis.geometry.DirectPosition;

/**
 * Google Maps static api graphic.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsGraphic extends AbstractPyramidGraphic{

    private static final double SCALE_TOLERANCE = 35d;
    
    private final GoogleMapsMapLayer layer;
    
    public GoogleMapsGraphic(final J2DCanvas canvas, final GoogleMapsMapLayer layer){
        super(canvas,layer.getBounds().getCoordinateReferenceSystem(), SCALE_TOLERANCE);
        this.layer = layer;        
    }
    
    @Override
    protected Request createRequest(final GridMosaic mosaic, int col, int row) {
        
        final int zoom = ((GoogleMapsMosaic)mosaic).getScaleLevel();
        
        final GetMapRequest request = layer.getServer().createGetMap();
        request.setFormat(layer.getFormat());
        request.setMapType(layer.getMapType());
        request.setDimension(new Dimension(GoogleMapsUtilities.BASE_TILE_SIZE, GoogleMapsUtilities.BASE_TILE_SIZE));
        request.setZoom(zoom);

        final DirectPosition position = GoogleMapsUtilities.getCenter(zoom, col, row);
        request.setCenter(position);
        return request;
    }

    @Override
    protected PyramidSet getPyramidSet() {
        return new GoogleMapsPyramidSet(layer.getServer(),layer.getMapType());
    }
    
}
