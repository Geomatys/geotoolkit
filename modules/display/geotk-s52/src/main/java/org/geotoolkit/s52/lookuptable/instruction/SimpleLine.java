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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import static org.geotoolkit.s52.S52Utilities.*;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.referencing.operation.TransformException;

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
        DOTT;

        public Stroke createStroke(float width){
            final Stroke stroke;
             if(PStyle.this.equals(PStyle.DASH)){
                stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND,
                        5, new float[]{mmToPixel(3.6f),mmToPixel(1.8f)},0);
            }else if(PStyle.this.equals(PStyle.DOTT)){
                stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND,
                        5, new float[]{mmToPixel(0.6f),mmToPixel(1.2f)},0);
            }else{
                stroke = new BasicStroke(width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND);
            }
            return stroke;
        }

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

    public SimpleLine(PStyle style, int width, String color) {
        this();
        this.style = style;
        this.width = width;
        this.color = color;
    }

    /**
     * Java2D stroke object that match this line style.
     * @return Stroke
     */
    public Stroke getStroke() {
        if(stroke==null){
            stroke = style.createStroke(width);
        }
        return stroke;
    }

    public Color getColor(S52Palette colorTable){
        return colorTable.getColor(this.color);
    }

    @Override
    public void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        style = PStyle.valueOf(parts[0]);
        width = Integer.valueOf(parts[1]);
        color = parts[2];
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();
        final Stroke stroke = getStroke();
        final Color color = getColor(colorTable);
        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
        g2d.setColor(color);
        g2d.setStroke(stroke);
        try {
            g2d.draw(s52graphic.graphic.getGeometry(null).getDisplayShape());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
    }

}
