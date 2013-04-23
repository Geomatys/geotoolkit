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
package org.geotoolkit.gui.swing.referencing;

import java.util.Objects;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField;
import javax.swing.AbstractSpinnerModel;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.io.Serializable;
import java.text.ParseException;

import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.measure.AngleFormat;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;


/**
 * A {@link SpinnerModel} for sequences of angles.
 * This model work like {@link SpinnerNumberModel}.
 *
 * @author Adapted from Hans Muller
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see JSpinner
 * @see SpinnerNumberModel
 *
 * @since 2.3
 * @module
 */
final class SpinnerAngleModel extends AbstractSpinnerModel implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2469714130798196115L;

    /**
     * The current value.
     */
    private Angle value;

    /**
     * The minimum and maximum values.
     */
    private double minimum, maximum;

    /**
     * The step size.
     */
    private double stepSize = 1;

    /**
     * Constructs a {@code SpinnerAngleModel} that represents a closed sequence of angles.
     * Initial minimum and maximum values are chosen according the {@code value} type:
     * <p>
     * <table>
     *   <tr><td>{@link Longitude}&nbsp;</td> <td>-360° to 360°</td></tr>
     *   <tr><td>{@link Latitude}&nbsp;</td>  <td>-90° to 90°</td>  </tr>
     *   <tr><td>{@link Angle}&nbsp;</td>     <td>0° to 360°</td>   </tr>
     * </table>
     *
     * @param  value the current (non {@code null}) value of the model
     * @throws IllegalArgumentException if {@code value} is null.
     */
    public SpinnerAngleModel(final Angle value) {
        this.value = value;
        if (value instanceof Longitude) {
            minimum  = 2*Longitude.MIN_VALUE;
            maximum  = 2*Longitude.MAX_VALUE;
        } else if (value instanceof Latitude) {
            minimum  = Latitude.MIN_VALUE;
            maximum  = Latitude.MAX_VALUE;
        } else if (value != null) {
            minimum = 0;
            maximum = 360;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Changes the lower bound for angles in this sequence.
     */
    public void setMinimum(final double minimum) {
        if (this.minimum != minimum) {
            this.minimum = minimum;
            fireStateChanged();
        }
    }

    /**
     * Returns the first angle in this sequence.
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * Changes the upper bound for angles in this sequence.
     */
    public void setMaximum(final double maximum) {
        if (this.maximum != maximum) {
            this.maximum = maximum;
            fireStateChanged();
        }
    }

    /**
     * Returns the last angle in the sequence.
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * Changes the size of the value change computed by the {@code getNextValue}
     * and {@code getPreviousValue} methods.
     */
    public void setStepSize(final double stepSize) {
        if (this.stepSize != stepSize) {
            this.stepSize = stepSize;
            fireStateChanged();
        }
    }

    /**
     * Returns the size of the value change computed by the
     * {@code getNextValue} and {@code getPreviousValue} methods.
     */
    public double getStepSize() {
        return stepSize;
    }

    /**
     * Wraps the specified value into an {@link Angle} object.
     */
    final Angle toAngle(final double newValue) {
        if (value instanceof Longitude) return new Longitude(newValue);
        if (value instanceof  Latitude) return new  Latitude(newValue);
        return new Angle(newValue);
    }

    /**
     * Returns {@code value + factor * stepSize}.
     */
    private Angle getNextValue(final int factor) {
        final double newValue = value.degrees() + stepSize*factor;
        if (!(newValue>=minimum && newValue<=maximum)) return null;
        return toAngle(newValue);
    }

    /**
     * Returns the next angle in the sequence.
     */
    @Override
    public Angle getNextValue() {
        return getNextValue(+1);
    }

    /**
     * Returns the previous angle in the sequence.
     */
    @Override
    public Angle getPreviousValue() {
        return getNextValue(-1);
    }

    /**
     * Returns the value of the current angle of the sequence.
     */
    @Override
    public Angle getValue() {
        return value;
    }

    /**
     * Sets the current value for this sequence.
     */
    @Override
    public void setValue(final Object value) {
        if (!(value instanceof Angle)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "value", value));
        }
        if (!Objects.equals(value, this.value)) {
            this.value = (Angle)value;
            fireStateChanged();
        }
    }

    /**
     * This subclass of {@link javax.swing.InternationalFormatter} maps the
     * minimum/maximum properties to a {@link SpinnerAngleModel}.
     *
     * @author Adapted from Hans Muller
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    @SuppressWarnings("serial")
    private final class EditorFormatter extends InternationalFormatter {
        /**
         * Constructs a formatter.
         */
        EditorFormatter(final AngleFormat format) {
            super(format);
            setAllowsInvalid(true);
            setCommitsOnValidEdit(false);
            setOverwriteMode(false);

            final Class<? extends Angle> classe;
            final Object value = getValue();
            if      (value instanceof Longitude) classe = Longitude.class;
            else if (value instanceof  Latitude) classe = Latitude.class;
            else                                 classe = Angle.class;
            setValueClass(classe);
        }

        /**
         * Returns the {@link Object} representation of the given string.
         */
        @Override
        public Object stringToValue(final String text) throws ParseException {
            final Object value = super.stringToValue(text);
            if (value instanceof Longitude) return value;
            if (value instanceof  Latitude) return value;
            if (value instanceof     Angle) {
                final Class<?> valueClass = getValueClass();
                if (Longitude.class.isAssignableFrom(valueClass)) {
                    return new Longitude(((Angle)value).degrees());
                }
                if (Latitude.class.isAssignableFrom(valueClass)) {
                    return new Latitude(((Angle)value).degrees());
                }
            }
            return value;
        }

        /**
         * Sets the minimum value.
         */
        @Override
        @SuppressWarnings("rawtypes")
        public void setMinimum(final Comparable min) {
            SpinnerAngleModel.this.setMinimum(((Angle) min).degrees());
        }

        /**
         * Gets the minimum value.
         */
        @Override
        public Angle getMinimum() {
            return toAngle(SpinnerAngleModel.this.getMinimum());
        }

        /**
         * Sets the maximum value.
         */
        @Override
        @SuppressWarnings("rawtypes")
        public void setMaximum(final Comparable max) {
            SpinnerAngleModel.this.setMaximum(((Angle) max).degrees());
        }

        /**
         * Gets the maximum value.
         */
        @Override
        public Angle getMaximum() {
            return toAngle(SpinnerAngleModel.this.getMaximum());
        }
    }

    /**
     * An editor for a {@link javax.swing.JSpinner}. The value of the editor is
     * displayed with a {@link javax.swing.JFormattedTextField} whose format is
     * defined by a {@link javax.swing.text.InternationalFormatter} instance
     * whose minimum and maximum properties are mapped to the {@link SpinnerNumberModel}.
     *
     * @author Adapted from Hans Muller
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    static final class Editor extends JSpinner.DefaultEditor {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -4148754662854034336L;

        /**
         * Construct an editor for the specified format.
         */
        public Editor(final JSpinner spinner, final AngleFormat format) {
            super(spinner);
            final SpinnerModel genericModel = spinner.getModel();
            if (!(genericModel instanceof SpinnerAngleModel)) {
                throw new IllegalArgumentException();
            }
            final SpinnerAngleModel         model = (SpinnerAngleModel) genericModel;
            final EditorFormatter       formatter = model.new EditorFormatter(format);
            final DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
            final JFormattedTextField       field = getTextField();
            field.setEditable(true);
            field.setFormatterFactory(factory);
            field.setHorizontalAlignment(JFormattedTextField.RIGHT);
            /*
             * TODO - initializing the column width of the text field
             * is imprecise and doing it here is tricky because
             * the developer may configure the formatter later.
             */
            try {
                final String maxString = formatter.valueToString(formatter.getMaximum());
                final String minString = formatter.valueToString(formatter.getMinimum());
                field.setColumns(Math.max(maxString.length(), minString.length()));
            } catch (ParseException exception) {
                Logging.recoverableException(Editor.class, "<init>", exception);
                // Left the column width to its default value.
            }
        }
    }
}
