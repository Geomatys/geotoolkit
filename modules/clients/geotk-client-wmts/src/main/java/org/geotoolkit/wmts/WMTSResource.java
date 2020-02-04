/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.wmts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.coverage.PyramidReader;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.opengis.util.GenericName;

/**
 * WMTS Coverage Reference.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSResource extends AbstractGridResource implements MultiResolutionResource, StoreResource {

    private final WebMapTileClient server;
    private final GenericName name;
    private final WMTSPyramidSet set;

    WMTSResource(WebMapTileClient server, GenericName name, boolean cacheImage){
        super(null);
        this.server = server;
        this.name = name;
        set = new WMTSPyramidSet(server, name.tip().toString(), cacheImage);
    }

    @Override
    public DataStore getOriginator() {
        return server;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    public WMTSPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return set.getPyramids();
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new PyramidReader<>(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final List<SampleDimension> sd = new ArrayList<>();

        String format = null;
        final WMTSPyramidSet ps = getPyramidSet();
        final List<LayerType> layers = ps.getCapabilities().getContents().getLayers();
        for(LayerType lt : layers){
            final String name = lt.getIdentifier().getValue();
            if(this.name.tip().equals(name)){
                final List<String> formats = lt.getFormat();
                if(formats != null && !formats.isEmpty()){
                    format = formats.get(0);
                }
            }
        }

        //last chance, use png as default
        if (format == null) {
            //set a default value
            format = "image/png";
        }

        switch (format) {
            case "image/png" :
                //4 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());
                sd.add(new SampleDimension.Builder().setName("4").build());
                break;
            default :
                //3 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());

        }
        return sd;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new PyramidReader<>(this).read(domain, range);
    }

}
