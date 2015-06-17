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
package org.geotoolkit.gui.swing;

import java.text.ParseException;
import java.awt.Component;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.geotoolkit.lang.Workaround;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.internal.swing.InternalWindowListener;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;


/**
 * Base class of <cite>Swing</cite> components which may create new windows. For example
 * the {@link org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox} widget has an
 * information button which popup a window providing information about the selected CRS.
 * <p>
 * By default the new windows are instances of either {@link JDialog}, {@link JFrame} or
 * {@link JInternalFrame} - the later case occurs if and only if this {@code WindowCreator}
 * has a {@link JDesktopPane} ancestor. However this class provides a
 * {@link #setWindowHandler(Handler)} method allowing users to plugin their own mechanism,
 * for example in order to integrate the widget in the NetBeans platform.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see Window
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
public abstract class WindowCreator extends JComponent {
    /**
     * The system-wide {@link Handler} to use when none is explicitly set.
     */
    private static Handler defaultWindowHandler;

    /**
     * The handler for creating new windows, or {@code null} if not yet initialized.
     */
    private Handler windowHandler;

    /**
     * Creates a new {@code WindowCreator} with the default handler.
     */
    protected WindowCreator() {
    }

    /**
     * Returns the current handler for creating new windows.
     * The default value is {@link #getDefaultWindowHandler()}.
     *
     * @return The current window handler.
     */
    public Handler getWindowHandler() {
        if (windowHandler == null) {
            windowHandler = getDefaultWindowHandler();
        }
        return windowHandler;
    }

    /**
     * Sets the new handler for creating windows. A {@code null} value resets the
     * {@linkplain #getDefaultWindowHandler() default handler}.
     *
     * @param handler The new window handler, or {@code null} for the default one.
     */
    public void setWindowHandler(Handler handler) {
        if (handler == null) {
            handler = getDefaultWindowHandler();
        }
        final Handler old = getWindowHandler();
        windowHandler = handler;
        firePropertyChange("windowHandler", old, handler);
    }

    /**
     * Returns the system-wide default handler. Every {@link WindowCreator} instance will
     * use this handler, unless {@link #setWindowHandler(Handler)} has been explicitly invoked.
     * <p>
     * This method returns {@link Handler#DEFAULT}, unless a different default handler has
     * been given to the {@link #setDefaultWindowHandler(Handler)} method.
     *
     * @return The default handler for all {@link WindowCreator} instances.
     */
    public static synchronized Handler getDefaultWindowHandler() {
        if (defaultWindowHandler == null) {
            defaultWindowHandler = Handler.DEFAULT;
        }
        return defaultWindowHandler;
    }

    /**
     * Sets the system-wide default handler. Applications will typically invoke this method
     * at startup time if they want windows of some other kind than {@link JDialog},
     * {@link JFrame} or {@link JInternalFrame}.
     * <p>
     * Invoking this method has no effect on the existing {@code WindowCreator} instances
     * on which the {@link #setWindowHandler(Handler)} method has already been invoked.
     *
     * @param handler The new default window handler, or {@code null} for the default one.
     */
    @Configuration
    public static synchronized void setDefaultWindowHandler(Handler handler) {
        if (handler == null) {
            handler = Handler.DEFAULT;
        }
        defaultWindowHandler = handler;
    }

    /**
     * Creates new {@linkplain Window Windows} for the purpose of widgets extending
     * {@link WindowCreator}. The widget will typically use this handler as below:
     *
     * {@preformat java
     *     public class Widget extends WindowCreator {
     *         private JPanel accessoryContent = ...;
     *         private Window accessoryWindow;
     *
     *         void showAccessoryInformation() {
     *             accessoryContent... // Do some update here
     *
     *             if (accessoryWindow == null) {
     *                 accessoryWindow = getWindowHandler().createWindow(this, accessoryContent, title);
     *             }
     *             accessoryWindow.setVisible(true);
     *         }
     *     }
     * }
     *
     * The {@linkplain #DEFAULT default handler} will create new windows of kind
     * {@link JDialog}, {@link JFrame} or {@link JInternalFrame}. However users can provide
     * a different handler to {@link WindowCreator}, for example in order to integrate the
     * windows with the NetBeans platform.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.12
     * @module
     */
    public interface Handler {
        /**
         * The default implementation. The kind of window created by this implementation depends
         * on the parent of the {@code owner} argument:
         * <p>
         * <ul>
         *   <li>If a parent is a {@link JDesktopPane}, then the content is added into a
         *       {@link JInternalFrame}.</li>
         *   <li>If a parent is a {@link JFrame} or a {@link JDialog}, then the content is
         *       added into a {@link JDialog}.</li>
         *   <li>Otherwise, the content is added into a {@link JFrame}.</li>
         * </ul>
         */
        Handler DEFAULT = new DefaultHandler();

        /**
         * Invoked when the given {@code owner} needs to create a new window for the given
         * {@code content}. This method shall create new windows initialized as below:
         * <p>
         * <ul>
         *   <li>The window is initially hiden. Callers need to invoke
         *       {@link Window#setVisible(boolean)} in order to make it visible.</li>
         *   <li>The {@linkplain Window#setDefaultCloseOperation(int) default close
         *       operation} shall be {@link JFrame#DISPOSE_ON_CLOSE DISPOSE_ON_CLOSE}.</li>
         * </ul>
         *
         * @param  owner   The {@link WindowCreator} which need to create a new window.
         * @param  content The content to put in the window.
         * @param  title   The window title.
         * @return The new window.
         */
        Window createWindow(Component owner, Component content, String title);

        /**
         * Shows the given content in a modal dialog with "<cite>Ok</cite>" and "<cite>Cancel</cite>"
         * buttons, and wait for the user to close the dialog.
         *
         * @param  owner   The {@link WindowCreator} which need to display a dialog window.
         * @param  content The content to put in the dialog wondow.
         * @param  title   The dialog title.
         * @return {@code true} if the user clicked on the "<cite>Ok</cite>" button, or
         *         {@code false} otherwise.
         */
        boolean showDialog(Component owner, Component content, String title);

        /**
         * Shows an error message in a modal dialog with "<cite>Ok</cite>" button,
         * and wait for the user to close the dialog.
         *
         * @param  owner   The {@link WindowCreator} which need to display a dialog window.
         * @param  content The content to put in the dialog wondow.
         * @param  title   The dialog title.
         *
         * @since 3.14
         */
        void showError(Component owner, Component content, String title);
    }

    /**
     * The default implementation of {@link Handler}.
     * This is the type of {@link Handler#DEFAULT}.
     */
    private static final class DefaultHandler implements Handler {
        /**
         * Creates a {@link JDialog}, {@link JFrame} or {@link JInternalFrame} depending on
         * the {@code owner} ancestor.
         */
        @Override
        public Window createWindow(final Component owner, final Component content, final String title) {
            java.awt.Window window = null;
            Component parent = owner;
            while ((parent = parent.getParent()) != null) {
                if (parent instanceof JDesktopPane) {
                    final InternalFrame frame = new InternalFrame(title);
                    ((JDesktopPane) parent).add(frame);
                    frame.add(content);
                    frame.pack();
                    return frame;
                }
                if (parent instanceof java.awt.Frame) {
                    window = new Dialog((java.awt.Frame) parent, title);
                    break;
                } else if (parent instanceof java.awt.Dialog) {
                    window = new Dialog((java.awt.Dialog) parent, title);
                    break;
                }
            }
            if (window == null) {
                window = new Frame(title);
            }
            window.add(content);
            window.pack();
            window.setLocationRelativeTo(owner);
            ((Window) window).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            return (Window) window;
        }

        /**
         * Shows a dialog box as a {@link JDialog} or {@link JInternalFrame} depending on
         * the {@code owner} ancestor.
         */
        @Override
        @Workaround(library="MacOS", version="10.5")
        public boolean showDialog(Component owner, final Component content, final String title) {
            // Workaround for the Mac L&F, where the internal dialog box has no border
            // and can not be moved. We will use a native dialog window instead.
            if (UIManager.getLookAndFeel().getName().equalsIgnoreCase("Mac OS X")) {
                if (!(owner instanceof java.awt.Window)) {
                    owner = javax.swing.SwingUtilities.getWindowAncestor(owner);
                }
            }
            while (SwingUtilities.showOptionDialog(owner, content, title)) {
                if (!(content instanceof org.geotoolkit.gui.swing.Dialog)) {
                    return true;
                }
                try {
                    ((org.geotoolkit.gui.swing.Dialog) content).commitEdit();
                    return true;
                } catch (ParseException exception) {
                    SwingUtilities.showMessageDialog(owner, exception.getLocalizedMessage(),
                            Errors.getResources(content.getLocale()).getString(Errors.Keys.IllegalEntry),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            return false;
        }

        /**
         * Shows a dialog box as a {@link JDialog} or {@link JInternalFrame} depending on
         * the {@code owner} ancestor.
         */
        @Override
        public void showError(final Component owner, final Component content, String title) {
            if (title == null) {
                title = Vocabulary.getResources(owner.getLocale()).getString(Vocabulary.Keys.Error);
            }
            JOptionPane.showMessageDialog(owner, content, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * A {@link JInternalFrame} which implement the {@link Window} interface.
     * This is one of the types of windows created by {@link DefaultHandler}.
     */
    @SuppressWarnings("serial")
    private static final class InternalFrame extends JInternalFrame implements Window {
        InternalFrame(final String title) {
            super(title, true, true, true, true);
        }

        @Override
        public void addWindowListener(final WindowListener listener) {
            addInternalFrameListener(InternalWindowListener.wrap(listener));
        }

        @Override
        public void removeWindowListener(final WindowListener listener) {
            InternalWindowListener.removeWindowListener(this, listener);
        }
    }

    /**
     * A {@link JFrame} which implement the {@link Window} interface.
     * This is one of the types of windows created by {@link DefaultHandler}.
     */
    @SuppressWarnings("serial")
    private static final class Frame extends JFrame implements Window {
        Frame(final String title) {super(title);}
    }

    /**
     * A {@link JDialog} which implement the {@link Window} interface.
     * This is one of the types of windows created by {@link DefaultHandler}.
     */
    @SuppressWarnings("serial")
    private static final class Dialog extends JDialog implements Window {
        Dialog(final java.awt.Frame  owner, final String title) {super(owner, title);}
        Dialog(final java.awt.Dialog owner, final String title) {super(owner, title);}
    }
}
