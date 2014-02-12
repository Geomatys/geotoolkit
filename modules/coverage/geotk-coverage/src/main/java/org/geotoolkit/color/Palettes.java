/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.color;

import java.awt.Color;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.lang.Static;

/**
 * Palette utility methods.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class Palettes extends Static{
    
    /**
     * Regular interpolation of colors between start and end colors.
     * 
     * @param start range start color, not null
     * @param end range end color, not null
     * @param divisions number of colors to generate, minimum 2
     * @return Interpolated colors. never null, size equals divisions
     */
    public static Color[] interpolate(Color start, Color end, int divisions){
        ArgumentChecks.ensureBetween("Divisions", 2, Integer.MAX_VALUE, divisions);
                
        final int argb1 = start.getRGB();
        final int argb2 = end.getRGB();
        final int sa = (argb1>>>24) & 0xFF;
        final int sr = (argb1>>>16) & 0xFF;
        final int sg = (argb1>>> 8) & 0xFF;
        final int sb = (argb1     ) & 0xFF;
        final int ia = ((argb2>>>24) & 0xFF) - sa;
        final int ir = ((argb2>>>16) & 0xFF) - sr;
        final int ig = ((argb2>>> 8) & 0xFF) - sg;
        final int ib = ((argb2     ) & 0xFF) - sb;
        
        final Color[] colors = new Color[divisions];
        for(int i=0; i<divisions; i++){
            final float ratio = (float)i/(divisions-1);
            final int a = sa + (int)(ratio*ia);
            final int r = sr + (int)(ratio*ir);
            final int g = sg + (int)(ratio*ig);
            final int b = sb + (int)(ratio*ib);
            colors[i] = new Color(r, g, b, a);
        }
        
        return colors;
    }
    
    /**
     * Interpolate a color between two other using a [0...1] ratio.
     * 
     * @param start range start color, not null
     * @param end range end color, not null
     * @param ratio between 0 and 1. 0 will return start color, 1 will return end color.
     * @return interpolated color
     */
    public static Color interpolate(final Color start, final Color end, final float ratio){
        final int argb1 = start.getRGB();
        final int argb2 = end.getRGB();

        final int sa = (argb1>>>24) & 0xFF;
        final int sr = (argb1>>>16) & 0xFF;
        final int sg = (argb1>>> 8) & 0xFF;
        final int sb = (argb1     ) & 0xFF;
        final int ia = ((argb2>>>24) & 0xFF) - sa;
        final int ir = ((argb2>>>16) & 0xFF) - sr;
        final int ig = ((argb2>>> 8) & 0xFF) - sg;
        final int ib = ((argb2     ) & 0xFF) - sb;

        //calculate interpolated color
        final int a = sa + (int)(ratio*ia);
        final int r = sr + (int)(ratio*ir);
        final int g = sg + (int)(ratio*ig);
        final int b = sb + (int)(ratio*ib);
        return new Color(r, g, b, a);
    }
    
}
