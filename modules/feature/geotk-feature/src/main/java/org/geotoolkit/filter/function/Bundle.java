/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
package org.geotoolkit.filter.function;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.resources.IndexedResourceBundle;


/**
 * Locale-dependent resources for words or simple sentences.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class Bundle extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Martin Desruisseaux (IRD)
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_AllPointsFunction_arg0 = 3;

        /**
         * Extract all points of the geometry, result is a <MultiPoint>.
         */
        public static final short org_geotoolkit_filter_function_geometry_AllPointsFunction_description = 4;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_AreaFunction_arg0 = 5;

        /**
         * Calculate the geometry area, result is a <Number> in the squared geometry coordinate
         * reference system unit.
         */
        public static final short org_geotoolkit_filter_function_geometry_AreaFunction_description = 6;

        /**
         * <Geometry>, Base geometry to apply buffer on
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferFunction_arg0 = 0;

        /**
         * <Number>, Buffer distance in units of the geometry coordinate reference system
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferFunction_arg1 = 1;

        /**
         * Expand or shrink a geometry, result is a <Geometry>.
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferFunction_description = 2;

        /**
         * <Geometry>, Base geometry to apply buffer on
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferGeoFunction_arg0 = 7;

        /**
         * <Number>, Buffer distance in given units
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferGeoFunction_arg1 = 8;

        /**
         * <String>, Buffer distance unit (km,m,...)
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferGeoFunction_arg2 = 9;

        /**
         * Expand or shrink a geometry, result is a <Geometry>.
         */
        public static final short org_geotoolkit_filter_function_geometry_BufferGeoFunction_description = 10;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_EndAngleFunction_arg0 = 11;

        /**
         * Extract the last geometry segment angle, result is a <Number> in radians.
         */
        public static final short org_geotoolkit_filter_function_geometry_EndAngleFunction_description = 12;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_EndPointFunction_arg0 = 13;

        /**
         * Extract the last point of the geometry, result is a <Point>.
         */
        public static final short org_geotoolkit_filter_function_geometry_EndPointFunction_description = 14;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_GeometryTypeFunction_arg0 = 15;

        /**
         * Get a string representation of the geometry type (POINT,LINESTRING,...), result is a
         * <String>.
         */
        public static final short org_geotoolkit_filter_function_geometry_GeometryTypeFunction_description = 16;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_LengthFunction_arg0 = 17;

        /**
         * Calculate the geometry length, result is a <Number> in the geometry coordinate reference
         * system unit.
         */
        public static final short org_geotoolkit_filter_function_geometry_LengthFunction_description = 18;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_StartAngleFunction_arg0 = 19;

        /**
         * Extract the first geometry segment angle, result is a <Number> in radians.
         */
        public static final short org_geotoolkit_filter_function_geometry_StartAngleFunction_description = 20;

        /**
         * <Geometry>, Base geometry to evaluate
         */
        public static final short org_geotoolkit_filter_function_geometry_StartPointFunction_arg0 = 21;

        /**
         * Extract the first point of the geometry, result is a <Point>.
         */
        public static final short org_geotoolkit_filter_function_geometry_StartPointFunction_description = 22;

        /**
         * <String>, Groovy script to execute
         */
        public static final short org_geotoolkit_filter_function_groovy_GroovyFunction_arg0 = 23;

        /**
         * Execute a groovy script, result is the returned value of the script.
         */
        public static final short org_geotoolkit_filter_function_groovy_GroovyFunction_description = 24;

        /**
         * <String>, Javascript to execute
         */
        public static final short org_geotoolkit_filter_function_javascript_JavaScriptFunction_arg0 = 25;

        /**
         * Execute a javascript, result is the returned value of the script.
         */
        public static final short org_geotoolkit_filter_function_javascript_JavaScriptFunction_description = 26;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_AbsFunction_arg0 = 27;

        /**
         * Calculate the absolute value of given number, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_AbsFunction_description = 28;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_AcosFunction_arg0 = 29;

        /**
         * Calculate the arc cosinus value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_AcosFunction_description = 30;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_AsinFunction_arg0 = 31;

        /**
         * Calculate the arc sinus value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_AsinFunction_description = 32;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_Atan2Function_arg0 = 33;

        /**
         * Calculate the theta component of the point (r, theta) in polar coordinates that corresponds
         * to the point (x, y) in Cartesian coordinates.
         */
        public static final short org_geotoolkit_filter_function_math_Atan2Function_description = 34;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_AtanFunction_arg0 = 35;

        /**
         * Calculate the arc tangent value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_AtanFunction_description = 36;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_CeilFunction_arg0 = 37;

        /**
         * Calculate the smallest (closest to negative infinity) double value that is greater than or
         * equal to the argument and is equal to a mathematical integer, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_CeilFunction_description = 38;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_CosFunction_arg0 = 39;

        /**
         * Calculate the cosinus value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_CosFunction_description = 40;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_ExpFunction_arg0 = 41;

        /**
         * Calculate exponential value, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_ExpFunction_description = 42;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_FloorFunction_arg0 = 43;

        /**
         * Calculate the largest (closest to positive infinity) double value that is less than or equal
         * to the argument and is equal to a mathematical integer, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_FloorFunction_description = 44;

        /**
         * <Number>, x1 value
         */
        public static final short org_geotoolkit_filter_function_math_HypotFunction_arg0 = 45;

        /**
         * <Number>, x2 value
         */
        public static final short org_geotoolkit_filter_function_math_HypotFunction_arg1 = 46;

        /**
         * Shortcut for equation sqrt(x1^2 + x2^2), called hypothenuse, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_HypotFunction_description = 47;

        /**
         * <Number>, f1 value
         */
        public static final short org_geotoolkit_filter_function_math_IEEERemainderFunction_arg0 = 48;

        /**
         * <Number>, f2 value
         */
        public static final short org_geotoolkit_filter_function_math_IEEERemainderFunction_arg1 = 49;

        /**
         * Computes the remainder operation on two arguments as prescribed by the IEEE 754 standard,
         * result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_IEEERemainderFunction_description = 50;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_LogFunction_arg0 = 51;

        /**
         * Calculate logarithm (base e), result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_LogFunction_description = 52;

        /**
         * <Number>, first value
         */
        public static final short org_geotoolkit_filter_function_math_MaxFunction_arg0 = 53;

        /**
         * <Number>, value value
         */
        public static final short org_geotoolkit_filter_function_math_MaxFunction_arg1 = 54;

        /**
         * Compare and return the maximum value, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_MaxFunction_description = 55;

        /**
         * <Number>, first value
         */
        public static final short org_geotoolkit_filter_function_math_MinFunction_arg0 = 56;

        /**
         * <Number>, value value
         */
        public static final short org_geotoolkit_filter_function_math_MinFunction_arg1 = 57;

        /**
         * Compare and return the minimum value, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_MinFunction_description = 58;

        /**
         * Returns the PI constant value with all digits, result is a <Double>.
         */
        public static final short org_geotoolkit_filter_function_math_PiFunction_description = 59;

        /**
         * <Number>, base value
         */
        public static final short org_geotoolkit_filter_function_math_PowFunction_arg0 = 60;

        /**
         * <Number>, exponent value
         */
        public static final short org_geotoolkit_filter_function_math_PowFunction_arg1 = 61;

        /**
         * Returns the value of the first argument raised to the power of the second argument, result
         * is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_PowFunction_description = 62;

        /**
         * Returns a random value between 0 and 1, result is a <Double>.
         */
        public static final short org_geotoolkit_filter_function_math_RandomFunction_description = 63;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_RintFunction_arg0 = 64;

        /**
         * Calculate the closest floating-point value to a that is equal to a mathematical integer,
         * result is a <Double>.
         */
        public static final short org_geotoolkit_filter_function_math_RintFunction_description = 65;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_RoundFunction_arg0 = 66;

        /**
         * Calculate the closest integer value, result is a <Integer>.
         */
        public static final short org_geotoolkit_filter_function_math_RoundFunction_description = 67;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_SinFunction_arg0 = 68;

        /**
         * Calculate the sinus value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_SinFunction_description = 69;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_math_SqrtFunction_arg0 = 70;

        /**
         * Calculate the square root value, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_SqrtFunction_description = 71;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_TanFunction_arg0 = 72;

        /**
         * Calculate the tangent value of given angle, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_math_TanFunction_description = 73;

        /**
         * <Number>, angle in radians
         */
        public static final short org_geotoolkit_filter_function_math_ToDegreesFunction_arg0 = 74;

        /**
         * Convert from radians to degrees, result is a <Number> in degrees.
         */
        public static final short org_geotoolkit_filter_function_math_ToDegreesFunction_description = 75;

        /**
         * <Number>, angle in degrees
         */
        public static final short org_geotoolkit_filter_function_math_ToRadiansFunction_arg0 = 76;

        /**
         * Convert from degrees to radians, result is a <Number> in radians.
         */
        public static final short org_geotoolkit_filter_function_math_ToRadiansFunction_description = 77;

        /**
         * <Any>, base value
         */
        public static final short org_geotoolkit_filter_function_other_ConvertFunction_arg0 = 78;

        /**
         * <Class>, wanted result class.
         */
        public static final short org_geotoolkit_filter_function_other_ConvertFunction_arg1 = 79;

        /**
         * Convert a value to a different type, result is of given class type, if convertion failed a
         * null is returned.
         */
        public static final short org_geotoolkit_filter_function_other_ConvertFunction_description = 80;

        /**
         * <Date>, date value
         */
        public static final short org_geotoolkit_filter_function_other_DateFormatFunction_arg0 = 81;

        /**
         * <String>, wanted date format pattern (example : yyyy-MM-dd'T'HH:mm:ss.SSSZ)
         */
        public static final short org_geotoolkit_filter_function_other_DateFormatFunction_arg1 = 82;

        /**
         * Format a date to a string using given format, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_other_DateFormatFunction_description = 83;

        /**
         * <String>, date value in string format
         */
        public static final short org_geotoolkit_filter_function_other_DateParseFunction_arg0 = 84;

        /**
         * <String>, date format pattern (example : yyyy-MM-dd'T'HH:mm:ss.SSSZ)
         */
        public static final short org_geotoolkit_filter_function_other_DateParseFunction_arg1 = 85;

        /**
         * Parse a date from a string using given format, result is a <Date>.
         */
        public static final short org_geotoolkit_filter_function_other_DateParseFunction_description = 86;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_EqualToFunction_arg0 = 87;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_EqualToFunction_arg1 = 88;

        /**
         * Test if two values are equal, this is a strict type test unlike the '=' operand, result is a
         * <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_EqualToFunction_description = 89;

        /**
         * <Geometry>, first geometry
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactFunction_arg0 = 90;

        /**
         * <Geometry>, second geometry
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactFunction_arg1 = 91;

        /**
         * Test if two geometries are equal, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactFunction_description = 92;

        /**
         * <Geometry>, first geometry
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactToleranceFunction_arg0 = 93;

        /**
         * <Geometry>, second geometry
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactToleranceFunction_arg1 = 94;

        /**
         * <Number>, distance tolerance in geometry coordinate system unit.
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactToleranceFunction_arg2 = 95;

        /**
         * Test if two geometries are equal with a tolerance margin, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_EqualsExactToleranceFunction_description = 96;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_GreaterEqualThanFunction_arg0 = 97;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_GreaterEqualThanFunction_arg1 = 98;

        /**
         * Same as '>=' operand, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_GreaterEqualThanFunction_description = 99;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_GreaterThanFunction_arg0 = 100;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_GreaterThanFunction_arg1 = 101;

        /**
         * Same as '>' operand, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_GreaterThanFunction_description = 102;

        /**
         * <Boolean>, expression which evaluates to a boolean
         */
        public static final short org_geotoolkit_filter_function_other_IfThenElseFunction_arg0 = 103;

        /**
         * <Any>, returned value if first argument is true
         */
        public static final short org_geotoolkit_filter_function_other_IfThenElseFunction_arg1 = 104;

        /**
         * <Any>, returned value if first argument is false
         */
        public static final short org_geotoolkit_filter_function_other_IfThenElseFunction_arg2 = 105;

        /**
         * Simple conditional branch.
         */
        public static final short org_geotoolkit_filter_function_other_IfThenElseFunction_description = 106;

        /**
         * <Any>, searched value
         */
        public static final short org_geotoolkit_filter_function_other_InFunction_arg0 = 107;

        /**
         * <Any>, multiple arguments which compose the search list
         */
        public static final short org_geotoolkit_filter_function_other_InFunction_arg1 = 108;

        /**
         * Test if a value is in the given list, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_InFunction_description = 109;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_other_IntToBbool_arg0 = 110;

        /**
         * Convert a number to a <Boolean>, if value is 0 result is false, true otherwise.
         */
        public static final short org_geotoolkit_filter_function_other_IntToBbool_description = 111;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_other_IntToDdoubleFunction_arg0 = 112;

        /**
         * Convert a number to a <Double>.
         */
        public static final short org_geotoolkit_filter_function_other_IntToDdoubleFunction_description = 113;

        /**
         * <String>, string value to test
         */
        public static final short org_geotoolkit_filter_function_other_IsLikeFunction_arg0 = 114;

        /**
         * <String>, regex pattern
         */
        public static final short org_geotoolkit_filter_function_other_IsLikeFunction_arg1 = 115;

        /**
         * Test if the given string matches the regex pattern, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_IsLikeFunction_description = 116;

        /**
         * <Any>, value to test
         */
        public static final short org_geotoolkit_filter_function_other_IsNullFunction_arg0 = 117;

        /**
         * Test if the given value is null, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_IsNullFunction_description = 118;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_LessEqualThanFunction_arg0 = 119;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_LessEqualThanFunction_arg1 = 120;

        /**
         * Same as '<=' operand, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_LessEqualThanFunction_description = 121;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_LessThanFunction_arg0 = 122;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_LessThanFunction_arg1 = 123;

        /**
         * Same as '<' operand, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_LessThanFunction_description = 124;

        /**
         * <Any>, first value
         */
        public static final short org_geotoolkit_filter_function_other_NotEqualToFunction_arg0 = 125;

        /**
         * <Any>, second value
         */
        public static final short org_geotoolkit_filter_function_other_NotEqualToFunction_arg1 = 126;

        /**
         * Test if two values are not equal, this is a strict type test unlike the '=' operand, result
         * is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_NotEqualToFunction_description = 127;

        /**
         * <Boolean>, value
         */
        public static final short org_geotoolkit_filter_function_other_NotFunction_arg0 = 128;

        /**
         * Invert boolean value, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_NotFunction_description = 129;

        /**
         * <Number>, number value
         */
        public static final short org_geotoolkit_filter_function_other_NumberFormatFunction_arg0 = 130;

        /**
         * <String>, wanted number format pattern (example : ###.##)
         */
        public static final short org_geotoolkit_filter_function_other_NumberFormatFunction_arg1 = 131;

        /**
         * Format a number to a string using given format, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_other_NumberFormatFunction_description = 132;

        /**
         * <String>, boolean value as a string, can be '0','1','f','t,'false','true'
         */
        public static final short org_geotoolkit_filter_function_other_ParseBooleanFunction_arg0 = 133;

        /**
         * Parse a boolean from string, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_ParseBooleanFunction_description = 134;

        /**
         * <String>, double value as a string
         */
        public static final short org_geotoolkit_filter_function_other_ParseDoubleFunction_arg0 = 135;

        /**
         * Parse a double from string, result is a <Double>.
         */
        public static final short org_geotoolkit_filter_function_other_ParseDoubleFunction_description = 136;

        /**
         * <String>, integer value as a string
         */
        public static final short org_geotoolkit_filter_function_other_ParseIntFunction_arg0 = 137;

        /**
         * Parse an integer from string, result is a <Integer>.
         */
        public static final short org_geotoolkit_filter_function_other_ParseIntFunction_description = 138;

        /**
         * <String>, long value as a string
         */
        public static final short org_geotoolkit_filter_function_other_ParseLongFunction_arg0 = 139;

        /**
         * Parse a long from string, result is a <Long>.
         */
        public static final short org_geotoolkit_filter_function_other_ParseLongFunction_description = 140;

        /**
         * <String>, property name
         */
        public static final short org_geotoolkit_filter_function_other_PropertyExistsFunction_arg0 = 141;

        /**
         * Verify is a property exists in the evaluated object, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_other_PropertyExistsFunction_description = 142;

        /**
         * <Number>, value to evaluate
         */
        public static final short org_geotoolkit_filter_function_other_RoundDoubleFunction_arg0 = 143;

        /**
         * Calculate the closest integer value, result is a <Number>.
         */
        public static final short org_geotoolkit_filter_function_other_RoundDoubleFunction_description = 144;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_ConcatFunction_arg0 = 145;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_ConcatFunction_arg1 = 146;

        /**
         * Concatenate two strings, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_ConcatFunction_description = 147;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_EndsWithFunction_arg0 = 148;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_EndsWithFunction_arg1 = 149;

        /**
         * Test if the first string ends with the second string, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_string_EndsWithFunction_description = 150;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_EqualsIgnoreCaseFunction_arg0 = 151;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_EqualsIgnoreCaseFunction_arg1 = 152;

        /**
         * Test if the first string equals the second, this is case insensitive, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_string_EqualsIgnoreCaseFunction_description = 153;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_IndexOfFunction_arg0 = 154;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_IndexOfFunction_arg1 = 155;

        /**
         * Find the index of the first occurence of second string in the first one, result is a
         * <Integer>.
         */
        public static final short org_geotoolkit_filter_function_string_IndexOfFunction_description = 156;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_LastIndexOfFunction_arg0 = 157;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_LastIndexOfFunction_arg1 = 158;

        /**
         * Find the index of the last occurence of second string in the first one, result is a
         * <Integer>.
         */
        public static final short org_geotoolkit_filter_function_string_LastIndexOfFunction_description = 159;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_LengthFunction_arg0 = 160;

        /**
         * Calculate string length, result is a <Integer>.
         */
        public static final short org_geotoolkit_filter_function_string_LengthFunction_description = 161;

        /**
         * <String>, string value to test
         */
        public static final short org_geotoolkit_filter_function_string_MatchesFunction_arg0 = 162;

        /**
         * <String>, regex pattern
         */
        public static final short org_geotoolkit_filter_function_string_MatchesFunction_arg1 = 163;

        /**
         * Test if the given string matches the regex pattern, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_string_MatchesFunction_description = 164;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_ReplaceFunction_arg0 = 165;

        /**
         * <String>, searched text
         */
        public static final short org_geotoolkit_filter_function_string_ReplaceFunction_arg1 = 166;

        /**
         * <String>, replacement text
         */
        public static final short org_geotoolkit_filter_function_string_ReplaceFunction_arg2 = 167;

        /**
         * <Boolean>, true to replace all occurences, false to replace only the first
         */
        public static final short org_geotoolkit_filter_function_string_ReplaceFunction_arg3 = 168;

        /**
         * Replace text fragments in first string, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_ReplaceFunction_description = 169;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_StartsWithFunction_arg0 = 170;

        /**
         * <String>, second string
         */
        public static final short org_geotoolkit_filter_function_string_StartsWithFunction_arg1 = 171;

        /**
         * Test if the first string starts with the second string, result is a <Boolean>.
         */
        public static final short org_geotoolkit_filter_function_string_StartsWithFunction_description = 172;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_SubstringFunction_arg0 = 173;

        /**
         * <Integer>, start index inclusive
         */
        public static final short org_geotoolkit_filter_function_string_SubstringFunction_arg1 = 174;

        /**
         * <Integer>, end index exclusive
         */
        public static final short org_geotoolkit_filter_function_string_SubstringFunction_arg2 = 175;

        /**
         * Extract a range in the first string, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_SubstringFunction_description = 176;

        /**
         * <String>, first string
         */
        public static final short org_geotoolkit_filter_function_string_SubstringStartFunction_arg0 = 177;

        /**
         * <Integer>, start index inclusive
         */
        public static final short org_geotoolkit_filter_function_string_SubstringStartFunction_arg1 = 178;

        /**
         * Extract a range in the first string from start index to end, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_SubstringStartFunction_description = 179;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_ToLowerCaseFunction_arg0 = 180;

        /**
         * Convert to lower case, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_ToLowerCaseFunction_description = 181;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_ToUpperCaseFunction_arg0 = 182;

        /**
         * Convert to upper case, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_ToUpperCaseFunction_description = 183;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_TrimFunction_arg0 = 184;

        /**
         * Remove all starting or ending spaces, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_TrimFunction_description = 185;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_TruncateFirstFunction_arg0 = 186;

        /**
         * <Integer>, wanted length
         */
        public static final short org_geotoolkit_filter_function_string_TruncateFirstFunction_arg1 = 187;

        /**
         * Truncate string, removing end characters to match requested length, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_TruncateFirstFunction_description = 188;

        /**
         * <String>, string value
         */
        public static final short org_geotoolkit_filter_function_string_TruncateLastFunction_arg0 = 189;

        /**
         * <Integer>, wanted length
         */
        public static final short org_geotoolkit_filter_function_string_TruncateLastFunction_arg1 = 190;

        /**
         * Truncate string, removing start characters to match requested length, result is a <String>.
         */
        public static final short org_geotoolkit_filter_function_string_TruncateLastFunction_description = 191;

        /**
         * <Expression>, property name
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_arg0 = 192;

        /**
         * <Expression>, first value
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_arg1 = 193;

        /**
         * <Expression,Expression>*, threshold property value and following value
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_arg2 = 194;

        /**
         * <Expression>, method : 'numeric' or 'color'
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_arg3 = 195;

        /**
         * <Expression>, belong to : 'succeeding' or 'preceding'
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_arg4 = 196;

        /**
         * Categorization function, result can be a <Number> or <Color>
         */
        public static final short org_geotoolkit_style_function_DefaultCategorize_description = 197;

        /**
         * <Expression>, property name
         */
        public static final short org_geotoolkit_style_function_DefaultInterpolate_arg0 = 198;

        /**
         * <Expression,Expression>*, interpolation point property value and result value
         */
        public static final short org_geotoolkit_style_function_DefaultInterpolate_arg1 = 199;

        /**
         * <Expression>, method : 'numeric' or 'color'
         */
        public static final short org_geotoolkit_style_function_DefaultInterpolate_arg2 = 200;

        /**
         * <Expression>, mode : 'linear', 'cosine', 'cubic'
         */
        public static final short org_geotoolkit_style_function_DefaultInterpolate_arg3 = 201;

        /**
         * Linear interpolation, result can be a <Number> or <Color>
         */
        public static final short org_geotoolkit_style_function_DefaultInterpolate_description = 202;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Bundle(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Bundle getResources(Locale locale) throws MissingResourceException {
        return getBundle(Bundle.class, locale);
    }

    /**
     * The international string to be returned by {@link formatInternational}.
     */
    private static final class International extends ResourceInternationalString {
        private static final long serialVersionUID = -9199238559657784488L;

        International(final int key) {
            super(Bundle.class.getName(), String.valueOf(key));
        }

        @Override
        protected ResourceBundle getBundle(final Locale locale) {
            return getResources(locale);
        }
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @return An international string for the given key.
     */
    public static InternationalString formatInternational(final short key) {
        return new International(key);
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * {@note This method is redundant with the one expecting <code>Object...</code>, but is
     *        provided for binary compatibility with previous Geotk versions. It also avoid the
     *        creation of a temporary array. There is no risk of confusion since the two methods
     *        delegate their work to the same <code>format</code> method anyway.}
     *
     * @param  key The key for the desired string.
     * @param  arg Values to substitute to "{0}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object arg) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, arg));
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @param  args Values to substitute to "{0}", "{1}", <i>etc</i>.
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object... args) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, args));
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
