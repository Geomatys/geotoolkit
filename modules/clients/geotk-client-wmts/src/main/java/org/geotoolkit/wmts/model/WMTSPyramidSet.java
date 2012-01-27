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
import java.util.*;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
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

    /**
     * Additional hint : to specify the style.
     */
    public static final String HINT_STYLE = "style";
    
    private final WebMapTileServer server;
    private final String layerName;
    private final String id = UUID.randomUUID().toString();
    private LayerType wmtsLayer;
    
    public WMTSPyramidSet(final WebMapTileServer server, final String layerName){
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layerName);
        this.server = server;
        this.layerName = layerName;
                
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
    protected InputStream download(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException {
        final WMTSMosaic wmtsMosaic = (WMTSMosaic) mosaic;
        
        final GetTileRequest request = server.createGetTile();
        
        //set the format
        Object format = hints.get(PyramidSet.HINT_FORMAT);
        if(format == null){
            //set a default value
            format = "image/png";
        }        
        request.setFormat(format.toString());
        
        request.setLayer(layerName);
        request.setTileCol(col);
        request.setTileRow(row);
        request.setTileMatrix(wmtsMosaic.getMatrix().getIdentifier().getValue());
        request.setTileMatrixSet(wmtsMosaic.getPyramid().getMatrixset().getIdentifier().getValue());
        
        //set the style
        Object style = hints.get(HINT_STYLE);
        if(style == null || !(style instanceof String)){
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
        
        if(style != null){
            request.setStyle(style.toString());
        }
        
        try {
            return request.getResponseStream();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
        
}
