package org.geotoolkit.image.internal;

import java.awt.image.BufferedImage;
import org.apache.sis.image.PixelIterator;
import org.junit.Assert;
import org.junit.Test;

public class ImageUtilitiesTest {

    /**
     * Verify that all bands of the image are filled by fill utility.
     */
    @Test
    public void testFillImage() {
        final BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_3BYTE_BGR);
        ImageUtilities.fill(image, 7);
        final PixelIterator it = PixelIterator.create(image);
        final int[] expectedPixel = { 7, 7, 7 };
        final int[] pixel = new int[3];
        while (it.next()) Assert.assertArrayEquals("Pixel "+it.getPosition(), expectedPixel, it.getPixel(pixel));
    }
}
