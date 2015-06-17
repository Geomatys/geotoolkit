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
package org.geotoolkit.display.axis;

import java.util.Locale;
import java.util.Objects;
import java.io.Serializable;
import java.awt.RenderingHints;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import javax.measure.unit.Unit;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Base class for graduation.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public abstract class AbstractGraduation implements Graduation, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5215728323932315112L;

    /**
     * The axis units, or {@code null} if unknown.
     */
    Unit<?> unit;

    /**
     * The axis title for this graduation.
     */
    private String title;

    /**
     * The locale for formatting labels.
     */
    private Locale locale = Locale.getDefault(Locale.Category.FORMAT);

    /**
     * A list of event listeners for this component.
     */
    protected final PropertyChangeSupport listenerList;

    /**
     * Constructs a graduation with the supplied units.
     *
     * @param unit The axis's units, or {@code null} if unknown.
     */
    public AbstractGraduation(final Unit<?> unit) {
        listenerList = new PropertyChangeSupport(this);
        this.unit = unit;
    }

    /**
     * Sets the minimum value for this graduation. If the new minimum is greater than the current
     * maximum, then the maximum will also be set to a value greater than or equal to the minimum.
     *
     * @param  value The new minimum in {@link #getUnit} units.
     * @return {@code true} if the state of this graduation changed as a result of this call, or
     *         {@code false} if the new value is identical to the previous one.
     * @throws IllegalArgumentException If {@code value} is NaN ou infinite.
     *
     * @see #getMinimum
     * @see #setMaximum(double)
     */
    public abstract boolean setMinimum(final double value) throws IllegalArgumentException;

    /**
     * Sets the maximum value for this graduation. If the new maximum is less than the current
     * minimum, then the minimum will also be set to a value less than or equal to the maximum.
     *
     * @param  value The new maximum in {@link #getUnit} units.
     * @return {@code true} if the state of this graduation changed as a result of this call, or
     *         {@code false} if the new value is identical to the previous one.
     * @throws IllegalArgumentException If {@code value} is NaN ou infinite.
     *
     * @see #getMaximum
     * @see #setMinimum(double)
     */
    public abstract boolean setMaximum(final double value) throws IllegalArgumentException;

    /**
     * Returns the axis title. If {@code includeUnits} is {@code true}, then the returned string
     * will includes units as in "Temperature (°C)". The exact formatting is local-dependent.
     *
     * @param  includeSymbol {@code true} to format unit symbol after the name.
     * @return The graduation name (also to be use as axis title).
     *
     * @todo Add localization support.
     */
    @Override
    public synchronized String getTitle(final boolean includeSymbol) {
        if (includeSymbol) {
            final String symbol = getSymbol();
            if (symbol != null && !symbol.isEmpty()) {
                return (title != null) ? title + " (" + symbol + ')' : symbol;
            }
        }
        return title;
    }

    /**
     * Sets the axis title, not including unit symbol. This method will fire a
     * property change event with the {@code "title"} property name.
     *
     * @param title New axis title, or {@code null} to remove any previous setting.
     */
    public void setTitle(final String title) {
        final String old;
        synchronized (this) {
            old = this.title;
            this.title = title;
        }
        listenerList.firePropertyChange("title", old, title);
    }

    /**
     * Returns a string representation of axis's units, or {@code null}
     * if there is none. The default implementation returns the string
     * representation of {@link #getUnit}.
     */
    String getSymbol() {
        assert Thread.holdsLock(this);
        final Unit<?> unit = getUnit();
        return (unit != null) ? unit.toString() : null;
    }

    /**
     * Returns the graduation's units, or {@code null} if unknown.
     */
    @Override
    public synchronized Unit<?> getUnit() {
        return unit;
    }

    /**
     * Changes the graduation's units. Subclasses will automatically convert minimum and maximum
     * values from the old units to the new one. This method fires a property change event with the
     * {@code "unit"} property name.
     *
     * @param  unit The new units, or {@code null} if unknown. If null, minimum and maximum values
     *         are not converted.
     * @throws IllegalArgumentException if units are not convertible, or if the
     *         specified units is illegal for this graduation.
     */
    public void setUnit(final Unit<?> unit) throws IllegalArgumentException {
        final Unit<?> oldUnit;
        synchronized (this) {
            oldUnit = this.unit;
            this.unit = unit;
        }
        listenerList.firePropertyChange("unit", oldUnit, unit);
    }

    /**
     * Returns the locale to use for formatting labels.
     */
    @Override
    public synchronized Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale to use for formatting labels.
     * This will fire a property change event with the {@code "locale"} property name.
     *
     * @param locale The new labels format.
     */
    public synchronized void setLocale(final Locale locale) {
        ensureNonNull("locale", locale);
        final Locale old;
        synchronized (this) {
            old = this.locale;
            this.locale = locale;
            if (!locale.equals(old)) {
                clearFormat();
            }
        }
        listenerList.firePropertyChange("locale", old, locale);
    }

    /**
     * Convenience hook for reseting the format when the local changed.
     */
    void clearFormat() {
    }

    /**
     * Adds a {@link PropertyChangeListener} to the listener list. The listener is registered
     * for all properties. A {@link java.beans.PropertyChangeEvent} will get fired in response
     * to setting a property, such as {@link #setTitle} or {@link #setLocale}.
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        listenerList.addPropertyChangeListener(listener);
    }

    /**
     * Removes a {@link PropertyChangeListener} from the listener list.
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        listenerList.removePropertyChangeListener(listener);
    }

    /**
     * Retourne la longueur de l'axe, en pixels ou en points (1/72 de pouce).
     */
    static float getVisualAxisLength(final RenderingHints hints) {
        return getValue(hints, VISUAL_AXIS_LENGTH, 600);
    }

    /**
     * Retourne l'espace approximatif (en pixels ou en points) à laisser entre les
     * graduations principales. L'espace réel entre les graduations peut être légèrement
     * différent, par exemple pour avoir des étiquettes qui correspondent à des valeurs
     * arrondies.
     */
    static float getVisualTickSpacing(final RenderingHints hints) {
        return getValue(hints, VISUAL_TICK_SPACING, 48);
    }

    /**
     * Retourne une valeur sous forme de nombre réel.
     */
    private static float getValue(final RenderingHints   hints,
                                  final RenderingHints.Key key,
                                  final float defaultValue)
    {
        if (hints != null) {
            final Object object = hints.get(key);
            if (object instanceof Number) {
                final float value = ((Number) object).floatValue();
                if (value!=0 && !Float.isInfinite(value)) {
                    return value;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Vérifie que le nombre spécifié est non-nul. S'il
     * est 0, NaN ou infini, une exception sera lancée.
     *
     * @param  name Nom de l'argument.
     * @param  n Nombre à vérifier.
     * @throws IllegalArgumentException Si <var>n</var> est NaN ou infini.
     */
    static void ensureNonZero(final String name, final double n) throws IllegalArgumentException {
        if (Double.isNaN(n) || Double.isInfinite(n) || n==0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.IllegalArgument_2, name, n));
        }
    }

    /**
     * Vérifie que le nombre spécifié est réel. S'il est NaN ou infini, une exception sera lancée.
     *
     * @param  name Nom de l'argument.
     * @param  n Nombre à vérifier.
     * @throws IllegalArgumentException Si <var>n</var> est NaN ou infini.
     */
    static void ensureFinite(final String name, final double n) throws IllegalArgumentException {
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.IllegalArgument_2, name, n));
        }
    }

    /**
     * Vérifie que le nombre spécifié est réel. S'il est NaN ou infini, une exception sera lancée.
     *
     * @param  name Nom de l'argument.
     * @param  n Nombre à vérifier.
     * @throws IllegalArgumentException Si <var>n</var> est NaN ou infini.
     */
    static void ensureFinite(final String name, final float n) throws IllegalArgumentException {
        if (Float.isNaN(n) || Float.isInfinite(n)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.IllegalArgument_2, name, n));
        }
    }

    /**
     * Compares this graduation with the specified object for equality.
     * This method do not compare listeners registered in {@link #listenerList}.
     *
     * @param object The object to compare with.
     * @return {@code true} if this graduation is equal to the given object.
     */
    @Override
    public  boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final AbstractGraduation that = (AbstractGraduation) object;
            // We should lock object as well, but can't because of deadlocks.
            synchronized (this) {
                return Objects.equals(this.unit,   that.unit  ) &&
                       Objects.equals(this.title,  that.title ) &&
                       Objects.equals(this.locale, that.locale);
            }
        }
        return false;
    }

    /**
     * Returns a hash value for this graduation.
     */
    @Override
    public synchronized int hashCode() {
        int code = (int) serialVersionUID;
        if (title != null) {
            code ^= title.hashCode();
        }
        if (unit != null) {
            code ^= unit.hashCode();
        }
        return code;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getMinimum() + " \u2026 " + getMaximum() + ']';
    }
}
