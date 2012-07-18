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

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.media.jai.*;
import javax.media.jai.operator.AffineDescriptor;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.datum.PixelInCell;
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

    TiledImage sourceImg;

    private final DefaultMathTransformFactory defMatFact;

    public ResampleTest() {
        defMatFact = new DefaultMathTransformFactory();

        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 4, 4, 3);
        sourceImg = new TiledImage(0, 0, 4, 4, 0, 0, sourceSampleM, null);

        final WritableRaster raster = sourceImg.getWritableTile(0, 0);
        final int minx = raster.getMinX();
        final int miny = raster.getMinY();
        final int height = raster.getHeight();
        final int width = raster.getWidth();
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

    @Ignore
    @Test
    public void jaiBiLinearTest() throws NoninvertibleTransformException, FactoryException, TransformException {

        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBilinear();
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        final int tIminy = 0;
        final int tIminx = 0;
        final int tIH    = 8;
        final int tIW    = 8;
        final int tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_DOUBLE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BILINEAR);
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
            for (int y = tIminy+1; y<tIminy+tIH-1; y++) {
                for (int x = tIminx+1; x<tIminx+tIW-1; x++) {
                    assertTrue(Math.abs(rastresult.getSampleDouble(x, y, b) - coverageRaster.getSampleDouble(x, y, b)) <= 1E-9);
                }
            }
        }
    }

    @Ignore
    @Test
    public void jaiBiCubicTest() throws NoninvertibleTransformException, FactoryException, TransformException {
        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBicubic(8);
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        final int tIminy = 0;
        final int tIminx = 0;
        final int tIH    = 8;
        final int tIW    = 8;
        final int tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_DOUBLE, tINB, -1000);
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

    //a la limite le faire avec un raster plus gros pour plus d'exhaustivité
    @Test
    public void coverageBiCubicTest() throws NoninvertibleTransformException, FactoryException, TransformException {
        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBicubic(8);
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        final int tIminy = 0;
        final int tIminx = 0;
        final int tIH    = 8;
        final int tIW    = 8;
        final int tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_DOUBLE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BICUBIC);
        setAffineMathTransform(2, 0, 0, 2, 0.5, 0.5);//lag 1/2 pixel about JAI made.

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
//                    System.out.println("f("+x+", "+y+") = "+coverageRaster.getSampleDouble(x, y, b)+" "+rastresult.getSampleDouble(x, y, b));
                }
            }
        }

//        /*
//         * Compare JAI and Interpolation results.
//         */
//        for (int b = 0; b<tINB; b++) {
//            for (int y = tIminy; y<tIminy+tIH; y++) {
//                for (int x = tIminx; x<tIminx+tIW; x++) {
//                    System.out.println("f("+x+", "+y+") = "+coverageRaster.getSampleDouble(x, y, b));
//                }
//            }
//        }
    }

    @Test
    public void coverageBiCubicTest2() throws NoninvertibleTransformException, FactoryException, TransformException {
        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBicubic(8);
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        final int tIminy = 0;
        final int tIminx = 0;
        final int tIH    = 8;
        final int tIW    = 8;
        final int tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_DOUBLE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BICUBIC);
        setAffineMathTransform(2, 0, 0, 2, 0, 0);//lag 1/2 pixel about JAI made.

        /*
         * Resampling
         */
        final Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{0, 0, 0});
        resample.fillImage();
        final Raster coverageRaster = targetImage.getTile(0, 0);

//        /*
//         * Compare JAI and Interpolation results.
//         */
//        for (int b = 0; b<tINB; b++) {
//            for (int y = tIminy+3; y<tIminy+tIH-3; y++) {
//                for (int x = tIminx+3; x<tIminx+tIW-3; x++) {
//                    assertTrue(Math.abs(rastresult.getSampleDouble(x, y, b) - coverageRaster.getSampleDouble(x, y, b)) <= 1E-9);
////                    System.out.println("f("+x+", "+y+") = "+coverageRaster.getSampleDouble(x, y, b)+" "+rastresult.getSampleDouble(x, y, b));
//                }
//            }
//        }

        /*
         * Compare JAI and Interpolation results.
         */
        for (int b = 0; b<tINB; b++) {
            for (int y = tIminy; y<tIminy+tIH; y++) {
                for (int x = tIminx; x<tIminx+tIW; x++) {
                    System.out.println("f("+x+", "+y+") = "+coverageRaster.getSampleDouble(x, y, b));
                }
            }
        }
    }

    @Ignore
    @Test
    public void jaiBiCubic2Test() throws NoninvertibleTransformException, FactoryException, TransformException {

        /*
         * jai resampling
         */
        final AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);
        final javax.media.jai.Interpolation jaiInterpol =  new InterpolationBicubic2(8);
        final RenderedOp renderOp = AffineDescriptor.create(sourceImg, affTransform, jaiInterpol, new double[]{Double.NaN, Double.NaN, Double.NaN}, null);
        final Raster rastresult = renderOp.getData();

        final int tIminy = 0;
        final int tIminx = 0;
        final int tIH    = 8;
        final int tIW    = 8;
        final int tINB   = 3;

        setTargetImage(tIminx, tIminy, tIW, tIH, tIminx, tIminy, tIW, tIH, DataBuffer.TYPE_DOUBLE, tINB, -1000);
        setInterpolation(sourceImg, InterpolationCase.BICUBIC2);
        setAffineMathTransform(2, 0, 0, 2, 0.5, 0.5);//décalage de 1/2 pour concorder au decalage de JAI.

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
     * @param interpolCase chosen interpolation.
     */
    private void setInterpolation(WritableRenderedImage sourceImage, InterpolationCase interpolCase) {
        interpolation = Interpolation.create(PixelIteratorFactory.createDefaultIterator(sourceImage), interpolCase, 0);
    }

    /**
     *
     * @param mii
     * @throws FactoryException
     */
    private void setAffineMathTransform(double m00, double m10, double m01, double m11, double m02, double m12) throws FactoryException {
        mathTransform = new AffineTransform2D(m00, m10, m01, m11, m02, m12);
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
