/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.process.coverage.reformat;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.lang.reflect.Array;

/**
 * A grayscale color model.
 * 
 * 
 * @author Johann Sorel (Geomatys)
 */
public class GrayScaleColorModel extends ColorModel {

    private final int band;
    private final double min;
    private final double max;

    public GrayScaleColorModel(int bits, double min, double max) {
        this(bits,0,min,max);
    }
    
    public GrayScaleColorModel(int bits, int band, double min, double max) {
        super(bits);
        this.min = min;
        this.max = max;
        this.band = band;
    }
    
    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return true;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sm) {
        return true;
    }
    
    @Override
    public int getRGB(Object inData) {
        final Number value;
        if(inData instanceof Number){
            value = (Number) inData;
        }else if(inData.getClass().isArray()){
            value = (Number) Array.get(inData, band);
        }else{
            throw new UnsupportedOperationException("Can not extract value from type : " + inData.getClass());
        }
        double dv = value.doubleValue();
        if(Double.isNaN(dv)) dv=min;
        else if(dv<min) dv=min;
        else if(dv>max) dv=max;
        
        //interpolate color
        int gray = (int)(255 * ((dv-min)/(max-min)));
        int rgb = 255<<24 | gray<<16 | gray<<8 | gray;
        return rgb;
    }
    
    @Override
    public int getRed(int pixel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getGreen(int pixel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBlue(int pixel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int pixel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
