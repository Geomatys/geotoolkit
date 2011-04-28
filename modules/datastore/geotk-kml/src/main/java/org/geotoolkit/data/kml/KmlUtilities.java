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
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.awt.Color;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.geotoolkit.data.kml.model.KmlException;

import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 * <p>This class provides utilities for reading and writting KML files.</p>
 *
 * @author Samuel Andrés
 * @module pending
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
        final String r = Integer.toHexString(color.getRed());
        final String g = Integer.toHexString(color.getGreen());
        final String b = Integer.toHexString(color.getBlue());
        final String a = Integer.toHexString(color.getAlpha());
        final StringBuilder sb = new StringBuilder();

        if(a.length() == 1){
            sb.append('0');
        }
        sb.append(a);
        if(b.length() == 1){
            sb.append('0');
        }
        sb.append(b);
        if(g.length() == 1){
            sb.append('0');
        }
        sb.append(g);
        if(r.length() == 1){
            sb.append('0');
        }
        sb.append(r);
        
        return sb.toString();
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

    /**
     * <p>Retrieves a coma separated StringBuilder from a 2D/3D coordinate.</p>
     *
     * @param coordinate
     * @return
     */
    public static StringBuilder toString(Coordinate coordinate) {

        final StringBuilder sb = new StringBuilder();
        sb.append(coordinate.x);
        sb.append(',');
        sb.append(coordinate.y);
        if(!Double.isNaN(coordinate.z)) {
            sb.append(',');
            sb.append(coordinate.z);
        }
        return sb;
    }

    /**
     * <p>Retrieves a XML list (space separated values) of coordinates.</p>
     *
     * @param coordinates
     * @return
     */
    public static String toString(CoordinateSequence coordinates){

        final StringBuilder sb = new StringBuilder();

        for(int i=0, n=coordinates.size(); i<n; i++){
            sb.append(toString(coordinates.getCoordinate(i)));
            if(i != n-1)
                sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * <p>Retrieves Coordinate (2D/3D) from coma separated values.</p>
     *
     * @param coordinates
     * @return
     */
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

    /**
     * <p>Retrieves XML formated time.</p>
     *
     * @param hours
     * @param minutes
     * @param seconds
     * @param milli
     * @param forceTime
     * @param time
     * @return
     */
    public static StringBuilder appendXMLFormatedTime(
            int hours, int minutes, int seconds, int milli, boolean forceTime, StringBuilder time){

        if(!(hours == 0 && minutes == 0 && seconds == 0 && milli == 0) || forceTime){
            time.append('T');
            if(hours < 10) {
                time.append('0');
            }
            time.append(hours);

            time.append(':');
            if(minutes < 10) {
                time.append('0');
            }
            time.append(minutes);

            time.append(':');
            if(seconds < 10) {
                time.append('0');
            }
            time.append(seconds);

            if (milli != 0){
                time.append('.');
                time.append(milli);
            }
        }
        return time;
    }

    /**
     * <p>Retrieves XML formated timezone.</p>
     *
     * @param zoneOffset
     * @param timeZone
     * @return
     */
    public static StringBuilder appendXMLFormatedTimeZone(
            int zoneOffset, StringBuilder timeZone) {

        int minutesOffset = zoneOffset / (60 * 1000);
        final int hoursOffset = Math.abs(minutesOffset / 60);
        minutesOffset = (minutesOffset % 60)*60;

        if(zoneOffset == 0){
            timeZone.append('Z');
        } else {
            if(zoneOffset > 0)
                timeZone.append('+');
            else if (zoneOffset < 0)
                timeZone.append('-');
            
            if(hoursOffset < 10)
                timeZone.append('0');
            timeZone.append(hoursOffset);
            timeZone.append(':');
            if(minutesOffset < 10)
                timeZone.append('0');
            timeZone.append(minutesOffset);
        }
        return timeZone;
    }

    /**
     * <p>Retrieves XML formated date</p>
     *
     * @param year
     * @param month
     * @param day
     * @param forceDay
     * @param date
     * @return
     */
    public static StringBuilder appendXMLFormatedDate(
            int year, int month, int day, boolean forceDay, StringBuilder date){
        
        date.append(year);
        if (day > 1 || forceDay){
            date.append('-');
            if(month < 10)
                date.append('0');
            date.append(month);
            date.append('-');
            if(day < 10)
                date.append('0');
            date.append(day);
        } else if (month > 1){
            date.append('-');
            if(month < 10)
                date.append('0');
            date.append(month);
        }
        return date;
    }

    /**
     * <p>Retrieves XML formated datetime from Calendar.</p>
     *
     * @param calendar
     * @param forceDateTime
     * @return
     */
    public static String getXMLFormatedCalendar(Calendar calendar, boolean forceDateTime){

        StringBuilder date = null;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH)+1;
        final int year;

        if(calendar.get(Calendar.ERA) == GregorianCalendar.BC){
            year = -calendar.get(Calendar.YEAR);
        } else {
            year = calendar.get(Calendar.YEAR);
        }

        date = appendXMLFormatedDate(year, month, day, forceDateTime, new StringBuilder());
        
        date = appendXMLFormatedTime(calendar.get(
                Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND),
                forceDateTime, date);

        if(calendar.getTimeZone() != null){
            date = appendXMLFormatedTimeZone(calendar.get(Calendar.ZONE_OFFSET), date);
        }

        return date.toString();
    }

    /**
     *
     * @param d
     * @return true if d is not infinite nor NaN.
     */
    public static boolean isFiniteNumber(double d){
        return !Double.isInfinite(d) && !Double.isNaN(d);
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractGeometry element.
     */
    public static boolean isAbstractGeometry(String eName) {
        return (TAG_MULTI_GEOMETRY.equals(eName)
                || TAG_LINE_STRING.equals(eName)
                || TAG_POLYGON.equals(eName)
                || TAG_POINT.equals(eName)
                || TAG_LINEAR_RING.equals(eName)
                || TAG_MODEL.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractFeature element.
     */
    public static boolean isAbstractFeature(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_NETWORK_LINK.equals(eName)
                || TAG_DOCUMENT.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName)
                || TAG_PLACEMARK.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractContainer element.
     */
    public static boolean isAbstractContainer(String eName) {
        return (TAG_FOLDER.equals(eName)
                || TAG_DOCUMENT.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractOverlay element.
     */
    public static boolean isAbstractOverlay(String eName) {
        return (TAG_GROUND_OVERLAY.equals(eName)
                || TAG_PHOTO_OVERLAY.equals(eName)
                || TAG_SCREEN_OVERLAY.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractView element.
     */
    public static boolean isAbstractView(String eName) {
        return (TAG_LOOK_AT.equals(eName)
                || TAG_CAMERA.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractTimePrimitive element.
     */
    public static boolean isAbstractTimePrimitive(String eName) {
        return (TAG_TIME_STAMP.equals(eName)
                || TAG_TIME_SPAN.equals(eName));
    }

    /**
     * @param eName the tag name.
     * @return true if the tag name is an AbstractStyleSelector element.
     */
    public static boolean isAbstractStyleSelector(String eName) {
        return (TAG_STYLE.equals(eName)
                || TAG_STYLE_MAP.equals(eName));
    }

    /**
     * @param eName
     * @return
     */
    public static boolean isAbstractSubStyle(String eName) {
        return (TAG_BALLOON_STYLE.equals(eName)
                || TAG_LIST_STYLE.equals(eName)
                || isAbstractColorStyle(eName));
    }

    /**
     * @param eName
     * @return
     */
    public static boolean isAbstractColorStyle(String eName) {
        return (TAG_ICON_STYLE.equals(eName)
                || TAG_LABEL_STYLE.equals(eName)
                || TAG_POLY_STYLE.equals(eName)
                || TAG_LINE_STYLE.equals(eName));
    }

    /**
     * @param eName
     * @return
     */
    public static boolean isAbstractObject(String eName) {
        // Traiter le cas particuloer du TAG_ICON qui peut être un basicLink
        return (isAbstractFeature(eName)
                || isAbstractGeometry(eName)
                || isAbstractStyleSelector(eName)
                || isAbstractSubStyle(eName)
                || isAbstractView(eName)
                || TAG_PAIR.equals(eName)
                || TAG_LINK.equals(eName)
                || TAG_VIEW_VOLUME.equals(eName)
                || TAG_REGION.equals(eName)
                || TAG_LOD.equals(eName)
                || TAG_ORIENTATION.equals(eName)
                || TAG_SCHEMA_DATA.equals(eName));
    }

    /**
     * @param eName
     * @return
     */
    public static boolean isAbstractLatLonBox(String eName) {
        return (TAG_LAT_LON_ALT_BOX.equals(eName)
                || TAG_LAT_LON_BOX.equals(eName));
    }
    
    private KmlUtilities(){}
}
