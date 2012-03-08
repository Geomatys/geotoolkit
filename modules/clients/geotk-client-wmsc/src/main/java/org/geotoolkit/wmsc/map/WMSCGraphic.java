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
package org.geotoolkit.wmsc.map;

import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractPyramidGraphic;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wmsc.WebMapServerCached;
import org.geotoolkit.wmsc.model.WMSCPyramid;
import org.geotoolkit.wmsc.model.WMSCPyramidSet;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCGraphic extends AbstractPyramidGraphic{
    
    private static final double SCALE_TOLERANCE = 35d;
    
    private final WMSCMapLayer layer;
    private final PyramidSet pyramidset;

    public WMSCGraphic(final J2DCanvas canvas, final WMSCMapLayer layer) throws CapabilitiesException{
        super(canvas,layer.getBounds().getCoordinateReferenceSystem(), SCALE_TOLERANCE);
        this.layer = layer;
        setSilentErrors(true);
        
        pyramidset = new WMSCPyramidSet((WebMapServerCached)layer.getServer(), layer.getLayerNames()[0],true);
    }
    
    @Override
    protected PyramidSet getPyramidSet() {
        return pyramidset;
    }

    @Override
    protected Request createRequest(final GridMosaic mosaic, final int col, final int row) {
        final GetMapRequest request = layer.getServer().createGetMap();
        request.setLayers(layer.getCombinedLayerNames());
        request.setEnvelope(mosaic.getEnvelope(col, row));
        request.setDimension(mosaic.getTileSize());
        request.setFormat(((WMSCPyramid)mosaic.getPyramid()).getTileset().getFormat());
        return request;
    }
    
}
