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

import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.lang.Static;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S52Utilities extends Static{

    private static final RenderingHints.Key CONTEXT_KEY = new GO2Hints.NamedKey(S52Context.class,"context");

    public static final UnitConverter NAUTIC_MILES_TO_METERS = NonSI.NAUTICAL_MILE.getConverterTo(SI.METRE);

    public static S52Context getS52Context(Canvas canvas) throws PortrayalException{
        S52Context s52context = (S52Context) canvas.getRenderingHint(CONTEXT_KEY);
        if(s52context==null){
            s52context = new S52Context();
            try {
                final URL dai = S52Context.getDefaultDAI();
                if(dai == null){
                    throw new PortrayalException("S52 DAI file has not been configured with S52Context.setDefaultDAI(URL).");
                }
                s52context.load(dai);
            } catch (IOException ex) {
                throw new PortrayalException(ex);
            }
            canvas.setRenderingHint(CONTEXT_KEY, s52context);
        }
        return s52context;
    }

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
        if(sep >= 0 && (objClassCode.length()-sep-1 == 6)) {
            objClassCode = objClassCode.substring(sep+1);
        }
        return objClassCode;
    }

    /**
     *
     * @param array
     * @param values
     * @return true if one or more value is in the array
     */
    public static boolean containsAny(Object[] array, Object ... values){
        for(Object value : values){
            if(ArraysExt.contains(array, value)) return true;
        }
        return false;
    }

}
