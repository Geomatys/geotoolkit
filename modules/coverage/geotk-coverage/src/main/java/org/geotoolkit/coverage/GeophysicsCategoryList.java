/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.geotoolkit.util.Utilities;


/**
 * An immutable list of geophysics category. Elements are usually (but not always) instances
 * of [@link GeophysicsCategory}. Exception to this rule includes categories wrapping an
 * identity transforms.
 * <p>
 * This list can transform geophysics values into sample values using
 * the list of {@link Category}. This transform is thread safe if each
 * {@link Category#getSampleToGeophysics} transform is thread-safe too.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
@Immutable
final class GeophysicsCategoryList extends CategoryList {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 98602310176453958L;

    /**
     * Maximum value for {@link #ndigits}. This is the number of
     * significant digits to allow when formatting a geophysics value.
     */
    private static final int MAX_DIGITS = 6;

    /**
     * Workaround for rounding errors.
     */
    private static final double EPS = 1E-6;

    /**
     * Units of measurement of geophysical values, or {@code null} if not applicable.
     */
    private final Unit<?> unit;

    /**
     * Number of significant digits, used for formatting geophysical values.
     */
    private final int ndigits;

    /**
     * Locale used for creating {@link #format} last time.
     * May be {@code null} if default locale was requested.
     */
    private transient Locale locale;

    /**
     * Format to use for writing geophysical values.
     */
    private transient NumberFormat format;

    /**
     * Constructs a category list using the specified array of categories.
     *
     * @param categories
     *          The list of categories. Elements should be instances of {@code GeophysicsCategory}
     *          (most of the time, but not always).
     * @param unit
     *          The unit information for all quantitative categories.
     *          May be {@code null} if no category has units.
     * @param inverse
     *          The {@link CategoryList} which is constructing this {@code GeophysicsCategoryList}.
     * @throws IllegalArgumentException
     *          if two or more categories have overlapping sample value range.
     */
    GeophysicsCategoryList(Category[] categories, final Unit<?> unit, final CategoryList inverse) {
        super(categories, unit, true, inverse);
        this.unit    = unit;
        this.ndigits = getFractionDigitCount(categories);
        assert isGeophysics(true);
    }

    /**
     * Computes the smallest number of fraction digits necessary to resolve all
     * quantitative values. This method assumes that geophysics values in the range
     * {@code Category.geophysics(true).getRange} are stored as integer sample
     * values in the range {@code Category.geophysics(false).getRange}.
     */
    private static int getFractionDigitCount(final Category[] categories) {
        int ndigits = 0;
        final int length = categories.length;
        for (int i=0; i<length; i++) {
            final Category category   = categories[i];
            final Category geophysics = category.geophysics(true);
            final Category packed     = category.geophysics(false);
            final double ln = Math.log10((geophysics.maximum - geophysics.minimum)/
                                         (    packed.maximum -     packed.minimum));
            if (!Double.isNaN(ln)) {
                final int n = -(int) Math.floor(ln + EPS);
                if (n > ndigits) {
                    ndigits = Math.min(n, MAX_DIGITS);
                }
            }
        }
        return ndigits;
    }

    /**
     * If {@code geo} is {@code false}, cancel the action of a previous call to
     * {@code geophysics(true)}. This method always returns a list of categories in which
     * <code>{@linkplain Category#geophysics(boolean) Category.geophysics}(geo)</code>
     * has been invoked for each category.
     */
    @Override
    public CategoryList geophysics(final boolean geo) {
        final CategoryList scaled = geo ? this : inverse;
        assert scaled.isGeophysics(geo);
        return scaled;
    }

    /**
     * Returns the unit information for quantitative categories in this list.
     * May returns {@code null}  if there is no quantitative categories
     * in this list, or if there is no unit information.
     */
    @Override
    public Unit<?> getUnits() {
        return unit;
    }

    /**
     * Formatte la valeur spécifiée selon les conventions locales. Le nombre sera
     * écrit avec un nombre de chiffres après la virgule approprié pour la catégorie.
     * Le symbole des unités sera ajouté après le nombre si {@code writeUnit}
     * est {@code true}.
     *
     * @param  value Valeur du paramètre géophysique à formatter.
     * @param  writeUnit Indique s'il faut écrire le symbole des unités après le nombre.
     *         Cet argument sera ignoré si aucune unité n'avait été spécifiée au constructeur.
     * @param  locale Conventions locales à utiliser, ou {@code null} pour les conventions par
     *         défaut.
     * @param  buffer Le buffer dans lequel écrire la valeur.
     * @return Le buffer {@code buffer} dans lequel auront été écrit la valeur et les unités.
     */
    @Override
    synchronized StringBuffer format(final double value, final boolean writeUnits,
                                     final Locale locale, StringBuffer buffer)
    {
        if (format == null || !Utilities.equals(this.locale, locale)) {
            this.locale = locale;
            format = (locale != null) ? NumberFormat.getNumberInstance(locale) :
                                        NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(ndigits);
            format.setMaximumFractionDigits(ndigits);
        }
        buffer = format.format(value, buffer, new FieldPosition(0));
        if (writeUnits && unit!=null) {
            final int position = buffer.length();
            buffer.append('\u00A0').append(unit);  // No-break space
            if (buffer.length() == position+1) {
                buffer.setLength(position);
            }
        }
        return buffer;
    }

    /**
     * Compares the specified object with this category list for equality.
     * If the two objects are instances of {@link CategoryList}, then the
     * test is a little bit stricter than the default {@link AbstractList#equals}.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof GeophysicsCategoryList) {
            final GeophysicsCategoryList that = (GeophysicsCategoryList) object;
            return this.ndigits == that.ndigits &&
                   Utilities.equals(this.unit, that.unit) &&
                   super.equals(that);
        }
        return ndigits == 0 && unit == null && super.equals(object);
    }
}
