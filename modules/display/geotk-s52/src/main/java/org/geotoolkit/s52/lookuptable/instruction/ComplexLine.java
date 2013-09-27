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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;
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
        final DefaultPathWalker walker = new DefaultPathWalker(ite);
//        ss.render(g2d, context, colorTable, walker);

        final Point2D pt = new Point2D.Double();
        walker.walk(0f);
        while(!walker.isFinished()){

            if(walker.getSegmentLengthRemaining() < symbolwidth){
                //walk just enough th get on the next segment
                walker.walk(Math.nextUp(walker.getSegmentLengthRemaining()));
            }else{
                walker.getPosition(pt);
                final float rotation = walker.getRotation() + (float)Math.PI;
                ss.render(g2d, context, colorTable, new Coordinate(pt.getX(),pt.getY()), rotation);
                walker.walk(symbolwidth);
            }
        }
    }

}
