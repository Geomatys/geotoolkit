/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.metadata.landsat;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.large.ImageCacheConfiguration;
import org.geotoolkit.image.io.large.LargeCache;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.lang.Setup;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Improve {@link WritableLargeRenderedImage} {@link LargeCache} comportement.
 *
 * @author Alexis Manin (Geomatys).
 * @author Remi Marechal (Geomatys).
 */
public final strictfp class WritableLargeRenderedImageTests {

    /**
     * {@link Logger} to show times number and time length of a test loop.
     *
     * @see #test()
     */
    private static final Logger LOGGER = Logging.getLogger(WritableLargeRenderedImageTests.class.getCanonicalName());

    /**
     * Define test image size.
     *
     * @see #WritableLargeRenderedImageTests()
     */
    private final static int IMG_SIZE = 5000; //-- increase this attribut to improve LargeCache comportement

    /**
     * Init providers.
     */
    @BeforeClass
    public static void init() {
        ImageIO.scanForPlugins();
        Setup.initialize(null);
        ImageCacheConfiguration.setCacheMemorySize("3m");
        LargeCache.getInstance().setMemoryCapacity((long) 3E6);
    }

    /**
     * Fill and rewrite {@link WritableLargeRenderedImage} many times to stimulate
     * {@link LargeCache} and thrad safe comportement.
     *
     * @throws InterruptedException if problem during multi-thread pixel working.
     * @throws ExecutionException
     */
    @Test
    public void test() throws InterruptedException, ExecutionException {

        WritableLargeRenderedImage outPutImageTest = ImageUtils.createRGBLargeImage(IMG_SIZE, IMG_SIZE, SampleType.BYTE);
        WritableLargeRenderedImage inputTestImg    = ImageUtils.createLargeImage(IMG_SIZE, IMG_SIZE, SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE, false, false, null);

        final ExecutorService poule = Executors.newFixedThreadPool(3);

        for (int nb = 0, nbTimes = 1; nb < nbTimes; nb++) { //-- to improve comportement increase nbTimes attribut.

            final long t = System.currentTimeMillis();

            //-- assigned default test value.
            final byte value = (byte) (StrictMath.random() * 255);

            //-- fill input image by value.
            final PixelIterator It = PixelIteratorFactory.createDefaultWriteableIterator(inputTestImg, inputTestImg);
            while (It.next()) {
                It.setSample(value);
            }

            final pixelWork band0Pix = new pixelWork(inputTestImg, outPutImageTest, 0);
            final pixelWork band2Pix = new pixelWork(inputTestImg, outPutImageTest, 2);
            final pixelWork band1Pix = new pixelWork(inputTestImg, outPutImageTest, 1);

            final ArrayList<Future> futurList = new ArrayList<Future>();
            futurList.add(poule.submit(band0Pix));
            futurList.add(poule.submit(band1Pix));
            futurList.add(poule.submit(band2Pix));


            for (Future futurList1 : futurList) {
                futurList1.get(); //-- block program in attempt to end of multiple processes.
            }

            //-- verify result pertinency
            final PixelIterator createDefaultIterator = PixelIteratorFactory.createDefaultIterator(outPutImageTest);

            while (createDefaultIterator.next()) {
                Assert.assertEquals("unexpected byte value : at x = "+createDefaultIterator.getX()
                        +", y = "+createDefaultIterator.getY(), (value & 0xFF), (createDefaultIterator.getSample() & 0xFF));
            }
            LOGGER.log(Level.INFO, "iteration nb : "+nb+", time : "+(System.currentTimeMillis() - t));
        }

        poule.shutdown();
        poule.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * Implement {@link Callable} interface to effectuate multi-threading image filling.
     */
    private class pixelWork implements Callable<Object> {

        /**
         * Input image which will be rewriting into output image.
         */
        private final WritableLargeRenderedImage input;

        /**
         * Output thread image.
         */
        private final WritableLargeRenderedImage output;

        /**
         * Band index where {@link #input} is rewriting into
         */
        private final int outPutBandIndex;

        /**
         * Build a rewriting thread.
         *
         * @param input source image.
         * @param output destination filled image.
         * @param bandIndex band index of the destination image which will be filled.
         */
        public pixelWork(final WritableLargeRenderedImage input,
                         final WritableLargeRenderedImage output,
                         final int bandIndex) {
            ArgumentChecks.ensureNonNull("input", input);
            ArgumentChecks.ensureNonNull("output", output);
            ArgumentChecks.ensurePositive("band Index", bandIndex);
            this.input           = input;
            this.output          = output;
            this.outPutBandIndex = bandIndex;
        }

        /**
         * Rewrite {@link #input} into {@link #output} image at {@link #outPutBandIndex} band index.
         *
         * @return {@code null}.
         * @throws Exception
         */
        @Override
        public Object call() throws Exception {

            final PixelIterator inputPix  = PixelIteratorFactory.createDefaultIterator(input);
            final PixelIterator outPutPix = PixelIteratorFactory.createDefaultWriteableIterator(output, output);

            int currentb = 0;
            while (outPutPix.next()) {
                if (currentb++ == outPutBandIndex) {
                    inputPix.next();
                    outPutPix.setSampleDouble(inputPix.getSampleDouble());
                }

                if (currentb == outPutPix.getNumBands()) {
                    currentb = 0;
                }
            }
            return null;
        }
    }
}


