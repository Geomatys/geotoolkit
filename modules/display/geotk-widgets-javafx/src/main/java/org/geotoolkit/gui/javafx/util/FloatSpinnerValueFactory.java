package org.geotoolkit.gui.javafx.util;

import java.text.DecimalFormat;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;

/**
 * A {@link SpinnerValueFactory} which work on float values.
 *
 * @author Alexis Manin (Geomatys)
 */
public class FloatSpinnerValueFactory extends SpinnerValueFactory<Float> {

    protected static final DecimalFormat FLOAT_TO_STRING = new DecimalFormat("#.##");
    protected static final ObjectConverter<? super String, ? extends Float> STRING_TO_FLOAT = ObjectConverters.find(String.class, Float.class);

    /**
     * Minimum allowed value for edition.
     */
    private SimpleFloatProperty min = new SimpleFloatProperty(this, "min");

    /**
     * Maximum allowed value for edition.
     */
    private FloatProperty max = new SimpleFloatProperty(this, "max");

    /**
     * Incremented / decremented amount when an arrow is clicked.
     */
    private FloatProperty amountToStepBy = new SimpleFloatProperty(this, "amountToStepBy");

    public FloatSpinnerValueFactory() {
        this(-Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public FloatSpinnerValueFactory(float min, float max) {
        this(min, max, 0);
    }

    public FloatSpinnerValueFactory(float min, float max, float initialValue) {
        this(min, max, initialValue, 0.1f);
    }

    public FloatSpinnerValueFactory(float min, float max, float initialValue, float step) {
        setMin(min);
        setMax(max);
        setAmountToStepBy(step);

        setConverter(new StringConverter<Float>() {
            @Override
            public String toString(Float object) {
                return object == null
                        ? "" : FLOAT_TO_STRING.format(object);
            }

            @Override
            public Float fromString(String string) {
                return string == null || string.isEmpty()
                        ? null : STRING_TO_FLOAT.apply(string);
            }
        });

        valueProperty().addListener((o, oldValue, newValue) -> {
                // when the value is set, we need to react to ensure it is a
            // valid value (and if not, blow up appropriately)
            if (newValue < getMin()) {
                setValue(getMin());
            } else if (newValue > getMax()) {
                setValue(getMax());
            }
        });

        setValue(StrictMath.min(getMax(), StrictMath.max(getMin(), initialValue)));
    }

    /**
     * Set minimum allowed value for edition
     * @param value minimum value
     */
    public final void setMin(float value) {
        min.set(value);
    }

    /**
     * Get minimum allowed value for edition
     * @return minimum value
     */
    public final float getMin() {
        return min.get();
    }

    /**
     * Minimum allowed value for edition
     * @return minimum value property
     */
    public final FloatProperty minProperty() {
        return min;
    }

    /**
     * Set maximum allowed value for edition
     * @param value maximum value
     */
    public final void setMax(float value) {
        max.set(value);
    }

    /**
     * Get maximum allowed value for edition
     * @return maximum value
     */
    public final float getMax() {
        return max.get();
    }

    /**
     * Maximum allowed value for edition
     * @return maximum property
     */
    public final FloatProperty maxProperty() {
        return max;
    }

    /**
     * Set incremented / decremented amount when an arrow is clicked.
     * @param value step amount
     */
    public final void setAmountToStepBy(float value) {
        amountToStepBy.set(value);
    }

    /**
     * Get incremented / decremented amount when an arrow is clicked.
     * @return step amount
     */
    public final float getAmountToStepBy() {
        return amountToStepBy.get();
    }

    /**
     * Incremented / decremented amount when an arrow is clicked.
     * @return step amount property
     */
    public final FloatProperty amountToStepByProperty() {
        return amountToStepBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrement(int steps) {
        final float newValue = getValue() - (float) steps * amountToStepBy.floatValue();
        setValue(StrictMath.min(getMax(), StrictMath.max(getMin(), newValue)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment(int steps) {
        final float newValue = getValue() + (float) steps * amountToStepBy.floatValue();
        setValue(StrictMath.min(getMax(), StrictMath.max(getMin(), newValue)));
    }
}
