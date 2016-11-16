/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import org.opengis.filter.expression.Function;

/**
 * ColorModel which can calculate color from any sample model.
 * CAUTION : this color model is not accelerated by java2d, ComponentColorModel or
 * IndexedColorModel should always be used prior to this model.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CompatibleColorModel extends ColorModel{

    private static final int TRANSLUCENT = new Color(255, 255, 255, 0).getRGB();
    
    private final Function fct;
    
    /**
     * @param nbbits
     * @param fct : Interpolate or Categorize function
     */
    public CompatibleColorModel(final int nbbits, final Function fct){
        super(nbbits);
        this.fct = fct;
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
        Object value;
        // Most used cases. Compatible color model is designed for cases where indexColorModel cannot do the job (float or int samples).
        if (inData instanceof float[]) {
            value = ((float[]) inData)[0];
        } else if (inData instanceof int[]) {
            value = ((int[]) inData)[0];
        } else if (inData instanceof double[]) {
            value = ((double[]) inData)[0];
        } else if (inData instanceof byte[]) {
            value = ((byte[]) inData)[0];
        } else if (inData instanceof short[]) {
            value = ((short[]) inData)[0];
        } else if (inData instanceof long[]) {
            value = ((long[]) inData)[0];
        } else if (inData instanceof Number[]) {
            value = ((Number[]) inData)[0];
        } else if (inData instanceof Byte[]) {
            value = ((Byte[]) inData)[0];
        } else {
            value = inData;
        }
        final Color c = fct.evaluate(value, Color.class);
        if(c==null){
            return TRANSLUCENT;
        }
        return c.getRGB();
    }
    
    @Override
    public int getRed(int pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & (argb >> 16);
    }

    @Override
    public int getGreen(int pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 8);
    }

    @Override
    public int getBlue(int pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 0);
    }

    @Override
    public int getAlpha(int pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 24);
    }

    @Override
    public int getRed(Object pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & (argb >> 16);
    }

    @Override
    public int getGreen(Object pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 8);
    }
    
    @Override
    public int getBlue(Object pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 0);
    }

    @Override
    public int getAlpha(Object pixel) {
        final int argb = getRGB((Object)pixel);
        return 0xFF & ( argb >> 24);
    }

    @Override
    public WritableRaster createCompatibleWritableRaster(int w, int h) {        
        return Raster.createPackedRaster(new DataBufferInt(w*h),w,h,16,null); 
    }
    
}
