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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel
 */
public class DefiningTileMatrixSet implements TileMatrixSet {

    private final String identifier;
    private final String format;
    private final CoordinateReferenceSystem crs;
    private final Map<String,TileMatrix> mosaics = new HashMap<>();

    public DefiningTileMatrixSet(CoordinateReferenceSystem crs) {
        this(null,null,crs,new ArrayList());
    }

    public DefiningTileMatrixSet(String identifier, String format, CoordinateReferenceSystem crs, List<TileMatrix> mosaics) {
        this.identifier = identifier;
        this.format = format;
        this.crs = crs;

        for (TileMatrix m : mosaics) {
            this.mosaics.put(m.getIdentifier(), m);
        }
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public Collection<TileMatrix> getTileMatrices() {
        return Collections.unmodifiableCollection(mosaics.values());
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public TileMatrix createTileMatrix(TileMatrix template) {
        String uid = template.getIdentifier();
        if (mosaics.containsKey(uid)) {
            uid = UUID.randomUUID().toString();
        }

        final DefiningTileMatrix m2 = new DefiningTileMatrix(uid, template.getUpperLeftCorner(),
                template.getScale(), template.getTileSize(), template.getGridSize());
        mosaics.put(m2.getIdentifier(), m2);
        return m2;
    }

    @Override
    public void deleteTileMatrix(String mosaicId) {
        mosaics.remove(mosaicId);
    }

    @Override
    public String toString(){
        return AbstractTileMatrixSet.toString(this);
    }
}
