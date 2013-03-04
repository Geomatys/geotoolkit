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
 */
package org.geotoolkit.internal;

import java.util.Arrays;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.ArgumentChecks;


/**
 * Utility methods related to listeners.
 * <p>
 * This method provides a single place where we manage Swing listeners used in a non-Swing context.
 * If a future Geotk version, we may replace Swing listeners by JavaFX observable. In such case, we
 * can just search for usage of this {@code Listeners} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final class Listeners extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Listeners() {
    }

    /**
     * Adds the given listener to the given list of listeners. If the given listener is added more
     * than once, then it will be notified more than once.
     *
     * @param  listener The listener to add.
     * @param  listeners The list of listeners to add to, or {@code null} if it does not yet exist.
     * @return The new list of listeners.
     */
    public static ChangeListener[] addListener(final ChangeListener listener, ChangeListener[] listeners) {
        ArgumentChecks.ensureNonNull("listener", listener);
        final int length;
        if (listeners == null) {
            length = 0;
            listeners = new ChangeListener[1];
        } else {
            length = listeners.length;
            listeners = Arrays.copyOf(listeners, length + 1);
        }
        listeners[length] = listener;
        return listeners;
    }

    /**
     * Removes the given listener from the given list of listeners. If the given listener does not
     * appear in the given list, this method does nothing. If it appears more than once, then only
     * the first occurrence is removed.
     *
     * @param  listener The listener to add.
     * @param  listeners The list of listeners from which to remove the listener, or {@code null} if none.
     * @return The new list of listeners, or {@code null} if the last listener has been removed.
     */
    public static ChangeListener[] removeListener(final ChangeListener listener, ChangeListener[] listeners) {
        if (listeners != null) {
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == listener) {
                    listeners = ArraysExt.remove(listeners, i, 1);
                    if (listeners.length == 0) {
                        listeners = null;
                    }
                    break;
                }
            }
        }
        return listeners;
    }

    /**
     * Notifies all registered listeners that the given object changed.
     *
     * @param source The event source.
     * @param listeners The list of listeners to notify, or {@code null} if none.
     */
    public static void fireChanged(final Object source, final ChangeListener[] listeners) {
        if (listeners != null) {
            final ChangeEvent event = new ChangeEvent(source);
            for (final ChangeListener listener : listeners) {
                listener.stateChanged(event);
            }
        }
    }
}
