/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Color;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.KmlException;

/**
 * <p>This class provides utilities for reading and writting KML files.</p>
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

    public static String toString(Coordinate coordinate) {
        final StringBuilder sb = new StringBuilder();
        sb.append(coordinate.x);
        sb.append(',');
        sb.append(coordinate.y);
        if(!Double.isNaN(coordinate.z)) {
            sb.append(',');
            sb.append(coordinate.z);
        }
        return sb.toString();
    }

    public static String toString(Coordinates coordinates){
        final StringBuilder sb = new StringBuilder();

        for(int i=0, n=coordinates.size(); i<n; i++){
            sb.append(toString(coordinates.getCoordinate(i)));
            if(i != n-1)
                sb.append(' ');
        }
        return sb.toString();
    }


    public static Coordinate toCoordinate(String coordinates){
        final String[] coordinatesList = coordinates.split(",");
        final Coordinate c = new Coordinate();

        c.x = Double.valueOf(coordinatesList[0].trim());
        c.y = Double.valueOf(coordinatesList[1].trim());
        if(coordinatesList.length == 3){
            c.z = Double.valueOf(coordinatesList[2].trim());
        }

        return c;
    }

    public static String getFormatedString(Calendar calendar, boolean forceDateTime){
        String result = "";

        int milli = calendar.get(Calendar.MILLISECOND);
        int seconds = calendar.get(Calendar.SECOND);
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year;
        if(calendar.get(Calendar.ERA) == GregorianCalendar.BC){
            year = -calendar.get(Calendar.YEAR);
        } else {
            year = calendar.get(Calendar.YEAR);
        }

        boolean isOffset = (calendar.getTimeZone() != null);
        boolean isTime = !(hours == 0 && minutes == 0 && seconds == 0 && milli == 0);


        if(isOffset){
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            int minutesOffset = zoneOffset / (60 * 1000);
            int hoursOffset = minutesOffset / 60;
            minutesOffset = (minutesOffset % 60)*60;
            String zhh = null, zmm = null, zs= null;

            if(minutesOffset < 10)
                zmm = "0"+minutesOffset;
            else
                zmm = Integer.toString(minutesOffset);

            if(hoursOffset < 10)
                zhh = "0"+hoursOffset;
            else
                zhh = Integer.toString(hoursOffset);

            if(zoneOffset > 0)
                result = '+'+zhh+':'+zmm;
            else if (zoneOffset < 0)
                result = '-'+zhh+':'+zmm;
            else
                result = "Z";
        }

        if (milli != 0){
            result = "."+milli+result;
        }

        if (isTime || forceDateTime){
            if(seconds < 10)
                result = ":0"+seconds+result;
            else
                result = ":"+seconds+result;
            if(minutes < 10)
                result = ":0"+minutes+result;
            else
                result = ":"+minutes+result;
            if(hours < 10)
                result = "T0"+hours+result;
            else
                result = "T"+hours+result;
        }

        String date;
        if (day > 1 || forceDateTime){
            date = year+"-"+
                ((month < 10) ? ("0"+month) : month) +"-"+
                ((day < 10) ? ("0"+day) : day);}
        else if (month > 1)
            date = year+"-"+
                ((month < 10) ? ("0"+month) : month);
        else
            date = Integer.toString(year);
        return date+result;
    }

}
