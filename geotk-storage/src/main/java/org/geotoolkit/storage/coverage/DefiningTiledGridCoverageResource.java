/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.TileFormat;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TiledResource;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningTiledGridCoverageResource extends DefiningGridCoverageResource implements TiledResource {

    public final Map<String,TileMatrixSet> models = new HashMap<>();
    private TileFormat tileFormat;

    public DefiningTiledGridCoverageResource(GenericName identifier) {
        super(identifier);
    }

    public void setTileFormat(TileFormat tileFormat) {
        ArgumentChecks.ensureNonNull(null, tileFormat);
        this.tileFormat = tileFormat;
    }

    @Override
    public TileFormat getTileFormat() {
        return tileFormat;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        GridGeometry gridGeometry = super.getGridGeometry();
        if (gridGeometry == null) {
            gridGeometry = new TileMatrixSetCoverageReader(this).getGridGeometry();
        }
        return gridGeometry;
    }

    @Override
    public Collection<TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return Collections.unmodifiableCollection(models.values());
    }

    @Override
    public TileMatrixSet createTileMatrixSet(TileMatrixSet template) throws DataStoreException {
        String id = template.getIdentifier();
        if (id == null) {
            //create a unique id
            id = UUID.randomUUID().toString();
        } else if (models.containsKey(id)) {
            //change id to avoid overriding an existing pyramid
            id = UUID.randomUUID().toString();
        }
        DefiningTileMatrixSet cp = new DefiningTileMatrixSet(id, template.getFormat(), template.getCoordinateReferenceSystem(), new ArrayList<>());
        TileMatrices.copyStructure(template, cp);
        models.put(id, cp);
        return cp;
    }

    @Override
    public void removeTileMatrixSet(String identifier) throws DataStoreException {
        models.remove(identifier);
    }

}
