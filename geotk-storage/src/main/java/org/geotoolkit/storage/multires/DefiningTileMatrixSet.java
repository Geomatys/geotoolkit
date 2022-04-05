/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import org.geotoolkit.util.NamesExt;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel
 */
public class DefiningTileMatrixSet implements WritableTileMatrixSet {

    private final GenericName identifier;
    private final CoordinateReferenceSystem crs;
    private final ScaleSortedMap<WritableTileMatrix> matrices = new ScaleSortedMap<>();

    public DefiningTileMatrixSet(CoordinateReferenceSystem crs) {
        this(null,crs,new ArrayList());
    }

    public DefiningTileMatrixSet(GenericName identifier, CoordinateReferenceSystem crs, List<TileMatrix> mosaics) {
        this.identifier = identifier;
        this.crs = crs;

        for (TileMatrix m : mosaics) {
            createTileMatrix(m);
        }
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public SortedMap<GenericName, WritableTileMatrix> getTileMatrices() {
        return Collections.unmodifiableSortedMap(matrices);
    }

    @Override
    public GenericName getIdentifier() {
        return identifier;
    }

    @Override
    public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix template) {
        GenericName uid = template.getIdentifier();
        if (matrices.containsKey(uid)) {
            uid = NamesExt.createRandomUUID();
        }
        final DefiningTileMatrix m2 = new DefiningTileMatrix(uid, template.getTilingScheme(), ((TileMatrix)template).getTileSize());
        matrices.insertByScale(m2);
        return m2;
    }

    @Override
    public void deleteTileMatrix(String matrixId) {
        for (WritableTileMatrix tm : matrices.values()) {
            if (tm.getIdentifier().toString().equals(matrixId)) {
                matrices.removeByScale(tm);
                break;
            }
        }
    }

    @Override
    public String toString(){
        return AbstractTileMatrixSet.toString(this);
    }
}
