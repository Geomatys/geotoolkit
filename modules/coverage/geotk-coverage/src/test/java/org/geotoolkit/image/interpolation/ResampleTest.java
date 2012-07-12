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
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.media.jai.*;
import javax.media.jai.operator.AffineDescriptor;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
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

    /**
     *
     * @throws FactoryException
     * @throws NoninvertibleTransformException
     * @throws TransformException
     */
    @Ignore
    @Test
    public void resampleFactor2BicubicTest() throws FactoryException, NoninvertibleTransformException, TransformException {

        setTargetImage(-4, -4, 8, 8, -4, -4, 8, 8, DataBuffer.TYPE_DOUBLE, 1, -1000);

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
        Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{Double.NaN});
        resample.fillImage();
        testInternalValues(targetImage, new double[]{0.5, 3});//valeur arbitraire
   }

    @Test
    public void jaiTest() throws NoninvertibleTransformException, TransformException, FactoryException {
        /*
         * espace de couleur : cyan jaune magenta, RGB, ...
         *
         * conversion d'un espace de couleur vers RGB.
         */
        ColorSpace colorspace = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        /*
         * lien entre sample model et color space
         * alpha : 0 -> 255 : totalement transparent -> opaque
         * parametre Transparency.opaque : si on l'a veut opaque
         *                       .transparency : si on l'a veut transparente
         */
        ComponentColorModel colorModel = new ComponentColorModel(colorspace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
//        WritableRaster raster = colorModel.createCompatibleWritableRaster(4, 4);
//        WritableRaster rasterDest = colorModel.createCompatibleWritableRaster(4, 4);
//        BufferedImage buffImg  = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        AffineTransformOp affT = new AffineTransformOp(new AffineTransform(2, 0, 0, 2, 0, 0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//        WritableRaster rast = buffImg.getRaster();
//        WritableRaster rast = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, 4, 4, 3, new Point(0, 0));

        AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);


        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 4, 4, 3);
        TiledImage renderImg = new TiledImage(0, 0, 4, 4, 0, 0, sourceSampleM, null);
        WritableRaster raster = renderImg.getWritableTile(0, 0);
        int numb = raster.getNumBands();
        int minx = raster.getMinX();
        int miny = raster.getMinY();
        int height = raster.getHeight();
        int width = raster.getWidth();
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

        Raster rasterDest = AffineDescriptor.create(renderImg, affTransform, new InterpolationBilinear(8), new double[]{10,10,10}, null).getData();


        Matrix mtrans = Matrices.create(3, 3);
        mtrans.setElement(0, 0, 2); mtrans.setElement(0, 1, 0); mtrans.setElement(0, 2, 0);
        mtrans.setElement(1, 0, 0); mtrans.setElement(1, 1, 2); mtrans.setElement(1, 2, 0);
        mtrans.setElement(2, 0, 0); mtrans.setElement(2, 1, 0); mtrans.setElement(2, 2, 1);
        mathTransform = defMatFact.createAffineTransform(mtrans);
        setTargetImage(0, 0, 8, 8, 0, 0, 8, 8, DataBuffer.TYPE_DOUBLE, 3, 0);
        setInterpolation(renderImg, InterpolationCase.BILINEAR);
        Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{Double.NaN, Double.NaN, Double.NaN});
        resample.fillImage();



//        WritableRaster rasterDest = affT.createCompatibleDestRaster(raster);

//        WritableRaster rasterDest = affT.filter(raster, null);
        System.out.println("");
        for (int b = 0; b<rasterDest.getNumBands(); b++) {
            for (int y = rasterDest.getMinY(); y<rasterDest.getMinY()+rasterDest.getHeight(); y++) {
                for (int x = rasterDest.getMinX(); x<rasterDest.getMinX()+rasterDest.getWidth(); x++) {
                    if (b==0) System.out.println(rasterDest.getSampleDouble(x, y, b)+" "+targetImage.getData().getSampleDouble(x, y, b));
                }
            }
        }

    }

    @Ignore
    @Test
    public void jaiBilineairTest() {
        /*
         * espace de couleur : cyan jaune magenta, RGB, ...
         *
         * conversion d'un espace de couleur vers RGB.
         */
        ColorSpace colorspace = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        /*
         * lien entre sample model et color space
         * alpha : 0 -> 255 : totalement transparent -> opaque
         * parametre Transparency.opaque : si on l'a veut opaque
         *                       .transparency : si on l'a veut transparente
         */
        ComponentColorModel colorModel = new ComponentColorModel(colorspace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
        AffineTransformOp affT = new AffineTransformOp(new AffineTransform(2, 0, 0, 2, 0, 0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        AffineTransform affTransform = new AffineTransform(2, 0, 0, 2, 0, 0);


        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, 4, 4, 3);
        TiledImage renderImg = new TiledImage(0, 0, 4, 4, 0, 0, sourceSampleM, null);
        WritableRaster raster = renderImg.getWritableTile(0, 0);
        int minx = raster.getMinX();
        int miny = raster.getMinY();
        int height = raster.getHeight();
        int width = raster.getWidth();
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

//        BufferedImage buffImg  = new BufferedImage(colorModel, raster, false, null);
        InterpolationBilinear jaiInterpol =  new InterpolationBilinear(8);
        double jaiVal;
        for (float x = 0; x<=1; x+=0.1) {
            jaiVal = jaiInterpol.interpolateH(new double[]{10, 20}, x);
            System.out.println("jaiInterpol : f("+x+") = "+jaiVal);
        }
    }


    /**
     *
     * @throws FactoryException
     * @throws NoninvertibleTransformException
     * @throws TransformException
     */
    @Ignore
    @Test
    public void resampleFactor2BilinearTest() throws FactoryException, NoninvertibleTransformException, TransformException {
        setTargetImage(-4, -4, 8, 8, -4, -4, 8, 8, DataBuffer.TYPE_DOUBLE, 1, -1000);

        final BandedSampleModel sourceSampleM = new BandedSampleModel(DataBuffer.TYPE_INT, 4, 4, 1);
        WritableRenderedImage sourceImage = new TiledImage(-2, -2, 4, 4, -2, -2, sourceSampleM, null);
        int pixVal;
        for (int y = -2; y<2; y++) {
            for (int x = -2; x<2; x++) {
                pixVal = (int) Math.hypot(x, y);
                if (x+y >= 0) pixVal++;
                sourceImage.getWritableTile(0, 0).setSample(x, y, 0, pixVal);
            }
        }

        Matrix mtrans = Matrices.create(3, 3);
        mtrans.setElement(0, 0, 2); mtrans.setElement(0, 1, 0); mtrans.setElement(0, 2, 0);
        mtrans.setElement(1, 0, 0); mtrans.setElement(1, 1, 2); mtrans.setElement(1, 2, 0);
        mtrans.setElement(2, 0, 0); mtrans.setElement(2, 1, 0); mtrans.setElement(2, 2, 1);
        mathTransform = defMatFact.createAffineTransform(mtrans);
        setInterpolation(sourceImage, InterpolationCase.BILINEAR);
        Resample resample = new Resample(mathTransform, targetImage, interpolation, new double[]{Double.NaN});
        resample.fillImage();
        testInternalValues(targetImage, 1, 2);
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
