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
import org.geotoolkit.filter.capability.FunctionName;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.util.ScopedName;

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
 */
public class RecolorFunction extends AbstractExpression {

    private static final int MASK_ALPHA = 0xFF000000;
    private static final int MASK_NO_ALPHA = 0x00FFFFFF;

    private final List<ColorItem> items;
    private final Literal fallback;

    /**
     * Make the instance of FunctionName available in
     * a consistent spot.
     */
    public static final FunctionName NAME = new FunctionName("Recolor", Arrays.asList(
            "LookupValue",
            "Data 1", "Value 1",
            "Data 2", "Value 2"),
            -2);    // indicating unbounded, 2 minimum

    public RecolorFunction(final List<ColorItem> items, final Literal fallback){
        this.items = items;
        this.fallback = fallback;
    }

    public List<ColorItem> getColorItems() {
        return items;
    }

    @Override
    public ScopedName getFunctionName() {
        return createName("Recolor");
    }

    @Override
    public List<Expression<Object,?>> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public Object apply(final Object object) {

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
            final int dataExp = ((Color) item.getSourceColor().apply(null)).getRGB() & MASK_NO_ALPHA;
            final int resultExp = ((Color) item.getTargetColor().apply(null)).getRGB() & MASK_NO_ALPHA;

            for (int y=0,h=buffer.getHeight(); y<h ; y++) {
                for (int x=0,w=buffer.getWidth(); x<w; x++) {
                    final int pixelColor = buffer.getRGB(x, y);
                    final int noalpha = MASK_NO_ALPHA & pixelColor;
                    //compare ignoring alpha value
                    if(noalpha == dataExp){
                        //combine the pixel alpha with the result color
                        final int composite = resultExp | (pixelColor & MASK_ALPHA);
                        buffer.setRGB(x, y, composite);
                    }
                }
            }
        }
        return buffer;
    }

    public Literal getFallbackValue() {
        return fallback;
    }
}
