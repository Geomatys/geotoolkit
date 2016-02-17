/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.image.relief;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for {@link ReliefShadow} class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ReliefShadowTest extends org.geotoolkit.test.TestBase {

    /**
     * Source image.
     */
    private final BufferedImage sourceImage;

    /**
     * Source image iterator.
     */
    private final PixelIterator srcIter;

    /**
     * source Digital Elevation Model.
     */
    private final BufferedImage mnt;

    /**
     * source Digital Elevation Model iterator.
     */
    private final PixelIterator mntIter;

    public ReliefShadowTest() {
        sourceImage = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        srcIter     = PixelIteratorFactory.createDefaultWriteableIterator(sourceImage, sourceImage);
        mnt     = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        mntIter = PixelIteratorFactory.createDefaultWriteableIterator(mnt, mnt);
    }

    @Test
    public void angle0Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(0, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y<5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 2 && y > 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-360, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle45Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(45, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y<5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x==y && x>2 && y>2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-315, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle90Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(90, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x > 2 && y == 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-270, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle135Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(135, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x > 2 && y < 2 && (5-x-1) == y) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-225, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle180Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(180, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 2 && y < 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-180, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle225Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(225, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y < 2 && x == y) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-135, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle270Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(270, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y == 2 ) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert               = new ReliefShadow(-90, 45, 0);
        final RenderedImage resultInvert          = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message        = "at ("+pixResult.getX()+", "+pixResult.getY()
                    +") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void angle315Test() {
        initTest(2,2,128);

        final ReliefShadow rf         = new ReliefShadow(315, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y > 2 && 5-y-1 == x) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        final ReliefShadow rfInvert         = new ReliefShadow(-45, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }

    @Test
    public void altitudeTest() {
        initTest(2, 2, 2);

        ReliefShadow rf         = new ReliefShadow(45, 45, 0);
        RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 3 && y == 3 ) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        initTest(2, 2, 3);
        rf        = new ReliefShadow(45, 45, 0);
        result    = rf.getRelief(sourceImage, mnt, 1);
        pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if ((x == 3 && y == 3) || (x == 4 && y == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }

        initTest(2, 2, 2);
        rf        = new ReliefShadow(45, 22.5, 0);
        result    = rf.getRelief(sourceImage, mnt, 1);
        pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if ((x == 3 && y == 3) || (x == 4 && y == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
    }

    @Test
    public void twoPikesTest() {
        initTest(0, 2, 1, 2, 2, 1);

        ReliefShadow rf         = new ReliefShadow(90, 22.5, 0);
        RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (y == 2 && (x == 1 || x == 3 || x == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
    }

    /**
     * Create two appropriates images to this test suite.
     *
     * @param coordinates pikes coordinates in DEM.
     */
    private void initTest(int ...coordinates) {
        // fill src images with white color
        srcIter.rewind();
        while (srcIter.next()) {
            srcIter.setSample(255);
        }

        // fill mnt at 0 altitude
        mntIter.rewind();
        while (mntIter.next()) {
            mntIter.setSample(0);
        }

        for (int p = 0; p < coordinates.length; p += 3) {
            mntIter.moveTo(coordinates[p], coordinates[p+1], 0);
            mntIter.setSample(coordinates[p+2]);
        }
    }
}
