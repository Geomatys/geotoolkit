/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 */
package org.geotoolkit.filter.function.other;

import com.vividsolutions.jts.geom.Geometry;


/**
 * @author David Blasby (The Open Planning Project)
 * @module
 */
public class StaticUtils {

    private StaticUtils(){}

    public static boolean equalsExactTolerance(final Geometry geom, final Geometry geom2, final double tolerance) {
        return geom.equalsExact(geom2, tolerance);
    }

    public static boolean equalsExact(final Geometry geom, final Geometry geom2) {
        return geom.equalsExact(geom2);
    }


    //--------------------------------------------------------------------------
    //JAVA String functions
    public static String strConcat(final String s1, final String s2) {
        return s1 + s2;
    }

    public static boolean strEndsWith(final String s1, final String s2) {
        return s1.endsWith(s2);
    }

    public static boolean strStartsWith(final String s1, final String s2) {
        return s1.startsWith(s2);
    }

    public static boolean strEqualsIgnoreCase(final String s1, final String s2) {
        return s1.equalsIgnoreCase(s2);
    }

    public static int strIndexOf(final String s1, final String s2) {
        return s1.indexOf(s2);
    }

    public static int strLastIndexOf(final String s1, final String s2) {
        return s1.lastIndexOf(s2);
    }

    public static String strToLowerCase(final String s1) {
        return s1.toLowerCase();
    }

    public static String strToUpperCase(final String s1) {
        return s1.toUpperCase();
    }

    public static boolean strMatches(final String s1, final String s2) {
        return s1.matches(s2);
    }

    public static String strReplace(final String s1, final String s2, final String s3, final boolean bAll) {
        if (bAll) {
            return s1.replaceAll(s2, s3);
        } else {
            return s1.replaceFirst(s2, s3);
        }
    }

    public static String strSubstring(final String s1, final int beg, final int end) {
        return s1.substring(beg, end);
    }

    public static String strSubstringStart(final String s1, final int beg) {
        return s1.substring(beg);
    }

    public static String strTrim(final String s1) {
        return s1.trim();
    }

    public static String strTruncateFirst(final String s1, final int lenght){
        if(s1.length()<=lenght){
            return s1;
        }else{
            return s1.substring(s1.length()-lenght);
        }
    }

    public static String strTruncateLast(final String s1, final int lenght){
        if(s1.length()<=lenght){
            return s1;
        }else{
            return s1.substring(0,lenght);
        }
    }


    //--------------------------------------------------------------------------
    //data type xform
    public static double parseDouble(final String s) {
        return Double.parseDouble(s);
    }

    public static int parseInt(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e){
            // be nice for silly people!
            return (int) Math.round(Double.parseDouble(s));
        }
    }

    public static long parseLong(final String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            // be nice for silly people!
            return (long) Math.round(Double.parseDouble(s));
        }
    }

    public static boolean parseBoolean(final String s) {
        if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("f") || s.equalsIgnoreCase("false")
                || s.equalsIgnoreCase("0") || s.equalsIgnoreCase("0.0")) {
            return false;
        }
        return true;
    }

    public static int roundDouble(final double d) {
        return (int) Math.round(d);
    }

    public static double int2ddouble(final int i) {
        return (double) i;
    }

    public static boolean int2bbool(final int i) {
        return i == 0;
    }

    public static boolean double2bool(final double d) {
        return d == 0;
    }

    public static Object ifThenElse(final boolean p, final Object a, final Object b) {
        if (p) {
            return a;
        } else {
            return b;
        }
    }

//   --------------------------------------------------------------------------
    //OGC Filter comparisionOP functions
    public static boolean equalTo(final Object o1, final Object o2) {
        if (o1.getClass() == o2.getClass()) {
            return o1.equals(o2);
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
        }
        return o1.toString().equals(o2.toString());
    }

    public static boolean notEqualTo(final Object o1, final Object o2) {
        return !equalTo(o1, o2);
    }

    public static boolean lessThan(final Object o1, final Object o2) {
        if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).intValue() < ((Integer) o2).intValue();
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
        }
        return o1.toString().compareTo(o2.toString()) == 0;
    }

    public static boolean greaterThan(final Object o1, final Object o2) {
        if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).intValue() > ((Integer) o2).intValue();
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() > ((Number) o2).doubleValue();
        }
        return o1.toString().compareTo(o2.toString()) == 2;
    }

    public static boolean greaterEqualThan(final Object o1, final Object o2) {
        if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).intValue() >= ((Integer) o2).intValue();
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() >= ((Number) o2).doubleValue();
        }
        return o1.toString().compareTo(o2.toString()) == 2 ||
               o1.toString().compareTo(o2.toString()) == 1;
    }

    public static boolean lessEqualThan(final Object o1, final Object o2) {
        if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).intValue() <= ((Integer) o2).intValue();
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() <= ((Number) o2).doubleValue();
        }
        return o1.toString().compareTo(o2.toString()) == 0 ||
               o1.toString().compareTo(o2.toString()) == 1;
    }

    public static boolean isLike(final String s1, final String s2) {
        return s1.matches(s2); // this sucks, but hay...
    }

    public static boolean isNull(final Object o) {
        return o == null;
    }

    public static boolean between(final Object o, final Object min, final Object max) {
        return StaticUtils.greaterEqualThan(o, min) && StaticUtils.lessEqualThan(o, max);
    }

    public static boolean not(final boolean b) {
        return !b;
    }

//   --------------------------------------------------------------------------
    // SQL "var in (list)"

    public static boolean in(final Object s, final Object ... lst) {
        for(final Object obj : lst){
            if(equalTo(s, obj)) return true;
        }
        return false;
    }

}
