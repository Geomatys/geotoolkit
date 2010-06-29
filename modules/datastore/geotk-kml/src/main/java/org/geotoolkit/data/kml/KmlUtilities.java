package org.geotoolkit.data.kml;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.geotoolkit.data.kml.model.KmlException;

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
     * <pre>
     * &lt;simpleType name="colorType">
     *  &lt;annotation>
     *      &lt;documentation>&lt;![CDATA[
     *       aabbggrr
     *       ffffffff: opaque white
     *       ff000000: opaque]]>
     *      &lt;/documentation>
     *  &lt;/annotation>
     *  &lt;restriction base="hexBinary">
     *      &lt;length value="4"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     *
     * @param kmlColor Kml string color hexadecimal representation.
     * @return
     * @throws KmlException
     */
    public static Color parseColor(final String kmlColor) throws KmlException {

        Color color = null;

        if (kmlColor.matches("[0-9a-fA-F]{8}")) {
            int r = Integer.parseInt(kmlColor.substring(0, 2), 16);
            int g = Integer.parseInt(kmlColor.substring(2, 4), 16);
            int b = Integer.parseInt(kmlColor.substring(4, 6), 16);
            int a = Integer.parseInt(kmlColor.substring(6, 8), 16);
            color = new Color(a, b, g, r);
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
     * <pre>
     * &lt;simpleType name="colorType">
     *  &lt;annotation>
     *      &lt;documentation>&lt;![CDATA[
     *       aabbggrr
     *       ffffffff: opaque white
     *       ff000000: opaque]]>
     *      &lt;/documentation>
     *  &lt;/annotation>
     *  &lt;restriction base="hexBinary">
     *      &lt;length value="4"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     * @param color
     * @return
     */
    public static String toKmlColor(final Color color) {
        String r = Integer.toHexString(color.getRed());
        String g = Integer.toHexString(color.getGreen());
        String b = Integer.toHexString(color.getBlue());
        String a = Integer.toHexString(color.getAlpha());
        r = ((r.length() == 1) ? "0" : "") + r;
        g = ((g.length() == 1) ? "0" : "") + g;
        b = ((b.length() == 1) ? "0" : "") + b;
        a = ((a.length() == 1) ? "0" : "") + a;
        return a + b + g + r;
    }

    /**
     * <p>This method check value for Anglepos180 element.</p>
     *
     * <pre>
     * &lt;simpleType name="anglepos180Type">
     *  &lt;restriction base="double">
     *      &lt;minInclusive value="0"/>
     *      &lt;maxInclusive value="180.0"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     * @param angle
     * @return
     */
    public static double checkAnglePos180(double angle) {
        if (angle < 0 || angle > 180) {
            throw new IllegalArgumentException("This angle type requires a value "
                    + "between 0 and 180 degrees. You've intented an initialization with "
                    + angle + " degree(s)");
        }
        return angle;
    }

    /**
     * <p>This method check value for Anglepos90 element.</p>
     *
     * <pre>
     * &lt;simpleType name="angle90posType">
     *  &lt;restriction base="double">
     *      &lt;minInclusive value="0"/>
     *      &lt;maxInclusive value="90.0"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     *
     * @param angle
     * @return
     */
    public static double checkAnglePos90(double angle) {
        if (angle < 0 || angle > 90) {
            throw new IllegalArgumentException("This angle type requires a value "
                    + "between 0 and 90 degrees. You've intented an initialization with "
                    + angle + " degree(s)");
        }
        return angle;
    }

    /**
     * <p>This method check value for Angle180 element.</p>
     *
     * <pre>
     * &lt;simpleType name="angle180Type">
     *  &lt;restriction base="double">
     *      &lt;minInclusive value="-180"/>
     *      &lt;maxInclusive value="180.0"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     *
     * @param angle
     * @return
     */
    public static double checkAngle180(double angle) {
        if (angle < -180 || angle > 180) {
            throw new IllegalArgumentException("This angle type requires a value "
                    + "between -180 and 180 degrees. You've intented an initialization with "
                    + angle + " degree(s)");
        }
        return angle;
    }

    /**
     * <p>This method check value for Angle180 element.</p>
     *
     * <pre>
     * &lt;simpleType name="angle90Type">
     *  &lt;restriction base="double">
     *      &lt;minInclusive value="-90"/>
     *      &lt;maxInclusive value="90.0"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     *
     * @param angle
     * @return
     */
    public static double checkAngle90(double angle) {
        if (angle < -90 || angle > 90) {
            throw new IllegalArgumentException("This angle type requires a value "
                    + "between -90 and 90 degrees. You've intented an initialization with "
                    + angle + " degree(s)");
        }
        return angle;
    }

    /**
     * <p>This method check value for Angle360 element.</p>
     *
     * <pre>
     * &lt;simpleType name="angle360Type">
     *  &lt;restriction base="double">
     *      &lt;minInclusive value="-360"/>
     *      &lt;maxInclusive value="360.0"/>
     *  &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     *
     * @param angle
     * @return
     */
    public static double checkAngle360(double angle) {
        if (angle < -360 || angle > 360) {
            throw new IllegalArgumentException("This angle type requires a value "
                    + "between -360 and 360 degrees. You've intented an initialization with "
                    + angle + " degree(s)");
        }
        return angle;
    }

//    public static Date toDate(String kmlDateTimeType) throws ParseException{
//        Date date = new Date();
//        kmlDateTimeType.
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
//        return dateFormat.parse(kmlDateTimeType);
//    }
}
