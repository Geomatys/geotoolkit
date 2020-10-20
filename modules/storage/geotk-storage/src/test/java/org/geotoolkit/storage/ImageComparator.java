/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage;

import java.awt.Point;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import org.apache.sis.image.PixelIterator;
import org.junit.Assert;

/**
 * Tool to compare images.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ImageComparator {

    private static final int[][] NEIGHBORS = new int[][]{
        //lower neigbhors
        {-1,-1},
        { 0,-1},
        { 1,-1},
        //upper neighbors
        {-1, 1},
        { 0, 1},
        { 1, 1},
        //left
        {-1, 0},
        //right
        { 1, 0}
    };

    private final RenderedImage expected;
    private final RenderedImage result;

    /**
     * Specify the maximum distance from original pixel location
     * where to search for a matching comparison.
     *
     * Note : only a distance of one supported for now.
     */
    public int distanceTolerance = 0;

    public ImageComparator(RenderedImage expected, RenderedImage result) {
        this.expected = expected;
        this.result = result;
    }

    /**
     * Compares the images specified at construction time. Before to invoke this
     * method, users may consider to add some tolerances hints.
     */
    public void compare() {
        if (!(distanceTolerance == 0 || distanceTolerance == 1)) {
            throw new IllegalArgumentException("Distance tolerance can only be 0 or 1.");
        }

        final PixelIterator ite1 = PixelIterator.create(expected);
        final PixelIterator ite2 = PixelIterator.create(result);
        if (!ite1.getDomain().equals(ite2.getDomain())) {
            Assert.fail("Images domain do not match");
        }
        final double[] pixel1 = new double[ite1.getNumBands()];
        final double[] pixel2 = new double[ite2.getNumBands()];

        pixelLoop:
        while (ite1.next()) {
            final Point position = ite1.getPosition();
            ite2.moveTo(position.x, position.y);
            ite1.getPixel(pixel1);
            ite2.getPixel(pixel2);
            if (!Arrays.equals(pixel1, pixel2)) {
                if (distanceTolerance == 1) {
                    //search neighbor pixels
                    for (int[] n : NEIGHBORS) {
                        try {
                            ite2.moveTo(position.x + n[0], position.y + n[1]);
                            ite2.getPixel(pixel2);
                            if (Arrays.equals(pixel1, pixel2)) {
                                continue pixelLoop;
                            }
                        } catch (IndexOutOfBoundsException ex) {
                            //out of image
                        }

                    }
                }
                Assert.fail("Pixel differ at " + position + " expected " + Arrays.toString(pixel1) + " but was " + Arrays.toString(pixel2));
            }
        }
    }

}
