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
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TiledResource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.opengis.util.GenericName;

/**
 * WMTS Coverage Resource.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
public final class WMTSResource extends AbstractGridCoverageResource implements TiledResource, StoreResource {

    private final WebMapTileClient store;
    private final GenericName name;
    private final WMTSTileMatrixSets set;

    WMTSResource(WebMapTileClient store, String layerName) {
        super(null, false);
        this.store = store;
        this.name = Names.createLocalName(null, null, layerName);
        this.set = new WMTSTileMatrixSets(store, layerName);
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.of(name);
    }

    @Override
    public Collection<TileMatrixSet> getTileMatrixSets() {
        return set.getTileMatrixSets();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final List<SampleDimension> sd = new ArrayList<>();

        String format = null;
        final LayerType layer;
        try {
            layer = store.getServiceCapabilities().getContents().getLayers().stream()
                    .filter(lt -> name.tip().toString().equals(lt.getIdentifier().getValue()))
                    .findFirst().orElse(null);
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }
        if (layer != null) {
            final List<String> formats = layer.getFormat();
            if (formats != null && !formats.isEmpty()) {
                format = formats.get(0);
            }
        }

        //last chance, use png as default
        if (format == null) {
            format = "image/png";
        }

        switch (format) {
            case "image/png":
                //4 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());
                sd.add(new SampleDimension.Builder().setName("4").build());
                break;
            default:
                //3 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());
        }
        return sd;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).read(domain, range);
    }

}
