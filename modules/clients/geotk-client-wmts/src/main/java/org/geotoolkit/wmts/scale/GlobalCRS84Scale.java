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
package org.geotoolkit.wmts.scale;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GlobalCRS84Scale {
    
    public static final double SCALE = 500e6;
    public static final double PIXEL_SIZE = 1.25764139776733;
        
    private GlobalCRS84Scale(){}
    
    public static double getPixelScale(double scaleDenominator) {
        return (PIXEL_SIZE * 1.118164528) * scaleDenominator / SCALE ;
    }
    
}
