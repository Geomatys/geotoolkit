/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.awt.Color;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.text.NumberFormat;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import javax.swing.ComboBoxModel;

import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.internal.coverage.ColorPalette;
import org.geotoolkit.internal.coverage.TransferFunction;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.LinearTransform1D;
import org.geotoolkit.referencing.operation.transform.LogarithmicTransform1D;
import org.geotoolkit.referencing.operation.transform.ExponentialTransform1D;
import org.geotoolkit.resources.Errors;


/**
 * A single row in a {@link CategoryTable}. A row contains the minimal and maximal sample values,
 * together with the <cite>transfer function</cite> type and coefficients. Those informations can
 * be inferred from an existing {@link Category}, edited, then used for creating a new
 * {@link Category}.
 * <p>
 * The attributes in a {@code CategoryRecord} are interdependent. Invoking any setter method
 * may have an effect on other attributes. For example if the scale factor is changed, then
 * the {@linkplain #getValueRange() range of values} will be recomputed accordingly.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.13
 * @module
 */
public class CategoryRecord implements Cloneable, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2445769517016833850L;

    /**
     * Some constants of interest for {@link #functionType}.
     */
    static final int NONE=0, LINEAR=1, LOGARITHMIC=2, EXPONENTIAL=3;

    /**
     * The category represented by this record, or {@code null} if the field has
     * been updated and the category has not yet been recreated.
     */
    private Category category;

    /**
     * The category name.
     */
    private String name;

    /**
     * Range of sample values.
     */
    private int sampleMin, sampleMax;

    /**
     * The range of sample values. Computed when first needed.
     */
    private transient NumberRange<Integer> sampleRange;

    /**
     * The range of geophysics values. Computed when first needed.
     */
    private transient NumberRange<Double> valueRange;

    /**
     * The type of the transfer fonction.
     * <p>
     * <ul>
     *   <li>0: none</li>
     *   <li>1: linear</li>
     *   <li>2: logarithmic</li>
     *   <li>3: exponential</li>
     * </ul>
     */
    private int functionType;

    /**
     * The coefficients of the transfer function.
     */
    private double offset, scale;

    /**
     * The number of fraction digits to use in the decimal format, or -1 if not yet computed.
     */
    private transient int fractionDigits;

    /**
     * The name of a color palette, or the RGB code of a single color, or {@code null} if unknown.
     * This is used for selection in a {@link org.geotoolkit.gui.swing.image.PaletteComboBox}.
     */
    private String paletteName;

    /**
     * Creates a new row initialized to the [0 &hellip; 255] range of sample values,
     * with no transfer function.
     */
    public CategoryRecord() {
        sampleMax = 255;
        scale = 1;
    }

    /**
     * Creates a new row initialized to the values inferred from the given category.
     *
     * @param category The category from which to infer the values.
     * @param locale The locale to use for the localization of the category name,
     *        or {@code null} for an implementation-dependent default locale.
     *
     * @todo Current implementation does not yet recognize logarithmic transfer function.
     */
    public CategoryRecord(Category category, final Locale locale) {
        this(category, locale, null, null);
    }

    /**
     * Creates a new row initialized to the values inferred from the given category.
     *
     * @param category The category from which to infer the values.
     * @param locale The locale to use for the localization of the category name,
     *        or {@code null} for an implementation-dependent default locale.
     * @param paletteFactory The factory to use for fetching the colors from their name,
     *        or {@code null} for the {@linkplain PaletteFactory#getDefault() default one}.
     * @param palettes A list of palettes to use for inferring the palettes names,
     *        or {@code null} if none. This can be used for the common case where
     *        this list is already available from the {@link SampleDimensionPanel} GUI.
     */
    CategoryRecord(Category category, final Locale locale,
            PaletteFactory paletteFactory, ComboBoxModel<ColorPalette> palettes)
    {
        this.category = category = category.geophysics(false);
        final InternationalString name = category.getName();
        if (name != null) {
            this.name = name.toString(locale);
        }
        final TransferFunction tf = new TransferFunction(category, locale);
        sampleMin = tf.minimum;
        sampleMax = tf.maximum;
        scale     = tf.scale;
        offset    = tf.offset;
        if (tf.isQuantitative) {
            fractionDigits = -1;
        }
        if (tf.type != null) {
            setTransferFunctionType(tf.type);
        }
        if (tf.warning != null) {
            warning("<init>", tf.warning);
        }
        paletteName = ColorPalette.findName(category.getColors(), palettes, paletteFactory);
    }

    /**
     * Logs a warning from the given method with the given message.
     *
     * @param message The message to log.
     */
    private static void warning(final String method, final String message) {
        Logging.log(CategoryRecord.class, method, new LogRecord(Level.WARNING, message));
    }

    /**
     * Returns the category represented by this record. If a category has been specified at
     * construction time and no setter method changed the attributes, then that category is
     * returned unchanged. Otherwise a new category is created and returned.
     *
     * @return The category represented by this record.
     */
    public Category getCategory() {
        return getCategory(null);
    }

    /**
     * Returns the category represented by this record, using the given palette factory
     * if a new category needs to be built.
     *
     * @param  paletteFactory The factory to use for loading colors from a palette name,
     *         or {@code null} for the {@linkplain PaletteFactory#getDefault() default}.
     * @return The category represented by this record.
     *
     * @since 3.14
     */
    final Category getCategory(PaletteFactory paletteFactory) {
        if (category == null) {
            Color[] colors = null;
            if (paletteName != null) {
                if (paletteFactory == null) {
                    paletteFactory = PaletteFactory.getDefault();
                }
                try {
                    colors = paletteFactory.getColors(paletteName);
                } catch (IOException e) {
                    warning("getCategory", e.toString());
                    // Leave 'colors' to null, which let Category chooses a default value.
                }
            } else {
                colors = new Color[] {new Color(0,0,0,0)};
            }
            MathTransform1D sampleToGeophysics = null;
            if (functionType != NONE) {
                sampleToGeophysics = LinearTransform1D.create(scale, offset);
                switch (functionType) {
                    case LOGARITHMIC: {
                        sampleToGeophysics = MathTransforms.concatenate(
                                LogarithmicTransform1D.create(10), sampleToGeophysics);
                        break;
                    }
                    case EXPONENTIAL: {
                        sampleToGeophysics = MathTransforms.concatenate(
                                sampleToGeophysics, ExponentialTransform1D.create(10));
                        break;
                    }
                }
            }
            category = new Category(name, colors, getSampleRange(), sampleToGeophysics);
        }
        return category;
    }

    /**
     * Returns the category name.
     *
     * @return The cateogory name, or {@code null} if none.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name.
     *
     * @param  name The new category name.
     * @return {@code true} if this object changed as a result of this method call.
     */
    public boolean setName(final String name) {
        if (Objects.equals(name, this.name)) {
            return false;
        }
        this.name = name;
        category  = null;
        return true;
    }

    /**
     * Returns the range of valid sample values.
     *
     * @param extremum {@code -1} for the range of valid minimal sample values, or
     *                 {@code +1} for the range of valid maximal sample values.
     */
    final NumberRange<Integer> getValidSamples(final int extremum) {
        Integer min=null, max=null;
        if (extremum < 0) {
            // Minimal value can not be greater than 'sampleMax'.
            max = sampleMax;
        } else {
            // Maximal value can not be less than 'sampleMin'.
            min = sampleMin;
        }
        if (functionType == LOGARITHMIC) {
            if (min != null && min <= 0) min = 1;
            if (max != null && max <= 0) max = min;
        }
        return new NumberRange<>(Integer.class, min, max);
    }

    /**
     * Returns the range of valid geophysics values.
     *
     * @param extremum {@code -1} for the range of valid minimal geophysics values, or
     *                 {@code +1} for the range of valid maximal geophysics values.
     */
    final NumberRange<Double> getValidValues(final int extremum) {
        Double min=null, max=null;
        final NumberRange<Double> range = getValueRange();
        if (range != null) {
            if (extremum < 0) {
                // Minimal value can not be greater than 'sampleMax'.
                max = range.getMaxValue();
            } else {
                // Maximal value can not be less than 'sampleMin'.
                min = range.getMinValue();
            }
        }
        if (functionType == EXPONENTIAL) {
            if (min != null && min <= 0) min = Double.MIN_VALUE;
            if (max != null && max <= 0) max = min;
        }
        return new NumberRange<>(Double.class, min, max);
    }

    /**
     * Returns the range of sample values.
     *
     * @return The range of sample values (never {@code null}).
     */
    public NumberRange<Integer> getSampleRange() {
        if (sampleRange == null) {
            sampleRange = NumberRange.create(sampleMin, sampleMax);
        }
        return sampleRange;
    }

    /**
     * Sets the range of sample values.
     *
     * @param  minimum The new minimal sample value, or {@code null} if unchanged.
     * @param  maximum The new maximal sample value, or {@code null} if unchanged.
     * @return {@code true} if this object changed as a result of this method call.
     */
    public boolean setSampleRange(final Integer minimum, final Integer maximum) {
        boolean changed = false;
        if (minimum != null) {
            int min = minimum;
            if (min != sampleMin) {
                if (functionType == LOGARITHMIC && min <= 0) {
                    min = 1; // log(min) requires min > 0.
                }
                if (min > sampleMax) {
                    sampleMax = min;
                }
                sampleMin = min;
                changed = true;
            }
        }
        if (maximum != null) {
            int max = maximum;
            if (max != sampleMax) {
                if (functionType == LOGARITHMIC && max <= 0) {
                    max = 1; // log(max) requires max > 0.
                }
                if (max < sampleMin) {
                    sampleMin = max;
                }
                sampleMax = max;
                changed = true;
            }
        }
        if (changed) {
            sampleRange = null;
            valueRange  = null;
            category    = null;
        }
        return changed;
    }

    /*
     * NOTE: The relationship between sample values and geophysics values are implemented in
     *       getValueRange(), setValueRange() and the various methods computing the minimal
     *       and maximal allowed values. If those formulas are changed, then the labels in
     *       CategoryTable.CellRenderer shall be modified accordingly.
     */

    /**
     * Returns the range of geophysics value, or {@code null} if there is no transfer function.
     * If non-null, the returned range is computed from the range of sample values and the
     * coefficients of the transfer function.
     *
     * @return The range of geophysics value, or {@code null}.
     */
    public NumberRange<Double> getValueRange() {
        if (functionType == NONE) {
            return null;
        }
        if (valueRange == null) {
            double min = sampleMin;
            double max = sampleMax;
            if (functionType == LOGARITHMIC) {
                min = Math.log10(min);
                max = Math.log10(max);
            }
            min = offset + scale * min;
            max = offset + scale * max;
            if (min > max) {
                final double tmp = min;
                min = max;
                max = tmp;
            }
            if (functionType == EXPONENTIAL) {
                min = MathFunctions.pow10(min);
                max = MathFunctions.pow10(max);
            }
            valueRange = NumberRange.create(min, max);
        }
        return valueRange;
    }

    /**
     * Sets the range of geophysics values. This method compute the offset and scale factors
     * of the transfer function in order to match the given range.
     *
     * @param  minimum The new minimal geophysics value, or {@code null} if unchanged.
     * @param  maximum The new maximal geophysics value, or {@code null} if unchanged.
     * @return {@code true} if this object changed as a result of this method call.
     */
    public boolean setValueRange(final Number minimum, final Number maximum) {
        boolean changed = false;
        double xmin = sampleMin;
        double xmax = sampleMax;
        switch (functionType) {
            case LOGARITHMIC: {
                xmin = Math.log10(xmin);
                xmax = Math.log10(xmax);
                break;
            }
            case NONE: {
                functionType = LINEAR;
                valueRange   = null;
                changed      = true;
                break;
            }
        }
        double ymin = (minimum != null) ? minimum.doubleValue() : getValueRange().getMinimum(true);
        double ymax = (maximum != null) ? maximum.doubleValue() : getValueRange().getMaximum(true);
        if (ymin > ymax) {
            final double tmp = ymin;
            ymin = ymax;
            ymax = tmp;
        }
        if (functionType == EXPONENTIAL) {
            ymin = Math.log10(Math.max(ymin, Double.MIN_VALUE));
            ymax = Math.log10(Math.max(ymax, Double.MIN_VALUE));
        }
        double scale = (xmin != xmax) ? Math.abs(ymax - ymin) / (xmax - xmin) : 1;
        if (this.scale < 0) {
            scale = -scale;
        }
        final double offset = ymin - scale * xmin;
        if (!Utilities.equals(offset, this.offset) || !Utilities.equals(scale, this.scale)) {
            this.offset    = offset;
            this.scale     = scale;
            valueRange     = null;
            category       = null;
            fractionDigits = -1;
            changed        = true;
        }
        return changed;
    }

    /**
     * Returns the transfer function type, or {@code null} if the category is not quantitative.
     *
     * @return The transfer function type, or {@code null}.
     */
    public TransferFunctionType getTransferFunctionType() {
        switch (functionType) {
            case LINEAR:      return TransferFunctionType.LINEAR;
            case LOGARITHMIC: return TransferFunctionType.LOGARITHMIC;
            case EXPONENTIAL: return TransferFunctionType.EXPONENTIAL;
            default: return null;
        }
    }

    /**
     * Sets the transfer function type. IF the given argument is null ot an unknown code,
     * then this method set the transfer function type to "none".
     *
     * @param type The new transfer function type, or {@code null} for a qualitative category.
     * @return {@code true} if this object changed as a result of this method call.
     */
    public boolean setTransferFunctionType(final TransferFunctionType type) {
        final int code;
        if (TransferFunctionType.LINEAR.equals(type)) {
            code = LINEAR;
        } else if (TransferFunctionType.LOGARITHMIC.equals(type)) {
            code = LOGARITHMIC;
            // log(sample) requires sample > 0.
            if (sampleMin <= 0) {sampleMin = 1; sampleRange = null;}
            if (sampleMax <= 0) {sampleMax = 1; sampleRange = null;}
        } else if (TransferFunctionType.EXPONENTIAL.equals(type)) {
            code = EXPONENTIAL;
        } else {
            code = NONE;
        }
        if (code == functionType) {
            return false;
        }
        functionType = code;
        valueRange   = null;
        category     = null;
        return true;
    }

    /**
     * Returns a coefficient of the transfer function, or {@code null} if the category is not
     * quantitative. The coefficient to fetch is determined by the {@code order} argument.
     * Current implementation accepts only 0 (the offset) or 1 (the scale), but subclasses
     * can add higher order.
     *
     * @param  order 0 for the offset, or 1 for the scale factor.
     * @return The requested coefficient of the transfer function, or {@code null}.
     * @throws IllegalArgumentException If the {@code order} argument is out of bounds.
     */
    public Double getCoefficient(final int order) throws IllegalArgumentException {
        if (functionType == NONE) {
            return null;
        }
        final double value;
        switch (order) {
            case 0: value = offset; break;
            case 1: value = scale;  break;
            default: {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, "order", order));
            }
        }
        return value;
    }

    /**
     * Sets a coefficient of the transfer function.
     *
     * @param  order 0 for the offset, or 1 for the scale factor.
     * @param  coeff The new coefficient value.
     * @return {@code true} if this object changed as a result of this method call.
     * @throws IllegalArgumentException If the {@code order} argument is out of bounds.
     */
    public boolean setCoefficient(final int order, final double coeff) throws IllegalArgumentException {
        final double old;
        switch (order) {
            case 0: old = offset; offset = coeff; break;
            case 1: old = scale;  scale  = coeff; break;
            default: {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, "order", order));
            }
        }
        boolean changed = !Utilities.equals(old, coeff);
        if (functionType == NONE) {
            functionType = LINEAR;
            changed = true;
        }
        if (changed) {
            valueRange = null;
            category   = null;
            if (order != 0) {
                fractionDigits = -1;
            }
        }
        return changed;
    }

    /**
     * Sets a coefficient of the transfer function, rounding it is reasonable.
     * This is for internal usage by {@link CategoryTable} only.
     */
    final boolean setCoefficient(final int order, final Number coeff) throws IllegalArgumentException {
        return setCoefficient(order, InternalUtilities.adjustForRoundingError(coeff.doubleValue(), 3600, 8));
    }

    /**
     * Configure the given {@linkplain java.text.DecimalFormat} for use in formatting the
     * {@linkplain #getValueRange() value range} and the {@linkplain #getCoefficient(int)
     * coefficients}.
     */
    final void configure(final NumberFormat format) {
        if (fractionDigits < 0) {
            InternalUtilities.configure(format, scale, 9);
            fractionDigits = format.getMaximumFractionDigits();
        }
        format.setMinimumFractionDigits(Math.min(fractionDigits,   3));
        format.setMaximumFractionDigits(Math.min(fractionDigits+3, 9));
    }

    /**
     * Returns the colors, as a palette name or as a RGB code.
     * The syntax of the returned string is described in
     * {@link org.geotoolkit.gui.swing.image.PaletteComboBox#getSelectedItem()}.
     *
     * @return The palette name or RGB code, or {@code null} if none.
     *
     * @since 3.14
     */
    public String getPaletteName() {
        return paletteName;
    }

    /**
     * Sets the colors, as a palette name or as a RGB code.
     * The syntax of the string argument is described in
     * {@link org.geotoolkit.gui.swing.image.PaletteComboBox#getSelectedItem()}.
     * <p>
     * A list of available palette names is provided by the {@link PaletteFactory} javadoc.
     *
     * @param name The palette name or RGB code, or {@code null} if none.
     * @return {@code true} if this object changed as a result of this method call.
     *
     * @since 3.14
     */
    public boolean setPaletteName(final String name) {
        final boolean changed = !Objects.equals(name, paletteName);
        if (changed) {
            paletteName = name;
            category = null;
        }
        return changed;
    }

    /**
     * Returns a clone of this record.
     */
    @Override
    public CategoryRecord clone() {
        try {
            return (CategoryRecord) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Invoked on deserialization. This method restores some transient fields.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        fractionDigits = -1;
    }

    /**
     * Returns a string representation of this record, for debugging purpose.
     * The current implementation formats the value in the same order than the
     * column order documented in {@link CategoryTable}.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this))
                .append('[').append(sampleMin).append(" \u2026 ").append(sampleMax);
        final NumberRange<Double> range = getValueRange();
        if (range != null) {
            buffer.append(", ").append(range.getMinValue()).append(" \u2026 ").append(range.getMaxValue())
                  .append(", ").append(getTransferFunctionType().name())
                  .append(", ").append(getCoefficient(0))
                  .append(", ").append(getCoefficient(1));
        }
        return buffer.append(']').toString();
    }
}
