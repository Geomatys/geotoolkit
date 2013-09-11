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
import com.vividsolutions.jts.geom.Geometry;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.render.SymbolStyle;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Property;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.52  7.2
 *
 * @author Johann Sorel (Geomatys)
 */
public class Symbol extends Instruction{


    /**
     * The symbol name is an 8 letter‑code that is composed of a class code
     * (6 letters) and a serial number (2 letters).
     */
    public String symbolName;

    /**
     * .2.1 Symbols with no rotation should always be drawn upright with respect to the screen.
     * .2.2 Symbols with a rotation instruction should be rotated with respect to the
     *      top of the screen (-y axis in figure 2 of section 5.1). (See example below).
     * .2.3 Symbols rotated by means of the six-character code of an S-57 attribute
     *      such as ORIENT should be rotated with respect to true north.
     * .2.4 The symbol should be rotated about its pivot point. Rotation angle is
     *      in degrees clockwise from 0 to 360. The default value is 0 degrees."
     */
    public String rotation;

    public Symbol() {
        super("SY");
    }

    public Symbol(String symbolName,String rotation) {
        this();
        this.symbolName = symbolName;
        this.rotation = rotation;
    }

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        symbolName = parts[0];
        if(parts.length>1){
            rotation = parts[1];
        }else{
            rotation = null;
        }
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();

        //find rotation
        float rotation = 0f;
        if(this.rotation == null || this.rotation.isEmpty()){
            rotation = 0f;
        }else{
            try{
                rotation = (float)Math.toRadians(Integer.valueOf(this.rotation));
            }catch(NumberFormatException ex){
                //it's a field
                final Property prop = s52graphic.feature.getProperty(this.rotation);
                if(prop!=null){
                    Float val = Converters.convert(prop.getValue(),Float.class);
                    if(val!=null){
                        //combine with map rotation
                        rotation = -(float)XAffineTransform.getRotation(ctx.getObjectiveToDisplay());
                        rotation += Math.toRadians(val);
                    }
                }
            }
        }

        final Geometry displayGeometryJTS;
        try {
            displayGeometryJTS = s52graphic.graphic.getGeometry(null).getDisplayGeometryJTS();
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        renderGeometry(ctx, context, colorTable, displayGeometryJTS, rotation);
    }

    public void renderGeometry(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            Geometry displayGeomJTS, float rotation) throws PortrayalException {

        final Graphics2D g2d = ctx.getGraphics();

        final Coordinate center = getPivotPoint(displayGeomJTS);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        final SymbolStyle ss = context.getSyle(this.symbolName);
        if(ss == null){
            ctx.getMonitor().exceptionOccured(new PortrayalException("No symbol for name : "+this.symbolName), Level.FINE);
            return;
        }
        ss.render(g2d, context, colorTable, center, rotation);
    }

}
