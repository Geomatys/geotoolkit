/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.io.wkt;

import java.util.Locale;
import java.util.Collection;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.lang.reflect.Array;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.util.CodeList;

import org.geotoolkit.io.X364;
import org.geotoolkit.lang.Visitor;
import org.geotoolkit.measure.Units;
import org.geotoolkit.util.Strings;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.internal.InternalUtilities;
import org.apache.sis.internal.util.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.resources.Errors;


/**
 * Formats {@linkplain FormattableObject formattable objects} as <cite>Well Known Text</cite>
 * (WKT). A formatter is constructed with a specified set of {@linkplain Symbols symbols}.
 * The {@linkplain Locale locale} associated with the symbols is used for querying
 * {@linkplain Citation#getTitle() authority titles}.
 * <p>
 * For example in order to format an object with {@linkplain Symbols#CURLY_BRACKETS curly brackets}
 * instead of square ones and the whole text on the same line (no indentation), use the following:
 *
 * {@preformat java
 *     Formatter formatter = new Formatter(Symbols.CURLY_BRACKETS, null, WKTFormat.SINGLE_LINE);
 *     formatter.append(theObject);
 *     String wkt = formatter.toString();
 *
 *     // Following is needed only if you want to reuse
 *     // the formatter again for other objects.
 *     formatter.clear();
 * }
 *
 * Formatters are not synchronized. It is recommended to create separate formatter instances for
 * each thread. If multiple threads access a formatter concurrently, it must be synchronized
 * externally.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @level advanced
 * @module
 */
@Visitor(Formattable.class)
public class Formatter {
    /**
     * Do not format an {@code "AUTHORITY"} element for instances of those classes.
     *
     * @see #isAuthorityAllowed
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static final Class<? extends IdentifiedObject>[] AUTHORITY_EXCLUDES = new Class[] {
        CoordinateSystemAxis.class
    };

    /**
     * The symbols to use for this formatter.
     *
     * @see WKTFormat#getSymbols
     * @see WKTFormat#setSymbols
     */
    private final Symbols symbols;

    /**
     * The colors to use for this formatter, or {@code null} for no syntax coloring.
     * If non-null, the terminal must be ANSI X3.64 compatible. The default value is
     * {@code null}.
     *
     * @see WKTFormat#getColors
     * @see WKTFormat#setColors
     */
    Colors colors;

    /**
     * The preferred convention for objects or parameter names.
     * This field should never be {@code null}.
     *
     * @see WKTFormat#getConvention()
     * @see WKTFormat#setConvention(Convention)
     */
    private Convention convention = Convention.OGC;

    /**
     * The preferred authority for objects or parameter names.
     *
     * @see WKTFormat#getAuthority
     * @see WKTFormat#setAuthority
     */
    private Citation authority;

    /**
     * The unit for formatting measures, or {@code null} for the "natural" unit of each WKT
     * element.
     */
    private Unit<Length> linearUnit;

    /**
     * The unit for formatting measures, or {@code null} for the "natural" unit of each WKT
     * element. This value is set for example by {@code "GEOGCS"}, which force its enclosing
     * {@code "PRIMEM"} to take the same units than itself.
     */
    private Unit<Angle> angularUnit;

    /**
     * The object to use for formatting numbers.
     */
    private final NumberFormat numberFormat;

    /**
     * The object to use for formatting units.
     */
    private final UnitFormat unitFormat;

    /**
     * Dummy field position.
     */
    private final FieldPosition dummy = new FieldPosition(0);

    /**
     * The buffer in which to format. Consider this field as private and final. The only method to
     * change the value of this field is {@link WKTFormat#format(Object, StringBuffer, FieldPosition)}.
     */
    StringBuffer buffer;

    /**
     * The starting point in the buffer. Always 0, except when used by
     * {@link WKTFormat#format(Object, StringBuffer, FieldPosition)}.
     */
    int bufferBase;

    /**
     * The amount of spaces to use in indentation, or
     * {@value org.geotoolkit.io.wkt.WKTFormat#SINGLE_LINE} if indentation is disabled.
     */
    int indentation;

    /**
     * The amount of space to write on the left side of each line. This amount is increased
     * by {@code indentation} every time a {@link FormattableObject} is appended in a new
     * indentation level.
     */
    private int margin;

    /**
     * The line separator to use.
     */
    private final String lineSeparator = System.lineSeparator();

    /**
     * {@code true} if a new line were requested during the execution of
     * {@link #append(Formattable)}. This is used to determine if {@code UNIT}
     * and {@code AUTHORITY} elements should appears on a new line too.
     */
    private boolean lineChanged;

    /**
     * {@code true} if the WKT is invalid. Similar to {@link #unformattable}, except that
     * this field is reset to {@code false} after the invalid part has been processed by
     * {@link #append(Formattable)}. This field is for internal use only.
     */
    private boolean invalidWKT;

    /**
     * Non-null if the WKT is invalid. If non-null, then this field contains the interface class
     * of the problematic part (e.g. {@link org.opengis.referencing.crs.EngineeringCRS}).
     */
    private Class<?> unformattable;

    /**
     * Warning that may be produced during WKT formatting, or {@code null} if none.
     */
    String warning;

    /**
     * Creates a new instance of the formatter with the default symbols, no syntax coloring
     * and the {@linkplain WKTFormat#getDefaultIndentation() default indentation}.
     */
    public Formatter() {
        this(FormattableObject.defaultIndentation);
    }

    /**
     * Creates a new instance of the formatter with the default symbols, no syntax coloring
     * and the given indentation.
     *
     * @param  indentation The amount of spaces to use in indentation for WKT formatting,
     *         or {@value org.geotoolkit.io.wkt.WKTFormat#SINGLE_LINE} for formatting the
     *         whole WKT on a single line.
     *
     * @since 3.20
     */
    public Formatter(final int indentation) {
        this(Symbols.DEFAULT, null, indentation);
    }

    /**
     * Creates a new instance of the formatter with the specified indentation.
     * The WKT can be formatted on many lines, and the indentation will have
     * the value specified to this constructor. If the specified indentation
     * is {@value org.geotoolkit.io.wkt.WKTFormat#SINGLE_LINE}, then the
     * whole WKT will be formatted on a single line.
     *
     * @param symbols The symbols.
     * @param colors  The syntax coloring, or {@code null} if none.
     * @param  indentation The amount of spaces to use in indentation for WKT formatting,
     *         or {@value org.geotoolkit.io.wkt.WKTFormat#SINGLE_LINE} for formatting the
     *         whole WKT on a single line.
     *
     * @since 3.00
     */
    public Formatter(final Symbols symbols, final Colors colors, final int indentation) {
        ArgumentChecks.ensureNonNull("symbols", symbols);
        if (indentation < WKTFormat.SINGLE_LINE) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "indentation", indentation));
        }
        this.symbols      = symbols;
        this.colors       = colors;
        this.indentation  = indentation;
        this.numberFormat = (NumberFormat) symbols.numberFormat.clone();
        this.unitFormat   = UnitFormat.getInstance(symbols.locale);
        this.buffer       = new StringBuffer();
    }

    /**
     * Constructor for private use by {@link WKTFormat#format} only.
     * This constructor help to share some objects with {@link Parser}.
     */
    Formatter(final Symbols symbols, final NumberFormat numberFormat) {
        this.symbols      = symbols;
        this.indentation  = FormattableObject.defaultIndentation;
        this.numberFormat = numberFormat; // No clone needed.
        this.unitFormat   = UnitFormat.getInstance(symbols.locale);
        // Do not set the buffer. It will be set by WKTFormat.format(...).
    }

    /**
     * Sets the convention and the authority to use for formatting WKT elements.
     *
     * @param convention The convention, or {@code null} for the default value.
     * @param authority  The authority, or {@code null} for inferring it from the convention.
     */
    final void setConvention(Convention convention, final Citation authority) {
        if (convention == null) {
            convention = Convention.forCitation(authority, Convention.OGC);
        }
        this.convention = convention;
        this.authority  = authority; // Will be inferred when first needed.
    }

    /**
     * Sets the color in the {@linkplain #buffer} using the specified ANSI escape.
     * The color is ignored unless syntax coloring has been explicitly enabled.
     */
    private void setColor(final Colors.Element color) {
        if (colors != null) {
            final X364 c = colors.get(color);
            if (c != null) {
                buffer.append(c.sequence());
            }
        }
    }

    /**
     * Resets the color in the {@linkplain #buffer} to the default. This method
     * does nothing unless syntax coloring has been explicitly enabled.
     */
    private void resetColor() {
        if (colors != null) {
            buffer.append(X364.FOREGROUND_DEFAULT.sequence());
        }
    }

    /**
     * Returns the color to use for the name of the specified object.
     */
    private static Colors.Element getNameColor(final IdentifiedObject object) {
        if (object instanceof Datum) {
            return Colors.Element.DATUM;
        }
        if (object instanceof OperationMethod) {
            return Colors.Element.METHOD;
        }
        if (object instanceof CoordinateSystemAxis) {
            return Colors.Element.AXIS;
        }
        // Note: we can't test for MathTransform here, since it is not an IdentifiedObject.
        //       If we want to provide a color for the MathTransform name, we would need to
        //       do that in 'append(String)' method, but the later is for general string...
        return null;
    }

    /**
     * Appends a separator to the buffer, if needed.
     *
     * @param newLine If {@code true}, add a line separator too.
     */
    private void appendSeparator(final boolean newLine) {
        final Symbols symbols = this.symbols;
        final StringBuffer buffer = this.buffer;
        int length = buffer.length();
        char c;
        do {
            if (length == bufferBase) {
                return;
            }
            c = buffer.charAt(--length);
            if (c == symbols.open || c == symbols.openArray) {
                return;
            }
        } while (Character.isWhitespace(c) || c == symbols.space);
        buffer.append(symbols.separator).append(symbols.space);
        if (newLine && indentation > WKTFormat.SINGLE_LINE) {
            buffer.append(lineSeparator).append(Strings.spaces(margin));
            lineChanged = true;
        }
    }

    /**
     * Appends the given {@code Formattable} object. This method will automatically append
     * the keyword (e.g. {@code "GEOCS"}), the name and the authority code, and will invokes
     * <code>formattable.{@linkplain FormattableObject#formatWKT(Formatter) formatWKT}(this)</code>
     * for completing the inner part of the WKT.
     *
     * @param formattable The formattable object to append to the WKT.
     */
    public void append(final Formattable formattable) {
        final Symbols symbols = this.symbols;
        final StringBuffer buffer = this.buffer;
        /*
         * Formats the opening bracket and the object name (e.g. "NAD27").
         * The WKT entity name (e.g. "PROJCS") will be formatted later.
         * The result of this code portion looks like the following:
         *
         *         <previous text>,
         *           ["NAD27 / Idaho Central"
         */
        appendSeparator(true);
        int base = buffer.length();
        buffer.append(symbols.open);
        final IdentifiedObject info = (formattable instanceof IdentifiedObject) ?
                (IdentifiedObject) formattable : null;
        if (info != null) {
            final Colors.Element c = getNameColor(info);
            if (c != null) {
                setColor(c);
            }
            buffer.append(symbols.quote).append(getName(info)).append(symbols.quote);
            if (c != null) {
                resetColor();
            }
        }
        /*
         * Formats the part after the object name, then insert the WKT element name
         * in front of them. The result of this code portion looks like the following:
         *
         *         <previous text>,
         *           PROJCS["NAD27 / Idaho Central",
         *             GEOGCS[...etc...],
         *             ...etc...
         */
        indent(+1);
        lineChanged = false;
        String keyword = formattable.formatWKT(this);
        if (colors != null && invalidWKT) {
            invalidWKT = false;
            final X364 c = colors.get(Colors.Element.ERROR);
            if (c != null) {
                final String sequence = c.sequence();
                buffer.insert(base, sequence + X364.BACKGROUND_DEFAULT.sequence());
                base += sequence.length();
            }
        }
        buffer.insert(base, keyword);
        /*
         * Formats the AUTHORITY[<name>,<code>] entity, if there is one. The entity
         * will be on the same line than the enclosing one if no line separator were
         * added (e.g. SPHEROID["Clarke 1866", ..., AUTHORITY["EPSG","7008"]]), or on
         * a new line otherwise. After this block, the result looks like the following:
         *
         *         <previous text>,
         *           PROJCS["NAD27 / Idaho Central",
         *             GEOGCS[...etc...],
         *             ...etc...
         *             AUTHORITY["EPSG","26769"]]
         */
        final Identifier identifier = getIdentifier(info);
        if (identifier!=null && isAuthorityAllowed(info)) {
            final Citation authority = identifier.getAuthority();
            if (authority != null) {
                final String title = Citations.getIdentifier(authority);
                if (title != null) {
                    appendSeparator(lineChanged);
                    buffer.append("AUTHORITY")
                          .append(symbols.open)
                          .append(symbols.quote)
                          .append(title)
                          .append(symbols.quote);
                    final String code = identifier.getCode();
                    if (code != null) {
                        buffer.append(symbols.separator)
                              .append(symbols.quote)
                              .append(code)
                              .append(symbols.quote);
                    }
                    buffer.append(symbols.close);
                }
            }
        }
        buffer.append(symbols.close);
        lineChanged = true;
        indent(-1);
    }

    /**
     * Appends the specified OpenGIS's {@code IdentifiedObject} object.
     *
     * @param info The info object to append to the WKT.
     */
    public void append(final IdentifiedObject info) {
        if (info instanceof Formattable) {
            append((Formattable) info);
        } else {
            throw unsupported(info);
        }
    }

    /**
     * Appends the specified math transform.
     *
     * @param transform The transform object to append to the WKT.
     */
    public void append(final MathTransform transform) {
        if (transform instanceof Formattable) {
            append((Formattable) transform);
        } else {
            throw unsupported(transform);
        }
    }

    /**
     * Invoked when an object is not a supported implementation.
     *
     * @param object The object of unknown type.
     * @return The exception to be thrown.
     */
    private static UnformattableObjectException unsupported(final Object object) {
        final Class<?> type = object.getClass();
        return new UnformattableObjectException(Errors.format(
                Errors.Keys.ILLEGAL_CLASS_$2, type, Formattable.class), type);
    }

    /**
     * Appends a code list.
     *
     * @param code The code list to append to the WKT.
     */
    public void append(final CodeList<?> code) {
        if (code != null) {
            appendSeparator(false);
            setColor(Colors.Element.CODE_LIST);
            final String name = code.name();
            final boolean needQuotes = (name.indexOf(' ') >= 0);
            final Symbols symbols = this.symbols;
            final StringBuffer buffer = this.buffer;
            if (needQuotes) {
                buffer.append(symbols.quote);
            }
            buffer.append(name);
            if (needQuotes) {
                buffer.append(symbols.quote);
                setInvalidWKT(code.getClass());
            }
            resetColor();
        }
    }

    /**
     * Appends a {@linkplain ParameterValue parameter} in WKT form. If the supplied parameter
     * is actually a {@linkplain ParameterValueGroup parameter group}, all contained parameters
     * will flattened in a single list.
     *
     * @param parameter The parameter to append to the WKT.
     */
    public void append(final GeneralParameterValue parameter) {
        if (parameter instanceof ParameterValueGroup) {
            for (final GeneralParameterValue param : ((ParameterValueGroup)parameter).values()) {
                append(param);
            }
        }
        if (parameter instanceof ParameterValue<?>) {
            final ParameterValue<?> param = (ParameterValue<?>) parameter;
            final ParameterDescriptor<?> descriptor = param.getDescriptor();
            final Unit<?> valueUnit = descriptor.getUnit();
            Unit<?> unit = valueUnit;
            if (unit!=null && !Unit.ONE.equals(unit)) {
                Unit<?> contextUnit = linearUnit;
                if (contextUnit!=null && unit.isCompatible(contextUnit)) {
                    unit = contextUnit;
                } else {
                    contextUnit = convention.forcedAngularUnit;
                    if (contextUnit == null) {
                        contextUnit = angularUnit;
                    }
                    if (contextUnit!=null && unit.isCompatible(contextUnit)) {
                        unit = contextUnit;
                    }
                }
            }
            appendSeparator(true);
            final Symbols symbols = this.symbols;
            final StringBuffer buffer = this.buffer;
            final int start = buffer.length();
            buffer.append("PARAMETER");
            final int stop = buffer.length();
            buffer.append(symbols.open);
            setColor(Colors.Element.PARAMETER);
            buffer.append(symbols.quote).append(getName(descriptor)).append(symbols.quote);
            resetColor();
            buffer.append(symbols.separator).append(symbols.space);
            if (unit != null) {
                double value;
                try {
                    value = param.doubleValue(unit);
                } catch (IllegalStateException exception) {
                    // May happen if a parameter is mandatory (e.g. "semi-major")
                    // but no value has been set for this parameter.
                    if (colors != null) {
                        final X364 c = colors.get(Colors.Element.ERROR);
                        if (c != null) {
                            buffer.insert(stop, X364.BACKGROUND_DEFAULT.sequence())
                                  .insert(start, c.sequence());
                        }
                    }
                    warning = exception.getLocalizedMessage();
                    value = Double.NaN;
                }
                if (!unit.equals(valueUnit)) {
                    value = InternalUtilities.adjustForRoundingError(value, 360000, 9);
                }
                format(value);
            } else {
                appendObject(param.getValue());
            }
            buffer.append(symbols.close);
        }
    }

    /**
     * Appends the specified value to a string buffer. If the value is an array, then the
     * array elements are appended recursively (i.e. the array may contains sub-array).
     */
    private void appendObject(final Object value) {
        final StringBuffer buffer = this.buffer;
        if (value == null) {
            buffer.append("null");
            return;
        }
        final Symbols symbols = this.symbols;
        if (value.getClass().isArray()) {
            buffer.append(symbols.openArray);
            final int length = Array.getLength(value);
            for (int i=0; i<length; i++) {
                if (i != 0) {
                    buffer.append(symbols.separator).append(symbols.space);
                }
                appendObject(Array.get(value, i));
            }
            buffer.append(symbols.closeArray);
            return;
        }
        if (value instanceof Number) {
            format((Number) value);
        } else if (value instanceof Boolean) {
            buffer.append(((Boolean) value).booleanValue() ? "TRUE" : "FALSE");
        } else {
            buffer.append(symbols.quote).append(value).append(symbols.quote);
        }
    }

    /**
     * Appends an integer number. A comma (or any other element
     * separator) will be written before the number if needed.
     *
     * @param number The integer to append to the WKT.
     */
    public void append(final long number) {
        appendSeparator(false);
        format(number);
    }

    /**
     * Appends a floating point number. A comma (or any other element
     * separator) will be written before the number if needed.
     *
     * @param number The floating point value to append to the WKT.
     */
    public void append(final double number) {
        appendSeparator(false);
        format(number);
    }

    /**
     * Appends a unit in WKT form. For example {@code append(SI.KILO(SI.METRE))}
     * will append "{@code UNIT["km", 1000]}" to the WKT.
     *
     * @param unit The unit to append to the WKT.
     */
    public void append(final Unit<?> unit) {
        if (unit != null) {
            final StringBuffer buffer = this.buffer;
            final Symbols symbols = this.symbols;
            appendSeparator(lineChanged);
            buffer.append("UNIT").append(symbols.open);
            setColor(Colors.Element.UNIT);
            buffer.append(symbols.quote);
            if (NonSI.DEGREE_ANGLE.equals(unit)) {
                buffer.append("degree");
            } else if (SI.METRE.equals(unit)) {
                buffer.append(convention.unitUS ? "meter" : "metre");
            } else {
                unitFormat.format(unit, buffer, dummy);
            }
            buffer.append(symbols.quote);
            resetColor();
            append(Units.toStandardUnit(unit));
            buffer.append(symbols.close);
        }
    }

    /**
     * Appends a character string. The string will be written between quotes.
     * A comma (or any other element separator) will be written before the string if needed.
     *
     * @param text The string to format to the WKT.
     */
    public void append(final String text) {
        appendSeparator(false);
        buffer.append(symbols.quote).append(text).append(symbols.quote);
    }

    /**
     * Formats an arbitrary number.
     */
    private void format(final Number number) {
        if (Numbers.isInteger(number.getClass())) {
            format(number.longValue());
        } else {
            format(number.doubleValue());
        }
    }

    /**
     * Formats an integer number.
     */
    private void format(final long number) {
        setColor(Colors.Element.INTEGER);
        final NumberFormat numberFormat = this.numberFormat;
        final int fraction = numberFormat.getMinimumFractionDigits();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.format(number, buffer, dummy);
        numberFormat.setMinimumFractionDigits(fraction);
        resetColor();
    }

    /**
     * Formats a floating point number.
     */
    private void format(double number) {
        number = InternalUtilities.adjustForRoundingError(number);
        setColor(Colors.Element.NUMBER);
        numberFormat.format(number, buffer, dummy);
        resetColor();
    }

    /**
     * Increase or reduce the indentation. A value of {@code +1} increase
     * the indentation by the amount of spaces specified at construction time,
     * and a value of {@code +1} reduce it.
     */
    private void indent(final int amount) {
        margin = Math.max(0, margin + indentation*amount);
    }

    /**
     * Tells if an {@code "AUTHORITY"} element is allowed for the specified object.
     */
    private static boolean isAuthorityAllowed(final IdentifiedObject info) {
        for (int i=0; i<AUTHORITY_EXCLUDES.length; i++) {
            if (AUTHORITY_EXCLUDES[i].isInstance(info)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the preferred identifier for the specified object.
     * If the specified object contains an identifier from the preferred authority
     * (usually {@linkplain org.geotoolkit.metadata.iso.citation.Citations#OGC Open Geospatial}),
     * then this identifier is returned. Otherwise, the first identifier is returned. If the specified
     * object contains no identifier, then this method returns {@code null}.
     *
     * @param  info The object to looks for a preferred identifier.
     * @return The preferred identifier, or {@code null} if none.
     *
     * @since 2.3
     */
    public Identifier getIdentifier(final IdentifiedObject info) {
        Identifier first = null;
        if (info != null) {
            final Collection<? extends Identifier> identifiers = info.getIdentifiers();
            if (identifiers != null) {
                for (final Identifier id : identifiers) {
                    if (Citations.identifierMatches(getAuthority(), id.getAuthority())) {
                        return id;
                    }
                    if (first == null) {
                        first = id;
                    }
                }
            }
        }
        return first;
    }

    /**
     * Returns the preferred name for the specified object.
     * If the specified object contains a name from the preferred authority
     * (usually {@linkplain org.geotoolkit.metadata.iso.citation.Citations#OGC Open Geospatial}),
     * then this name is returned. Otherwise, the first name found is returned.
     * <p>
     * The preferred authority can be set by the {@link WKTFormat#setAuthority(Citation)} method.
     * This is not necessarily the authority of the given {@linkplain IdentifiedObject#getName()
     * object name}.
     * <p>
     * <b>Example:</b> The EPSG name of the {@code EPSG:6326} datum is "<cite>World Geodetic System
     * 1984</cite>". However if the preferred authority is OGC (which is the case by default), then
     * this method usually returns "<cite>WGS84</cite>" (the exact string to be returned depends on
     * the list of declared {@linkplain IdentifiedObject#getAlias() aliases}).
     *
     * @param  info The object to looks for a preferred name.
     * @return The preferred name, or {@code null} if the given object has no name.
     *
     * @see WKTFormat#getAuthority()
     * @see IdentifiedObjects#getName(IdentifiedObject, Citation)
     */
    public String getName(final IdentifiedObject info) {
        String name = IdentifiedObjects.getName(info, getAuthority());
        if (name == null) {
            name = IdentifiedObjects.getName(info, null);
        }
        return name;
    }

    /**
     * The linear unit for formatting measures, or {@code null} for the "natural" unit of each
     * WKT element.
     *
     * @return The unit for linear measurement. Default value is {@code null}.
     */
    public Unit<Length> getLinearUnit() {
        return linearUnit;
    }

    /**
     * Sets the unit for formatting linear measures.
     *
     * @param unit The new unit, or {@code null}.
     * @throws IllegalArgumentException If the given unit is not null and not a
     *         {@linkplain Units#isLinear(Unit) linear unit}.
     */
    public void setLinearUnit(final Unit<Length> unit) throws IllegalArgumentException {
        if (unit != null && !Units.isLinear(unit)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NON_LINEAR_UNIT_$1, unit));
        }
        linearUnit = unit;
    }

    /**
     * The angular unit for formatting measures, or {@code null} for the "natural" unit of each WKT
     * element. This value is set for example when parsing the "{@code GEOGCS}" element, in which
     * case the enclosed "{@code PRIMEM}" element shall use the unit of the enclosing "{@code GEOGCS}".
     *
     * @return The unit for angle measurement. Default value is {@code null}.
     */
    public Unit<Angle> getAngularUnit() {
        return angularUnit;
    }

    /**
     * Sets the angular unit for formatting measures.
     *
     * @param unit The new unit, or {@code null}.
     * @throws IllegalArgumentException If the given unit is not null and not an
     *         {@linkplain Units#isAngular(Unit) angular unit}.
     */
    public void setAngularUnit(final Unit<Angle> unit) throws IllegalArgumentException {
        if (unit != null && !Units.isAngular(unit)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NON_ANGULAR_UNIT_$1, unit));
        }
        angularUnit = unit;
    }

    /**
     * Returns the authority to use for fetching the name to format.
     * The internal authority is replaced by the OGC one.
     */
    private Citation getAuthority() {
        Citation result = authority;
        if (result == null) {
            Convention c = convention;
            if (c == Convention.INTERNAL) {
                c = Convention.OGC;
            }
            authority = result = c.getCitation();
        }
        return result;
    }

    /**
     * Returns the convention to use for formatting the WKT. The default convention is
     * {@link Convention#OGC OGC}. A different convention will usually result in different
     * parameter names, but may also change the WKT syntax.
     *
     * @return The convention (never {@code null}).
     *
     * @see WKTFormat#setConvention(Convention)
     * @see FormattableObject#toWKT(Convention, int)
     *
     * @since 3.20
     */
    public Convention getConvention() {
        return convention;
    }

    /**
     * Returns {@code true} if the WKT in this formatter is not strictly compliant to the
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">WKT
     * specification</A>. This method returns {@code true} if {@link #setInvalidWKT} has
     * been invoked at least once. The action to take regarding invalid WKT is caller-dependent.
     * For example {@link FormattableObject#toString()} will accepts loose WKT formatting and ignore
     * this flag, while {@link FormattableObject#toWKT()} requires strict WKT formatting and will
     * thrown an exception if this flag is set.
     *
     * @return {@code true} if the WKT is invalid.
     */
    public boolean isInvalidWKT() {
        return unformattable != null || (buffer!=null && buffer.length() == 0);
        /*
         * Note: we really use a "and" condition (not an other "or") for the buffer test because
         *       the buffer is reset to 'null' by WKTFormat after a successfull formatting.
         */
    }

    /**
     * Returns the class declared by the last call to {@link #setInvalidWKT}.
     */
    final Class<?> getUnformattableClass() {
        return unformattable;
    }

    /**
     * Set a flag marking the current WKT as not strictly compliant to the
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">WKT
     * specification</A>. This method is invoked by {@link FormattableObject#formatWKT(Formatter)}
     * methods when the object to format is more complex than what the WKT specification allows.
     * For example this method is invoked when an
     * {@linkplain org.geotoolkit.referencing.crs.DefaultEngineeringCRS engineering CRS} uses
     * different unit for each axis, An application can tests {@link #isInvalidWKT} later for
     * checking WKT validity.
     *
     * @param unformattable The type of the component that can't be formatted,
     *        for example {@link org.opengis.referencing.crs.EngineeringCRS}.
     *
     * @see UnformattableObjectException#getUnformattableClass
     *
     * @since 2.4
     */
    public void setInvalidWKT(final Class<?> unformattable) {
        this.unformattable = unformattable;
        invalidWKT = true;
    }

    /**
     * Returns the WKT in its current state.
     */
    @Override
    public String toString() {
        return buffer.toString();
    }

    /**
     * Clears this formatter. All properties (including {@linkplain #getLinearUnit unit}
     * and {@linkplain #isInvalidWKT WKT validity flag} are reset to their default value.
     * After this method call, this {@code Formatter} object is ready for formatting
     * a new object.
     */
    public void clear() {
        if (buffer != null) {
            buffer.setLength(0);
        }
        linearUnit    = null;
        angularUnit   = null;
        unformattable = null;
        warning       = null;
        invalidWKT    = false;
        lineChanged   = false;
        margin        = 0;
    }
}
