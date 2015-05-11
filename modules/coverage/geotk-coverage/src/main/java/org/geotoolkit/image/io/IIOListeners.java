/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.event.IIOWriteProgressListener;

import org.apache.sis.util.ArraysExt;


/**
 * A container of image I/O listeners. This class provides a set of {@code addFooListener(...)}
 * and {@code removeFooListener(...)} methods for adding and removing various listeners, and a
 * {@code addListenersTo(...)} method for copying listeners to the an image reader. This class is
 * convenient when {@code ImageReader.addFooListener(...)} can't be invoked directly because the
 * {@link ImageReader} instance is not yet know or available.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 2.1
 * @module
 */
public class IIOListeners implements Serializable {
    /**
     * For compatibility with different versions.
     */
    private static final long serialVersionUID = 3747976429353858766L;

    /**
     * The listener categories for read operations.
     */
    private static final Class<?>[] READ = {
        IIOReadProgressListener.class,
        IIOReadWarningListener .class,
        IIOReadUpdateListener  .class
    };

    /**
     * The listener categories for read operations.
     */
    private static final Class<?>[] WRITE = {
        IIOWriteProgressListener.class,
        IIOWriteWarningListener .class
    };

    /**
     * The list of listeners, as a subclass which can copy its value from an other list of
     * listeners. We can safely takes the array reference without cloning it because every
     * methods modifying the list internally create a copy of the array and modify that copy.
     */
    private static final class List extends EventListenerList {
        private static final long serialVersionUID = 3398556089737845953L;

        final void setTo(final EventListenerList other) {
            listenerList = other.getListenerList();
        }
    }

    /**
     * List of listeners.
     */
    private final List listeners = new List();

    /**
     * Creates a new instance of {@code IIOListeners}.
     */
    public IIOListeners() {
    }

    /**
     * Sets the list of listeners in this object to the same listeners than the given object.
     * Any listeners previously hold by this {@code IIOListeners} are discarded.
     *
     * @param other The other object from which to copy the list of listeners.
     *
     * @since 3.10
     */
    public void setListeners(final IIOListeners other) {
        listeners.setTo(other.listeners);
    }

    /**
     * Adds an {@code IIOReadProgressListener} to the list of registered progress listeners.
     *
     * @param listener The listener to add.
     */
    public void addIIOReadProgressListener(final IIOReadProgressListener listener) {
        listeners.add(IIOReadProgressListener.class, listener);
    }

    /**
     * Removes an {@code IIOReadProgressListener} from the list of registered progress listeners.
     *
     * @param listener The listener to remove.
     *
     * @category Reader
     */
    public void removeIIOReadProgressListener(final IIOReadProgressListener listener) {
        listeners.remove(IIOReadProgressListener.class, listener);
    }

    /**
     * Adds an {@code IIOReadUpdateListener} to the list of registered update listeners.
     *
     * @param listener The listener to add.
     *
     * @since 3.02
     * @category Reader
     */
    public void addIIOReadUpdateListener(final IIOReadUpdateListener listener) {
        listeners.add(IIOReadUpdateListener.class, listener);
    }

    /**
     * Removes an {@code IIOReadUpdateListener} from the list of registered update listeners.
     *
     * @param listener The listener to remove.
     *
     * @since 3.02
     * @category Reader
     */
    public void removeIIOReadUpdateListener(final IIOReadUpdateListener listener) {
        listeners.remove(IIOReadUpdateListener.class, listener);
    }

    /**
     * Adds an {@code IIOReadWarningListener} to the list of registered warning listeners.
     *
     * @param listener The listener to add.
     *
     * @category Reader
     */
    public void addIIOReadWarningListener(final IIOReadWarningListener listener) {
        listeners.add(IIOReadWarningListener.class, listener);
    }

    /**
     * Removes an {@code IIOReadWarningListener} from the list of registered warning listeners.
     *
     * @param listener The listener to remove.
     *
     * @category Reader
     */
    public void removeIIOReadWarningListener(final IIOReadWarningListener listener) {
        listeners.remove(IIOReadWarningListener.class, listener);
    }

    /**
     * Adds an {@code IIOWriteProgressListener} to the list of registered progress listeners.
     *
     * @param listener The listener to add.
     *
     * @since 3.02
     * @category Writer
     */
    public void addIIOWriteProgressListener(final IIOWriteProgressListener listener) {
        listeners.add(IIOWriteProgressListener.class, listener);
    }

    /**
     * Removes an {@code IIOWriteProgressListener} from the list of registered progress listeners.
     *
     * @param listener The listener to remove.
     *
     * @since 3.02
     * @category Writer
     */
    public void removeIIOWriteProgressListener(final IIOWriteProgressListener listener) {
        listeners.remove(IIOWriteProgressListener.class, listener);
    }

    /**
     * Adds an {@code IIOWriteWarningListener} to the list of registered warning listeners.
     *
     * @param listener The listener to add.
     *
     * @since 3.02
     * @category Writer
     */
    public void addIIOWriteWarningListener(final IIOWriteWarningListener listener) {
        listeners.add(IIOWriteWarningListener.class, listener);
    }

    /**
     * Removes an {@code IIOWriteWarningListener} from the list of registered warning listeners.
     *
     * @param listener The listener to remove.
     *
     * @since 3.02
     * @category Writer
     */
    public void removeIIOWriteWarningListener(final IIOWriteWarningListener listener) {
        listeners.remove(IIOWriteWarningListener.class, listener);
    }

    /**
     * Returns all {@linkplain IIOReadProgressListener read progress},
     * {@linkplain IIOReadUpdateListener read update} and
     * {@linkplain IIOReadWarningListener read warning} listeners.
     *
     * @return All read listeners.
     *
     * @category Reader
     */
    public EventListener[] getReadListeners() {
        return getListeners(READ);
    }

    /**
     * Returns all {@linkplain IIOWriteProgressListener write progress} and
     * {@linkplain IIOWriteWarningListener write warning} listeners.
     *
     * @return All read listeners.
     *
     * @since 3.02
     * @category Writer
     */
    public EventListener[] getWriteListeners() {
        return getListeners(WRITE);
    }

    /**
     * Returns all listeners of the given classes.
     */
    private EventListener[] getListeners(final Class<?>[] categories) {
        int   count = 0;
        final Object[] list = listeners.getListenerList();
        final EventListener[] listeners = new EventListener[list.length/2];
   add: for (int i=0; i<list.length; i+=2) {
            final Class<?> type = (Class<?>) list[i];
            for (int j=categories.length; --j>=0;) {
                if (type.equals(categories[i])) {
                    /*
                     * Found a listener in one of the specified categories.
                     * Ensure that it was not already added in the list.
                     */
                    final EventListener candidate = (EventListener) list[i+1];
                    for (int k=count; --k>=0;) {
                        if (listeners[k] == candidate) {
                            // Avoid duplication.
                            continue add;
                        }
                    }
                    listeners[count++] = candidate;
                    continue add;
                }
            }
        }
        return ArraysExt.resize(listeners, count);
    }

    /**
     * Adds all listeners registered in this object to the specified image reader.
     *
     * @param reader The reader on which to register the listeners.
     *
     * @category Reader
     */
    public void addListenersTo(final ImageReader reader) {
        manageListeners(reader, true);
    }

    /**
     * Adds all listeners registered in this object to the specified image writer.
     *
     * @param writer The writer on which to register the listeners.
     *
     * @since 3.02
     * @category Writer
     */
    public void addListenersTo(final ImageWriter writer) {
        manageListeners(writer, true);
    }

    /**
     * Removes all listeners registered in this object from the specified image reader.
     *
     * @param reader The reader from which to unregister the listeners.
     *
     * @since 3.07
     * @category Reader
     */
    public void removeListenersFrom(final ImageReader reader) {
        manageListeners(reader, false);
    }

    /**
     * Removes all listeners registered in this object from the specified image writer.
     *
     * @param writer The writer from which to unregister the listeners.
     *
     * @since 3.07
     * @category Writer
     */
    public void removeListenersFrom(final ImageWriter writer) {
        manageListeners(writer, false);
    }

    /**
     * Adds or removes all listeners registered in this object to/from the specified image reader.
     *
     * @param reader The reader from which to (un)register the listeners.
     * @param add {@code true} for adding the listeners, or {@code false} for removing them.
     *
     * @category Reader
     */
    private void manageListeners(final ImageReader reader, final boolean add) {
        final Object[] listeners = this.listeners.getListenerList();
        for (int i=0; i<listeners.length;) {
            final Object classe   = listeners[i++];
            final Object listener = listeners[i++];
            if (classe == IIOReadProgressListener.class) {
                final IIOReadProgressListener l = (IIOReadProgressListener) listener;
                reader.removeIIOReadProgressListener(l); // Ensure singleton
                if (add) reader.addIIOReadProgressListener(l);
                continue;
            }
            if (classe == IIOReadUpdateListener.class) {
                final IIOReadUpdateListener l = (IIOReadUpdateListener) listener;
                reader.removeIIOReadUpdateListener(l); // Ensure singleton
                if (add) reader.addIIOReadUpdateListener(l);
                continue;
            }
            if (classe == IIOReadWarningListener.class) {
                final IIOReadWarningListener l = (IIOReadWarningListener) listener;
                reader.removeIIOReadWarningListener(l); // Ensure singleton
                if (add) reader.addIIOReadWarningListener(l);
                continue;
            }
        }
    }

    /**
     * Adds or removes all listeners registered in this object to/from the specified image writer.
     *
     * @param writer The writer from which to (un)register the listeners.
     * @param add {@code true} for adding the listeners, or {@code false} for removing them.
     *
     * @category Writer
     */
    private void manageListeners(final ImageWriter writer, final boolean add) {
        final Object[] listeners = this.listeners.getListenerList();
        for (int i=0; i<listeners.length;) {
            final Object classe   = listeners[i++];
            final Object listener = listeners[i++];
            if (classe == IIOWriteProgressListener.class) {
                final IIOWriteProgressListener l = (IIOWriteProgressListener) listener;
                writer.removeIIOWriteProgressListener(l); // Ensure singleton
                if (add) writer.addIIOWriteProgressListener(l);
                continue;
            }
            if (classe == IIOWriteWarningListener.class) {
                final IIOWriteWarningListener l = (IIOWriteWarningListener) listener;
                writer.removeIIOWriteWarningListener(l); // Ensure singleton
                if (add) writer.addIIOWriteWarningListener(l);
                continue;
            }
        }
    }
}
