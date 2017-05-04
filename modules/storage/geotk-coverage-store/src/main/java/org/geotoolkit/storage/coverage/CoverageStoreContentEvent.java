/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.awt.Point;
import java.util.List;
import org.geotoolkit.storage.StorageEvent;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageStoreContentEvent extends StorageEvent {

    public static enum Type{
        /** unspecific data change */
        DATA_UPDATE,
        /** tiles added */
        TILE_ADD,
        /** tiles updated */
        TILE_UPDATE,
        /** tiles deleted */
        TILE_DELETE
    };

    private final Type type;
    private final GenericName coverageName;
    private final String pyramidId;
    private final String mosaicId;
    private final List<Point> tiles;


    public CoverageStoreContentEvent(Object source, Type type,
            GenericName name, String pyramidId, String mosaicId, List<Point> tiles) {
        super(source);
        this.type = type;
        this.coverageName = name;
        this.pyramidId = pyramidId;
        this.mosaicId = mosaicId;
        this.tiles = tiles;
    }

    public Type getType() {
        return type;
    }

    public GenericName getCoverageName() {
        return coverageName;
    }

    public String getPyramidId() {
        return pyramidId;
    }

    public String getMosaicId() {
        return mosaicId;
    }

    public List<Point> getTiles() {
        return tiles;
    }

    @Override
    public CoverageStoreContentEvent copy(final Object source){
        return new CoverageStoreContentEvent(source, type, coverageName, pyramidId, mosaicId, tiles);
    }

    public static CoverageStoreContentEvent createDataUpdateEvent(
            final Object source, final GenericName name){
        return new CoverageStoreContentEvent(source, Type.DATA_UPDATE, name, null, null,null);
    }

    public static CoverageStoreContentEvent createTileAddEvent(final Object source,
            final GenericName name, final String pyramidId, final String mosaicId, final List<Point> tiles){
        return new CoverageStoreContentEvent(source, Type.TILE_ADD, name, pyramidId, mosaicId, tiles);
    }

    public static CoverageStoreContentEvent createTileUpdateEvent(final Object source,
            final GenericName name, final String pyramidId, final String mosaicId, final List<Point> tiles){
        return new CoverageStoreContentEvent(source, Type.TILE_UPDATE, name, pyramidId, mosaicId, tiles);
    }

    public static CoverageStoreContentEvent createTileDeleteEvent(final Object source,
            final GenericName name, final String pyramidId, final String mosaicId, final List<Point> tiles){
        return new CoverageStoreContentEvent(source, Type.TILE_DELETE, name, pyramidId, mosaicId, tiles);
    }

}
