package org.geotoolkit.ogc.xml;

import java.awt.Color;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterUtilities {

    public static String toString(final Color color) {
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
        return colorCode.toUpperCase();
    }
}
