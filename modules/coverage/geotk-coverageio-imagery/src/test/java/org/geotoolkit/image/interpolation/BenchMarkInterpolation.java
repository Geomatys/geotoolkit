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

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import javax.media.jai.RasterFactory;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.apache.sis.util.ArgumentChecks;

/**
 * <p>To test interpolation performance.<br/><br/>
 *
 * Use example : <br/>
 * BenchMarkInterpolation benchMark = new BenchMarkInterpolation();<br/>
 * double benchMarkTime = benchMark.benchMark(InterpolationCase.BICUBIC, 0, 100, 110, 10, 500);<br/><br/>
 *
 * In this example we choose to test performance of biCubic interpolation.<br/>
 * Lanczos windows equal 0 because it has no impact about biCubic interpolation.<br/>
 * for each loop we choose to interpolate 100 times in X direction and 110 times in Y direction.<br/>
 * We want repeat this process 10 times.<br/>
 * And for each pass we want a pause of 500 milliseconds.</p>
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BenchMarkInterpolation {

    /**
     * Iterator which iterate on benchmark raster used.
     */
    private final PixelIterator pixelIterator;

    /**
     * raster width.
     */
    private final int width;

    /**
     * raster height.
     */
    private final int height;

    /**
     * Raster upper corner X coordinate.
     */
    private final int minx;

    /**
     * Raster upper corner Y coordinate.
     */
    private final int miny;

    public BenchMarkInterpolation() {

        width  = 4;
        height = 4;
        minx = -2;
        miny = -1;

        final WritableRaster rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, height, 3, new Point(minx, miny));
        //fill first band
        rastertest.setSample(-2, -1, 0, 1);
        rastertest.setSample(-1, -1, 0, 1);
        rastertest.setSample( 0, -1, 0, 1);
        rastertest.setSample( 1, -1, 0, 1);
        rastertest.setSample(-2, 0, 0, 1);
        rastertest.setSample(-1, 0, 0, 2);
        rastertest.setSample(-0, 0, 0, 2);
        rastertest.setSample( 1, 0, 0, 1);
        rastertest.setSample(-2, 1, 0, 1);
        rastertest.setSample(-1, 1, 0, 2);
        rastertest.setSample( 0, 1, 0, 2);
        rastertest.setSample( 1, 1, 0, 1);
        rastertest.setSample(-2, 2, 0, 1);
        rastertest.setSample(-1, 2, 0, 1);
        rastertest.setSample( 0, 2, 0, 1);
        rastertest.setSample( 1, 2, 0, 1);

        //fill second band
        rastertest.setSample(-2, -1, 1, 2);
        rastertest.setSample(-1, -1, 1, 2);
        rastertest.setSample( 0, -1, 1, 2);
        rastertest.setSample( 1, -1, 1, 2);
        rastertest.setSample(-2, 0, 1, 2);
        rastertest.setSample(-1, 0, 1, 1);
        rastertest.setSample(-0, 0, 1, 1);
        rastertest.setSample( 1, 0, 1, 2);
        rastertest.setSample(-2, 1, 1, 2);
        rastertest.setSample(-1, 1, 1, 1);
        rastertest.setSample( 0, 1, 1, 1);
        rastertest.setSample( 1, 1, 1, 2);
        rastertest.setSample(-2, 2, 1, 2);
        rastertest.setSample(-1, 2, 1, 2);
        rastertest.setSample( 0, 2, 1, 2);
        rastertest.setSample( 1, 2, 1, 2);

        //fill third band
        double val = 32;
        for (int y = miny; y<miny+height; y++) {
            for (int x = minx; x<minx+width; x++) {
                rastertest.setSample(x, y, 2, val++);
            }
        }
        pixelIterator = PixelIteratorFactory.createDefaultIterator(rastertest);
    }

    /**
     * <p>BenchMark to Interpolation.<br/>
     *
     * Consist to compute interpolation on a pre-define Raster, nbreItX * nbreItY times * timeNbre.<br/>
     *
     * Note : if lanczos interpolation doesn't choose lanczosWindow parameter has no impact.</p>
     *
     * @param interpolatorCase type of Interpolation.
     * @param lanczosWindow side of Lanczos interpolation area.
     * @param nbreItX iteration number in X direction.
     * @param nbreItY iteration number in Y direction.
     * @param timeNbre numbers of times that iterate.
     * @param loopSleepMilli times sleep between each times.
     * @return time difference from fourth loop to last loop.
     * @throws InterruptedException
     */
    public double benchMark(InterpolationCase interpolatorCase, int lanczosWindow, int nbreItX, int nbreItY, int timeNbre, int loopSleepMilli) throws InterruptedException {
        if (interpolatorCase == InterpolationCase.LANCZOS)
            ArgumentChecks.ensureStrictlyPositive("lanczos window", lanczosWindow);
        ArgumentChecks.ensureStrictlyPositive("Iteration number in X direction", nbreItX);
        ArgumentChecks.ensureStrictlyPositive("Iteration number in Y direction", nbreItY);
        ArgumentChecks.ensureStrictlyPositive("loop time", loopSleepMilli);
        assert(timeNbre >= 10) : "timeNbre isn't enought large, it must be higher to 10 : "+timeNbre;
        final Interpolation interpolation = Interpolation.create(pixelIterator, interpolatorCase, lanczosWindow);
        final double pasx = (double) width /nbreItX;
        final double pasy = (double) height/nbreItY;

        double temDeb = 0;
        //benchmark
        for(int i = 0; i<timeNbre; i++){
            if (i==4)temDeb = System.currentTimeMillis();
            for (double y = miny; y<miny+height; y+=pasy) {
                for(double x = minx; x<minx+width; x+=pasx){
                    interpolation.interpolate(x, y,0);
                }
            }
            if(i>=4)Thread.sleep(loopSleepMilli);
        }
        temDeb-=System.currentTimeMillis();
        return Math.abs(temDeb);
    }
}
