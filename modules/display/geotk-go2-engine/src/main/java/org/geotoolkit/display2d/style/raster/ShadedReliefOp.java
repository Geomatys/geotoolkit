/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style.raster;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import org.geotoolkit.internal.image.ColorUtilities;

/**
 * JAI operation to return a Shaded relief of the gridCoverage.
 * The provided RenderedImage source must have height values stored in meter
 * values.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShadedReliefOp extends AreaOpImage{

    private static final ColorModel SHADOW_MODEL;
    private static final double DEFAULT_AZIMUTH = 315;
    private static final double DEFAULT_ALTITUDE = 45;
    private static final double DEFAULT_ZFACTOR = 1f;
    static{
        final int[] ARGB = new int[360];
        final int black = Color.BLACK.getRGB();
        final int white = Color.WHITE.getRGB();

        for(int i=0; i<180; i++){
            int alpha = (int) (((180-i)/180f)*255f);
            int saturation = 0;
            ARGB[i] =  (saturation ) | (saturation << 8) | (saturation << 16) | (alpha << 24);
//            ARGB[i] = white | (alpha << 24);
        }

        ARGB[180] = 0;

        for(int i=181; i<360; i++){
            int alpha = (int) ( ((i-180)/180f) *255f);
            ARGB[i] = (0 ) | (0 << 8) | (0 << 16) | (0 << 24);
//            ARGB[i] = black | (alpha << 24);
        }

        SHADOW_MODEL = ColorUtilities.getIndexColorModel(ARGB, 1, 1);
    }

    public ShadedReliefOp(RenderedImage source, ImageLayout layout, Map configuration, BorderExtender extender){
        super(source,
              ((layout != null)? layout.setColorModel(SHADOW_MODEL) :
                  new ImageLayout().setColorModel(SHADOW_MODEL)),
              configuration,
              false,
              extender,
              0,
              0,
              0,
              0);
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        final PlanarImage source = sources[0];


        /* PUPROSE: Calculate hillshade for a digital elevation model (DEM)
         -------------------------------------------------------------------
         USAGE: h = hillshade(dem,X,Y,varagin)
         where: dem is the DEM to calculate hillshade for
                X and Y are the DEM coordinate vectors
                varargin are parameters options

         OPTIONS:
                'azimuth'  is the direction of lighting in deg (default 315)
                'altitude' is the altitude of the lighting source in
                           in degrees above horizontal (default 45)
                'zfactor'  is the DEM altitude scaling z-factor (default 1)

         EXAMPLE:
               h=hillshade(peaks(50),1:50,1:50,'azimuth',45,'altitude',100,'plotit')
               - calculates the hillshade for an example 50x50 peak surface.
               - changes the default settings for azimuth and altitude.
               - creates an output hillshade plot

         Note: Uses simple unweighted gradient of 4 nearest neighbours for slope
               calculation (instead of Horn's method) with ESRIs hillshade
               algorithm.

         Felix Hebeler, Dept. of Geography, University Zurich, February 2007.
         modified by Andrew Stevens (astevens@usgs.gov), 5/04/2007 */

        //configure inputs
        //default parameters
        double azimuth = DEFAULT_AZIMUTH;
        double altitude = DEFAULT_ALTITUDE;
        double zf = DEFAULT_ZFACTOR;

        // lighting azimuth
        // convert to mathematic unit
        azimuth  = 360.0-azimuth+90;
        azimuth %= 360;
        azimuth  = Math.toRadians(azimuth);

        // lighting altitude
        // convert to zenith angle in radians
        altitude = Math.toRadians(90-altitude);

        //calculate convolved images -------------------------------------------
        final KernelJAI kernelX = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
        final KernelJAI kernelY = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;

        final ParameterBlock pbx = new ParameterBlock();
        pbx.addSource(source);
        pbx.add(kernelX);
        final RenderedImage fx = JAI.create("convolve", pbx, null);

        final ParameterBlock pby = new ParameterBlock();
        pby.addSource(source);
        pby.add(kernelY);
        final RenderedImage fy = JAI.create("convolve", pby, null);

        final Rectangle rec = mapDestRect(destRect, 0);
        final WritableRectIter writer = RectIterFactory.createWritable(dest, rec);
        final RectIter iteConvolveX = RectIterFactory.create(fx, rec);
        final RectIter iteConvolveY = RectIterFactory.create(fy, rec);

        writer.startBands();
        iteConvolveX.startBands();
        iteConvolveY.startBands();

        if (!writer.finishedBands()) do {
            writer.startLines();
            iteConvolveX.startLines();
            iteConvolveY.startLines();
            if (!writer.finishedLines()) do {
                writer.startPixels();
                iteConvolveX.startPixels();
                iteConvolveY.startPixels();
                if (!writer.finishedPixels()) do {
                    final double cx = iteConvolveX.getSampleDouble() ;
                    final double cy = iteConvolveY.getSampleDouble() ;
                    double asp = Math.atan2( cx, cy );
                    double grad = Math.sqrt( Math.pow(cx,2) + Math.pow(cy,2) );
                    grad = Math.atan(zf*grad);
                    if(asp<Math.PI) asp += Math.PI/2 ;
                    if(asp<0)       asp += 2*Math.PI ;
                    double h = 255.0*( ( Math.cos(altitude)*Math.cos(grad) ) + ( Math.sin(altitude)*Math.sin(grad)*Math.cos(azimuth-asp)) );
                    if(h<0) h = 0;
                    writer.setSample((int)h);
                    iteConvolveX.nextPixel();
                    iteConvolveY.nextPixel();
                } while (!writer.nextPixelDone());
                iteConvolveX.nextLine();
                iteConvolveY.nextLine();
            } while (!writer.nextLineDone());
            iteConvolveX.nextBand();
            iteConvolveY.nextBand();
        } while (!writer.nextBandDone());

    }

}
