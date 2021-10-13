/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
package org.geotoolkit.display2d.style;

import java.util.List;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.filter.Expression;
import org.opengis.style.Font;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedFont extends Cache<Font>{

    private java.awt.Font cache = null;
    private String fontFamily = null;
    private int fontSize = Integer.MIN_VALUE;
    private int fontStyle = Integer.MIN_VALUE;

    private CachedFont(final Font font){
        super(font);
    }

    public java.awt.Font getJ2dFont(final Object candidate, final float coeff) {
        evaluate();

        if(cache != null && coeff == 1){
            return cache;
        }

        final Font font = styleElement;

        final int j2dSize;
        if(fontSize != Integer.MIN_VALUE){
            j2dSize = fontSize;
        }else{
            //size is dynamic
            j2dSize = GO2Utilities.evaluate(font.getSize(), candidate, Integer.class, 10);
        }

        final int j2dStyle;
        if(fontStyle != Integer.MIN_VALUE){
            j2dStyle = fontStyle;
        }else{
            //style is dynamic
            String style = GO2Utilities.evaluate(font.getStyle(), candidate, String.class, FONT_STYLE_NORMAL_STRING);
            String weight = GO2Utilities.evaluate(font.getWeight(), candidate, String.class, FONT_WEIGHT_NORMAL_STRING);

            if (FONT_WEIGHT_BOLD_STRING.equalsIgnoreCase(weight)) {
                if (FONT_STYLE_ITALIC_STRING.equalsIgnoreCase(style)) {
                    j2dStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else if (FONT_STYLE_OBLIQUE_STRING.equalsIgnoreCase(style)) {
                    j2dStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else {
                    j2dStyle = java.awt.Font.BOLD;
                }
            } else {
                if (FONT_STYLE_ITALIC_STRING.equalsIgnoreCase(style)) {
                    j2dStyle = java.awt.Font.ITALIC;
                } else if (FONT_STYLE_OBLIQUE_STRING.equalsIgnoreCase(style)) {
                    j2dStyle = java.awt.Font.ITALIC;
                } else {
                    j2dStyle = java.awt.Font.PLAIN;
                }
            }
        }

        final String name;
        if(fontFamily != null){
            name = fontFamily;
        }else{
            final List<Expression> families = font.getFamily();
            if (families != null && !families.isEmpty()) {
                name = GO2Utilities.evaluate(font.getStyle(), candidate, String.class, "arial");
            }else{
                name = "Dialog";
            }
        }

        return new java.awt.Font(name, j2dStyle, (int) (j2dSize * coeff));
    }

    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final List<Expression> expFamily = styleElement.getFamily();
        final Expression expSize = styleElement.getSize();
        final Expression expWeight = styleElement.getWeight();
        final Expression expStyle = styleElement.getStyle();

        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;


        //TODO find the best font acoording to OS font list
        if(!expFamily.isEmpty() && GO2Utilities.isStatic(expFamily.get(0))){
            fontFamily = GO2Utilities.evaluate(expFamily.get(0), null, String.class, "Dialog");
        }else if(!expFamily.isEmpty()){
            GO2Utilities.getRequieredAttributsName(expFamily.get(0),requieredAttributs);
            isStatic = false;
        }else{
            fontFamily = "Dialog";
        }


        if(GO2Utilities.isStatic(expSize)){
            final Float f = GO2Utilities.evaluate(expSize, null, Float.class, 10f);
            if (f != null) {
                fontSize = f.intValue();
            }
        }else{
            GO2Utilities.getRequieredAttributsName(expSize,requieredAttributs);
            isStatic = false;
        }

        if(GO2Utilities.isStatic(expWeight) && GO2Utilities.isStatic(expStyle)){
            String strWeight = GO2Utilities.evaluate(expWeight, null, String.class, FONT_STYLE_NORMAL_STRING);
            String strStyle = GO2Utilities.evaluate(expStyle, null, String.class, FONT_WEIGHT_NORMAL_STRING);

            if (FONT_WEIGHT_BOLD_STRING.equalsIgnoreCase(strWeight)) {
                if (FONT_STYLE_ITALIC_STRING.equalsIgnoreCase(strStyle)) {
                    fontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else if (FONT_STYLE_OBLIQUE_STRING.equalsIgnoreCase(strStyle)) {
                    fontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else {
                    fontStyle = java.awt.Font.BOLD;
                }
            } else {
                if (FONT_STYLE_ITALIC_STRING.equalsIgnoreCase(strStyle)) {
                    fontStyle = java.awt.Font.ITALIC;
                } else if (FONT_STYLE_OBLIQUE_STRING.equalsIgnoreCase(strStyle)) {
                    fontStyle = java.awt.Font.ITALIC;
                } else {
                    fontStyle = java.awt.Font.PLAIN;
                }
            }

        }else{
            GO2Utilities.getRequieredAttributsName(expWeight,requieredAttributs);
            GO2Utilities.getRequieredAttributsName(expStyle,requieredAttributs);
            isStatic = false;
        }

        //no attributs needed replace with static empty list.
        if(requieredAttributs.isEmpty()){
            requieredAttributs = EMPTY_ATTRIBUTS;
        }

        //cache the font if possible
        if(fontSize != Integer.MIN_VALUE && fontStyle != Integer.MIN_VALUE && fontFamily != null){
            cache = new java.awt.Font(fontFamily, fontStyle, (int)fontSize);
        }

        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(final Object candidate) {
        evaluate();
        //font doesnt know if it's visible or not whit those informations, always true.
        return true;
    }

    public static CachedFont cache(final Font font){
        return new CachedFont(font);
    }

}
