/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.memory;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.data.multires.AbstractMosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Tile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author rmarechal
 */
public class MPMosaic extends AbstractMosaic {

    private final MPCoverageResource res;
    private final MPImageTile[][] mpTileReference;

    public MPMosaic( MPCoverageResource res, final long id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
        super(String.valueOf(id),pyramid, upperLeft, gridSize, tileSize, scale);
        this.res = res;
        mpTileReference = new MPImageTile[gridSize.width][gridSize.height];
    }

    @Override
    public boolean isMissing(long col, long row) {
        return mpTileReference[Math.toIntExact(col)][Math.toIntExact(row)] == null;
    }

    @Override
    public MPImageTile getTile(long col, long row, Map hints) throws DataStoreException {
        return mpTileReference[Math.toIntExact(col)][Math.toIntExact(row)];
    }

    public void setTile(int col, int row, MPImageTile tile){
        mpTileReference[col][row] = tile;
    }

    @Override
    protected boolean isWritable() throws CoverageStoreException {
        return true;
    }

    @Override
    protected void writeTile(Tile tile) throws DataStoreException {
        if (tile instanceof ImageTile) {
            final ImageTile imgTile = (ImageTile) tile;
            try {
                RenderedImage image = imgTile.getImage();

                if (res.getColorModel() == null) res.setColorModel(image.getColorModel());

                final Dimension tileSize = getTileSize();
                if (tileSize.width < image.getWidth() || tileSize.height < image.getHeight()) {
                    throw new IllegalArgumentException("Uncorrect image size ["+image.getWidth()+","+image.getHeight()+"] expecting size ["+tileSize.width+","+tileSize.height+"]");
                }
                final int tileX = imgTile.getPosition().x;
                final int tileY = imgTile.getPosition().y;
                setTile(tileX, tileY, new MPImageTile(image, 0, new Point(tileX, tileY)));
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        } else {
            throw new DataStoreException("Only ImageTile are supported.");
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        setTile(tileX,tileY,null);
    }
}
