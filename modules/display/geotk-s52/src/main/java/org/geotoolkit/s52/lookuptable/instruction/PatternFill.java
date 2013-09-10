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
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.dai.PatternDefinition;
import org.geotoolkit.s52.render.PatternSymbolStyle;
import org.geotoolkit.s52.render.SymbolStyle;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.62  7.4.8
 *
 * LS ( PATNAME [,ROTATION] )
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternFill extends Instruction{

    /**
     * The pattern symbol name is an 8 letter‑code which is composed
     * of a class code (6 letters) and a serial number (2 letters).
     */
    public String patternName;

    /**
     * 0 to 360 nautical degrees (clockwise, starting North);
     * default: 0 degree;
     *
     * Note: the ROTATION parameter is optional; if a raster symbol is called
     * the ROTATION parameter is ignored; the six character code of
     * an S-57 attribute can be passed as ROTATION parameter.
     *
     * The rotation function would operate on individual symbols of the pattern
     * and not on the pattern as a whole. It is not in use at present.
     */
    public String rotation;

    public PatternFill() {
        super("AP");
    }

    public PatternFill(String patternName) {
        this();
        this.patternName = patternName;
    }

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        patternName = parts[0];
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

        SymbolStyle sst = context.getSyle(patternName);
        if(!(sst instanceof PatternSymbolStyle)){
            return;
        }
        final PatternSymbolStyle ss = (PatternSymbolStyle) sst;
        final PatternDefinition pd = (PatternDefinition) ss.definition;

        //TODO handle size/distance correction
        final int maxdist = pd.PAMA;
        final int mindist = pd.PAMI; //used in constant scaling
        final String scaling = pd.PASP;
        final String placement = pd.PATP;

        final float spacing = SymbolStyle.SCALE*mindist;
        final Rectangle2D rect = ss.getBounds();
        final float px = ss.definition.getPivotX()*SymbolStyle.SCALE;
        final float py = ss.definition.getPivotY()*SymbolStyle.SCALE;

        //use a fixed pattern start position, to avoid pattern 'moving' when dragging map
        final double[] xy = new double[]{0,0};
        ctx.getObjectiveToDisplay().transform(xy, 0, xy, 0, 1);


        final TexturePaint paint;
        if("LIN".equals(placement)){
            final Coordinate center = new Coordinate(px-rect.getX(), py-rect.getY());
            final BufferedImage img = new BufferedImage((int)(rect.getWidth()+spacing),
                                                        (int)(rect.getHeight()+spacing),
                                                        BufferedImage.TYPE_INT_ARGB);
            ss.render(img.createGraphics(), context, colorTable, center, 0f);
            paint = new TexturePaint(img, new Rectangle2D.Double(xy[0], xy[1],
                    rect.getWidth()+spacing, rect.getHeight()+spacing));
        }else if("STG".equals(placement)){
            final Coordinate center = new Coordinate(px-rect.getX(), py-rect.getY());
            final BufferedImage img = new BufferedImage((int)(rect.getWidth()*2+spacing*2),
                                                        (int)(rect.getHeight()*2+spacing*2),
                                                        BufferedImage.TYPE_INT_ARGB);
            //first symbol
            ss.render(img.createGraphics(), context, colorTable, center, 0f);
            //second symbol with displacement
            center.x += rect.getWidth() + spacing;
            center.y += rect.getHeight()+ spacing;
            ss.render(img.createGraphics(), context, colorTable, center, 0f);

            paint = new TexturePaint(img, new Rectangle2D.Double(xy[0], xy[1],
                    rect.getWidth()*2+spacing*2, rect.getHeight()*2+spacing*2));
        }else{
            throw new PortrayalException("Unexpected placement : "+placement);
        }

        g2d.setPaint(paint);
        try {
            g2d.fill(s52graphic.graphic.getGeometry(null).getDisplayShape());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

    }

}
