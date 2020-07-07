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

import java.awt.Point;
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
     * Create tile is not stored in the pyramid.
     *
     * @param pyramid
     * @param mosaic
     * @param tileCoord
     * @return
     * @throws DataStoreException
     */
    Tile generateTile(TileMatrixSet pyramid, TileMatrix mosaic, Point tileCoord) throws DataStoreException;

    /**
     * Generate given box of data.
     * Create tiles will be stored in the pyramid.
     *
     * @param pyramid
     * @param env
     * @param resolutions
     * @param listener
     * @throws DataStoreException
     * @throws java.lang.InterruptedException
     */
    void generate(TileMatrixSet pyramid, Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException, InterruptedException;

}
