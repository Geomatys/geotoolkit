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

import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.ProcessListener;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TileGenerator {

    /**
     * Create a single tile.
     * Create tile is not stored in the tile matrixset.
     *
     * @param tileMatrixSet tileMatrixSet tiles to generate
     * @param tileMatrix tileMatrix tiles to generate
     * @param tileCoord tile coordinate to generate
     * @return created tile
     * @throws DataStoreException
     */
    Tile generateTile(WritableTileMatrixSet tileMatrixSet, WritableTileMatrix tileMatrix, long[] tileCoord) throws DataStoreException;

    /**
     * Generate given box of data.
     * Create tiles will be stored in the tile matrixset.
     *
     * @param tileMatrixSet tileMatrixSet tiles to generate
     * @param env Envelope to generate, null for all tiles.
     * @param resolutions resolution range in which to generate tiles,
     *      only tileMatrix within range will be generated, null for all tile matrices.
     *      Resolution range is in given envelope CRS, is envelope is null, resolution range is assumed to be in tileMatrixSet CRS.
     * @param listener progess listener, can be null.
     * @throws DataStoreException
     * @throws java.lang.InterruptedException
     */
    void generate(WritableTileMatrixSet tileMatrixSet, Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException, InterruptedException;

}
