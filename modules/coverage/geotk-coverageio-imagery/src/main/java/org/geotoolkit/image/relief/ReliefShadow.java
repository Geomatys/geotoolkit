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
import java.awt.image.WritableRenderedImage;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 * Travel a source image and a dem (Digital Elevation Model) image to compute shadow.<br/>
 * 
 * To Do : replace destination {@link BufferedImage} to destination {@link WritableLargeRenderedImage}. 
 * @author Rémi Marechal (Geomatys).
 */
public final class ReliefShadow {
    
    private static double PI = Math.PI;
    
    /**
     * Table which contain (X, Y) or (Y, X) couple pixel ordinate position in source image.
     * 
     * @see ReliefShadow#computeShadow(double) table use case.
     */
    private final static double[] TABV = new double[2];
    
    /**
     * Cosinus PI/4.<br/>
     * Permit to define along which axis we compute shadow.
     * @see ReliefShadow#getRelief(java.awt.image.RenderedImage, java.awt.image.RenderedImage, double) use case.
     */
    private final static double COS45 = Math.cos(PI/4);
    
    /**
     * Coefficient to attenuate pixel values. 
     */
    private final double shadowDimming;
    
    /**
     * {@link PixelIterator} which travel on source image.
     */
    private  PixelIterator srcIter;
    
    /**
     * {@link PixelIterator} which travel on dem image.
     */
    private  PixelIterator mntIter;
    
    /**
     * {@link PixelIterator} which travel on destination image which is resulting image from shadow computing.
     */
    private  PixelIterator destIter;
    
    /**
     * Source image minimum position on X axis.
     */
    private int minX;
    
    /**
     * Source image maximum position on X axis.
     */
    private int maxX;
    
    /**
     * Source image minimum position on Y axis.
     */
    private int minY;
    
    /**
     * Source image maximum position on Y axis.
     */
    private int maxY;
    
    /**
     * Angle in radian define by alpha = PI/2 - azimuth.
     */
    private double alpha;
    
    /**
     * Alpha cosinus.
     */
    private final double cosAlpha;
    
    /**
     * Alpha sinus.
     */
    private final double sinAlpha;
    
    /**
     * Alpha tangente.
     */
    private final double tanAlpha;
    
    /**
     * Source light tangente altitude.
     */
    private double tanAltitude;
    
    /**
     * Define for one pixel, how much the elevation go down.
     */
    private final double pash;
    
    /**
     * Define X axis ordinate position in {@link ReliefShadow#TABV}.
     * 
     * @see ReliefShadow#computeShadow(double) use case.
     */
    private final int ordinateX;
    
    /**
     * Define Y axis ordinate position in {@link ReliefShadow#TABV}.
     * 
     * @see ReliefShadow#computeShadow(double) use case.
     */
    private final int ordinateY;
    
    /**
     * The step of the choosen axis to follow image during shadow computing.
     * 
     * @see ReliefShadow#computeShadow(double) use case. 
     */
    private final int pasv;
    
    /**
     * The step of the other axis which is image of step {@link ReliefShadow#pasv}.<br/>
     * Note : pasfv = f(pasv).
     * 
     * @see ReliefShadow#computeShadow(double) use case.
     */
    private final double pasfv;
    
    /**
     * It is the same step like {@link ReliefShadow#pash} but expressed in DEM unit.
     * 
     * @see ReliefShadow#computeShadow(double) use case.
     */
    private double pasz;
    
    /**
     * Create an object to apply shadow on some {@link RenderedImage}.<br/><br/>
     * 
     * Note : if a pixel is defined as a shadow the destination pixel values are <br/> 
     * sampleValue * shadowDimming for each bands with shadowDimming attribut € [0; 1].
     * 
     * @param lightSRCAzimuth Light angle in degree from {@link RenderedImage} Y axe.
     * @param lightSRCAltitude Light angle in degree of the light from the ground.
     * @param shadowDimming dimming coefficient apply on each band on each pixel which are define as a shadow.
     * @throws IllegalArgumentException if shadow dimming is out from [0; 1] interval.
     */
    public ReliefShadow(final double lightSRCAzimuth, final double lightSRCAltitude, final double shadowDimming) {
        if (shadowDimming > 1 || shadowDimming < 0) {
            throw new IllegalArgumentException("shadowDimming should belong in [0; 1] interval. value found : "+shadowDimming);
        }
        this.shadowDimming = shadowDimming;
        alpha    = (PI/2) - ((lightSRCAzimuth % 360) * PI / 180);// on enleve les n 2kPI
        cosAlpha = Math.cos(alpha);
        sinAlpha = Math.sin(alpha);
        tanAlpha = Math.tan(alpha);
        this.tanAltitude = Math.tan(Math.abs(lightSRCAltitude % 360) * PI / 180);
        
        if (Math.abs(cosAlpha) >= COS45) {
            // alpha € [-PI/4 ; PI/4] U [3PI/4 ; 5PI/4] + 2kPI
            // we iterate along x axis
            ordinateX = 0;
            ordinateY = 1;
            
            // step on x axis.
            pasv = (int) Math.signum(cosAlpha);
            
            // step on y axis.
            pasfv = tanAlpha * pasv;
            
            // step on z axis.
            pash = - Math.abs(1/cosAlpha) * tanAltitude;
            
        } else {
            // alpha € ]PI/4 ; 3PI/4[ U ]5PI/4 ; 7PI/4[ + 2kPI
            // we iterate along y axis.
            ordinateX = 1;
            ordinateY = 0;
            
            // step on Y axis.
            pasv = (int) Math.signum(sinAlpha);
            
            // step on x axis.
            pasfv = pasv/tanAlpha;
            
            // step on z axis.
            pash = - Math.abs(1/sinAlpha) * tanAltitude;
        }
    }
    
    
    /**
     * Compute and return an appropriate {@link RenderedImage} which is a copy of source image with shadow added.
     * 
     * @param imgSource {@link RenderedImage} with no relief shadow.
     * @param dem {@link RenderedImage} which contain all pixel elevations values (Digital Elevation Model).
     * @param scaleZ elevation value of a pixel.
     * @throws NullArgumentException if imgSource or mnt is {@code null}.
     * @throws IllegalArgumentException if scaleZ is lesser or equal to zero.
     * @throws IllegalArgumentException if source image width and mnt image have a differente width.
     * @throws IllegalArgumentException if source image width and mnt image have a differente height.
     * @return an appropriate {@link RenderedImage} witch is a copy of source image with shadow added.
     */
    public RenderedImage getRelief(final RenderedImage imgSource, final RenderedImage dem, final double scaleZ) {
        ArgumentChecks.ensureNonNull("source image", imgSource);
        ArgumentChecks.ensureNonNull("MNT", dem);
        ArgumentChecks.ensureStrictlyPositive("pixel altitude", scaleZ);
        final int imgWidth  = imgSource.getWidth();
        final int imgHeight = imgSource.getHeight();
        if (dem.getWidth() != imgWidth) {
            throw new IllegalArgumentException("mnt image and source image should have same width. image source width = "
                    +imgWidth+" mnt width = "+dem.getWidth());
        }
        if (dem.getHeight() != imgHeight) {
            throw new IllegalArgumentException("mnt image and source image should have same height. image source height = "
                    +imgWidth+" mnt height = "+dem.getWidth());
        }
        this.minX = imgSource.getMinX();
        this.maxX = minX + imgWidth;
        this.minY = imgSource.getMinY();
        this.maxY = minY + imgHeight;
        
        // define step altitude, when iterator travel up along v axis. 
        pasz      = pash * scaleZ;
        
        final WritableRenderedImage imgDest = new BufferedImage(imgSource.getColorModel(), imgSource.getColorModel().createCompatibleWritableRaster(imgWidth, imgHeight), false, null);
        //        imgDest = new WritableLargeRenderedImage(imgWidth, imgHeight, imgSource.getColorModel());
        
        srcIter            = PixelIteratorFactory.createRowMajorIterator(imgSource);
        final int numBand  = srcIter.getNumBands();
        mntIter            = PixelIteratorFactory.createRowMajorIterator(dem);
        destIter           = PixelIteratorFactory.createRowMajorWriteableIterator(imgDest, imgDest);
        
        // iteration attribut
        final int iterBeginX;
        final int iterPasX;
        
        int iterBeginY;
        final int iterPasY;
        
        // we define destination image iteration sens, in function of alpha angle value.
        if (cosAlpha >= 0) {
            // travel left to right on X axis.
                iterBeginX = minX;
                iterPasX   = 1;
            if (sinAlpha >= 0) {
                // lower left corner
                // travel down to up on Y axis.
                iterBeginY = minY;
                iterPasY   = 1;
            } else {
                // upper left corner
                // travel up to down on Y axis
                iterBeginY = maxY-1;
                iterPasY   = -1;
            }
        } else {
            // travel right to left on X axis.
                iterBeginX = maxX-1;
                iterPasX   = -1;
            if (sinAlpha >= 0) {
                // lower right corner
                // travel down to up on Y axis.
                iterBeginY = minY;
                iterPasY   = 1;
            } else {
                // upper right corner
                // travel up to down on Y axis
                iterBeginY = maxY-1;
                iterPasY   = -1;
            }
        }
        
        while (iterBeginY >= minY && iterBeginY < maxY) {
            int x = iterBeginX;
            while (x >= minX && x < maxX) {
                srcIter.moveTo(x, iterBeginY, 0);
                destIter.moveTo(x, iterBeginY, 0);
                if (destIter.getSample() == 1) {
                    // already define as a shadow
                    for (int b = 0; b < numBand; b++) {
                        destIter.setSampleDouble(srcIter.getSampleDouble() * shadowDimming);
                        srcIter.next();
                        destIter.next();
                    }
                } else {
                    // current pixel is a pikes.
                    for (int b = 0; b < numBand; b++) {
                        destIter.setSampleDouble(srcIter.getSampleDouble());
                        srcIter.next();
                        destIter.next();
                    }
                    mntIter.moveTo(x, iterBeginY, 0);
                    final double z  = mntIter.getSampleDouble();
                    TABV[ordinateX] = x;
                    TABV[ordinateY] = iterBeginY;
                    computeShadow(z);
                }
                x += iterPasX;
            }
            iterBeginY += iterPasY;
        }
        return imgDest;
    }
    
    /**
     * Compute shadow produced by current pixel (pikes) at {@link ReliefShadow#TABV} position.
     * 
     * @param z pixel elevation of current pixel (pikes).
     */
    private void computeShadow(final double z) {
        // position on pixel center and go to next pixel
        TABV[0] += 0.5 + pasv;
        TABV[1] += 0.5 + pasfv;
        
        // define pixel x, y position
        int ordX  = (int) TABV[ordinateX];
        int ordY  = (int) TABV[ordinateY];
        
        // current altitude.
        double sz = z + pasz;
        
        while (ordX >= minX && ordX < maxX && ordY >= minY && ordY < maxY) {
            
            mntIter.moveTo(ordX, ordY, 0);
            final double currentZ = mntIter.getSampleDouble();
            if (currentZ > sz) return;
            destIter.moveTo(ordX, ordY, 0);
            // set an arbitrary value just to define a shadow pixel.
            destIter.setSample(1);
            TABV[0] += pasv;
            TABV[1] += pasfv;
            sz      += pasz;
            
            // define pixel x, y position
            ordX = (int) TABV[ordinateX];
            ordY = (int) TABV[ordinateY];
        }
    }
}
