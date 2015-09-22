/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.math.XMath;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractRenderedImage implements RenderedImage {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    @Override
    public Vector<RenderedImage> getSources() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[0];
    }

    @Override
    public Raster getData() {
        final SampleModel sm = getSampleModel().createCompatibleSampleModel(getWidth(), getHeight());
        final Raster rasterOut = Raster.createWritableRaster(sm, null);

        // Clear dataBuffer to 0 value for all bank
        for (int s=0; s<rasterOut.getDataBuffer().getSize(); s++){
            for (int b=0; b<rasterOut.getDataBuffer().getNumBanks(); b++){
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }

        for (int y=0; y<this.getNumYTiles(); y++){
            for (int x=0; x<this.getNumYTiles(); x++){
                final Raster rasterIn = getTile(x, y);
                rasterOut.getSampleModel().setDataElements(x*this.getTileWidth(), y*this.getTileHeight(), this.getTileWidth(), this.getTileHeight(),
                        rasterIn.getSampleModel().getDataElements(0, 0, this.getTileWidth(), this.getTileHeight(), null, rasterIn.getDataBuffer()),
                        rasterOut.getDataBuffer());
            }
        }

        return rasterOut;
    }

    @Override
    public Raster getData(Rectangle rect) {
        final SampleModel sm = getSampleModel().createCompatibleSampleModel(rect.width, rect.height);
        final Raster rasterOut = Raster.createWritableRaster(sm, null);

        // Clear dataBuffer to 0 value for all bank
        for (int s=0; s<rasterOut.getDataBuffer().getSize(); s++){
            for (int b=0; b<rasterOut.getDataBuffer().getNumBanks(); b++){
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }

        try {
            final Point upperLeftPosition = this.getPositionOf(rect.x, rect.y);
            final Point lowerRightPosition = this.getPositionOf(rect.x+rect.width-1, rect.y+rect.height-1);

            for (int y=Math.max(upperLeftPosition.y,0); y<Math.min(lowerRightPosition.y+1,this.getNumYTiles()); y++){
                for (int x=Math.max(upperLeftPosition.x,0); x<Math.min(lowerRightPosition.x+1, this.getNumXTiles()); x++){
                    final Rectangle tileRect = new Rectangle(x*this.getTileWidth(), y*this.getTileHeight(), this.getTileWidth(), this.getTileHeight());

                    final int minX, maxX, minY, maxY;
                    minX = XMath.clamp(rect.x, tileRect.x, tileRect.x + tileRect.width);
                    maxX = XMath.clamp(rect.x+rect.width, tileRect.x, tileRect.x+tileRect.width);
                    minY = XMath.clamp(rect.y,            tileRect.y, tileRect.y+tileRect.height);
                    maxY = XMath.clamp(rect.y+rect.height,tileRect.y, tileRect.y+tileRect.height);

                    final Rectangle rectIn = new Rectangle(minX, minY, maxX-minX, maxY-minY);
                    rectIn.translate(-tileRect.x, -tileRect.y);
                    final Rectangle rectOut = new Rectangle(minX, minY, maxX-minX, maxY-minY);
                    rectOut.translate(-rect.x, -rect.y);

                    if (rectIn.width <= 0 || rectIn.height <= 0 || rectOut.width <= 0 || rectOut.height <= 0){
                        continue;
                    }

                    final Raster rasterIn = getTile(x, y);

                    rasterOut.getSampleModel().setDataElements(rectOut.x, rectOut.y, rectOut.width, rectOut.height,
                            rasterIn.getSampleModel().getDataElements(rectIn.x, rectIn.y, rectIn.width, rectIn.height, null, rasterIn.getDataBuffer()),
                            rasterOut.getDataBuffer());
                }
            }

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return rasterOut;
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the tile column and row position for a pixel.
     * Return value can be out of the gridSize
     *
     * @param x
     * @param y
     * @return
     */
    protected Point getPositionOf(int x, int y){
        final int posX = (int)(Math.floor(x/this.getTileWidth()));
        final int posY = (int)(Math.floor(y/this.getTileHeight()));
        return new Point(posX, posY);
    }

}
