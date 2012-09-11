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

import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.media.jai.*;
import javax.media.jai.operator.AffineDescriptor;
import org.geotoolkit.image.iterator.PixelIteratorConform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test resampling class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ResampleTest {

    /**
     * Transformation applicate on source image for resampling.
     */
    MathTransform mathTransform;

    /**
     * Interpolation applicate during resampling.
     */
    Interpolation interpolation;

    /**
     * Destination image.
     * Resampling result.
     */
    WritableRenderedImage targetImage;

    /**
     * Source image.
     * Image within interpolation computing is applicate.
     */
    TiledImage sourceImg;

    /**
     * Destination image parameters.
     */
    int tIminy, tIminx, tIH, tIW, tINB;

    public ResampleTest() {

        final SampleModel sourceSampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, 4, 4, 3, 12,new int[]{0, 1, 2});
        sourceImg = new TiledImage(0, 0, 4, 4, 0, 0, sourceSampleM, null);

        final WritableRaster raster = sourceImg.getWritableTile(0, 0);
        final int minx   = raster.getMinX();
        final int miny   = raster.getMinY();
        final int height = raster.getHeight();
        final int width  = raster.getWidth();
        //band0
        raster.setSample(0, 0, 0, 1);
        raster.setSample(1, 0, 0, 1);
        raster.setSample(2, 0, 0, 1);
        raster.setSample(3, 0, 0, 1);
        raster.setSample(0, 1, 0, 1);
        raster.setSample(1, 1, 0, 2);
        raster.setSample(2, 1, 0, 2);
        raster.setSample(3, 1, 0, 1);
        raster.setSample(0, 2, 0, 1);
        raster.setSample(1, 2, 0, 2);
        raster.setSample(2, 2, 0, 2);
        raster.setSample(3, 2, 0, 1);
        raster.setSample(0, 3, 0, 1);
        raster.setSample(1, 3, 0, 1);
        raster.setSample(2, 3, 0, 1);
        raster.setSample(3, 3, 0, 1);
        //band1
        raster.setSample(0, 0, 1, 2);
        raster.setSample(1, 0, 1, 2);
        raster.setSample(2, 0, 1, 2);
        raster.setSample(3, 0, 1, 2);
        raster.setSample(0, 1, 1, 2);
        raster.setSample(1, 1, 1, 1);
        raster.setSample(2, 1, 1, 1);
        raster.setSample(3, 1, 1, 2);
        raster.setSample(0, 2, 1, 2);
        raster.setSample(1, 2, 1, 1);
        raster.setSample(2, 2, 1, 1);
        raster.setSample(3, 2, 1, 2);
        raster.setSample(0, 3, 1, 2);
        raster.setSample(1, 3, 1, 2);
        raster.setSample(2, 3, 1, 2);
        raster.setSample(3, 3, 1, 2);

        //band2
        int val = 0;
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                raster.setSample(x, y, 2, val++);
            }
        }
    }

    /**
     * Test result obtained from neighBor interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiNeighBorTest() throws NoninvertibleTransformException, FactoryException, TransformException {

        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationNearest();
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        tIminy = tIminx = 0;
        tIH = tIW = 8;
        tINB = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_BYTE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.NEIGHBOR);
        setAffineMathTransform(2, 0, 0, 2, 0.5, 0.5);//decalage de 1/2 pour concordé au decalage de JAI.

        /*
         * Resampling
         */
        Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{0, 0, 0});
        resample.fillImage();
        Raster coverageRaster = targetImage.getTile(0, 0);

        //check border
        for (int b = 1; b <= 2; b++) {
            for (int y = tIminy; y < tIminy+tIH-1;y++) {
                assertTrue((coverageRaster.getSampleDouble(0, y, b-1)-b) <= 1E-9);
            }
            for (int x = tIminx; x < tIminx+tIW; x++) {
                assertTrue((coverageRaster.getSampleDouble(x, 0, b-1)-b) <= 1E-9);
            }
        }

        /*
         * Compare JAI and Interpolation results.
         */
        for (int b = 0; b<tINB; b++) {
            for (int y = tIminy+1; y<tIminy+tIH-1; y++) {
                for (int x = tIminx+1; x<tIminx+tIW-1; x++) {
                    assertTrue(Math.abs(rastresult.getSampleDouble(x, y, b) - coverageRaster.getSampleDouble(x, y, b)) <= 1E-9);
                }
            }
        }
    }

    /**
     * Test result obtained from biLinear interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiBiLinearTest() throws NoninvertibleTransformException, FactoryException, TransformException {

        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBilinear();
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        tIminy = tIminx = 0;
        tIH =  tIW = 8;
        tINB = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_BYTE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BILINEAR);
        setAffineMathTransform(2, 0, 0, 2, 0.5, 0.5);//decalage de 1/2 pour concordé au decalage de JAI.

        /*
         * Resampling
         */
        final Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{0, 0, 0});
        resample.fillImage();
        final Raster coverageRaster = targetImage.getTile(0, 0);

        //check border
        for (int b = 1; b <= 2; b++) {
            for (int y = tIminy; y < tIminy+tIH-1;y++) {
                assertTrue((coverageRaster.getSampleDouble(0, y, b-1)-b) <= 1E-9);
            }
            for (int x = tIminx; x < tIminx+tIW; x++) {
                assertTrue((coverageRaster.getSampleDouble(x, 0, b-1)-b) <= 1E-9);
            }
        }

        /*
         * Compare JAI and Interpolation results.
         */
        for (int b = 0; b<tINB; b++) {
            for (int y = tIminy+1; y<tIminy+tIH-1; y++) {
                for (int x = tIminx+1; x<tIminx+tIW-1; x++) {
                    assertTrue(Math.abs(rastresult.getSampleDouble(x, y, b) - coverageRaster.getSampleDouble(x, y, b)) <= 1E-9);
                }
            }
        }
    }

    /**
     * Test result obtained from biCubic interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiBiCubicTest() throws NoninvertibleTransformException, FactoryException, TransformException {
        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBicubic(8);
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult   = renderOp.getData();

        tIminy = tIminx = 0;
        tIH = tIW = 8;
        tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_BYTE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BICUBIC);
        setAffineMathTransform(2, 0, 0, 2, 0.5, 0.5);//decalage de 1/2 pour concordé au decalage de JAI.

        /*
         * Resampling
         */
        final Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{0, 0, 0});
        resample.fillImage();
        final Raster coverageRaster = targetImage.getTile(0, 0);

        /*
         * Compare JAI and Interpolation results.
         */
        for (int b = 0; b<tINB; b++) {
            for (int y = tIminy+3; y<tIminy+tIH-3; y++) {
                for (int x = tIminx+3; x<tIminx+tIW-3; x++) {
                    assertTrue(Math.abs(rastresult.getSampleDouble(x, y, b) - coverageRaster.getSampleDouble(x, y, b)) <= 1E-9);
                }
            }
        }
    }

    /**
     * Affect appropriate image for tests.
     *
     * @param minX lower corner pixel index in X direction.
     * @param minY lower corner pixel index in Y direction.
     * @param width image width.
     * @param height image height.
     * @param tileGridXOffset minimum tiles index in X direction.
     * @param tileGridYOffset minimum tiles index in Y direction.
     * @param tilesWidth width of each raster (tiles) from image.
     * @param tilesHeight height of each raster (tiles) from image.
     * @param dataType image data type.
     * @param numBand band number
     * @param value fill image with this value.
     */
    private void setTargetImage(int minX, int minY, int width, int height,
            int tileGridXOffset, int tileGridYOffset, int tilesWidth, int tilesHeight,
            int dataType, int numBand, double value) {

        final SampleModel targetSampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight, numBand, width*numBand, new int[]{0, 1, 2});
        targetImage = new TiledImage(minX, minY, width, height, tileGridXOffset, tileGridYOffset, targetSampleM, null);
        final int minTX = targetImage.getMinTileX();
        final int minTY = targetImage.getMinTileY();
        final int maxTX = minTX + width/tilesWidth;
        final int maxTY = minTY + height/tilesHeight;
        WritableRaster raster;

        int minx, miny;
        for (int tY = minTY; tY<maxTY; tY++) {
            for (int tX = minTX; tX<maxTX; tX++) {
                raster = targetImage.getWritableTile(tX, tY);
                minx = raster.getMinX();
                miny = raster.getMinY();
                for (int y = miny; y<miny + tilesHeight; y++) {
                    for (int x = minx; x<minx + tilesWidth; x++) {
                        for (int b = 0; b < numBand; b++) {
                            raster.setSample(x, y, b, value);
                        }
                    }
                }
            }
        }
    }

    /**
     * Affect appropriate interpolation about test.
     *
     * @param sourceImage image which will be iterate for tests.
     * @param interpolCase chosen interpolator.
     */
    private void setInterpolation(WritableRenderedImage sourceImage, InterpolationCase interpolCase) {
        interpolation = Interpolation.create(new PixelIteratorConform(sourceImage), interpolCase, 0);
    }

    /**
     * Affect MathTransform with appropriate test values.
     *
     * @param m00 the X coordinate scaling.
     * @param m10 the Y coordinate shearing.
     * @param m01 the X coordinate shearing.
     * @param m11 the Y coordinate scaling.
     * @param m02 the X coordinate translation.
     * @param m12 the Y coordinate translation.
     * @throws FactoryException
     */
    private void setAffineMathTransform(double m00, double m10, double m01, double m11, double m02, double m12) throws FactoryException {
        mathTransform = new AffineTransform2D(m00, m10, m01, m11, m02, m12);
    }
}
