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

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * TODO : this is an incomplete work. some parameters are not used and evaluator 
 * interface is just a draft.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ProcessedRenderedImage extends AbstractRenderedImage {

    private final SampleModel sampleModel;
    private final ColorModel colorModel;
    private final Evaluator evaluator;
    private final int width;
    private final int height;
    private final int minX;
    private final int minY;
    private final int numXTiles;
    private final int numYTiles;
    private final int minTileX;
    private final int minTileY;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;

    public ProcessedRenderedImage(SampleModel sampleModel, ColorModel colorModel, Evaluator evaluator,
            int width, int height) {
        this(sampleModel,colorModel,evaluator,width,height,0,0,1,1,0,0,width,height,0,0);
    }
    
    public ProcessedRenderedImage(SampleModel sampleModel, ColorModel colorModel, Evaluator evaluator,
            int width, int height, int minX, int minY, int numTileX, int numTileY, 
            int minTileX, int minTileY, int tileWidth, int tileHeight, int tileGridXOffset, int tileGridYOffset) {
        this.sampleModel = sampleModel;
        this.colorModel = colorModel;
        this.evaluator = evaluator;
        this.width = width;
        this.height = height;
        this.minX = minX;
        this.minY = minY;
        this.numXTiles = numTileX;
        this.numYTiles = numTileY;
        this.minTileX = minTileX;
        this.minTileY = minTileY;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileGridXOffset = tileGridXOffset;
        this.tileGridYOffset = tileGridYOffset;
    }
    
    @Override
    public ColorModel getColorModel() {
        return colorModel;
    }

    @Override
    public SampleModel getSampleModel() {
        return sampleModel;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMinX() {
        return minX;
    }

    @Override
    public int getMinY() {
        return minY;
    }

    @Override
    public int getNumXTiles() {
        return numXTiles;
    }

    @Override
    public int getNumYTiles() {
        return numYTiles;
    }

    @Override
    public int getMinTileX() {
        return minTileX;
    }

    @Override
    public int getMinTileY() {
        return minTileY;
    }

    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    @Override
    public int getTileGridXOffset() {
        return tileGridXOffset;
    }

    @Override
    public int getTileGridYOffset() {
        return tileGridYOffset;
    }

    @Override
    public synchronized Raster getTile(int tileX, int tileY) {
        
        final SampleModel sm = getSampleModel().createCompatibleSampleModel(getTileWidth(), getTileHeight());
        final WritableRaster raster = Raster.createWritableRaster(sm, null);
        final int nbBand = sm.getNumBands();
        final double[] sampleBuffer = new double[nbBand];
        
        //TODO take in consideration other values
        final int offsetX = tileX * tileWidth;
        final int offsetY = tileY * tileHeight;
        for(int y=0;y<tileHeight;y++){
            for(int x=0;x<tileWidth;x++){
                evaluator.evaluate(x+offsetX, y+offsetY, sampleBuffer);
                for(int b=0;b<nbBand;b++){
                    raster.setSample(x, y, b, sampleBuffer[b]);
                }
            }
        }
        
        return raster;
    }

    public static interface Evaluator {
        
        void evaluate(int x, int y, double[] sampleBuffer);
        
    }
    
}
