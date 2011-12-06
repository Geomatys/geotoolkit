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
package org.geotoolkit.display2d.style.raster;

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
 * @module pending
 */
public class CompatibleColorModel extends ColorModel{

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
        final Number value;
        if(inData instanceof Number){
            value = (Number) inData;
        }else if(inData.getClass().isArray()){
            value = (Number) Array.get(inData, 0);
        }else{
            throw new UnsupportedOperationException("Can not extract value from type : " + inData.getClass());
        }        
        final Color c = fct.evaluate(value, Color.class);
        return c.getRGB();
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

    @Override
    public WritableRaster createCompatibleWritableRaster(int w, int h) {        
        return Raster.createPackedRaster(new DataBufferInt(w*h),w,h,16,null); 
    }
    
}
