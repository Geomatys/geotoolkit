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
package org.geotoolkit.image.interpolation;

import java.awt.image.BufferedImage;
import java.util.Random;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.math.Statistics;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.apache.sis.referencing.CRS;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;

/**
 * Test class to stimulate many times {@link Resample} processing.
 *
 * @author Remi Marechal (Geomatys).
 */
public class BenchMarkResample {

    /**
     * Source image using to resample.
     */
    private final BufferedImage source;

    /**
     * Target image which is fill by resampling.
     */
    private final BufferedImage target;

    /**
     * Current image source and target dimensions.
     */
    private final static int SRC_WIDTH  = 4000;
    private final static int SRC_HEIGHT = 4000;

    /**
     * Projected Lambert CRS.
     */
    private final CoordinateReferenceSystem wilsonCRS;

    /**
     *  The base coordinate reference system, which must be geographic from Lambert CRS.
     */
    private final CoordinateReferenceSystem baseWilsonCRS;

    /**
     * MathTransform use to pass from base CRS to projected CRS, about Lambert CRS.
     */
    private final MathTransform lambertTransform;

    /**
     * Projected Mercator CRS.
     */
    private final CoordinateReferenceSystem mercatCRS;

    /**
     * The base coordinate reference system, which must be geographic from Mercator CRS.
     */
    private final CoordinateReferenceSystem baseMercatCRS;

    /**
     * MathTransform use to pass from base CRS to projected CRS, about Lambert CRS.
     */
    private final MathTransform mercatTransform;

    /**
     * A random
     */
    private final static Random RANDOM = new Random();
    /**
     * Transformation applicate to source or destination image to considered a pixel orientation center.
     */
    private static MathTransform pixelInCellCenter = new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5);

    public BenchMarkResample() throws Exception {

        //----------------------------- Lambert -------------------------------
        wilsonCRS     = CRS.forCode("EPSG:2154");//-- Lambert
        baseWilsonCRS = ((ProjectedCRS) wilsonCRS).getBaseCRS();

        //-- source to dest
        final MathTransform mercatMt = ((ProjectedCRS) wilsonCRS).getConversionFromBase().getMathTransform();

//        final Envelope srcEnv = new Envelope2D(baseCrs, 20, 44.5, 45, 45); //-- geographic

        final Envelope srcLambertEnv = new Envelope2D(baseWilsonCRS,  45,-8, 5, 16); //-- geographic

        final Envelope destLambertEnv = Envelopes.transform(mercatMt, srcLambertEnv);//-- dest envelop

        final AffineTransform2D srcLambertGridToCrs = new AffineTransform2D(srcLambertEnv.getSpan(0) / SRC_WIDTH, 0, 0, -srcLambertEnv.getSpan(1) / SRC_HEIGHT, srcLambertEnv.getMinimum(0), srcLambertEnv.getMaximum(1));

        final AffineTransform2D destLambertGridToCrs = new AffineTransform2D(destLambertEnv.getSpan(0) / SRC_WIDTH, 0, 0, -destLambertEnv.getSpan(1) / SRC_HEIGHT, destLambertEnv.getMinimum(0), destLambertEnv.getMaximum(1));

        final MathTransform pixSrcGridToLCrs = MathTransforms.concatenate(pixelInCellCenter, srcLambertGridToCrs);

        final MathTransform pixDestGridToProjLCrs = MathTransforms.concatenate(pixelInCellCenter, destLambertGridToCrs);

        lambertTransform = MathTransforms.concatenate(pixSrcGridToLCrs, mercatMt, pixDestGridToProjLCrs.inverse());



        //------------------------------ Mercator ------------------------------
        mercatCRS     = CRS.forCode("EPSG:3395"); //-- Mercator
        baseMercatCRS = ((ProjectedCRS) mercatCRS).getBaseCRS();

        //-- source to dest
        final MathTransform mercatT = ((ProjectedCRS) mercatCRS).getConversionFromBase().getMathTransform();

        final Envelope srcMercatEnv = new Envelope2D(baseMercatCRS, 20, 44.5, 45, 45); //-- geographic

//        final Envelope srcEnv = new Envelope2D(baseWilsonCRS,  45,-8, 5, 16); //-- geographic

        final Envelope destMercatEnv = Envelopes.transform(mercatT, srcMercatEnv);//-- dest envelop

        final AffineTransform2D srcGridToMCrs  = new AffineTransform2D(srcMercatEnv.getSpan(0) / SRC_WIDTH, 0, 0, -srcMercatEnv.getSpan(1) / SRC_HEIGHT, srcMercatEnv.getMinimum(0), srcMercatEnv.getMaximum(1));

        final AffineTransform2D destGridToMCrs = new AffineTransform2D(destMercatEnv.getSpan(0) / SRC_WIDTH, 0, 0, -destMercatEnv.getSpan(1) / SRC_HEIGHT, destMercatEnv.getMinimum(0), destMercatEnv.getMaximum(1));

        final MathTransform pixSrcGridToMCrs = MathTransforms.concatenate(pixelInCellCenter, srcGridToMCrs);

        final MathTransform pixDestGridToProjMCrs = MathTransforms.concatenate(pixelInCellCenter, destGridToMCrs);

        mercatTransform = MathTransforms.concatenate(pixSrcGridToMCrs, mercatT, pixDestGridToProjMCrs.inverse());




        source    = ImageUtils.createScaledBandedImage(SRC_WIDTH, SRC_HEIGHT, SampleType.INTEGER, 1);
        final PixelIterator pix = PixelIteratorFactory.createDefaultWriteableIterator(source, source);
        while (pix.next()) {
            pix.setSample(RANDOM.nextInt());
        }

        target    = ImageUtils.createScaledBandedImage(SRC_WIDTH, SRC_HEIGHT, SampleType.INTEGER, 1);

    }

    @Test
    @Ignore
    public void benchLambertTest() throws Exception {

        final MathTransform invertLambert = lambertTransform.inverse();

        final Statistics stats = new Statistics("lambert resample");

        int n = 0;
        while (n++ < 100) {
           /*
            * Resampling
            */
           final Resample resample = new Resample(invertLambert.inverse(), target, source,
                   InterpolationCase.BICUBIC, ResampleBorderComportement.EXTRAPOLATION, new double[]{0});
           final long t0 = System.currentTimeMillis();
           resample.fillImage();
           final long t = System.currentTimeMillis() - t0;
           if (n > 10) {
               stats.accept(t);
           }
        }

        System.out.println("stats lambert : "+stats);

    }

    @Test
    @Ignore
    public void benchMercatorTest() throws Exception {

        final MathTransform invertMercator = mercatTransform.inverse();

        final Statistics stats = new Statistics("mercator resample");

        int n = 0;
        while (n++ < 40) {
           /*
            * Resampling
            */
           final Resample resample = new Resample(invertMercator.inverse(), target, source,
                   InterpolationCase.BICUBIC, ResampleBorderComportement.EXTRAPOLATION, new double[]{0});
           final long t0 = System.currentTimeMillis();
           resample.fillImage();
           final long t = System.currentTimeMillis() - t0;
           if (n > 10) {
               stats.accept(t);
           }
        }

        System.out.println("stats mercator : "+stats);

    }
}
