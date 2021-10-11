/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.style.visitor;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.TextSymbolizer;

/**
 * Style visitor that returns a set of all colors used by this style.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ListingColorVisitor extends DefaultStyleVisitor{

    private final Set<Integer> colors = new HashSet<Integer>();

    /**
     * Set to true whenever a style member could not be evaluated.
     * this means there is no need to explore the style further.
     */
    private boolean unpredictable = false;


    /**
     * visit each style member and return all used colors.
     */
    public ListingColorVisitor() {
    }

    public void reset(){
        colors.clear();
        unpredictable = false;
    }

    /**
     * @return Set of colors or null if some style member could not be evaluated.
     * colors are in RGB.
     */
    public Set<Integer> getColors(){
        return (unpredictable) ? null : colors;
    }

    @Override
    public Object visit(final Style style, final Object data) {
        return (unpredictable) ? data : super.visit(style,data);
    }

    @Override
    public Object visit(final FeatureTypeStyle fts, final Object data) {
        return (unpredictable) ? data : super.visit(fts,data);
    }

    @Override
    public Object visit(final Rule rule, final Object data) {
        return (unpredictable) ? data : super.visit(rule,data);
    }

    @Override
    public Object visit(final PointSymbolizer symbol, final Object data) {
        return (unpredictable) ? data : super.visit(symbol,data);
    }

    @Override
    public Object visit(final LineSymbolizer symbol, final Object data) {
        return (unpredictable) ? data : super.visit(symbol,data);
    }

    @Override
    public Object visit(final PolygonSymbolizer symbol, final Object data) {
        return (unpredictable) ? data : super.visit(symbol,data);
    }

    @Override
    public Object visit(final TextSymbolizer symbol, final Object data) {
        return (unpredictable) ? data : super.visit(symbol,data);
    }

    // Elements unpredictable //////////////////////////////////////////////////

    @Override
    public Object visit(final RasterSymbolizer symbol, final Object data) {
        unpredictable = true;
        return data;
    }

    @Override
    public Object visit(final ExtensionSymbolizer symbol, final Object data) {
        unpredictable = true;
        return data;
    }

    @Override
    public Object visit(final ExternalGraphic external, final Object data) {
        unpredictable = true;
        return data;
    }


    // Elements that can contain colors or opacity /////////////////////////////

    private void checkColor(final Expression exp){
        if(exp == null || unpredictable){
            return;
        }else if(exp instanceof Literal){
            final Literal l = (Literal) exp;
            final Object value = l.getValue();
            if (value instanceof Color) {
                colors.add(((Color)value).getRGB());
            } else {
                try {
                    colors.add(Integer.decode(value.toString()) | 0xFF000000);
                } catch (NumberFormatException ex) {
                    //not a color ? this style is invalid
                    unpredictable = true;
                }
            }
        }else{
            unpredictable = true;
        }
    }

    private void checkOpacity(final Expression exp){
        if(exp == null || unpredictable){
            return;
        }else if(exp instanceof Literal){
            final Number n = (Number) exp.apply(null);
            if(n != null){
                final float val = n.floatValue();
                unpredictable = (val!=1 && val!=0);
            }else{
                unpredictable = true;
            }
        }else{
            unpredictable = true;
        }
    }

    @Override
    public Object visit(final Fill fill, final Object data) {
        checkColor(fill.getColor());
        checkOpacity(fill.getOpacity());
        return super.visit(fill, data);
    }

    @Override
    public Object visit(final Stroke stroke, final Object data) {
        checkColor(stroke.getColor());
        checkOpacity(stroke.getOpacity());
        return super.visit(stroke, data);
    }

    @Override
    public Object visit(final Graphic gr, final Object data) {
        checkOpacity(gr.getOpacity());
        return super.visit(gr, data);
    }

}
