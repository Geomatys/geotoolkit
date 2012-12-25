/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.geotoolkit.internal.InternalUtilities;


/**
 * Parses and formats angles according a specified pattern. The pattern is a string
 * containing any characters, with a special meaning for the following characters:
 * <p>
 * <blockquote><table cellpadding="3">
 *     <tr><td>{@code D}</td><td>&nbsp;&nbsp;The integer part of degrees</td></tr>
 *     <tr><td>{@code d}</td><td>&nbsp;&nbsp;The fractional part of degrees</td></tr>
 *     <tr><td>{@code M}</td><td>&nbsp;&nbsp;The integer part of minutes</td></tr>
 *     <tr><td>{@code m}</td><td>&nbsp;&nbsp;The fractional part of minutes</td></tr>
 *     <tr><td>{@code S}</td><td>&nbsp;&nbsp;The integer part of seconds</td></tr>
 *     <tr><td>{@code s}</td><td>&nbsp;&nbsp;The fractional part of seconds</td></tr>
 *     <tr><td>{@code .}</td><td>&nbsp;&nbsp;The decimal separator</td></tr>
 * </table></blockquote>
 * <p>
 * Upper-case letters {@code D}, {@code M} and {@code S} are for the integer parts of degrees,
 * minutes and seconds respectively. They must appear in this order. For example {@code M'D} is
 * illegal because "M" and "S" are inverted; {@code D°S} is illegal too because there is no "M"
 * between "D" and "S".
 * <p>
 * Lower-case letters {@code d}, {@code m} and {@code s} are for fractional parts of degrees,
 * minutes and seconds respectively. Only one of those may appears in a pattern, and it must
 * be the last special symbol. For example {@code D.dd°MM'} is illegal because "d" is followed
 * by "M"; {@code D.mm} is illegal because "m" is not the fractional part of "D".
 * <p>
 * The number of occurrence of {@code D}, {@code M}, {@code S} and their lower-case counterpart
 * is the number of digits to format. For example, {@code DD.ddd} will format angle with two
 * digits for the integer part and three digits for the fractional part (e.g. 4.4578 will be
 * formatted as "04.458").
 * <p>
 * Separator characters like {@code °}, {@code ′} and {@code ″} are inserted "as-is" in the
 * formatted string, except the decimal separator dot ("{@code .}") which is replaced by the
 * local-dependent decimal separator. Separator characters may be completely omitted;
 * {@code AngleFormat} will still differentiate degrees, minutes and seconds fields according
 * the pattern. For example, "{@code 0480439}" with the pattern {@code DDDMMmm} will be parsed
 * as 48°04.39'.
 * <p>
 * The following table gives some examples of legal patterns.
 *
 * <blockquote><table cellpadding="3">
 * <tr><th>Pattern           </th>  <th>Example   </th></tr>
 * <tr><td>{@code DD°MM′SS″ }</td>  <td>48°30′00″ </td></tr>
 * <tr><td>{@code DD°MM′    }</td>  <td>48°30′    </td></tr>
 * <tr><td>{@code DD.ddd    }</td>  <td>48.500    </td></tr>
 * <tr><td>{@code DDMM      }</td>  <td>4830      </td></tr>
 * <tr><td>{@code DDMMSS    }</td>  <td>483000    </td></tr>
 * </table></blockquote>
 *
 * {@section Synchronization}
 * Angle formats are generally not synchronized. It is recommended to create separate format
 * instances for each thread. If multiple threads access a format concurrently, it must be
 * synchronized externally.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @see Angle
 * @see Latitude
 * @see Longitude
 *
 * @since 1.0
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.measure.AngleFormat}.
 */
@Deprecated
public class AngleFormat extends org.apache.sis.measure.AngleFormat {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4320403817210439764L;

    /**
     * Constructs a new {@code AngleFormat} for the default locale.
     *
     * @return An angle format in the default locale.
     *
     * @since 3.09
     */
    public static AngleFormat getInstance() {
        return new AngleFormat();
    }

    /**
     * Constructs a new {@code AngleFormat} for the specified locale.
     *
     * @param locale The locale.
     * @return An angle format in the given locale.
     */
    public static AngleFormat getInstance(final Locale locale) {
        return new AngleFormat("D°MM.m′", locale);
    }

    /**
     * Constructs a new {@code AngleFormat} using
     * the current default locale and a default pattern.
     */
    public AngleFormat() {
        super("D°MM.m′");
    }

    /**
     * Constructs a new {@code AngleFormat} using the
     * current default locale and the specified pattern.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     *         See class description for an explanation of how this pattern work.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public AngleFormat(final String pattern) throws IllegalArgumentException {
        super(pattern);
    }

    /**
     * Constructs a new {@code AngleFormat} using the specified pattern and locale.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     *         See class description for an explanation of how this pattern work.
     * @param  locale Locale to use.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public AngleFormat(final String pattern, final Locale locale) throws IllegalArgumentException {
        super(pattern, locale);
    }

    /**
     * Formats an angle, a latitude or a longitude and appends the resulting text to a given
     * string buffer. The string will be formatted according the pattern set in the last call
     * to {@link #applyPattern}. The argument {@code obj} shall be an {@link Angle} object or
     * one of its derived class ({@link Latitude}, {@link Longitude}). If {@code obj} is a
     * {@link Latitude} object, then a symbol "N" or "S" will be appended to the end of the
     * string (the symbol will be chosen according the angle sign). Otherwise, if {@code obj}
     * is a {@link Longitude} object, then a symbol "E" or "W" will be appended to the end of the
     * string. Otherwise, no hemisphere symbol will be appended.
     * <p>
     * <del>
     * Strictly speaking, formatting ordinary numbers is not the {@code AngleFormat}'s job.
     * Nevertheless, this method accept {@link Number} objects. This capability is provided
     * as a convenient way to format altitude numbers together with longitude and latitude angles.
     * </del>
     *
     * @param obj
     *          {@link Angle} or {@link Number} object to format.
     * @param toAppendTo
     *          Where the text is to be appended.
     * @param pos
     *          An optional {@link FieldPosition} identifying a field in the formatted text,
     *          or {@code null} if this information is not wanted. This field position shall
     *          be constructed with one of the following constants: {@link #DEGREES_FIELD},
     *          {@link #MINUTES_FIELD}, {@link #SECONDS_FIELD} or {@link #HEMISPHERE_FIELD}.
     *
     * @return The string buffer passed in as {@code toAppendTo}, with formatted text appended.
     * @throws IllegalArgumentException if {@code obj} if not an object of class {@link Angle}
     *         or {@link Number}.
     */
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, final FieldPosition pos)
            throws IllegalArgumentException
    {
        if (obj instanceof Latitude) {
            obj = new org.apache.sis.measure.Latitude(((Latitude) obj).degrees());
        } else if (obj instanceof Longitude) {
            obj = new org.apache.sis.measure.Longitude(((Longitude) obj).degrees());
        } else if (obj instanceof Number) {
            // Deprecated feature, keeped here for compatibility but removed on Apache SIS.
            final NumberFormat numberFormat = NumberFormat.getInstance(getLocale());
            numberFormat.setMinimumIntegerDigits(1);
            InternalUtilities.configure(numberFormat, ((Number) obj).doubleValue(), 6);
            return numberFormat.format(obj, toAppendTo, (pos!=null) ? pos : new FieldPosition(0));
        }
        return super.format(obj, toAppendTo, pos);
    }

    private static Angle cast(final org.apache.sis.measure.Angle angle) {
        if (angle == null || angle instanceof Angle) {
            return (Angle) angle;
        }
        if (angle instanceof org.apache.sis.measure.Latitude) {
            return new Latitude(angle.degrees());
        }
        if (angle instanceof org.apache.sis.measure.Longitude) {
            return new Longitude(angle.degrees());
        }
        return new Angle(angle.degrees());
    }

    /**
     * Parses a string as an angle. This method can parse an angle even if it doesn't comply
     * exactly to the expected pattern. For example, this method will parse correctly string
     * "{@code 48°12.34'}" even if the expected pattern was "{@code DDMM.mm}" (i.e. the string
     * should have been "{@code 4812.34}").
     * <p>
     * Spaces between degrees, minutes and seconds are ignored. If the string ends with an
     * hemisphere symbol "N" or "S", then this method returns an object of class {@link Latitude}.
     * Otherwise, if the string ends with an hemisphere symbol "E" or "W", then this method returns
     * an object of class {@link Longitude}. Otherwise, this method returns an object of class
     * {@link Angle}.
     *
     * @param  source A String whose beginning should be parsed.
     * @param  pos    Position where to start parsing.
     * @return The parsed string as an {@link Angle}, {@link Latitude} or {@link Longitude} object.
     */
    @Override
    public Angle parse(final String source, final ParsePosition pos) {
        return cast(super.parse(source, pos));
    }

    /**
     * Parses a string as an angle.
     *
     * @param  source The string to parse.
     * @return The parsed string as an {@link Angle}, {@link Latitude}
     *         or {@link Longitude} object.
     * @throws ParseException if the string has not been fully parsed.
     */
    @Override
    public Angle parse(final String source) throws ParseException {
        return cast(super.parse(source));
    }

    /**
     * Parses a substring as an angle. Default implementation invokes
     * {@link #parse(String, ParsePosition)}.
     *
     * @param  source A String whose beginning should be parsed.
     * @param  pos    Position where to start parsing.
     * @return The parsed string as an {@link Angle}, {@link Latitude} or {@link Longitude} object.
     */
    @Override
    public Angle parseObject(final String source, final ParsePosition pos) {
        return cast((org.apache.sis.measure.Angle) super.parseObject(source, pos));
    }

    /**
     * Parses a string as an object. Default implementation invokes {@link #parse(String)}.
     *
     * @param  source The string to parse.
     * @return The parsed string as an {@link Angle}, {@link Latitude} or {@link Longitude} object.
     * @throws ParseException if the string has not been fully parsed.
     */
    @Override
    public Angle parseObject(final String source) throws ParseException {
        return cast((org.apache.sis.measure.Angle) super.parseObject(source));
    }
}
