/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2018, Geomatys
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

import java.util.Collections;
import java.util.SortedMap;
import org.apache.sis.storage.DataStoreException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Default pyramid
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTileMatrixSet extends AbstractTileMatrixSet {

    private final ScaleSortedMap<TileMatrix> mosaics = new ScaleSortedMap<>();

    public DefaultTileMatrixSet(CoordinateReferenceSystem crs) {
        this(null,crs);
    }

    public DefaultTileMatrixSet(GenericName id, CoordinateReferenceSystem crs) {
        super(id,crs);
    }

    /**
     * Internal list of pyramids, modify with causion.
     */
    public ScaleSortedMap<TileMatrix> getMosaicsInternal() {
        return mosaics;
    }

    @Override
    public SortedMap<GenericName, ? extends TileMatrix> getTileMatrices() {
        return Collections.unmodifiableSortedMap(mosaics);
    }


    public static abstract class Writable extends DefaultTileMatrixSet implements WritableTileMatrixSet {

        public Writable(CoordinateReferenceSystem crs) {
            this(null,crs);
        }

        public Writable(GenericName id, CoordinateReferenceSystem crs) {
            super(id,crs);
        }

        @Override
        public SortedMap<GenericName,? extends WritableTileMatrix> getTileMatrices() {
            return (SortedMap<GenericName,? extends WritableTileMatrix>) super.getTileMatrices();
        }

        @Override
        public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix template) throws DataStoreException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
