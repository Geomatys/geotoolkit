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

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.j2d.RegularPathWalker;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.render.SymbolStyle;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A part I 7.3.5 p.55
 * LC(LINNAME)
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexLine extends Instruction{

    /**
     * The line-style name is an 8 letter‑code that is composed from
     * an object class code and a serial number (2 letters).
     */
    public String LINNAME;

    public ComplexLine() {
        super("LC");
    }

    public ComplexLine(String LINNAME) {
        this();
        this.LINNAME = LINNAME;
    }

    @Override
    protected void readParameters(String str) throws IOException {
        LINNAME = str.trim();
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException {
        //if(true)return;
        final Graphics2D g2d = ctx.getGraphics();
        final SymbolStyle ss = context.getSyle(LINNAME);
        final Rectangle2D rect = ss.getBounds();
        final float symbolwidth = (float) rect.getWidth();

        final PathIterator ite;
        try {
            ite = s52graphic.graphic.getGeometry(null).getDisplayShape().getPathIterator(null);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        final RegularPathWalker walker = new RegularPathWalker(ite, symbolwidth);
        while(!walker.isFinished()){
            final Line2D.Double line = walker.next();
            float angle = (float)angle(line.x1, line.y1, line.x2, line.y2);
            angle += (float)Math.PI;
            ss.render(g2d, context, colorTable, new Coordinate(line.x1,line.y1), angle);
        }

    }

    private static double angle(final double x1, final double y1, final double x2, final double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.atan2(dy, dx);
    }

}
