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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.61  7.4.7
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorFill extends Instruction{



    /**
     * Color token as described in section 4 and 13.
     */
    public String color;

    /**
     * 0 opaque (= default value)
     * 1 25 % (3 of 4 pixels use COLOUR, 1 uses TRNSP)
     * 2 50 % (2 of 4 pixels use COLOUR, 2 use TRNSP)
     * 3 75 % (1 of 4 pixels use COLOUR, 3 use TRNSP)
     * Note: the TRANSPARENCY parameter is an optional part of the colour fill command;
     * if it is not included, the command defaults to opaque fill.
     */
    public int transparency;

    public ColorFill() {
        super("AC");
    }

    public ColorFill(String color) {
        this();
        this.color = color;
    }

    public float getAlpha(){
        switch(transparency){
            case 1 : return 0.25f;
            case 2 : return 0.5f;
            case 3 : return 0.75f;
            default : return 1f;
        }
    }

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        color = parts[0];
        if(parts.length>1){
            transparency = Integer.valueOf(parts[1]);
        }else{
            transparency = 0;
        }
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException{
        final Graphics2D g2d = ctx.getGraphics();
        final Color color = colorTable.getColor(this.color);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
        g2d.setColor(color);
        try {
            g2d.fill(s52graphic.graphic.getGeometry(null).getDisplayShape());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
    }

}
