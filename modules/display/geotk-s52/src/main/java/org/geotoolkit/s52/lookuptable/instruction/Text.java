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
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import static org.geotoolkit.s52.S52Utilities.*;
import static org.geotoolkit.s52.lookuptable.instruction.Instruction.getPivotPoint;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.47  7.1.1
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Text extends Instruction{

    public Text(String code) {
        super(code);
    }

    /**
     * The STRING parameter passes a text string that shall appear on the ECDIS screen.
     * or ATTB in TE type.
     *
     * Note: the six character acronym of a S-57 attribute (e.g. ,LITVES, OBJNAM) can be passed
     * as STRING parameter; if the attribute is either of enumeration type or list type (e.g. COLOUR),
     * the enumeration value shall be converted into the respective text string from the attribute
     * definition in the object catalogue; if the attribute is of a numerical type, just convert
     * the attribute value to a string. In the case that the text originates in an L-type attribute
     * (e.g. SBDARE, NATSUR) the  text equivalent of the listed attribute values should be written
     * sequentially separated by a space with no punctuation marks.
     */
    public String[] text;

    /**
     * "horizontal justification" parameter:
     * <ul>
     * <li>
     * '1' means CENTRE justified
     *    (i.e. pivot point is located at the centre of the overall length of text string)
     * </li>
     * <li>
     * '2' means RIGHT justified
     *    (i.e. pivot point is located at the right side of the last character of text string)
     * </li>
     * <li>
     * '3' means LEFT justified. This is the default value.
     *    (i.e. pivot point is located at the left side of the first character of text string)
     * </li>
     * </ul>
     */
    public int horizontalAdjust;

    /**
     * "vertical justification" parameter:
     * <ul>
     * <li>
     * '1' means BOTTOM justified. This is the default value.
     *     (i.e. the pivot point is located at the bottom line of the text string)
     * </li>
     * <li>
     * '2' means CENTRE justified
     *     (i.e. the pivot point is located at the centre line of the text string)
     * </li>
     * <li>
     * '3' means TOP justified
     *     (i.e. the pivot point is located at the top line of the text string)
     * </li>
     * </ul>
     */
    public int verticalAdjust;

    /**
     * "character spacing" parameter:
     * <ul>
     * <li>
     * '1' means FIT spacing
     *     (i.e. the text string should be expanded or condensed to fit between
     *      the first and the last position in a spatial object)
     * </li>
     * <li>
     * '2' means STANDARD spacing. This is the default value.
     *     (i.e. the standard spacing in accordance with the typeface given
     *      in CHARS should be used)
     * </li>
     * <li>
     * '3' means STANDARD spacing with word wrap
     *     (i.e. the standard spacing in accordance with the typeface given in
     *      CHARS should be used; text longer than 8 characters should be broken into separate lines)
     * </li>
     * </ul>
     */
    public int space;

    /**
     * "Character Specification" parameter:
     * the CHARS parameter defines style (font), weight, width (upright/italic),
     * and size of the text characters:
     *
     * STYLE
     *  "1" a plain, sans serif font should be used.
     *
     * WEIGHT
     * 4 means "light"
     * 5 means "medium". This is the default value.
     * 6 means "bold"
     *
     * WIDTH
     * "1" means upright i.e. non-italic,
     * ENC $CHARS attributes using "2" for width should be converted to "1".
     *
     * BODY SIZE
     * This given in pica points (1 point = 0.351 mm) that specify the height of
     * an uppercase character. The smallest size to be used is pica 10,
     * and this is also the default size. Larger sizes may be used.
     */
    public String chars;

    /**
     * Defines the X-offset of the pivot point given in units of BODY SIZE
     * (see CHARS parameter) relative to the location of the spatial object
     * (0 is default if XOFFS is not given or undefined);
     * <br/>
     * positive x-offset extends to the right (the "units of BODYSIZE" means that if for example,
     * the body size is 10 pica points each unit of offset is 10 (0.351) = 3.51 mm).
     */
    public int xOffset;

    /**
     * Defines the y-offset of the pivot point given in units of BODY SIZE
     * (see CHARS parameter) relative to the location of the spatial object
     * (0 is default if YOFFS is not given or undefined);
     * <br/>
     * positive y-offset extends downwards.
     */
    public int yOffset;

    /**
     * colour token as described in section 4 and 13.
     */
    public String color;

    /**
     * define text groupings for selection by the mariner.
     */
    public String display;

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        text = new String[parts.length-8];
        int i = 0;
        for(;i<text.length;i++){
            text[i] = parts[i];
        }
        horizontalAdjust = Integer.valueOf(parts[i+0]);
        verticalAdjust   = Integer.valueOf(parts[i+1]);
        space            = Integer.valueOf(parts[i+2]);
        chars            = parts[i+3].substring(1,parts[i+3].length()-1);
        xOffset          = Integer.valueOf(parts[i+4]);
        yOffset          = Integer.valueOf(parts[i+5]);
        color            = parts[i+6];
        display          = parts[i+7];
    }

    public Font getFont(){

        int weight = Font.PLAIN;
        switch(chars.charAt(1)){
            case 4 : weight = Font.PLAIN; break; //TODO should be 'light' but we don't have that
            case 5 : weight = Font.PLAIN; break;
            case 6 : weight = Font.BOLD; break;
        }

        switch(chars.charAt(2)){
            case 2 : weight |= Font.ITALIC; break;
        }

        float size = Integer.valueOf(chars.substring(3));
        //convert size from pica to mm to pixel
        size = picaToPixel(size);

        return new Font("SansSerif", weight, Math.round(size));
    }

    public abstract Expression getText();


    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException {
        if(context.isNoText()) return;

        final Graphics2D g2d = ctx.getGraphics();
        final Feature feature = s52graphic.feature;

        //this includ alphanumeric and numeric texts

        //get font and text metas
        final Font font = this.getFont();
        g2d.setFont(font);
        final Expression expStr = this.getText();
        final String str = expStr.evaluate(feature, null);
        FontMetrics fm = null;
        Integer fontHeight = null;

        //find and adjust pivot point
        final Coordinate pivotPoint;
        try {
            pivotPoint = getPivotPoint(s52graphic.graphic.getGeometry(null).getDisplayGeometryJTS());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
        if(this.xOffset != 0){
            if(fm==null) fm = g2d.getFontMetrics(font);
            if(fontHeight==null) fontHeight = fm.getAscent();
            pivotPoint.x += this.xOffset * fontHeight;
        }
        if(this.yOffset != 0){
            if(fm==null) fm = g2d.getFontMetrics(font);
            if(fontHeight==null) fontHeight = fm.getAscent();
            pivotPoint.y += this.yOffset * fontHeight;
        }

        //set color
        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
        final Color color = colorTable.getColor(this.color);
        g2d.setColor(color);


        if(this.horizontalAdjust != 3 || this.verticalAdjust != 1){
            //calculate horizontal and vertical adjustement
            if(fm==null) fm = g2d.getFontMetrics(font);

            if(this.horizontalAdjust==1){
                final int width = fm.stringWidth(str);
                pivotPoint.x -= width/2;
            }else if(this.horizontalAdjust==2){
                final int width = fm.stringWidth(str);
                pivotPoint.x -= width;
            }

            if(this.verticalAdjust==2){
            if(fontHeight==null) fontHeight = fm.getAscent();
                pivotPoint.y += fontHeight/2;
            }else if(this.verticalAdjust==3){
            if(fontHeight==null) fontHeight = fm.getAscent();
                pivotPoint.y += fontHeight;
            }
        }

        //TODO handle SPACE parameter

        g2d.drawString(str, (float)pivotPoint.x, (float)pivotPoint.y);
    }

}
