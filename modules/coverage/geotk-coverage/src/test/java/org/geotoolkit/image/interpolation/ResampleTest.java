/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.image.*;
import javax.media.jai.TiledImage;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ResampleTest {

    MathTransform mathTransform;

    Interpolation interpolation;

    WritableRenderedImage targetImage;

    private final DefaultMathTransformFactory defMatFact;

    public ResampleTest() {
        defMatFact = new DefaultMathTransformFactory();
    }

    @Test
    public void resampleFactor2BicubicTest() throws FactoryException, NoninvertibleTransformException, TransformException {

        setTargetImage(-4, -4, 8, 8, -4, -4, 8, 8, DataBuffer.TYPE_DOUBLE, 1, Double.NaN);

        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_INT, 4, 4, 1);
        WritableRenderedImage sourceImage = new TiledImage(-2, -2, 4, 4, -2, -2, sourceSampleM, null);
        int pixVal;
        for (int y = -2; y<2; y++) {
            for (int x = -2; x<2; x++) {
                pixVal = (int) Math.hypot(x, y);
                if(x+y >= 0)pixVal++;
                sourceImage.getWritableTile(0, 0).setSample(x, y, 0, pixVal);
            }
        }

        Matrix mtrans = Matrices.create(3, 3);//x2
        mtrans.setElement(0, 0, 2); mtrans.setElement(0, 1, 0); mtrans.setElement(0, 2, 0);
        mtrans.setElement(1, 0, 0); mtrans.setElement(1, 1, 2); mtrans.setElement(1, 2, 0);
        mtrans.setElement(2, 0, 0); mtrans.setElement(2, 1, 0); mtrans.setElement(2, 2, 1);
        mathTransform = defMatFact.createAffineTransform(mtrans);
        setInterpolation(sourceImage, InterpolationCase.BICUBIC);
        Resample resample = new Resample(mathTransform, targetImage, interpolation);
        resample.fillImage();
        testInternalValues(targetImage, new double[]{0.5, 3});//valeur arbitraire
   }

    @Test
    public void resampleFactor2BilinearTest() throws FactoryException, NoninvertibleTransformException, TransformException {
//        final BandedSampleModel targetSampleM = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 8, 8, 1);
//        targetImage = new TiledImage(-4, -4, 8, 8, -4, -4, targetSampleM, null);
//        for (int y = -4; y<4; y++) {
//            for (int x = -4; x<4; x++) {
//                targetImage.getWritableTile(0, 0).setSample(x, y, 0, Double.NaN);
//            }
//        }
        setTargetImage(-4, -4, 8, 8, -4, -4, 8, 8, DataBuffer.TYPE_DOUBLE, 1, Double.NaN);

        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_INT, 4, 4, 1);
        WritableRenderedImage sourceImage = new TiledImage(-2, -2, 4, 4, -2, -2, sourceSampleM, null);
        int pixVal;
        for (int y = -2; y<2; y++) {
            for (int x = -2; x<2; x++) {
                pixVal = (int) Math.hypot(x, y);
                if(x+y >= 0)pixVal++;
                sourceImage.getWritableTile(0, 0).setSample(x, y, 0, pixVal);
            }
        }

        Matrix mtrans = Matrices.create(3, 3);
        mtrans.setElement(0, 0, 2); mtrans.setElement(0, 1, 0); mtrans.setElement(0, 2, 0);
        mtrans.setElement(1, 0, 0); mtrans.setElement(1, 1, 2); mtrans.setElement(1, 2, 0);
        mtrans.setElement(2, 0, 0); mtrans.setElement(2, 1, 0); mtrans.setElement(2, 2, 1);
        mathTransform = defMatFact.createAffineTransform(mtrans);
        setInterpolation(sourceImage, InterpolationCase.BILINEAR);
        Resample resample = new Resample(mathTransform, targetImage, interpolation);
        resample.fillImage();
        testInternalValues(targetImage, 1, 2);
   }

    private void setTargetImage(int minX, int minY, int width, int height,
            int tileGridXOffset, int tileGridYOffset, int tilesWidth, int tilesHeight,
            int dataType, int numBand, double value) {
        final BandedSampleModel targetSampleM = new BandedSampleModel(dataType, tilesWidth, tilesHeight, numBand);
        targetImage = new TiledImage(minX, minY, width, height, tileGridXOffset, tileGridYOffset, targetSampleM, null);
        int minTX = targetImage.getMinTileX();//par le calcul ca se fait
        int minTY = targetImage.getMinTileY();
        int maxTX = minTX + width/tilesWidth;
        int maxTY = minTY + height/tilesHeight;
        WritableRaster raster;
        int minx, miny;
        for (int tY = minTY; tY<maxTY; tY++) {
            for (int tX = minTX; tX<maxTX; tX++) {
                raster = targetImage.getWritableTile(tX, tY);
                minx = raster.getMinX();
                miny = raster.getMinY();
                for (int y = miny; y<miny+tilesHeight; y++) {
                    for (int x = minx; x<minx+tilesWidth; x++) {
                        for (int b = 0; b<numBand; b++) {
                            raster.setSample(x, y, b, value);
                        }
                    }
                }
            }
        }
    }

    private void setInterpolation(WritableRenderedImage sourceImage, InterpolationCase interpolCase) {
        interpolation = Interpolation.create(PixelIteratorFactory.createDefaultIterator(sourceImage), interpolCase, 0);
    }

    /**
     * min and max values for each renderedImage bands.<br/>
     * <var>min<sub>0</sub></var> means : min from band 0.<br/>
     * <var>max<sub>0</sub></var> means : max from band 0.<br/>
     * <var>min<sub>n</sub></var> means : min from nth band.<br/>
     * <var>max<sub>n</sub></var> means : max from nth band.<br/><br/>
     *
     * <var>min<sub>0</sub></var>, <var>max<sub>0</sub></var>,
     * <var>min<sub>1</sub></var>, <var>max<sub>1</sub></var>, ... ,
     * <var>min<sub>n</sub></var>, <var>max<sub>n</sub></var>
     *
     * @param image
     * @param minMax
     */
    private void testInternalValues(RenderedImage image, double ...minMax) {
        final int minTY  = image.getMinTileY();
        final int minTX  = image.getMinTileX();
        final int tilesWidth  = image.getNumXTiles();
        final int tilesHeigth = image.getNumYTiles();
        int minx, miny, width, heigth, band, maxx, maxy;
        Raster raster;
        double sample;
        for (int tY = minTY; tY<minTY+tilesHeigth; tY++) {
            for (int tX = minTX; tX<minTX+tilesWidth; tX++) {
                raster = image.getTile(tX, tY);
                minx   = raster.getMinX();
                miny   = raster.getMinY();
                heigth = raster.getHeight();
                width  = raster.getWidth();
                band   = raster.getNumBands();
                maxx = minx+width;
                maxy = miny+heigth;
                if (tX == minTX+tilesWidth -1) {
                    maxx--;
                    for (int y = miny; y<miny + heigth; y++) {
                        for (int b = 0; b<band; b++) {
                            sample = raster.getSampleDouble(maxx, y, b);
                            assertTrue(Double.isNaN(sample));
                        }
                    }
                }
                if (tY == minTY+tilesHeigth-1) {
                    maxy--;
                    for (int x = minx; x<minx + width; x++) {
                        for (int b = 0; b<band; b++) {
                            sample = raster.getSampleDouble(x, maxy, b);
                            assertTrue(Double.isNaN(sample));
                        }
                    }
                }
                for (int y = miny; y<maxy; y++) {
                    for (int x = minx; x<maxx; x++) {
                        for (int b = 0; b<band; b++) {
                            sample = raster.getSampleDouble(x, y, b);
                            assertTrue(sample >= minMax[0]-1E-12 && sample <= minMax[1]+1E-12);
                        }
                    }
                }
            }
        }
    }
}
