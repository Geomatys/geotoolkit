/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.parameter;

import javax.swing.event.ChangeListener;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.geotoolkit.internal.Listeners;


/**
 * The base class of {@link ParameterValue} implementations. This base class provides support
 * for {@link ChangeListener} notifications. Those change listeners are not included in the
 * serialization neither in {@linkplain #clone() cloned} objects.
 *
 * @param <T> The value type.
 *
 * @deprecated Replaced by Apache SIS parameters.
 */
@Deprecated
public abstract class AbstractParameterValue<T> extends AbstractParameter implements ParameterValue<T> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4166422894375776520L;

    /**
     * The listeners, or {@code null} if none.
     */
    private transient ChangeListener[] listeners;

    /**
     * Constructs a parameter value from the specified descriptor.
     *
     * @param descriptor The abstract definition of this parameter.
     */
    protected AbstractParameterValue(final ParameterDescriptor<T> descriptor) {
        super(descriptor);
    }

    /**
     * Returns the abstract definition of this parameter.
     */
    @Override
    @SuppressWarnings("unchecked") // Type checked by the constructor.
    public ParameterDescriptor<T> getDescriptor() {
        return (ParameterDescriptor<T>) super.getDescriptor();
    }

    /**
     * Adds the specified listener to the list of objects to inform when the value changed.
     * Notes:
     * <ul>
     *   <li>Change listeners are not serialized</li>
     *   <li>Change listeners are not included in {@linkplain #clone() cloned} object.</li>
     * </ul>
     *
     * @param listener The listener to add.
     */
    public void addChangeListener(final ChangeListener listener) {
        listeners = Listeners.addListener(listener, listeners);
    }

    /**
     * Removes the specified listener from the list of objects to inform when the value changed.
     *
     * @param listener The listener to remove.
     */
    public void removeChangeListener(final ChangeListener listener) {
        listeners = Listeners.removeListener(listener, listeners);
    }

    /**
     * Informs every listeners that the value changed.
     */
    final void fireValueChanged() {
        Listeners.fireChanged(this, listeners);
    }

    /**
     * Returns a clone of this parameter. The clone does not include any change listener.
     */
    @Override
    public AbstractParameterValue<T> clone() {
        @SuppressWarnings("unchecked")
        final AbstractParameterValue<T> copy = (AbstractParameterValue<T>) super.clone();
        copy.listeners = null;
        return copy;
    }
}
