/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.style.util.converter;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;

import java.awt.*;


/**
 * Implementation of ObjectConverter to convert a String into a Color.
 *
 * @module
 */
public class ColorToStringConverter extends SimpleConverter<Color, String> {

    public ColorToStringConverter(){
    }

    @Override
    public Class<Color> getSourceClass() {
        return Color.class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(Color color) throws UnconvertibleObjectException {
        if (color == null) {
            return null;
        }

        String redCode = Integer.toHexString(color.getRed());
        String greenCode = Integer.toHexString(color.getGreen());
        String blueCode = Integer.toHexString(color.getBlue());
        if (redCode.length() == 1)      redCode = "0" + redCode;
        if (greenCode.length() == 1)    greenCode = "0" + greenCode;
        if (blueCode.length() == 1)     blueCode = "0" + blueCode;

        final String colorCode;
        int alpha = color.getAlpha();
        if(alpha != 255){
            String alphaCode = Integer.toHexString(alpha);
            if (alphaCode.length() == 1) alphaCode = "0" + alphaCode;
            colorCode = "#" + alphaCode + redCode + greenCode + blueCode;
        }else{
            colorCode = "#" + redCode + greenCode + blueCode;
        }

        return colorCode;
    }
}
