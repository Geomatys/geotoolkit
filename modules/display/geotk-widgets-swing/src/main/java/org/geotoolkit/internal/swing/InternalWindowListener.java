/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.swing;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


/**
 * Wraps a {@link WindowListener} into an {@link InternalFrameListener}. This is used
 * by {@link SwingUtilities} in order to have the same methods working seemless on both
 * {@link java.awt.Frame} and {@link javax.swing.JInternalFrame}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.0
 * @module
 */
public final class InternalWindowListener implements InternalFrameListener {
    /**
     * The underlying {@link WindowListener}.
     */
    private final WindowListener listener;

    /**
     * Constructs a new {@link InternalFrameListener}
     * wrapping the specified {@link WindowListener}.
     */
    private InternalWindowListener(final WindowListener listener) {
        this.listener = listener;
    }

    /**
     * Wraps the specified {@link WindowListener} into an {@link InternalFrameListener}.
     * If the specified object is already an {@link InternalFrameListener}, then it is
     * returned as-is.
     *
     * @param  listener The window listener.
     * @return The internal frame listener.
     */
    public static InternalFrameListener wrap(final WindowListener listener) {
        if (listener == null) {
            return null;
        }
        if (listener instanceof InternalFrameListener) {
            return (InternalFrameListener) listener;
        }
        return new InternalWindowListener(listener);
    }

    /**
     * Wraps the given internal frame envent into a window event.
     */
    private static WindowEvent wrap(final InternalFrameEvent event) {
        // Don't use javax.swing.SwingUtilities.getWindowAncestor
        // because we want the check to include event.getSource().
        Component c = (Component) event.getSource();
        while (c != null) {
            if (c instanceof Window) {
                return new WindowEvent((Window) c, event.getID());
            }
            c = c.getParent();
        }
        return null; // We can't create a WindowEvent with a null source.
    }

    /**
     * Invoked when a internal frame has been opened.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameOpened(InternalFrameEvent event) {
        listener.windowOpened(wrap(event));
    }

    /**
     * Invoked when an internal frame is in the process of being closed.
     * The close operation can be overridden at this point.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameClosing(InternalFrameEvent event) {
        listener.windowClosing(wrap(event));
    }

    /**
     * Invoked when an internal frame has been closed.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameClosed(InternalFrameEvent event) {
        listener.windowClosed(wrap(event));
    }

    /**
     * Invoked when an internal frame is iconified.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameIconified(InternalFrameEvent event) {
        listener.windowIconified(wrap(event));
    }

    /**
     * Invoked when an internal frame is de-iconified.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameDeiconified(InternalFrameEvent event) {
        listener.windowDeiconified(wrap(event));
    }

    /**
     * Invoked when an internal frame is activated.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameActivated(InternalFrameEvent event) {
        listener.windowActivated(wrap(event));
    }

    /**
     * Invoked when an internal frame is de-activated.
     *
     * @param event The event.
     */
    @Override
    public void internalFrameDeactivated(InternalFrameEvent event) {
        listener.windowDeactivated(wrap(event));
    }

    /**
     * Removes the given window listener from the given internal frame. This method will look
     * for instances of {@code InternalWindowListener} and unwrap the listener if needed.
     *
     * @param frame    The frame from which to remove the listener.
     * @param listener The listener to remove.
     */
    public static void removeWindowListener(final JInternalFrame frame, final WindowListener listener) {
        for (final InternalFrameListener candidate : frame.getInternalFrameListeners()) {
            if (candidate instanceof InternalWindowListener &&
                    ((InternalWindowListener) candidate).listener.equals(listener))
            {
                frame.removeInternalFrameListener(candidate);
            }
        }
    }
}
