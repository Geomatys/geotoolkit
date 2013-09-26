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
package org.geotoolkit.s52.dai;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.LinkedHashMap;
import java.util.Map;
import org.geotoolkit.display2d.GO2Utilities;

/**
 * Color Definition CIE.
 * Describes CIE‑System's colour‑definition
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorDefinitionCIE extends DAIField{

    private static final ColorSpace CIEXYZ = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);

    /** A(5) : COLOUR (Color‑Token) */
    public String CTOK;
    /** R(1/15) : x-Coordinate (CIE‑System) */
    public double CHRX;
    /** R(1/15) : y‑Coordinate (CIE‑System */
    public double CHRY;
    /** R(1/15) : Luminance  (CIE‑System) */
    public double CLUM;
    /** A(1/15) : Use of color (free text) */
    public String CUSE;

    //cache values
    private Color color;
    private String hexa;


    public ColorDefinitionCIE() {
        super("CCIE");
    }

    public double getCHRX() {
        return CHRX;
    }

    public String getTokenName(){
        return CTOK;
    }

    public Color getColor(){
        checkCache();
        return color;
    }

    public String getColorHexa(){
        checkCache();
        return hexa;
    }

    /**
     * Convert CIE XYL color to Color and hexa.
     */
    private void checkCache(){
        if(color!=null)return;

        //convert CIE X,Y,L to CIE X,Y,Z
        // S-52 Appendix 2 (S-52_App.2_e4.3).doc
        // p.83
        double Y = CLUM;
        double X = (CHRX/CHRY) * Y;
        double Z = (1.0-CHRX-CHRY)/CHRY * Y;

        float[] colorValues = new float[]{
            (float)X,
            (float)Y,
            (float)Z
        };

        // divide by 100 for java color space
        colorValues[0] /= 100.0;
        colorValues[1] /= 100.0;
        colorValues[2] /= 100.0;

        colorValues = CIEXYZ.toRGB(colorValues);
        color = new Color(colorValues[0],colorValues[1],colorValues[2],1f);
        hexa = (String)GO2Utilities.STYLE_FACTORY.literal(color).getValue();

    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("CTOK", CTOK);
        map.put("CHRX", CHRX);
        map.put("CHRY", CHRY);
        map.put("CLUM", CLUM);
        map.put("CUSE", CUSE);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        CTOK = readStringBySize(str, offset, 5);
        CHRX = readDoubleByDelim(str, offset, DELIM_1F);
        CHRY = readDoubleByDelim(str, offset, DELIM_1F);
        CLUM = readDoubleByDelim(str, offset, DELIM_1F);
        CUSE = readStringByDelim(str, offset, DELIM_1F);
    }

}
