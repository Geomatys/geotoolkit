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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedTileMatrixSets;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.wmts.GetTileRequest;
import org.geotoolkit.wmts.WebMapTileClient;
import org.geotoolkit.wmts.xml.v100.*;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSTileMatrixSets extends CachedTileMatrixSets {

    /**
     * Additional hint : to specify the style.
     */
    public static final String HINT_STYLE = "style";

    private final String layerName;
    private LayerType wmtsLayer;
    private Collection<TileMatrixSet> pyramids;

    public WMTSTileMatrixSets(final WebMapTileClient server, final String layerName, boolean cacheImage) {
        super(server,true,cacheImage);
        ArgumentChecks.ensureNonNull("layer name", layerName);
        this.layerName = layerName;

        //find the wmts layer
        final ContentsType contents = server.getServiceCapabilities().getContents();
        wmtsLayer = null;
        for (LayerType candidate : contents.getLayers()) {
            if (layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())) {
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
        return getServer().getServiceCapabilities();
    }

    public String getLayerName() {
        return layerName;
    }

    @Override
    public synchronized Collection<TileMatrixSet> getTileMatrixSets() {
        if (pyramids == null) {
            final List<TileMatrixSet> pyramids = new ArrayList<>();
            final ContentsType contents = getServer().getServiceCapabilities().getContents();

            //first find the layer
            LayerType layer = null;
            for (LayerType candidate : contents.getLayers()) {
                if (layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())) {
                    layer = candidate;
                    break;
                }
            }

            if (layer != null) {
                //layer found
                for (TileMatrixSetLink lk : layer.getTileMatrixSetLink()) {
                    pyramids.add(new WMTSTileMatrixSet(this,lk));
                }
            }
            this.pyramids = pyramids;
        }

        return pyramids;
    }

    @Override
    public Request getTileRequest(TileMatrixSet pyramid, TileMatrix mosaic, long col, long row, Map hints) throws DataStoreException {
        final WMTSTileMatrix wmtsMosaic = (WMTSTileMatrix) mosaic;

        if (hints == null) hints = new HashMap();

        final GetTileRequest request = getServer().createGetTile();

        //set the format
        Object format = hints.get(TileMatrices.HINT_FORMAT);

        //extract the default format from server
        if (format == null) {
            final WMTSTileMatrixSets ps = ((WMTSTileMatrixSet) pyramid).getPyramidSet();
            final List<LayerType> layers = ps.getCapabilities().getContents().getLayers();
            for (LayerType lt : layers) {
                final String name = lt.getIdentifier().getValue();
                if (layerName.equals(name)) {
                    final List<String> formats = lt.getFormat();
                    if (formats != null && !formats.isEmpty()) {
                        format = formats.get(0);
                    }
                }
            }
        }

        //last chance, use png as default
        if (format == null) {
            //set a default value
            format = "image/png";
        }
        request.setFormat(format.toString());

        request.setLayer(layerName);
        request.setTileCol(col);
        request.setTileRow(row);
        request.setTileMatrix(wmtsMosaic.getMatrix().getIdentifier().getValue());
        request.setTileMatrixSet(wmtsMosaic.getPyramid().getMatrixset().getIdentifier().getValue());

        //search if there is a resource url
        final List<URLTemplateType> resourceURLs = wmtsLayer.getResourceURL();
        for (URLTemplateType template : resourceURLs) {
            if (format.equals(template.getFormat()) && "tile".equals(template.getResourceType())) {
                request.setResourceURL(template.getTemplate());
                break;
            }
        }

        //set the style
        Object style = hints.get(HINT_STYLE);
        if (style == null || !(style instanceof String)) {
            //get the default style
            for (Style st : wmtsLayer.getStyle()) {
                if (style == null) {
                    style = st.getIdentifier().getValue();
                }
                if (st.isIsDefault()) {
                    break;
                }
            }
        }

        if (style != null) {
            request.setStyle(style.toString());
        }
        return request;
    }

}
