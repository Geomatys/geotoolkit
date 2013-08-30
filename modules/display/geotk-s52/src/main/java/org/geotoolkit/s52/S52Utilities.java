/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.s52;

import org.geotoolkit.lang.Static;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S52Utilities extends Static{

    /**
     * Convert pica to mm
     * @return mm size
     */
    public static float picaTomm(float value){
        return value * 0.351f;
    }

    /**
     * Convert pica to pixel
     * @return pixel size
     */
    public static float picaToPixel(float value){
        return value * 0.351f / 0.32f;
    }

    /**
     * Convert mm to pixel
     * @return pixel size
     */
    public static float mmToPixel(float value){
        return value / 0.32f;
    }

    /**
     * Calculate angle of a segment.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return angle in radians.
     */
    public static float angle(final float x1, final float y1, final float x2, final float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.atan2(dy, dx);
    }

    public static String getObjClass(Feature feature){
        String objClassCode = feature.getType().getName().getLocalPart();
        final int sep = objClassCode.indexOf('_');
        if(sep >= 0){
            objClassCode = objClassCode.substring(sep+1);
        }
        return objClassCode;
    }

}
