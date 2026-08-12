/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedTileMatrixSets;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.tile.GetTile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.wmts.xml.v100.ContentsType;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.geotoolkit.wmts.xml.v100.Style;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLink;
import org.geotoolkit.wmts.xml.v100.URLTemplateType;

/**
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
final class WMTSTileMatrixSets extends CachedTileMatrixSets {

    private final String layerName;
    private Collection<TileMatrixSet> pyramids;

    WMTSTileMatrixSets(final WebMapTileClient server, final String layerName) {
        super(server, true, false);
        this.layerName = layerName;
    }

    @Override
    protected WebMapTileClient getServer() {
        return (WebMapTileClient) super.getServer();
    }

    private LayerType findLayer(ContentsType contents) {
        for (LayerType candidate : contents.getLayers()) {
            if (layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public synchronized Collection<TileMatrixSet> getTileMatrixSets() {
        if (pyramids == null) {
            final ContentsType contents;
            try {
                contents = getServer().getServiceCapabilities().getContents();
            } catch (CapabilitiesException ex) {
                throw new UncheckedServiceException(ex);
            }

            final List<TileMatrixSet> list = new ArrayList<>();
            final LayerType layer = findLayer(contents);
            if (layer != null) {
                for (TileMatrixSetLink link : layer.getTileMatrixSetLink()) {
                    list.add(new WMTSTileMatrixSet(this, contents, link));
                }
            }
            pyramids = list;
        }
        return pyramids;
    }

    @Override
    public Request getTileRequest(TileMatrixSet pyramid, TileMatrix mosaic, long[] indices, Map hints) throws DataStoreException {
        final WMTSTileMatrix wmtsMosaic = (WMTSTileMatrix) mosaic;
        if (hints == null) hints = new HashMap();

        final ContentsType contents;
        try {
            contents = getServer().getServiceCapabilities().getContents();
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }
        final LayerType layer = findLayer(contents);

        //set the format
        Object format = hints.get(TileMatrices.HINT_FORMAT);
        if (format == null && layer != null) {
            final List<String> formats = layer.getFormat();
            if (formats != null && !formats.isEmpty()) {
                format = formats.get(0);
            }
        }
        //last chance, use png as default
        if (format == null) {
            format = "image/png";
        }

        final GetTile params = new GetTile();
        params.setFormat(format.toString());
        params.setLayer(layerName);
        params.setTileCol((long) indices[0]);
        params.setTileRow((long) indices[1]);
        params.setTileMatrix(wmtsMosaic.getMatrix().getIdentifier().getValue());
        params.setTileMatrixSet(wmtsMosaic.getPyramid().getMatrixset().getIdentifier().getValue());

        //search if there is a resource url
        String resourceUrlTemplate = null;
        if (layer != null) {
            for (URLTemplateType template : layer.getResourceURL()) {
                if (format.equals(template.getFormat()) && "tile".equals(template.getResourceType())) {
                    resourceUrlTemplate = template.getTemplate();
                    break;
                }
            }

            //set the default style
            for (Style st : layer.getStyle()) {
                if (params.getStyle() == null) {
                    params.setStyle(st.getIdentifier().getValue());
                }
                if (st.isIsDefault()) {
                    break;
                }
            }
        }

        return getServer().getApi().getTileRequest(params, resourceUrlTemplate);
    }

}
