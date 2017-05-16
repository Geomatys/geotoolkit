/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import org.geotoolkit.display.PortrayalException;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TTFMarkFactory extends MarkFactory {

    private static final String PROTOCOL_TTF = "ttf";
    private static final String PROPERTY_CHAR = "char";
    private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(
            new AffineTransform(), false, false);

    @Override
    public Shape evaluateShape(String format, Object markRef, int markIndex) throws PortrayalException {
        if(!(markRef instanceof String)) return null;

        final String fontPath;
        if(format!=null && PROTOCOL_TTF.equalsIgnoreCase(format)){
            //direct glyph reference
            fontPath = (String) markRef;
        }else{
            //expression type
            final List<Object> parts = splitPath((String)markRef);
            final String protocol = (String) parts.get(0);
            if(PROTOCOL_TTF.equals(protocol) || PROTOCOL_TTF.equals(format)){
                fontPath = (String)parts.get(1);

                if(markIndex<=0){
                    //search in parameters
                    for(int i=2,n=parts.size();i<n;i++){
                        final Map.Entry entry = (Map.Entry) parts.get(2);
                        if(PROPERTY_CHAR.equalsIgnoreCase((String)entry.getKey())){
                            String v = (String) entry.getValue();
                            if(v.startsWith("U+") || v.startsWith("\\u")){
                                v = "0x" + v.substring(2);
                            }
                            markIndex = Integer.decode(v);
                        }
                    }
                }
            }else{
                return null;
            }
        }

        Font font = FontCache.getDefaultInsance().getFont(fontPath);
        if (font == null) {
            throw new PortrayalException("Unkown font "+fontPath);
        }

        final GlyphVector glyph = font.createGlyphVector(FONT_RENDER_CONTEXT,new String(new int[]{markIndex}, 0, 1));
        final Shape shape = glyph.getOutline();
        final Rectangle2D bounds = shape.getBounds2D();
        final double scale = 1.0 / Math.max(bounds.getWidth(), bounds.getHeight());
        //center and downscale glyph
        final AffineTransform atrs = new AffineTransform(
                scale,
                0.0,
                0.0,
                scale,
                -bounds.getCenterX()*scale,
                -bounds.getCenterY()*scale);
        return atrs.createTransformedShape(shape);
    }

}
