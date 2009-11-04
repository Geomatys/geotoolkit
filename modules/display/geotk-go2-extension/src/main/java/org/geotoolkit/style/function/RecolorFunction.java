/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.filter.AbstractExpression;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Implementation of "Recode" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a colour map
 * <li>Literal: lookup value
 * <li>Literal: MapItem : data 1
 * <li>Literal: MapItem : value 1
 * <li>Literal: MapItem : data 2
 * <li>Literal: MapItem : value 2
 * </ol>
 * In reality any expression will do.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RecolorFunction extends AbstractExpression implements Function {
        
    private final List<ColorItem> items;
    private final Literal fallback;
    
    /**
     * Make the instance of FunctionName available in
     * a consistent spot.
     */
    public static final FunctionName NAME = new Name();


    /**
     * Describe how this function works.
     * (should be available via FactoryFinder lookup...)
     */
    public static class Name implements FunctionName {

        @Override
        public int getArgumentCount() {
            return -2; // indicating unbounded, 2 minimum
        }

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Data 1", "Value 1",
                        "Data 2", "Value 2"
                    });
        }

        @Override
        public String getName() {
            return "Recode";
        }
    };

    public RecolorFunction(List<ColorItem> items, Literal fallback){
        this.items = items;
        this.fallback = fallback;
    }

    public List<ColorItem> getColorItems() {
        return items;
    }

    @Override
    public String getName() {
        return "Recolor";
    }

    @Override
    public List<Expression> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public Object evaluate(Object object) {

        if(!(object instanceof Image)){
            throw new IllegalArgumentException("Unexpected type : " + object + ", need Image.");
        }

        final Image img = (Image) object;

        final BufferedImage buffer;
        if(img instanceof BufferedImage ){
            BufferedImage candidate = (BufferedImage) img;
            if(candidate.getColorModel() instanceof ComponentColorModel &&
                    candidate.getColorModel().getTransparency() == ColorModel.TRANSLUCENT){
                //valid buffered image
                buffer = (BufferedImage) img;
            }else{
                buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                buffer.getGraphics().drawImage(img, 0, 0, null);
            }
            
        }else{
            buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            buffer.getGraphics().drawImage(img, 0, 0, null);
        }

        for(ColorItem item : items){
            final Color dataExp = item.getSourceColor().evaluate(null, Color.class);
            final Color resultExp = item.getTargetColor().evaluate(null, Color.class);

            for (int y = 0; y < buffer.getHeight(); y++) {
                for (int x = 0; x < buffer.getWidth(); x++) {
                    Color c = new Color(buffer.getRGB(x, y));
                    if(c.equals(dataExp)){
                        buffer.setRGB(x, y, resultExp.getRGB());
                    }
                }
            }

        }

        return buffer;
    }

    @Override
    public Literal getFallbackValue() {
        return fallback;
    }
    
}
