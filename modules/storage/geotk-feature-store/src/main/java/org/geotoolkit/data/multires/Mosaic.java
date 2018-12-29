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
package org.geotoolkit.data.multires;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.Monitor;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * A Mosaic is collection of tiles with the same size and properties placed
 * on a regular grid with no overlaping.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Mosaic {

    /**
     * Sentinel object used to notify the end of the queue.
     */
    public static final Object END_OF_QUEUE = new Object();

    /**
     * @return unique id.
     */
    String getIdentifier();

    /**
     * Returns the upper left corner of the mosaic.
     * The corner is in PixelInCell.CELL_CORNER, so it contains a translate of a half
     * pixel compared to a GridToCrs transform of a coverage.
     *
     * @return upper left corner of the mosaic, expressed in pyramid CRS.
     */
    DirectPosition getUpperLeftCorner();

    /**
     * @return size of the grid in number of columns/rows.
     */
    Dimension getGridSize();

    /**
     * @return size of a pixel in crs unit
     */
    double getScale();

    /**
     * @return tile dimension in cell units.
     */
    Dimension getTileSize();

    /**
     * Envelope of the mosaic.
     *
     * @return Envelope
     */
    default Envelope getEnvelope() {
        return Pyramids.computeMosaicEnvelope(this);
    }

    /**
     * Some services define some missing tiles.
     * WMTS for example may define for a given layer a limitation saying
     * only tiles for column 10 to 30 are available.
     *
     * @param col
     * @param row
     * @return true if tile is missing
     * @throws org.opengis.coverage.PointOutsideCoverageException if the queried coordinate is not an allowed tile indice.
     */
    boolean isMissing(int col, int row) throws PointOutsideCoverageException;

    /**
     * Get a tile.
     * @param col : tile column index
     * @param row : row column index
     * @return Tile , may be null if tile is missing.
     * @throws DataStoreException
     */
    public default Tile getTile(int col, int row) throws DataStoreException {
        return getTile(col, row, null);
    }

    /**
     * Get a tile.
     * @param col : tile column index
     * @param row : row column index
     * @param hints : additional hints. Can be null.
     * @return TileReference , may be null if tile is missing.
     * @throws DataStoreException
     */
    Tile getTile(int col, int row, Map hints) throws DataStoreException;

    /**
     * Retrieve a set of TileReferences.<p>
     * The end of the queue is notified by the {@link GridMosaic#END_OF_QUEUE} object.<p>
     * The returned queue may implement Canceleable if for some reason there is no need
     * to continue iteration on the queue.
     *
     * @param positions : requested tiles positions
     * @param hints : additional hints
     * @return blocking queue over the requested tiles.
     *         Order might be different from the list of positions.
     * @throws DataStoreException
     */
    default BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        final ArrayBlockingQueue queue = new ArrayBlockingQueue(positions.size()+1);
        for(Point p : positions){
            final Tile t = getTile(p.x, p.y, hints);
            if (t != null) {
                queue.offer(t);
            }
        }
        queue.offer(END_OF_QUEUE);
        return queue;
    }

    /**
     * Returns Extent of written data into mosaic tile.<br>
     * If extent is not known by mosaic implementation, this method browse all
     * mosaic grid to returning a {@link java.awt.Rectangle} of where data are.<br>
     * Rectangle represente area exprimate in <strong>pixels</strong> grid coordinate.<br>
     * May return {@code null} if mosaic is empty.
     *
     * @return {@link java.awt.Rectangle} of data area or null if all
     * tiles of the mosaic are missing.
     */
    Rectangle getDataExtent();

    void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException;

    void deleteTile(int tileX, int tileY) throws DataStoreException;
}
