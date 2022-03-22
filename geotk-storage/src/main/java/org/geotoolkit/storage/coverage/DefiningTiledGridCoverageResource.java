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
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.TileFormat;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.geotoolkit.storage.multires.WritableTiledResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningTiledGridCoverageResource extends DefiningGridCoverageResource implements WritableTiledResource {

    public final Map<String,WritableTileMatrixSet> models = new HashMap<>();
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
    public Collection<WritableTileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return Collections.unmodifiableCollection(models.values());
    }

    @Override
    public WritableTileMatrixSet createTileMatrixSet(TileMatrixSet template) throws DataStoreException {
        GenericName id = template.getIdentifier();
        if (id == null) {
            //create a unique id
            id = NamesExt.createRandomUUID();
        } else if (models.containsKey(id.toString())) {
            //change id to avoid overriding an existing pyramid
            id = NamesExt.createRandomUUID();
        }
        DefiningTileMatrixSet cp = new DefiningTileMatrixSet(id, template.getCoordinateReferenceSystem(), new ArrayList<>());
        TileMatrices.copyStructure(template, cp);
        models.put(id.toString(), cp);
        return cp;
    }

    @Override
    public void deleteTileMatrixSet(String identifier) throws DataStoreException {
        models.remove(identifier);
    }

}
