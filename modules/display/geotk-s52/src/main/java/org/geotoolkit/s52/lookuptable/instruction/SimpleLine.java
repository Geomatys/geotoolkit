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
package org.geotoolkit.s52.lookuptable.instruction;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.IOException;

/**
 * S-52 Annex A part I 7.3.4 p.54
 * 
 * LS(PSTYLE,WIDTH,COLOUR)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SimpleLine extends Instruction{

    public static enum PStyle{
        /** solid line */
        SOLD,
        /** dashed line : dash 3.6mm , space 1.8mm */
        DASH,
        /** dot line : dash 0.6mm , space 1.2mm */
        DOTT
    }

    public PStyle style;
    /**
     * '1' x 0.32 mm <= WIDTH <= '8' x 0.32 mm;
     * line width is given in units of 0.32 mm pixel diameter or
     * whatever size is required in section 8 of S-52.
     */
    public int width;

    /**
     * Line color token as described in section 4 and 13.
     */
    public String color;

    //cached stroke
    private Stroke stroke;

    public SimpleLine() {
        super("LS");
    }

    /**
     * Java2D stroke object that match this line style.
     * @return Stroke
     */
    public Stroke getStroke() {
        if(style.equals(PStyle.DASH)){
            stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND,
                    5, new float[]{toPixel(3.6f),toPixel(1.8f)},0);
        }else if(style.equals(PStyle.DOTT)){
            stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND,
                    5, new float[]{toPixel(0.6f),toPixel(1.2f)},0);
        }else{
            stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND);
        }

        return stroke;
    }

    @Override
    public void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        style = PStyle.valueOf(parts[0]);
        width = Integer.valueOf(parts[1]);
        color = parts[2];
    }

    private static float toPixel(float size){
        return size * 0.32f;
    }

}
