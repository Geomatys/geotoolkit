/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.measure;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Quantity;
import javax.measure.converter.UnitConverter;

import org.geotoolkit.lang.Static;


/**
 * A set of units to use in addition of {@link SI} and {@link NonSI}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 2.1
 * @module
 */
@Static
public final class Units {
    /**
     * Small tolerance factor for the comparisons of floating point values.
     */
    private static final double EPS = 1E-12;

    /**
     * Do not allows instantiation of this class.
     */
    private Units() {
    }

    /**
     * Unit for milliseconds. Usefull for conversion from and to {@link java.util.Date} objects.
     */
    public static final Unit<Duration> MILLISECOND = SI.MetricPrefix.MILLI(SI.SECOND);

    /**
     * Pseudo-unit for sexagesimal degree. Numbers in this pseudo-unit has the following format:
     *
     * <cite>sign - degrees - decimal point - minutes (two digits) - integer seconds (two digits) -
     * fraction of seconds (any precision)</cite>.
     * <p>
     * This unit is non-linear and not pratical for computation. Consequently, it should be
     * avoid as much as possible. Unfortunatly, this pseudo-unit is extensively used in the
     * EPSG database (code 9110).
     *
     * @todo <a href="http://kenai.com/jira/browse/JSR_275-41">JSR-275 bug</a>
     */
    public static final Unit<Angle> SEXAGESIMAL_DMS = NonSI.DEGREE_ANGLE.transform(
            SexagesimalConverter.FRACTIONAL.inverse()).asType(Angle.class);//.alternate("D.MS");

    /**
     * Pseudo-unit for degree - minute - second. Numbers in this pseudo-unit has the following
     * format:
     *
     * <cite>signed degrees (integer) - arc-minutes (integer) - arc-seconds
     * (real, any precision)</cite>.
     * <p>
     * This unit is non-linear and not pratical for computation. Consequently, it should be
     * avoid as much as possible. Unfortunatly, this pseudo-unit is extensively used in the
     * EPSG database (code 9107).
     *
     * @todo <a href="http://kenai.com/jira/browse/JSR_275-41">JSR-275 bug</a>
     */
    public static final Unit<Angle> DEGREE_MINUTE_SECOND = NonSI.DEGREE_ANGLE.transform(
            SexagesimalConverter.INTEGER.inverse()).asType(Angle.class);//.alternate("DMS");

    /**
     * Parts per million.
     *
     * @todo <a href="http://kenai.com/jira/browse/JSR_275-41">JSR-275 bug</a>
     */
    public static final Unit<Dimensionless> PPM = Unit.ONE.times(1E-6);//.alternate("ppm");

    /**
     * A few units commonly used in GIS.
     */
    private static final Map<Unit<?>,Unit<?>> COMMONS = new HashMap<Unit<?>,Unit<?>>(48);
    static {
        COMMONS.put(PPM, PPM);
        boolean nonSI = false;
        do for (final Field field : (nonSI ? NonSI.class : SI.class).getFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                final Object value;
                try {
                    value = field.get(null);
                } catch (Exception e) {
                    // Should not happen since we asked only for public static constants.
                    throw new AssertionError(e);
                }
                if (value instanceof Unit<?>) {
                    final Unit<?> unit = (Unit<?>) value;
                    if (isLinear(unit) || isAngular(unit) || isScale(unit)) {
                        COMMONS.put(unit, unit);
                    }
                }
            }
        } while ((nonSI = !nonSI) == true);
    }

    /**
     * Returns {@code true} if the given unit is a temporal unit.
     * Temporal units are convertible to {@link SI#SECOND}.
     *
     * @param unit The unit to check (may be {@code null}).
     * @return {@code true} if the given unit is non-null and temporal.
     *
     * @since 3.00
     */
    public static boolean isTemporal(final Unit<?> unit) {
        return (unit != null) && unit.toSI().equals(SI.SECOND);
    }

    /**
     * Returns {@code true} if the given unit is a linear unit.
     * Linear units are convertible to {@link SI#METRE}.
     *
     * @param unit The unit to check (may be {@code null}).
     * @return {@code true} if the given unit is non-null and linear.
     *
     * @since 3.00
     */
    public static boolean isLinear(final Unit<?> unit) {
        return (unit != null) && unit.toSI().equals(SI.METRE);
    }

    /**
     * Returns {@code true} if the given unit is a linear unit.
     * Linear units are convertible to {@link NonSI#DEGREE_ANGLE}.
     * <p>
     * Angular units are dimensionless, which may be a cause of confusion with other
     * dimensionless units like {@link Unit#ONE} or {@link #PPM}. This method take
     * care of differentiating angular units from other dimensionless units.
     *
     * @param unit The unit to check (may be {@code null}).
     * @return {@code true} if the given unit is non-null and angular.
     *
     * @since 3.00
     */
    public static boolean isAngular(final Unit<?> unit) {
        return (unit != null) && unit.toSI().equals(SI.RADIAN);
    }

    /**
     * Returns {@code true} if the given unit is a dimensionless scale unit.
     * This include {@link Unit#ONE} and {@link #PPM}.
     *
     * @param unit The unit to check (may be {@code null}).
     * @return {@code true} if the given unit is non-null and a dimensionless scale.
     *
     * @since 3.00
     */
    public static boolean isScale(final Unit<?> unit) {
        return (unit != null) && unit.toSI().equals(Unit.ONE);
    }

    /**
     * Multiplies the given unit by the given factor. For example multiplying {@link SI#METRE}
     * by 1000 gives {@link SI#KILOMETRE}. Invoking this method is equivalent to invoking
     * {@link Unit#times(double)} except for the following:
     * <p>
     * <ul>
     *   <li>A small tolerance factor is applied for a few factors commonly used in GIS.
     *       For example {@code multiply(SI.RADIANS, 0.0174532925199...)} will return
     *       {@link NonSI#DEGREE_ANGLE} even if the given numerical value is slightly
     *       different than {@linkplain Math#PI pi}/180. The tolerance factor and the
     *       set of units handled especially may change in future Geotk versions.</li>
     *   <li>This method tries to returns unique instances for some common units.</li>
     * </ul>
     *
     * @param  <A>    The quantity measured by the unit.
     * @param  unit   The unit to multiply.
     * @param  factor The multiplication factor.
     * @return The unit multiplied by the given factor.
     *
     * @since 3.07
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <A extends Quantity> Unit<A> multiply(Unit<A> unit, final double factor) {
        if (SI.RADIAN.equals(unit)) {
            if (Math.abs(factor - (Math.PI / 180)) < EPS) {
                return (Unit) NonSI.DEGREE_ANGLE;
            }
            if (Math.abs(factor - (Math.PI / 200)) < EPS) {
                return (Unit) NonSI.GRADE;
            }
        }
        if (Math.abs(factor - 1) > EPS) {
            final long fl = (long) factor;
            if (fl == factor) {
                /*
                 * Invoke the Unit.times(long) overloaded method, not Unit.scale(double),
                 * because as of JSR-275 0.9.3 the method with the long argument seems to
                 * do a better work of detecting when the result is an existing unit.
                 */
                unit = unit.times(fl);
            } else {
                unit = unit.times(factor);
            }
        }
        return canonicalize(unit);
    }

    /**
     * Returns a unique instance of the given units if possible, or the units unchanged otherwise.
     *
     * @param  <A>    The quantity measured by the unit.
     * @param  unit   The unit to canonicalize.
     * @return A unit equivalents to the given unit, canonicalized if possible.
     *
     * @since 3.07
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static <A extends Quantity> Unit<A> canonicalize(final Unit<A> unit) {
        final Unit<?> candidate = COMMONS.get(unit);
        if (candidate != null) {
            return (Unit) candidate;
        }
        return unit;
    }

    /**
     * Returns the factor by which to multiply the standard unit in order to get the given unit.
     * The "standard" unit is usually the SI unit on which the given unit is based.
     * <p>
     * <b>Example:</b> If the given unit is <var>kilometre</var>, then this method returns 1000
     * since a measurement in kilometres must be multiplied by 1000 in order to give the equivalent
     * measurement in the "standard" units (here <var>metres</var>).
     *
     * @param  <A>  The quantity measured by the unit.
     * @param  unit The unit for which we want the multiplication factor to standard unit.
     * @return The factor by which to multiply a measurement in the given unit in order to
     *         get an equivalent measurement in the standard unit.
     *
     * @since 3.07
     */
    public static <A extends Quantity> double toStandardUnit(final Unit<A> unit) {
        return derivative(unit.getConverterTo(unit.toSI()), 0);
    }

    /**
     * Returns an estimation of the derivative of the given converter at the given value.
     * This method is a workaround for a method which existed in previous JSR-275 API but
     * have been removed in more recent releases. This method will be deprecated in the
     * removed API is reinserted in future JSR-275 release.
     * <p>
     * Current implementation computes the derivative as below:
     *
     * {@preformat java
     *     return converter.convert(value + 1) - converter.convert(value);
     * }
     *
     * The above is exact for {@linkplain javax.measure.converter.LinearConverter linear converters},
     * which is the case of the vast majority of unit converters in use. It may not be exact for a
     * few unusual converter like the one from {@link #SEXAGESIMAL_DMS} to decimal degrees for
     * example.
     *
     * @param  converter The converter for which we want the derivative at a given point.
     * @param  value The point at which to compute the derivative.
     * @return The derivative at the given point.
     *
     * @since 3.07
     */
    public static double derivative(final UnitConverter converter, final double value) {
        return converter.convert(value + 1) - converter.convert(value);
    }

    /**
     * Parses the given symbol. This method is similar to {@link Unit#valueOf(String)}, but hands
     * especially a few symbols found in WKT parsing or in XML files. The list of symbols handled
     * especially is implementation-dependant and may change in future Geotk versions.
     *
     * @param  uom The symbol to parse, or {@code null}.
     * @return The parsed symbol, or {@code null} if {@code uom} was null.
     * @throws IllegalArgumentException if the given symbol can not be parsed.
     *
     * @since 3.07
     */
    public static Unit<?> valueOf(String uom) throws IllegalArgumentException {
        if (uom == null) {
            return null;
        }
        uom = uom.trim();
        if (equalsIgnorePlural(uom, "pixel")) {
            return NonSI.PIXEL;
        } else if (uom.equalsIgnoreCase("deg") || equalsIgnorePlural(uom, "degree") || uom.equals("°")) {
            return NonSI.DEGREE_ANGLE;
        } else if (uom.equalsIgnoreCase("rad") || equalsIgnorePlural(uom, "radian")) {
            return SI.RADIAN;
        } else if (equalsIgnorePlural(uom, "kilometer") || equalsIgnorePlural(uom, "kilometre")) {
            return SI.KILOMETRE;
        } else if (equalsIgnorePlural(uom, "meter") || equalsIgnorePlural(uom, "metre")) {
            return SI.METRE;
        } else if (equalsIgnorePlural(uom, "day")) {
            return NonSI.DAY;
        } else if (equalsIgnorePlural(uom, "second")) {
            return SI.SECOND;
        } else if (uom.equalsIgnoreCase("psu")) { // Pratical Salinity Scale
            return Unit.ONE;
        } else if (uom.equalsIgnoreCase("level")) { // Sigma level
            return Unit.ONE;
        } else {
            return canonicalize(Unit.valueOf(uom));
        }
    }

    /**
     * Returns {@code true} if the given {@code uom} is equals to the given expected string,
     * ignoring trailing {@code 's'} character (if any).
     */
    private static boolean equalsIgnorePlural(final String uom, final String expected) {
        if (uom.equalsIgnoreCase(expected)) {
            return true;
        }
        final int length = expected.length();
        return uom.length() == length+1 && Character.toLowerCase(uom.charAt(length)) == 's' &&
                uom.regionMatches(true, 0, expected, 0, length);
    }
}
