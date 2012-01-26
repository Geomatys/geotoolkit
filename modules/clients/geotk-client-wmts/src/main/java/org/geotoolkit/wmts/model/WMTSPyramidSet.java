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
package org.geotoolkit.wmts.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.GetTileRequest;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.wmts.xml.v100.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSPyramidSet extends CachedPyramidSet{

    private final WebMapTileServer server;
    private final String layerName;
    private final String style;
    private final String id = UUID.randomUUID().toString();
    private LayerType wmtsLayer;
    
    public WMTSPyramidSet(final WebMapTileServer server, final String layerName, final String style){
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layerName);
        this.server = server;
        this.layerName = layerName;
        this.style = style;
        
        
        //find the wmts layer
        final ContentsType contents = server.getCapabilities().getContents();
        wmtsLayer = null;
        for(LayerType candidate : contents.getLayers()){            
            if(layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())){
                wmtsLayer = candidate;
                break;
            }            
        }
    }
    
    public Capabilities getCapabilities() {
        return server.getCapabilities();
    }

    public String getLayerName() {
        return layerName;
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Collection<Pyramid> getPyramids() {        
        final List<Pyramid> pyramids = new ArrayList<Pyramid>();
        
        final ContentsType contents = server.getCapabilities().getContents();
        
        //first find the layer
        LayerType layer = null;
        for(LayerType candidate : contents.getLayers()){            
            if(layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())){
                layer = candidate;
                break;
            }            
        }
        
        if(layer == null){
            //layer not found
            return pyramids;
        }
        
        for(TileMatrixSetLink lk : layer.getTileMatrixSetLink()){
            pyramids.add(new WMTSPyramid(this,lk));
        }
        
        return pyramids;
    }

    @Override
    protected InputStream download(GridMosaic mosaic, String mimeType, int col, int row) throws DataStoreException {
        final WMTSMosaic wmtsMosaic = (WMTSMosaic) mosaic;
        
        final GetTileRequest request = server.createGetTile();
        request.setFormat(mimeType);
        request.setLayer(layerName);
        request.setTileCol(col);
        request.setTileRow(row);
        request.setTileMatrix(wmtsMosaic.getMatrix().getIdentifier().getValue());
        request.setTileMatrixSet(wmtsMosaic.getPyramid().getMatrixset().getIdentifier().getValue());
        
        //find the style
        String style = this.style;
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
        
        try {
            return request.getResponseStream();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
    
}
