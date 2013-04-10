/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.text.Format;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.MutableComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.measure.CoordinateFormat;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;

import static java.awt.GridBagConstraints.*;


/**
 * Selects the pattern to use for {@linkplain Format formating} numbers, angles or dates.
 * This widget can be used with one of {@link Format} objects working with pattern, like
 * {@link DecimalFormat}, {@link SimpleDateFormat} or {@link AngleFormat}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.12
 *
 * @since 2.0
 * @module
 */
@SuppressWarnings("serial")
public class FormatChooser extends JComponent implements Dialog {
    /**
     * The maximum number of items to keep in the history list.
     */
    private static final int HISTORY_SIZE = 50;

    /**
     * The color for error message.
     */
    private static final Color ERROR_COLOR = Color.RED;

    /**
     * A set of default patterns for differents locales. Keys are {@link Locale} object
     * and values are {@code String[][]} with arrays in the following order: number
     * patterns, date patterns and angle patterns.
     */
    private static final Map<Locale,String[][]> PATTERNS = new HashMap<>();

    /**
     * A set of default pattern for {@link AngleFormat}.
     */
    private static final String[] ANGLE_PATTERNS = new String[] {
        "D.d°",
        "D.dd°",
        "D.ddd°",
        "D°MM'",
        "D°MM.m'",
        "D°MM.mm'",
        "D°MM.mmm'",
        "D°MM'SS\"",
        "D°MM'SS.s\""
    };

    /**
     * The format to configure by this {@code FormatChooser}.
     */
    protected Format format;

    /**
     * A sample value for the "preview" text.
     */
    private Object value;

    /**
     * The panel in which to edit the pattern.
     */
    private final JComboBox<String> choices = new JComboBox<>();

    /**
     * The preview text. This is the {@code value} formated using {@code format}.
     */
    private final JLabel preview = new JLabel();

    /**
     * Constructs a pattern chooser for the given format.
     *
     * @param  format The format to configure. The default implementation accepts instance of
     *                {@link DecimalFormat}, {@link SimpleDateFormat} or {@link AngleFormat}.
     * @throws IllegalArgumentException if the format is invalid.
     */
    public FormatChooser(final Format format) throws IllegalArgumentException {
        setLayout(new GridBagLayout());
        final String[] patterns = getPatterns(format);
        if (patterns != null) {
            final MutableComboBoxModel<String> model = (MutableComboBoxModel<String>) choices.getModel();
            for (int i=0; i<patterns.length; i++) {
                model.addElement(patterns[i]);
            }
        }
        choices.setEditable(true); // Must be invoked before 'setFormat'.
        value = suggestSampleValue(format);
        setFormat(format);

        final Vocabulary resources = Vocabulary.getResources(getDefaultLocale());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx=0; c.insets.right=6;
        c.gridy=0;                 add(new JLabel(resources.getLabel(Vocabulary.Keys.FORMAT )), c);
        c.gridy++; c.insets.top=3; add(new JLabel(resources.getLabel(Vocabulary.Keys.PREVIEW)), c);
        c.insets.right=0; c.gridx++; c.weightx=1; c.fill=HORIZONTAL;
        c.gridy=0; c.insets.top=0; add(choices, c);
        c.gridy++; c.insets.top=3; add(preview, c);
        choices.getEditor().getEditorComponent().requestFocus();
        choices.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                applyPattern(false);
            }
        });
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    }

    /**
     * Returns a set of patterns for formatting in the given locale, or {@code null} if none.
     *
     * @param format for which to get a set of default patterns.
     * @todo Need a way to find the format locale.
     */
    private static synchronized String[] getPatterns(final Format format) {
        final Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        String[][] patterns = PATTERNS.get(locale);
        if (patterns == null) {
            patterns = new String[3][];
        }
        if (format instanceof NumberFormat) {
            if (patterns[0] == null) {
                patterns[0] = getNumberPatterns(locale);
            }
            return patterns[0];
        }
        if (format instanceof DateFormat) {
            if (patterns[1] == null) {
                patterns[1] = getDatePatterns(locale);
            }
            return patterns[1];
        }
        if (format instanceof AngleFormat || format instanceof CoordinateFormat) {
            if (patterns[2] == null) {
                patterns[2] = ANGLE_PATTERNS;
            }
            return patterns[2];
        }
        return null;
    }

    /**
     * Returns a set of patterns for formatting numbers in the given locale.
     * Note: this method is costly and should be invoked only once for a given locale.
     */
    private static String[] getNumberPatterns(final Locale locale) {
        final Set<String> patterns = new LinkedHashSet<>();
        int type = 0;
  fill: while (true) {
            final int digits;
            final NumberFormat format;
            switch (type++) {
                case  0: format=NumberFormat.getInstance        (locale); digits=-1; break;
                case  1: format=NumberFormat.getNumberInstance  (locale); digits= 4; break;
                case  2: format=NumberFormat.getPercentInstance (locale); digits= 2; break;
                case  3: format=NumberFormat.getCurrencyInstance(locale); digits=-1; break;
                default: break fill;
            }
            if (format instanceof DecimalFormat) {
                final DecimalFormat decimal = (DecimalFormat) format;
                patterns.add(decimal.toLocalizedPattern());
                for (int i=0; i<=digits; i++) {
                    format.setMinimumFractionDigits(i);
                    format.setMaximumFractionDigits(i);
                    patterns.add(decimal.toLocalizedPattern());
                }
            }
        }
        return patterns.toArray(new String[patterns.size()]);
    }

    /**
     * Returns a set of patterns for formatting dates in the given locale.
     * Note: this method is costly and should be invoked only once for a given locale.
     */
    private static String[] getDatePatterns(final Locale locale) {
        final int[] codes = {
            SimpleDateFormat.SHORT,
            SimpleDateFormat.MEDIUM,
            SimpleDateFormat.LONG,
            SimpleDateFormat.FULL
        };
        final Set<String> patterns = new LinkedHashSet<>();
        for (int i=0; i<codes.length; i++) {
            for (int j=-1; j<codes.length; j++) {
                final DateFormat format;
                if (j<0) {
                    format = DateFormat.getDateInstance(codes[i], locale);
                } else {
                    format = DateFormat.getDateTimeInstance(codes[i], codes[j], locale);
                }
                if (format instanceof SimpleDateFormat) {
                    patterns.add(((SimpleDateFormat) format).toLocalizedPattern());
                }
            }
        }
        return patterns.toArray(new String[patterns.size()]);
    }

    /**
     * Suggests a sample value for the given format, or {@code null} if this method has no
     * suggestion.
     *
     * @param  format The format.
     * @return A sample value for the specified format, or {@code null} if none.
     */
    private static Object suggestSampleValue(final Format format) {
        if (format instanceof NumberFormat) {
            return 39.3; // Could be any random value.
        }
        if (format instanceof DateFormat) {
            return new Date(); // Could be any random value.
        }
        if (format instanceof AngleFormat) {
            return new Angle(39.3); // Could be any random value.
        }
        if (format instanceof CoordinateFormat) {
            final int dimension = ((CoordinateFormat) format)
                    .getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
            final GeneralDirectPosition point = new GeneralDirectPosition(dimension);
            for (int i=0; i<dimension; i++) {
                point.setOrdinate(i, (i&1)==0 ? 39.3 : 27.9); // Could be any random value.
            }
            return point;
        }
        return null;
    }

    /**
     * Returns the current format.
     *
     * @return The current format.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Sets the format to configure. The default implementation accepts instance of
     * {@link DecimalFormat}, {@link SimpleDateFormat} or {@link AngleFormat}. If
     * more format classes are wanted, then the methods {@link #getPattern} and
     * {@link #setPattern} need to be overridden.
     *
     * @param  format The format to congifure.
     * @throws IllegalArgumentException if the format is invalid.
     */
    public void setFormat(final Format format) throws IllegalArgumentException {
        final Format old = this.format;
        this.format = format;
        try {
            update();
        } catch (IllegalStateException exception) {
            this.format = old;
            /*
             * The format is not one of recognized type.  Since this format was given in argument
             * (rather then the internal format field), Change the exception type for consistency
             * with the usual specification.
             */
            throw new IllegalArgumentException(exception.getLocalizedMessage(), exception);
        }
        firePropertyChange("format", old, format);
    }

    /**
     * Returns the sample value to format as a "preview" text.
     * If no such object is defined, then this method returns {@code null}.
     *
     * @return The sample value to use for preview.
     */
    public Object getSampleValue() {
        return value;
    }

    /**
     * Sets the sample value to format as a "preview" text. The value should
     * be an object formatable with {@link #getFormat}.
     *
     * @param  value The value to format in previews, or {@code null}.
     * @throws IllegalArgumentException if the value can't be formatted.
     */
    public void setSampleValue(final Object value) throws IllegalArgumentException {
        preview.setText(value!=null ? format.format(value) : null);
        preview.setForeground(getForeground());
        final Object old = this.value;
        this.value = value;
        firePropertyChange("sampleValue", old, value);
    }

    /**
     * Returns the localized pattern for the {@linkplain #getFormat current format}.
     * The default implementation recognize {@link DecimalFormat}, {@link SimpleDateFormat}
     * and {@link AngleFormat} instances.
     *
     * @return The pattern for the current format.
     * @throws IllegalStateException is the current format is not one of recognized type.
     */
    public String getPattern() throws IllegalStateException {
        if (format instanceof DecimalFormat) {
            return ((DecimalFormat) format).toLocalizedPattern();
        }
        if (format instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) format).toLocalizedPattern();
        }
        if (format instanceof AngleFormat) {
            return ((AngleFormat) format).toPattern();
        }
        if (format instanceof CoordinateFormat) {
            final CoordinateFormat format = (CoordinateFormat) this.format;
            for (int i=format.getCoordinateReferenceSystem().getCoordinateSystem().getDimension(); --i>=0;) {
                final Format sub = format.getFormat(i);
                if (sub instanceof AngleFormat) {
                    return ((AngleFormat) sub).toPattern();
                }
            }
        }
        throw new IllegalStateException(Classes.getShortClassName(format));
    }

    /**
     * Sets the localized pattern for the {@linkplain #getFormat current format}.
     * The default implementation recognize {@link DecimalFormat}, {@link SimpleDateFormat}
     * and {@link AngleFormat} instances.
     *
     * @param  pattern The pattern for the current format.
     * @throws IllegalStateException is the current format is not one of recognized type.
     * @throws IllegalArgumentException if the specified pattern is invalid.
     */
    public void setPattern(final String pattern)
            throws IllegalStateException, IllegalArgumentException
    {
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).applyLocalizedPattern(pattern);
        } else if (format instanceof SimpleDateFormat) {
            ((SimpleDateFormat) format).applyLocalizedPattern(pattern);
        } else if (format instanceof AngleFormat) {
            ((AngleFormat) format).applyPattern(pattern);
        } else if (format instanceof CoordinateFormat) {
            ((CoordinateFormat) format).setAnglePattern(pattern);
        } else {
            throw new IllegalStateException(Classes.getShortClassName(format));
        }
        update();
    }

    /**
     * Update the preview text according the current format pattern.
     */
    final void update() {
        choices.setSelectedItem(getPattern());
        try {
            preview.setText(value!=null ? format.format(value) : null);
            preview.setForeground(getForeground());
        } catch (IllegalArgumentException exception) {
            /*
             * The value can't be formatted. Replace the
             * value by the format error message.
             */
            preview.setText(exception.getLocalizedMessage());
            preview.setForeground(ERROR_COLOR);
        }
    }

    /**
     * Applies the currently selected pattern. If {@code add} is {@code true},
     * then the pattern is added to the combo box list.
     *
     * @param  add {@code true} for adding the pattern to the combo box list.
     * @return {@code true} if the pattern is valid.
     */
    private boolean applyPattern(final boolean add) {
        String pattern = choices.getSelectedItem().toString();
        if (pattern.trim().isEmpty()) {
            update();
            return false;
        }
        try {
            setPattern(pattern);
        } catch (RuntimeException exception) {
            /*
             * The pattern is not valid. Replace the value by an error message.
             */
            preview.setText(exception.getLocalizedMessage());
            preview.setForeground(ERROR_COLOR);
            return false;
        }
        if (add) {
            final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) choices.getModel();
            pattern = choices.getSelectedItem().toString();
            final int index = model.getIndexOf(pattern);
            if (index > 0) {
                model.removeElementAt(index);
            }
            if (index != 0) {
                model.insertElementAt(pattern, 0);
            }
            int size;
            while ((size = model.getSize()) > HISTORY_SIZE) {
                model.removeElementAt(size - 1);
            }
            if (size != 0) {
                choices.setSelectedIndex(0);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.12
     */
    @Override
    public void commitEdit() throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        final String old = getPattern();
        while (SwingUtilities.showDialog(owner, this, title)) {
            if (applyPattern(true)) {
                return true;
            }
        }
        setPattern(old);
        return false;
    }
}
