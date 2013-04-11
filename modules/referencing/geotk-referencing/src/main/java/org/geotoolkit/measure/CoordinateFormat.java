/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.text.Format;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;

import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.measure.Angle;
import org.apache.sis.measure.AngleFormat;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.geometry.TransformedDirectPosition;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Formats a {@linkplain org.geotoolkit.geometry.GeneralDirectPosition direct position}
 * in an arbitrary {@linkplain CoordinateReferenceSystem coordinate reference system}.
 * The format for each ordinate is inferred from the coordinate system units using the
 * following rules:
 * <p>
 * <ul>
 *   <li>Ordinate values in angular units are formated as angles using {@link AngleFormat}.</li>
 *   <li>Ordinate values in temporal units are formated as dates using {@link DateFormat}.</li>
 *   <li>Other values are formatted as numbers using {@link NumberFormat}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 *
 * @todo parsing is not yet implemented in this version.
 */
public class CoordinateFormat extends Format {
    /**
     * Serial number for cross-version compatibility.
     */
    private static final long serialVersionUID = 8324486673169133932L;

    /**
     * The output coordinate reference system. May be {@code null}.
     */
    private CoordinateReferenceSystem crs;

    /**
     * The separator between each coordinate values to be formatted.
     */
    private String separator;

    /**
     * The locale for formatting coordinates and numbers.
     */
    private final Locale locale;

    /**
     * The formats to use for formatting. This array length must be equals
     * to the {@linkplain #getCoordinateReferenceSystem coordinate system}'s
     * dimension. This array is never {@code null}.
     * <p>
     * All elements in this array should be one of {@link #dateFormat},
     * {@link #angleFormat} or {@link #numberFormat}.
     */
    private transient Format[] formats;

    /**
     * Formatter for dates. Will be created only when first needed.
     */
    private DateFormat dateFormat;

    /**
     * Formatter for angles. Will be created only when first needed.
     */
    private AngleFormat angleFormat;

    /**
     * Formatter for numbers. Will be created only when first needed.
     */
    private NumberFormat numberFormat;

    /**
     * Formatter for units. Will be created only when first needed.
     */
    private transient UnitFormat unitFormat;

    /**
     * Units symbols. Used only for ordinate to be formatted as ordinary numbers.
     * Non-null only if at least one ordinate is to be formatted that way.
     */
    private transient String[] unitSymbols;

    /**
     * Conversions from arbitrary units to the unit used by formatter. For example in the
     * case of dates, this is the conversions from temporal axis units to milliseconds.
     */
    private transient UnitConverter[] toFormatUnit;

    /**
     * {@code true} if the sign of the value should be inverted. This is needed for example
     * if the axis is oriented toward past instead than future, or toward west instead than
     * east.
     */
    private transient boolean[] negate;

    /**
     * The time epochs. Non-null only if the at least on ordinate is to be formatted as a date.
     */
    private transient long[] epochs;

    /**
     * The type for each value in the {@code formats} array.
     * Types are: 0=number, 1=longitude, 2=latitude, 3=other angle,
     * 4=date, 5=elapsed time. This array is never {@code null}.
     */
    private transient byte[] types;

    /**
     * Constants for the {@code types} array.
     */
    private static final byte LONGITUDE=1, LATITUDE=2, ANGLE=3, DATE=4, TIME=5;

    /**
     * Dummy field position. Consider this field as final; it is assigned only from
     * constructors and from {@link #readObject}.
     */
    private transient FieldPosition dummy = new FieldPosition(0);

    /**
     * Temporary object to use for transforming direct position from an arbitrary CRS to
     * this format CRS. Will be created only if needed. Note that creating this field
     * implies fetching the CRS factories, which may be a heavy process if they have
     * never been used before in current JVM execution.
     */
    private transient TransformedDirectPosition transform;

    /**
     * Constructs a new coordinate format with default locale and a two-dimensional geographic
     * ({@linkplain DefaultGeographicCRS#WGS84 WGS 1984}) coordinate reference system.
     */
    public CoordinateFormat() {
        this(Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * Constructs a new coordinate format for the specified locale and a two-dimensional geographic
     * ({@linkplain DefaultGeographicCRS#WGS84 WGS 1984}) coordinate reference system.
     *
     * @param locale The locale for formatting coordinates and numbers.
     */
    public CoordinateFormat(final Locale locale) {
        this(locale, DefaultGeographicCRS.WGS84);
    }

    /**
     * Constructs a new coordinate format for the specified locale and coordinate reference system.
     *
     * @param locale The locale for formatting coordinates and numbers.
     * @param crs    The output coordinate reference system, or {@code null} if unknown.
     */
    public CoordinateFormat(final Locale locale, final CoordinateReferenceSystem crs) {
        ensureNonNull("locale", locale);
        this.locale    = locale;
        this.crs       = crs;
        this.separator = " ";
        initialize();
    }

    /**
     * Returns the coordinate reference system for points to be formatted.
     *
     * @return The output coordinate reference system.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Sets the coordinate reference system for points to be formatted. The number
     * of dimensions must matched the dimension of points to be formatted.
     *
     * @param crs The new coordinate reference system, or {@code null} if unknown.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        if (!CRS.equalsIgnoreMetadata(this.crs, (this.crs = crs))) {
            initialize();
        }
    }

    /**
     * Computes the value of transient fields from the current CRS.
     */
    private void initialize() {
        final CoordinateSystem cs;
        final int dimension;
        if (crs != null) {
            cs = crs.getCoordinateSystem();
            dimension = cs.getDimension();
        } else {
            cs = null;
            dimension = 1;
        }
        types        = new byte         [dimension];
        formats      = new Format       [dimension];
        toFormatUnit = new UnitConverter[dimension];
        negate       = new boolean      [dimension];
        epochs       = null;
        unitSymbols  = null;
        /*
         * If no CRS were specified, formats everything as numbers. Working with null CRS is
         * sometime useful because null CRS are allowed in DirectPosition according ISO 19107.
         * Otherwise (if a CRS is given), infers the format subclasses from the axes.
         */
        if (crs == null) {
            formats[0] = getNumberFormat();
            transform = null;
            return;
        }
        if (transform != null) {
            if (transform.getDimension() == dimension) {
                transform.setCoordinateReferenceSystem(crs);
            } else {
                transform = null;
                // Do not instantiate now. We will do that only when first needed,
                // because instantiating this object may be a heavy process if the
                // CRS factories have not yet been fetched in current running JVM.
            }
        }
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final Unit<?> unit = axis.getUnit();
            AxisDirection dir = axis.getDirection();
            final boolean neg = AxisDirections.isOpposite(dir);
            dir = AxisDirections.absolute(dir);
            /*
             * Formatter for angular units. Target unit is DEGREE_ANGLE.
             * Type is LONGITUDE, LATITUDE or ANGLE depending on axis direction.
             */
            if (Units.isAngular(unit)) {
                final byte type;
                if      (AxisDirection.EAST .equals(dir)) type = LONGITUDE;
                else if (AxisDirection.NORTH.equals(dir)) type = LATITUDE;
                else                                      type = ANGLE;
                types       [i] = type;
                formats     [i] = getAngleFormat();
                toFormatUnit[i] = unit.asType(javax.measure.quantity.Angle.class).getConverterTo(NonSI.DEGREE_ANGLE);
                negate      [i] = neg;
                continue;
            }
            /*
             * Formatter for temporal units. Target unit is MILLISECONDS.
             * Type is DATE.
             */
            if (Units.isTemporal(unit)) {
                final Datum datum = CRS.getDatum(CRS.getSubCRS(crs, i, i+1));
                if (datum instanceof TemporalDatum) {
                    if (epochs == null) {
                        epochs = new long[dimension];
                    }
                    types       [i] = DATE;
                    formats     [i] = getDateFormat();
                    toFormatUnit[i] = unit.asType(Duration.class).getConverterTo(Units.MILLISECOND);
                    epochs      [i] = ((TemporalDatum) datum).getOrigin().getTime();
                    negate      [i] = neg;
                    continue;
                }
                types[i] = TIME;
                // Fallthrough: formatted as number for now.
                // TODO: Provide elapsed time formatting later.
            }
            /*
             * Formatter for all other units. Do NOT set types[i] since it may have been set
             * to a non-zero value by previous case. If not, the default value (zero) is the
             * one we want.
             */
            if (unit != null) {
                final String symbol = getUnitFormat().format(unit).trim();
                if (!symbol.isEmpty()) {
                    if (unitSymbols == null) {
                        unitSymbols = new String[dimension];
                    }
                    unitSymbols[i] = symbol;
                }
            }
            formats[i] = getNumberFormat();
            // Keep negate[i] to false for now.
        }
    }

    /**
     * Returns the separator between each coordinate (number, angle or date).
     *
     * @return The current coordinate separator.
     *
     * @since 2.2
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the separator between each coordinate.
     *
     * @param separator The new coordinate separator.
     *
     * @since 2.2
     */
    public void setSeparator(final String separator) {
        ensureNonNull("separator", separator);
        this.separator = separator;
    }

    /**
     * Returns the pattern for number fields. May return {@code null} if the underlying
     * {@linkplain NumberFormat number format} can not provide a pattern.
     *
     * @return The pattern for number fields, or {@code null} if not applicable.
     *
     * @since 3.00
     */
    public String getNumberPattern() {
        final NumberFormat format = getNumberFormat();
        if (format instanceof DecimalFormat) {
            return ((DecimalFormat) format).toPattern();
        }
        return null;
    }

    /**
     * Sets the pattern for numbers fields. If some ordinates are formatted as plain number
     * (for example in {@linkplain org.geotoolkit.referencing.cs.DefaultCartesianCS Cartesian
     * coordinate system}), then those numbers will be formatted using this pattern.
     *
     * @param pattern The number pattern as specified in {@link DecimalFormat}.
     */
    public void setNumberPattern(final String pattern) {
        ensureNonNull("pattern", pattern);
        final NumberFormat format = getNumberFormat();
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).applyPattern(pattern);
        }
    }

    /**
     * Returns the pattern for angle fields. May return {@code null} if the underlying
     * {@linkplain AngleFormat angle format} can not provide a pattern.
     *
     * @return The pattern for angle fields, or {@code null} if not applicable.
     *
     * @since 3.00
     */
    public String getAnglePattern() {
        return getAngleFormat().toPattern();
    }

    /**
     * Sets the pattern for angles fields. If some ordinates are formatted as angle
     * (for example in {@linkplain org.geotoolkit.referencing.cs.DefaultEllipsoidalCS
     * ellipsoidal coordinate system}), then those angles will be formatted using
     * this pattern.
     *
     * @param pattern The angle pattern as specified in {@link AngleFormat}.
     */
    public void setAnglePattern(final String pattern) {
        ensureNonNull("pattern", pattern);
        getAngleFormat().applyPattern(pattern);
    }

    /**
     * Returns the pattern for date fields. May return {@code null} if the underlying
     * {@linkplain DateFormat date format} can not provide a pattern.
     *
     * @return The pattern for date fields, or {@code null} if not applicable.
     *
     * @since 3.00
     */
    public String getDatePattern() {
        final DateFormat format = getDateFormat();
        if (format instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) format).toPattern();
        }
        return null;
    }

    /**
     * Sets the pattern for dates fields. If some ordinates are formatted as date (for example
     * in {@linkplain org.geotoolkit.referencing.cs.DefaultTimeCS time coordinate system}), then
     * those dates will be formatted using this pattern.
     *
     * @param pattern The date pattern as specified in {@link SimpleDateFormat}.
     */
    public void setDatePattern(final String pattern) {
        ensureNonNull("pattern", pattern);
        final DateFormat format = getDateFormat();
        if (format instanceof SimpleDateFormat) {
            ((SimpleDateFormat) format).applyPattern(pattern);
        }
    }

    /**
     * Returns the time zone for dates fields.
     *
     * @return The current time zone for dates.
     *
     * @since 3.00
     */
    public TimeZone getTimeZone() {
        return getDateFormat().getTimeZone();
    }

    /**
     * Sets the time zone for dates fields. If some ordinates are formatted as date (for example
     * in {@linkplain org.geotoolkit.referencing.cs.DefaultTimeCS time coordinate system}), then
     * those dates will be formatted using the specified time zone.
     *
     * @param timezone The time zone for dates.
     */
    public void setTimeZone(final TimeZone timezone) {
        ensureNonNull("timezone", timezone);
        getDateFormat().setTimeZone(timezone);
    }

    /**
     * Returns the format to use for formatting an ordinate at the given dimension.
     * The dimension parameter range from 0 inclusive to the
     * {@linkplain #getCoordinateReferenceSystem coordinate reference system}'s dimension,
     * exclusive.
     *
     * {@note This method returns a direct reference to the internal format. Any change to
     *        the returned <code>Format</code> object will impact the formatting performed
     *        by this <code>CoordinateFormat</code> object. We recommend to avoid such
     *        changes for now since it may not be compatible with future versions. Use
     *        the public setter methods instead.}
     *
     * @param  dimension The dimension for the ordinate to format.
     * @return The format for the given dimension.
     * @throws IndexOutOfBoundsException if {@code dimension} is out of range.
     */
    public Format getFormat(final int dimension) throws IndexOutOfBoundsException {
        return formats[dimension];
    }

    /**
     * Returns the date format.
     */
    private DateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        }
        return dateFormat;
    }

    /**
     * Returns the angle format.
     */
    private AngleFormat getAngleFormat() {
        if (angleFormat == null) {
            angleFormat = AngleFormat.getInstance(locale);
        }
        return angleFormat;
    }

    /**
     * Returns the number format.
     */
    private NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getNumberInstance(locale);
        }
        return numberFormat;
    }

    /**
     * Returns the unit format.
     */
    private UnitFormat getUnitFormat() {
        if (unitFormat == null) {
            unitFormat = UnitFormat.getInstance(locale);
        }
        return unitFormat;
    }

    /**
     * Formats a direct position. The position dimension must matches the
     * {@linkplain #getCoordinateReferenceSystem coordinate reference system} dimension.
     *
     * @param  point The position to format.
     * @return The formatted position.
     * @throws IllegalArgumentException
     *          if this {@code CoordinateFormat} cannot format the given object.
     */
    public String format(final DirectPosition point) {
        return format(point, new StringBuffer(), null).toString();
    }

    /**
     * Formats a direct position and appends the resulting text to a given string buffer.
     * The position dimension must matches the {@linkplain #getCoordinateReferenceSystem
     * coordinate reference system} dimension.
     *
     * @param point
     *          The position to format.
     * @param toAppendTo
     *          Where the text is to be appended.
     * @param position
     *          A {@code FieldPosition} identifying a field in the formatted text, or {@code null} if none.
     * @return The string buffer passed in as {@code toAppendTo}, with formatted text appended.
     * @throws IllegalArgumentException
     *          If this {@code CoordinateFormat} cannot format the given position.
     */
    public StringBuffer format(DirectPosition point, final StringBuffer toAppendTo,
                               FieldPosition position) throws IllegalArgumentException
    {
        /*
         * Validates arguments, transforming the given point if needed. Note that we do not
         * enforce the dimension check if the CoordinateFormat CRS is null, since we don't
         * know the actual number of dimension of this CRS.
         */
        ensureNonNull("point", point);
        ensureNonNull("toAppendTo", toAppendTo);
        final int dimension = point.getDimension();
        if (dimension != formats.length && crs != null) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$3, "point", dimension, formats.length));
        }
        final CoordinateReferenceSystem pointCRS = point.getCoordinateReferenceSystem();
        if (!CRS.equalsIgnoreMetadata(pointCRS, crs) && pointCRS != null && crs != null) {
            if (transform == null) {
                transform = new TransformedDirectPosition(null, crs, null);
            }
            try {
                transform.transform(point);
                point = transform;
            } catch (TransformException e) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
            }
        }
        /*
         * Now process to the formatting.
         */
        for (int i=0; i<dimension; i++) {
            double value = point.getOrdinate(i);
            final int fi = Math.min(i, formats.length-1);
            if (negate[fi]) {
                value = -value;
            }
            final UnitConverter c = toFormatUnit[fi];
            if (c != null) {
                value = c.convert(value);
            }
            final Object object;
            final byte type = types[fi];
            switch (type) {
                default:        object = Double.valueOf(value); break;
                case LONGITUDE: object = new Longitude (value); break;
                case LATITUDE:  object = new Latitude  (value); break;
                case ANGLE:     object = new Angle     (value); break;
                case DATE: {
                    object = new Date(epochs[fi] + Math.round(value));
                    break;
                }
            }
            if (i != 0) {
                toAppendTo.append(separator);
            }
            formats[fi].format(object, toAppendTo, dummy);
            if (unitSymbols != null) {
                final String symbol = unitSymbols[fi];
                if (symbol != null) {
                    toAppendTo.append('\u00A0').append(symbol);
                }
            }
        }
        return toAppendTo;
    }

    /**
     * Formats a direct position and appends the resulting text to a given string buffer.
     * The position dimension must matches the {@linkplain #getCoordinateReferenceSystem
     * coordinate reference system} dimension.
     *
     * @param object     The {@link DirectPosition} to format.
     * @param toAppendTo Where the text is to be appended.
     * @param position   A {@code FieldPosition} identifying a field in the formatted text,
     *                   or {@code null} if none.
     * @return The string buffer passed in as {@code toAppendTo}, with formatted text appended.
     * @throws NullPointerException if {@code toAppendTo} is null.
     * @throws IllegalArgumentException if this {@code CoordinateFormat}
     *         cannot format the given object.
     */
    @Override
    public StringBuffer format(final Object        object,
                               final StringBuffer  toAppendTo,
                               final FieldPosition position)
            throws IllegalArgumentException
    {
        ArgumentChecks.ensureNonNull("object", object);
        if (object instanceof DirectPosition) {
            return format((DirectPosition) object, toAppendTo, position);
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_CLASS_$2,
                    object.getClass(), DirectPosition.class));
        }
    }

    /**
     * Not yet implemented.
     *
     * @param source The string to parse.
     * @param position The position of the first character to parse.
     */
    @Override
    public DirectPosition parseObject(final String source, final ParsePosition position) {
        throw new UnsupportedOperationException("DirectPosition parsing not yet implemented.");
    }

    /**
     * Restores the transient fields after deserialization.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        dummy = new FieldPosition(0);
        initialize();
    }
}
