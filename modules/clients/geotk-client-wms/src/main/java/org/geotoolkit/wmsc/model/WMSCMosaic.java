/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wmsc.model;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.wms.xml.v111.BoundingBox;
import org.geotoolkit.wmsc.xml.v111.TileSet;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCMosaic implements Mosaic {

    private final String id = UUID.randomUUID().toString();
    private final WMSCPyramid pyramid;
    private final double scale;

    private final Dimension gridSize = new Dimension();
    private final double tileSpanX;
    private final double tileSpanY;

    public WMSCMosaic(final WMSCPyramid pyramid, final double scaleLevel) {
        this.pyramid = pyramid;
        this.scale = scaleLevel;

        final int tileWidth = pyramid.getTileset().getWidth();
        final int tileHeight = pyramid.getTileset().getHeight();

        final BoundingBox env = pyramid.getTileset().getBoundingBox();
        final double spanX = env.getMaxx() - env.getMinx();
        final double spanY = env.getMaxy() - env.getMiny();

        gridSize.width = (int) (spanX / (scale*tileWidth));
        gridSize.height = (int) (spanY / (scale*tileHeight));

        tileSpanX = spanX / gridSize.width ;
        tileSpanY = spanY / gridSize.height ;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    public Pyramid getPyramid() {
        return pyramid;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        return pyramid.getUpperLeftCorner();
    }

    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone();
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(
                pyramid.getTileset().getWidth(),
                pyramid.getTileset().getHeight());
    }

    @Override
    public Envelope getEnvelope() {
        final DirectPosition ul = getUpperLeftCorner();
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * getGridSize().width * getScale();
        final double spanY = getTileSize().height* getGridSize().height* getScale();

        final GeneralEnvelope envelope = new GeneralEnvelope(
                pyramid.getCoordinateReferenceSystem());
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );

        return envelope;
    }

    @Override
    public boolean isMissing(long col, long row) {
        return false;
    }

    @Override
    public ImageTile getTile(long col, long row, Map hints) throws DataStoreException {
        return pyramid.getPyramidSet().getTile(pyramid, this, col, row, hints);
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        return pyramid.getPyramidSet().getTiles(pyramid, this, positions, hints);
    }

    @Override
    public GridExtent getDataExtent() {
        TileSet tileset = pyramid.getTileset();
        return new GridExtent(
                ((long) gridSize.width) * tileset.getWidth() ,
                ((long) gridSize.height) * tileset.getHeight());
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        throw new DataStoreException("WMS-C is not writable");
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        throw new DataStoreException("WMS-C is not writable");
    }
}
