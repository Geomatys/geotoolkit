/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRenderedImage;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author rmarech
 */
public class PixelIteratorConform extends PixelIterator {

    private final PixelIterator pixelIter1;
    private final PixelIterator pixelIter2;

    public PixelIteratorConform(WritableRenderedImage image) {
        super(Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 5, 5, 1, null), null);
        this.pixelIter1 = new DefaultWritableIterator(image, image, null);
        this.pixelIter2 = PixelIteratorFactory.createDefaultWriteableIterator(image, image);
    }

    public PixelIteratorConform(WritableRenderedImage image, Rectangle subArea) {
        super(Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 5, 5, 1, null), null);
        this.pixelIter1 = new DefaultWritableIterator(image, image, subArea);
        this.pixelIter2 = PixelIteratorFactory.createDefaultWriteableIterator(image, image);
    }


    @Override
    public boolean next() {
        final boolean p1 = pixelIter1.next();
        assertTrue(p1 == pixelIter2.next());
        return p1;
    }

    @Override
    public int getX() {
        assertTrue(pixelIter1.getX() == pixelIter2.getX());
        return pixelIter1.getX();
    }

    @Override
    public int getY() {
        assertTrue(pixelIter1.getY() == pixelIter2.getY());
        return pixelIter1.getY();
    }

    @Override
    public int getSample() {
        assertTrue(pixelIter1.getSample() == pixelIter2.getSample());
        return pixelIter1.getSample();
    }

    @Override
    public float getSampleFloat() {
        assertTrue(Math.abs(pixelIter1.getSampleFloat() - pixelIter2.getSampleFloat())<=1E-9);
        return pixelIter1.getSampleFloat();
    }

    @Override
    public double getSampleDouble() {
        assertTrue(Math.abs(pixelIter1.getSampleDouble() - pixelIter2.getSampleDouble())<=1E-9);
        return pixelIter1.getSampleDouble();
    }

    @Override
    public void rewind() {
        pixelIter1.rewind();
        pixelIter2.rewind();
    }

    @Override
    public void setSample(int value) {
        pixelIter1.setSample(value);
        pixelIter2.setSample(value);
    }

    @Override
    public void setSampleFloat(float value) {
        pixelIter1.setSampleFloat(value);
        pixelIter2.setSampleFloat(value);
    }

    @Override
    public void setSampleDouble(double value) {
        pixelIter1.setSampleDouble(value);
        pixelIter2.setSampleDouble(value);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNumBands() {
        assertTrue(pixelIter1.getNumBands() == pixelIter2.getNumBands());
        return pixelIter1.getNumBands();
    }

    @Override
    public void moveTo(int x, int y) {
        pixelIter1.moveTo(x, y);
        pixelIter2.moveTo(x, y);
//        assertTrue(Math.abs(pixelIter1.getSampleDouble() - pixelIter2.getSampleDouble())<=1E-9);
    }

    @Override
    public Rectangle getBoundary() {
        assertTrue(pixelIter1.getBoundary().equals(pixelIter2.getBoundary()));
        return pixelIter1.getBoundary();
    }



}
