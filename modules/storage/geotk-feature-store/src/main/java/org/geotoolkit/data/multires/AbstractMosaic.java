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
package org.geotoolkit.data.multires;

import java.awt.Dimension;
import java.awt.Point;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.process.Monitor;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * Abstract mosaic grid.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractMosaic implements Mosaic {

    private final String id;
    private final Pyramid pyramid;
    private final DirectPosition upperLeft;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;

    public AbstractMosaic(Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
            Dimension tileSize, double scale) {
        this(null,pyramid,upperLeft,gridSize,tileSize,scale);
    }

    public AbstractMosaic(String id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
            Dimension tileSize, double scale) {
        this.pyramid = pyramid;
        this.upperLeft = new GeneralDirectPosition(upperLeft);
        this.scale = scale;
        this.gridSize = (Dimension) gridSize.clone();
        this.tileSize = (Dimension) tileSize.clone();

        if(id == null){
            this.id = UUID.randomUUID().toString();
        }else{
            this.id = id;
        }

        //ensure we do not exceed integer max value.
        Math.multiplyExact(gridSize.width, tileSize.width);
        Math.multiplyExact(gridSize.height, tileSize.height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Pyramid getPyramid() {
        return pyramid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectPosition getUpperLeftCorner() {
        return new GeneralDirectPosition(upperLeft); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone(); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getTileSize() {
        return (Dimension) tileSize.clone(); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getScale() {
        return scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getEnvelope() {
        return Pyramids.computeMosaicEnvelope(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMissing(long col, long row) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GridExtent getDataExtent() {

        Point start = null;
        for (int y = 0; y < gridSize.height; y++) {
            for (int x = 0; x < gridSize.width; x++) {
                if (!isMissing(x,y)) {
                     start = new Point(x,y);
                     break;//--get only the first point of the grid
                }
            }
            if (start != null) break;//--get only the first point of the grid
        }

        if (start != null) {
            Point end = null;
            for (int y = gridSize.height-1; y >= start.y; y--) {
                for (int x = gridSize.width-1; x >= start.x; x--) {
                    if (!isMissing(x, y)) {
                        end = new Point(x, y);
                        break; //-- get only the last tile grid
                    }
                }
                if (end != null) break; //-- get only the last tile grid
            }

            assert end.x >= start.x;
            assert end.y >= start.y;

            long sx = ((long) start.x) * tileSize.width;
            long sy = ((long) start.y) * tileSize.height;
            long ex = (end.x - start.x + 1) * tileSize.width;
            long ey = (end.y - start.y + 1) * tileSize.height;
            return new GridExtent(null, new long[]{sx,sy}, new long[]{ex,ey}, false);
        } else {
            //all mosaic tiles are missing
            return new GridExtent(
                    ((long) gridSize.width) * tileSize.width,
                    ((long) gridSize.height) * tileSize.height);
        }
    }

    protected abstract boolean isWritable() throws CoverageStoreException;

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        try {
            tiles.parallel().forEach((Tile tile) -> {
                try {
                    writeTile(tile);
                } catch (DataStoreException ex) {
                    throw new BackingStoreException(ex);
                }
            });
        } catch (BackingStoreException ex) {
            throw (DataStoreException) ex.getCause();
        }
    }

    protected void writeTile(Tile tile) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        throw new DataStoreException("Pyramid writing not supported.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }

}
