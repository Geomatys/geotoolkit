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
package org.geotoolkit.s52.procedure;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.text.NumberFormat;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.j2d.TextStroke;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.render.SymbolStyle;
import org.opengis.feature.Feature;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.139 (12.2.1)
 *
 * @author Johann Sorel (Geomatys)
 */
public class CLRLIN01 extends Procedure{

    private static final SimpleLine LINE = new SimpleLine();
    static {
        LINE.color = "NINFO";
        LINE.width = 1;
        LINE.style = SimpleLine.PStyle.SOLD;
    }
    private static final Font FONT = new Font("Sans-serif", Font.PLAIN, 10);

    public CLRLIN01() {
        super("CLRLINO1");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Feature feature = (Feature) graphic.getCandidate();

        //render a simple line
        LINE.render(ctx, context, colorTable, graphic, null);

        //find the bearing
        final Geometry geom;
        final Shape shp;
        try {
            geom = graphic.getGeometry(null).getDisplayGeometryJTS();
            shp = graphic.getGeometry(null).getDisplayShape();
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //draw symbol at the end of the line
        final float mapRotation = (float)XAffineTransform.getRotation(ctx.getObjectiveToDisplay());
        final SymbolStyle ss = context.getSyle("CLRLINO1");
        final Coordinate[] coords = geom.getCoordinates();
        final float angle = S52Utilities.angle(
                (float)coords[0].x,               (float)coords[0].y,
                (float)coords[coords.length-1].x, (float)coords[coords.length-1].y);
        ss.render(null, context, colorTable, (Coordinate)coords[coords.length-1].clone(), angle+mapRotation);

        //draw the text
        final Object value = feature.getProperty("catclr").getValue();
        if(value != null){
            String text = "";
            if("1".equals(value)){
                text += "NMT ";
            }else{
                text += "NLT ";
            }
            text += NumberFormat.getNumberInstance().format(Math.toDegrees(angle));

            ctx.switchToDisplayCRS();
            final TextStroke stroke = new TextStroke(text, FONT, false, 0, 0, 0,ctx.getCanvasDisplayBounds());
            final Shape shape = stroke.createStrokedShape(shp);

            //paint text
            final Graphics2D g2d = ctx.getGraphics();
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            g2d.setStroke(new BasicStroke(0));
            g2d.setColor(colorTable.getColor("CHBLK"));
            g2d.fill(shape);
        }

    }

}
