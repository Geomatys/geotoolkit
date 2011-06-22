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
package org.geotoolkit.osmtms.map;

import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractPyramidGraphic;
import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.osmtms.GetTileRequest;
import org.geotoolkit.osmtms.model.OSMTMSMosaic;

/**
 * Open Street Map Tile Map Server layer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSGraphic extends AbstractPyramidGraphic{

    private static final double SCALE_TOLERANCE = 35d;
    
    private final OSMTMSMapLayer layer;
    
    public OSMTMSGraphic(final J2DCanvas canvas, final OSMTMSMapLayer layer){
        super(canvas,OSMTMSUtilities.GOOGLE_MERCATOR, SCALE_TOLERANCE);
        this.layer = layer;
    }
    
    @Override
    protected Request createRequest(final GridMosaic mosaic, int col, int row) {
        final GetTileRequest request = layer.getServer().createGetTile();
        request.setScaleLevel( ((OSMTMSMosaic)mosaic).getScaleLevel() );
        request.setTileCol(col);
        request.setTileRow(row);        
        return request;
    }

    @Override
    protected PyramidSet getPyramidSet() {
        return layer.getServer().getPyramidSet();
    }
    
}
