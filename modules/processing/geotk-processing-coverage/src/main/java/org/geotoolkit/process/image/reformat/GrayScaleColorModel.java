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
package org.geotoolkit.process.image.reformat;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import org.geotoolkit.internal.image.ScaledColorSpace;

/**
 * A grayscale color model.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class GrayScaleColorModel {

    private GrayScaleColorModel(){}
    
    public static ColorModel create(int dataType, int band, double min, double max){
        final ColorSpace colors = new ScaledColorSpace(band, 0, min, max);
        final ColorModel cm = new ComponentColorModel(colors, false, false, Transparency.OPAQUE, dataType);
        return cm;
    }
    
}
