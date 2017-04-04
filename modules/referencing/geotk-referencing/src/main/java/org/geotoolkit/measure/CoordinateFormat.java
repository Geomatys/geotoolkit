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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.measure.Angle;
import org.apache.sis.measure.AngleFormat;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.CommonCRS;


/**
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
public class CoordinateFormat extends org.apache.sis.geometry.CoordinateFormat {
    /**
     * The output coordinate reference system. May be {@code null}.
     */
    private CoordinateReferenceSystem crs;

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
        this(locale, CommonCRS.WGS84.normalizedGeographic());
    }

    /**
     * Constructs a new coordinate format for the specified locale and coordinate reference system.
     *
     * @param locale The locale for formatting coordinates and numbers.
     * @param crs    The output coordinate reference system, or {@code null} if unknown.
     */
    public CoordinateFormat(final Locale locale, final CoordinateReferenceSystem crs) {
        super(locale, null);
        this.crs = crs;
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
        this.crs = crs;
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
        return getPattern(Number.class);
    }

    /**
     * Sets the pattern for numbers fields. If some ordinates are formatted as plain number
     * (for example in {@linkplain org.geotoolkit.referencing.cs.DefaultCartesianCS Cartesian
     * coordinate system}), then those numbers will be formatted using this pattern.
     *
     * @param pattern The number pattern as specified in {@link DecimalFormat}.
     */
    public void setNumberPattern(final String pattern) {
        applyPattern(Number.class, pattern);
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
        return getPattern(Angle.class);
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
        applyPattern(Angle.class, pattern);
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
        return getPattern(Date.class);
    }

    /**
     * Sets the pattern for dates fields. If some ordinates are formatted as date (for example
     * in {@linkplain org.geotoolkit.referencing.cs.DefaultTimeCS time coordinate system}), then
     * those dates will be formatted using this pattern.
     *
     * @param pattern The date pattern as specified in {@link SimpleDateFormat}.
     */
    public void setDatePattern(final String pattern) {
        applyPattern(Date.class, pattern);
    }
}
