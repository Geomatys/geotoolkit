/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import org.geotoolkit.display2d.GO2Utilities;
import java.util.List;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Font;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedFont extends Cache<Font>{

    private String fontFamily = null;
    private int fontSize = Integer.MIN_VALUE;
    private int fontStyle = Integer.MIN_VALUE;
    
    public CachedFont(Font font){
        super(font);
    }
    
    public java.awt.Font getJ2dFont(Feature feature, float coeff) {

        final Font font = styleElement;
        
        final int j2dSize;
        if(fontSize != Integer.MIN_VALUE){
            j2dSize = fontSize;
        }else{
            //size is dynamic
            j2dSize = GO2Utilities.evaluate(font.getSize(), feature, Integer.class, 10);
        }
        
        final int j2dStyle;
        if(fontStyle != Integer.MIN_VALUE){
            j2dStyle = fontStyle;
        }else{
            //style is dynamic
            String style = GO2Utilities.evaluate(font.getStyle(), feature, String.class, "normal");
            String weight = GO2Utilities.evaluate(font.getWeight(), feature, String.class, "normal");

            if (weight.equalsIgnoreCase("bold")) {
                if (style.equalsIgnoreCase("italic")) {
                    j2dStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else if (style.equalsIgnoreCase("oblique")) {
                    j2dStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else {
                    j2dStyle = java.awt.Font.BOLD;
                }
            } else {
                if (style.equalsIgnoreCase("italic")) {
                    j2dStyle = java.awt.Font.ITALIC;
                } else if (style.equalsIgnoreCase("oblique")) {
                    j2dStyle = java.awt.Font.ITALIC;
                } else {
                    j2dStyle = java.awt.Font.BOLD;
                }
            }
        }
        
        final String name;
        if(fontFamily != null){
            name = fontFamily;
        }else{
            List<Expression> families = font.getFamily();
            if (families != null || !families.isEmpty()) {
                name = GO2Utilities.evaluate(font.getStyle(), feature, String.class, "arial");
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
            fontSize = GO2Utilities.evaluate(expSize, null, Integer.class, 10);
        }else{
            GO2Utilities.getRequieredAttributsName(expSize,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expWeight) && GO2Utilities.isStatic(expStyle)){
            String strWeight = GO2Utilities.evaluate(expWeight, null, String.class, "normal");
            String strStyle = GO2Utilities.evaluate(expStyle, null, String.class, "normal");
            
            if (strWeight.equalsIgnoreCase("bold")) {
                if (strStyle.equalsIgnoreCase("italic")) {
                    fontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else if (strStyle.equalsIgnoreCase("oblique")) {
                    fontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
                } else {
                    fontStyle = java.awt.Font.BOLD;
                }
            } else {
                if (strStyle.equalsIgnoreCase("italic")) {
                    fontStyle = java.awt.Font.ITALIC;
                } else if (strStyle.equalsIgnoreCase("oblique")) {
                    fontStyle = java.awt.Font.ITALIC;
                } else {
                    fontStyle = java.awt.Font.BOLD;
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
        
        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(Feature feature) {
        evaluate();
        //font doesnt know if it's visible or not whit those informations, always true.
        return true;
    }

}
