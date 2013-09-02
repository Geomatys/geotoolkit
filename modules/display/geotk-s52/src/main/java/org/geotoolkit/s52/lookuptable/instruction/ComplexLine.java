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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.render.SymbolStyle;
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

    @Override
    protected void readParameters(String str) throws IOException {
        LINNAME = str.trim();
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geoType) throws PortrayalException {
        System.out.println("TODO Complex line");
        if(true)return;
        final Graphics2D g2d = ctx.getGraphics();
        final SymbolStyle ss = context.getSyle(LINNAME);

        final PathIterator ite;
        try {
            ite = graphic.getGeometry(null).getDisplayShape().getPathIterator(null);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
        final PathWalker walker = new DefaultPathWalker(ite);
        final Point2D pt = new Point2D.Double();
        while(!walker.isFinished()){
            //TODO not correct
            walker.walk(1);
            walker.getPosition(pt);
            final float rotation = walker.getRotation();
            ss.render(g2d, context, colorTable, new Coordinate(pt.getX(),pt.getY()), rotation);
            walker.walk(9);
        }
    }

}
