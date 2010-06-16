package org.geotoolkit.data.kml;

import java.awt.Color;
import org.geotoolkit.data.model.kml.KmlException;

/**
 * <p>This clss provides utilities for reading and writting KML files.</p>
 *
 * @author Samuel Andrés
 */
public class KmlUtilities {

    /**
     * <p>This method transforms a Kml color string to java instance of java.awt.Color class.</p>
     * <p>BE CAREFUL : Color object RGB constructors and Kml representation are not in
     * same order (a : alpha; b: blue; g: green; r: red) : </p>
     * <ul>
     * <li>kml : &lt;color>aabbggrr&lt;/color>, with a, b, g, r hexadecimal characters between 0 and f.</li>
     * <li>Color : Color(r,g,b,a), with r, g, b, a int parameters between 0 and 255.</li>
     * </ul>
     *
     * @param kmlColor Kml string color hexadecimal representation.
     * @return
     * @throws KmlException
     */
    public static Color parseColor(final String kmlColor) throws KmlException{

        Color color = null;

        if(kmlColor.matches("[0-9a-fA-F]{8}")){
            int r = Integer.parseInt(kmlColor.substring(0,2), 16);
            int g = Integer.parseInt(kmlColor.substring(2,4), 16);
            int b = Integer.parseInt(kmlColor.substring(4,6), 16);
            int a = Integer.parseInt(kmlColor.substring(6,8), 16);
            color = new Color(a,b,g,r);
        } else {
            throw new KmlException("The color must be a suit of four hexabinaries");
        }
        return color;
    }

    /**
     * <p>This method transforms an instance of java.awt.Color class into a Kml color string.</p>
     * <p>BE CAREFUL : Color object RGB constructors and Kml representation are not in
     * same order (a : alpha; b: blue; g: green; r: red) : </p>
     * <ul>
     * <li>kml : &lt;color>aabbggrr&lt;/color>, with a, b, g, r hexadecimal characters between 0 and f.</li>
     * <li>Color : Color(r,g,b,a), with r, g, b, a int parameters between 0 and 255.</li>
     * </ul>
     *
     * @param color
     * @return
     */
    public static String toKmlColor(final Color color){
        String r = Integer.toHexString(color.getRed());
        String g = Integer.toHexString(color.getGreen());
        String b = Integer.toHexString(color.getBlue());
        String a = Integer.toHexString(color.getAlpha());
        r = ((r.length() == 1) ? "0" : "")+r;
        g = ((g.length() == 1) ? "0" : "")+g;
        b = ((b.length() == 1) ? "0" : "")+b;
        a = ((a.length() == 1) ? "0" : "")+a;
        return r+g+b+a;
    }

    public static void main(String[] args) throws KmlException {
        Color couleur = new Color (1,0,55,255);
        System.out.println(toKmlColor(couleur));

        parseColor("ffffffff");
    }
}
