/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.relief;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 *
 * @author rmarechal
 */
public class ReliefShadow {
    
    private static double PI = Math.PI;
    
    /**
     * {@link RenderedImage} with no relief shadow.
     */
    private RenderedImage imgSource;
    
    /**
     * {@link RenderedImage} which contain all pixel elevation values.
     */
    private RenderedImage mnt;
    
    /**
     * {@link RenderedImage} which is a copy of {@link ReliefShadow#imgSource} with relief shadow. 
     */
    private WritableRenderedImage imgDest;
    
//    /**
//     * Angle in degree from {@link RenderedImage} Y axe. 
//     */
//    private final double lightSRCAzimuth;
//    
//    /**
//     * Angle in degree of the light from the ground.
//     */
//    private final double lightSRCAltitude;
    
    /**
     * Coefficient to attenuate pixel values. 
     */
    private final double shadowDimming;
    
    private int imgWidth;
    private int imgHeight;
    
    private  PixelIterator srcIter;
    
    private  PixelIterator mntIter;
    
    private  PixelIterator destIter;
    
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int numBand;
    private final double cosAlpha;
    private final double sinAlpha;
    private final double tanAlpha;
    private double tanAltitude;
    private double alpha;
    private final double pash;
    private final int ordinateX;
    private final int ordinateY;
    private final int pasv;
    private final double pasfv;
    private double pasz;
    
            
    private final static double[] TABV = new double[2];
    private final static double COS45 = Math.cos(PI/4);

    
    

    /**
     * 
     * @param imgSource {@link RenderedImage} with no relief shadow.
     * @param mnt {@link RenderedImage} which contain all pixel elevations values.
     * @param lightSRCAzimuth Light angle in degree from {@link RenderedImage} Y axe.
     * @param lightSRCAltitude Light angle in degree of the light from the ground.
     * @param shadowDimming 
     */
    public ReliefShadow(double lightSRCAzimuth, double lightSRCAltitude, double shadowDimming) {
        
        this.shadowDimming = shadowDimming;
        alpha    = (PI/2) - ((lightSRCAzimuth % 360) * PI / 180);// on enleve les n 2kPI
        cosAlpha = Math.cos(alpha);
        sinAlpha = Math.sin(alpha);
        tanAlpha = Math.tan(alpha);
        
        if (Math.abs(cosAlpha) >= COS45) {
            // if alpha € [-PI/4 ; PI/4] U [3PI/4 ; 5PI/4] + 2kPI
            // we iterate along x axis
            ordinateX = 0;
            ordinateY = 1;
            
            // step on x axis
            pasv = (int) Math.signum(cosAlpha);
            pasfv = tanAlpha * pasv;
            pash = Math.abs(1/cosAlpha);
            
        } else {
            //if alpha € ]PI/4 ; 3PI/4[ U ]5PI/4 ; 7PI/4[ + 2kPI
            // we iterate along y axis.
            ordinateX = 1;
            ordinateY = 0;
            
            // step on Y axis.
            pasv = (int) Math.signum(sinAlpha);
            // step on x axis.
            pasfv = pasv/tanAlpha;
            pash = Math.abs(1/sinAlpha);
        }
        
        
        this.tanAltitude = Math.tan(Math.abs(lightSRCAltitude) * PI / 180);
        
//        imgDest = new WritableLargeRenderedImage(imgWidth, imgHeight, imgSource.getColorModel());
        
    }
    
    
    
    public RenderedImage getRelief(final RenderedImage imgSource, final RenderedImage mnt, double scaleZ) {
        this.imgSource = imgSource;
        this.imgWidth = imgSource.getWidth();
        this.imgHeight = imgSource.getHeight();
        this.minX = imgSource.getMinX();
        this.maxX = minX + imgWidth;
        this.minY = imgSource.getMinY();
        this.maxY = minY + imgHeight;
        this.mnt = mnt;
        // define step altitude, when iterator travel up along v axis. 
        pasz = - pash * tanAltitude * scaleZ;
//        imgDest = new BufferedImage(imgWidth, imgHeight, imgSource.getColorModel().get);
        
        imgDest = new BufferedImage(imgSource.getColorModel(), imgSource.getColorModel().createCompatibleWritableRaster(imgWidth, imgHeight), false, null);
        
        srcIter  = PixelIteratorFactory.createRowMajorIterator(this.imgSource);
        numBand = srcIter.getNumBands();
        mntIter  = PixelIteratorFactory.createRowMajorIterator(this.mnt);
        destIter = PixelIteratorFactory.createRowMajorWriteableIterator(this.imgDest, this.imgDest);
        
        // recopie on pourrai l'evitée
        while (srcIter.next()) {
            destIter.next();
            destIter.setSampleDouble(srcIter.getSampleDouble());
        }
        srcIter.rewind();
        destIter.rewind();
        
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                srcIter.moveTo(x, y, 0);
                mntIter.moveTo(x, y, 0);
                final double z = mntIter.getSampleDouble();
                TABV[ordinateX] = x;
                TABV[ordinateY] = y;
                computeShadow(z);
            }
        }
        
        return imgDest;
    }
    
    private void computeShadow(final double z) {
        // position on pixel center and follow at next pixel
        TABV[0] += 0.5 + pasv;
        TABV[1] += 0.5 + pasfv;
        
        // define pixel x,y position
        int ordX = (int)TABV[ordinateX];
        int ordY = (int)TABV[ordinateY];
        // current altitude.
        double sz = z + pasz;
        
        while (ordX >= minX && ordX < maxX && ordY >= minY && ordY < maxY) {
            
            mntIter.moveTo(ordX, ordY, 0);
            final double currentZ = mntIter.getSampleDouble();
            if (currentZ > sz) return;
            srcIter.moveTo(ordX, ordY, 0);
            destIter.moveTo(ordX, ordY, 0);
            for (int b = 0; b < numBand; b++) {
                destIter.setSampleDouble(srcIter.getSampleDouble() * shadowDimming);
                srcIter.next();
                destIter.next();
            }
            TABV[0] += pasv;
            TABV[1] += pasfv;
            sz += pasz;
            
            // define pixel x,y position
            ordX = (int)TABV[ordinateX];
            ordY = (int)TABV[ordinateY];
        }
    }
}
