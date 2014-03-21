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

import java.util.*;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.wmts.GetTileRequest;
import org.geotoolkit.wmts.WebMapTileClient;
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

    private final String layerName;
    private final String id = UUID.randomUUID().toString();
    private LayerType wmtsLayer;
    private Collection<Pyramid> pyramids;

    public WMTSPyramidSet(final WebMapTileClient server, final String layerName, boolean cacheImage){
        super(server,true,cacheImage);
        ArgumentChecks.ensureNonNull("layer name", layerName);
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

    @Override
    protected WebMapTileClient getServer() {
        return (WebMapTileClient)super.getServer();
    }

    public Capabilities getCapabilities() {
        return getServer().getCapabilities();
    }

    public String getLayerName() {
        return layerName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized Collection<Pyramid> getPyramids() {
        if(pyramids == null){
            final List<Pyramid> pyramids = new ArrayList<Pyramid>();
            final ContentsType contents = getServer().getCapabilities().getContents();

            //first find the layer
            LayerType layer = null;
            for(LayerType candidate : contents.getLayers()){
                if(layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())){
                    layer = candidate;
                    break;
                }
            }

            if(layer != null){
                //layer found
                for(TileMatrixSetLink lk : layer.getTileMatrixSetLink()){
                    pyramids.add(new WMTSPyramid(this,lk));
                }
            }
            this.pyramids = pyramids;
        }

        return pyramids;
    }

    @Override
    public Request getTileRequest(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException {
        final WMTSMosaic wmtsMosaic = (WMTSMosaic) mosaic;

        if(hints == null) hints = new HashMap();

        final GetTileRequest request = getServer().createGetTile();

        //set the format
        Object format = hints.get(PyramidSet.HINT_FORMAT);

        //extract the default format from server
        if(format == null){
            final WMTSPyramidSet ps = (WMTSPyramidSet) mosaic.getPyramid().getPyramidSet();
            final List<LayerType> layers = ps.getCapabilities().getContents().getLayers();
            for(LayerType lt : layers){
                final String name = lt.getIdentifier().getValue();
                if(layerName.equals(name)){
                    final List<String> formats = lt.getFormat();
                    if(formats != null && !formats.isEmpty()){
                        format = formats.get(0);
                    }
                }
            }
        }

        //last chance, use png as default
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
        return request;
    }

}
