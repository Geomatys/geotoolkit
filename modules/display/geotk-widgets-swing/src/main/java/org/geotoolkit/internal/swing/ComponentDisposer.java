/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.awt.Window;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JInternalFrame;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.geotoolkit.util.Disposable;
import org.apache.sis.util.ArraysExt;


/**
 * A listener for AWT/Swing {@link Window} or {@link JInternalFrame} which dispose every
 * component implementing the {@link Disposable} interface when the window is closed. In
 * order to install the listeners only when first needed, use the following code:
 *
 * {@preformat java
 *     addAncestorListener(ComponentDisposer.INSTANCE);
 * }
 *
 * This listener provides a convenient way to dispose resources in a way that reduce the
 * risk of memory leaks, since we never store direct references to the child components
 * to dispose.
 * <p>
 * Note that in order to get the resources disposed properly when the window is closed,
 * the window default close operation shall be {@code DISPOSE_ON_CLOSE} rather than
 * {@code EXIT_ON_CLOSE}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
public final class ComponentDisposer extends WindowAdapter implements InternalFrameListener, AncestorListener {
    /**
     * The singleton instance. See class javadoc for usage.
     */
    public static final ComponentDisposer INSTANCE = new ComponentDisposer();

    /**
     * Do not allow instantiation except the singleton.
     */
    private ComponentDisposer() {
    }

    /**
     * Installs the window listeners in the ancestor of the component specified by the given
     * event. This method does nothing if the window listeners are already installed.
     *
     * @param event The event from which to get the component for which to install the listeners.
     */
    @Override
    public void ancestorAdded(final AncestorEvent event) {
        event.getComponent().removeAncestorListener(this); // No need to call this method again.
        Container container = event.getAncestor();
        while (container != null) {
            if (container instanceof JInternalFrame) {
                final JInternalFrame window = (JInternalFrame) container;
                if (!ArraysExt.containsIdentity(window.getInternalFrameListeners(), INSTANCE)) {
                    window.addInternalFrameListener(INSTANCE);
                }
            }
            if (container instanceof Window) {
                final Window window = (Window) container;
                if (!ArraysExt.containsIdentity(window.getWindowListeners(), INSTANCE)) {
                    window.addWindowListener(INSTANCE);
                }
            }
            container = container.getParent();
        }
    }

    /**
     * Invoked when a window or an internal frame is closed. This method will call the
     * {@link Disposable#dispose()} method for every child components which implement
     * the {@link Disposable} interface. This method searches recursively in all children.
     */
    private void dispose(final Container container) {
        for (final Component component : container.getComponents()) {
            if (component instanceof Container) {
                dispose((Container) component);
            }
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }

    /**
     * Invoked when the AWT window has been closed.
     *
     * @param event The event from which to get the closed window.
     */
    @Override
    public void windowClosed(final WindowEvent event) {
        dispose(event.getWindow());
    }

    /**
     * Invoked when the Swing internal frame has been closed.
     *
     * @param event The event from which to get the closed frame.
     */
    @Override
    public void internalFrameClosed(final InternalFrameEvent event) {
        dispose(event.getInternalFrame());
    }

    @Override public void ancestorMoved           (final AncestorEvent      event) {}
    @Override public void ancestorRemoved         (final AncestorEvent      event) {}
    @Override public void internalFrameOpened     (final InternalFrameEvent event) {}
    @Override public void internalFrameIconified  (final InternalFrameEvent event) {}
    @Override public void internalFrameDeiconified(final InternalFrameEvent event) {}
    @Override public void internalFrameActivated  (final InternalFrameEvent event) {}
    @Override public void internalFrameDeactivated(final InternalFrameEvent event) {}
    @Override public void internalFrameClosing    (final InternalFrameEvent event) {}
}
