/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io;

import javax.media.jai.iterator.RectIter;


/**
 * A {@link RectIter} with subsampling.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
final class SubsampledRectIter implements RectIter {
    /**
     * The wrapped iterator.
     */
    private final RectIter iterator;

    /**
     * Index of current band in the {@link #sourceBands} array.
     */
    private int bandIndex;

    /**
     * The source bands.
     */
    private final int[] sourceBands;

    /**
     * The subsampling parameters.
     */
    private final int dx, dy;

    /**
     * Wraps the specified iterator.
     */
    public SubsampledRectIter(final RectIter iterator,
                              final int sourceXSubsampling, final int sourceYSubsampling,
                              final int[] sourceBands)
    {
        this.iterator = iterator;
        dx = sourceXSubsampling - 1;
        dy = sourceYSubsampling - 1;
        this.sourceBands = sourceBands;
    }

    /**
     * Sets the iterator to the first line of its bounding rectangle.
     */
    @Override
    public void startLines() {
        iterator.startLines();
    }

    /**
     * Sets the iterator to the next line of the image.
     */
    @Override
    public void nextLine() {
        nextLineDone();
    }

    /**
     * Sets the iterator to the next line in the image.
     */
    @Override
    public boolean nextLineDone() {
        if (iterator.nextLineDone()) {
            return true;
        }
        iterator.jumpLines(dy);
        return false;
    }

    /**
     * Jumps downward num lines from the current position.
     */
    @Override
    public void jumpLines(final int num) {
        iterator.jumpLines(num * (dy+1));
    }

    /**
     * Returns true if the bottom row of the bounding rectangle has been passed.
     */
    @Override
    public boolean finishedLines() {
        return iterator.finishedLines();
    }

    /**
     * Sets the iterator to the leftmost pixel of its bounding rectangle.
     */
    @Override
    public void startPixels() {
        iterator.startPixels();
    }

    /**
     * Sets the iterator to the next pixel in image.
     */
    @Override
    public void nextPixel() {
        nextPixelDone();
    }

    /**
     * Sets the iterator to the next pixel in the image.
     */
    @Override
    public boolean nextPixelDone() {
        if (iterator.nextPixelDone()) {
            return true;
        }
        iterator.jumpPixels(dx);
        return false;
    }

    /**
     * Jumps rightward num pixels from the current position.
     */
    @Override
    public void jumpPixels(final int num) {
        iterator.jumpPixels(num * (dx+1));
    }

    /**
     * Returns true if the right edge of the bounding rectangle has been passed.
     */
    @Override
    public boolean finishedPixels() {
        return iterator.finishedPixels();
    }

    /**
     * Sets the iterator to the first band of the image.
     */
    @Override
    public void startBands() {
        bandIndex = 0;
        iterator.startBands();
        for (int skip=sourceBands[0]; --skip>=0;) {
            iterator.nextBand();
        }
    }

    /**
     * Sets the iterator to the next band in the image.
     */
    @Override
    public void nextBand() {
        nextBandDone();
    }

    /**
     * Sets the iterator to the next band in the image, and returns
     * true if the max band has been exceeded.
     */
    @Override
    public boolean nextBandDone() {
        int skip = sourceBands[bandIndex];
        if (++bandIndex >= sourceBands.length) {
            return true;
        }
        skip = sourceBands[bandIndex] - skip;
        if (skip < 0) {
            iterator.startBands();
            skip = sourceBands[bandIndex];
        }
        while (--skip >= 0) {
            if (iterator.nextBandDone()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the max band in the image has been exceeded.
     */
    @Override
    public boolean finishedBands() {
        return iterator.finishedBands();
    }

    @Override public int      getSample      ()      {return iterator.getSample      ();}
    @Override public int      getSample      (int b) {return iterator.getSample      (sourceBands[b]);}
    @Override public float    getSampleFloat ()      {return iterator.getSampleFloat ();}
    @Override public float    getSampleFloat (int b) {return iterator.getSampleFloat (sourceBands[b]);}
    @Override public double   getSampleDouble()      {return iterator.getSampleDouble();}
    @Override public double   getSampleDouble(int b) {return iterator.getSampleDouble(sourceBands[b]);}

    @Override
    public int[] getPixel(int[] a) {
        final int length = sourceBands.length;
        if (a == null) {
            a = new int[length];
        }
        for (int i=0; i<length; i++) {
            a[i] = getSample(i);
        }
        return a;
    }

    @Override
    public float[] getPixel(float[] a) {
        final int length = sourceBands.length;
        if (a == null) {
            a = new float[length];
        }
        for (int i=0; i<length; i++) {
            a[i] = getSampleFloat(i);
        }
        return a;
    }

    @Override
    public double[] getPixel(double[] a) {
        final int length = sourceBands.length;
        if (a == null) {
            a = new double[length];
        }
        for (int i=0; i<length; i++) {
            a[i] = getSampleDouble(i);
        }
        return a;
    }
}
