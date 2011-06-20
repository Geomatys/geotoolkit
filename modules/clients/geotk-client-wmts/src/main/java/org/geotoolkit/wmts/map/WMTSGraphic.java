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
package org.geotoolkit.wmts.map;

import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractPyramidGraphic;
import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.wmts.GetTileRequest;
import org.geotoolkit.wmts.model.WMTSMosaic;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.geotoolkit.wmts.xml.v100.ContentsType;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.geotoolkit.wmts.xml.v100.Style;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSGraphic extends AbstractPyramidGraphic{

    private static final double SCALE_TOLERANCE = 35d;
    
    private final WMTSMapLayer layer;
    private LayerType wmtsLayer;
    
    public WMTSGraphic(final J2DCanvas canvas, final WMTSMapLayer layer){
        super(canvas,layer.getBounds().getCoordinateReferenceSystem(), SCALE_TOLERANCE);
        this.layer = layer;
        
        final ContentsType contents = layer.getServer().getCapabilities().getContents();
        
        //first find the layer
        wmtsLayer = null;
        for(LayerType candidate : contents.getLayers()){            
            if(layer.getLayer().equalsIgnoreCase(candidate.getIdentifier().getValue())){
                wmtsLayer = candidate;
                break;
            }            
        }
    }
    
    @Override
    protected Request createRequest(final GridMosaic mosaic, int col, int row) {
        final WMTSMosaic wmtsMosaic = (WMTSMosaic) mosaic;
        
        final GetTileRequest request = layer.getServer().createGetTile();
        request.setFormat(layer.getFormat());
        request.setLayer(layer.getLayer());
        request.setTileCol(col);
        request.setTileRow(row);
        request.setTileMatrix(wmtsMosaic.getMatrix().getIdentifier().getValue());
        request.setTileMatrixSet(wmtsMosaic.getPyramid().getMatrixset().getIdentifier().getValue());
        
        //find the style
        String style = layer.getTileSetStyle();
        if(style == null){
            //get the default style
            for(Style st : wmtsLayer.getStyle()){
                if(style == null){
                    style = st.getIdentifier().getValue();
                }
                if(st.isIsDefault()){
                    break;
                }
            }
        }        
        request.setStyle(style);
        return request;
    }

    @Override
    protected PyramidSet getPyramidSet() {
        return new WMTSPyramidSet(layer.getServer().getCapabilities(), layer.getLayer());
    }
    
}
