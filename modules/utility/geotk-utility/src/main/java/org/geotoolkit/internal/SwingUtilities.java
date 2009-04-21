/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.logging.Logging;


/**
 * A collection of utility methods for Swing. Every {@code show*} methods delegate
 * their work to the corresponding method in {@link JOptionPane}, with two differences:
 * <p>
 * <ul>
 *   <li>{@code SwingUtilities}'s method may be invoked from any thread. If they
 *       are invoked from a non-Swing thread, execution will be delegate to the Swing
 *       thread and the calling thread will block until completion.</li>
 *   <li>If a parent component is a {@link JDesktopPane}, dialogs will be rendered as
 *       internal frames instead of frames.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
@Static
public final class SwingUtilities {
    /**
     * The thread group for jobs to be run in background from a Swing widgets.
     * The main purpose of this group is to keep trace of swing workers that we
     * create in various location in Geotoolkit code.
     */
    public static final ThreadGroup WORKER_THREADS = new ThreadGroup("SwingWorkers");
    static {
        WORKER_THREADS.setMaxPriority(Thread.NORM_PRIORITY - 1);
    }

    /**
     * Do not allow any instance of this class to be created.
     */
    private SwingUtilities() {
    }

    /**
     * Sets the Swing Look and Feel to the default value used in Geotoolkit. This method exists
     * in order to have a central place where this setting can be performed, so we can change
     * the setting in a consistent fashion for the whole library.
     *
     * @param caller The class calling this method. Used only for logging purpose.
     * @param method The method invoking this one.  Used only for logging purpose.
     */
    public static void setLookAndFeel(final Class<?> caller, final String method) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            Logging.recoverableException(caller, method, e);
        }
    }

    /**
     * Inserts a Swing component into a frame. The kind of frame depends on the owner:
     * <p>
     * <ul>
     *   <li>If {@code owner} or one of its parent is a {@link JDesktopPane},
     *       then {@code panel} is added into a {@link JInternalFrame}.</li>
     *   <li>If {@code owner} or one of its parent is a {@link Frame} or a {@link Dialog},
     *       then {@code panel} is added into a {@link JDialog}.</li>
     *   <li>Otherwise, {@code panel} is added into a {@link JFrame}.</li>
     * </ul>
     *
     * @param  owner The frame's owner, or {@code null} if none.
     * @param  panel The panel to insert into a frame.
     * @param  title The frame's title.
     * @param  listener A listener to receives frame events.  If non-null, then this listener will
     *         be registered to whatever kind of frame this method will constructs. In the special
     *         case where this method constructs an {@linkplain JInternalFrame internal frame} and
     *         the {@code listener} is not an instance of {@link InternalFrameListener}, then this
     *         method will wrap the {@code listener} into an {@code InternalFrameListener}.
     * @return The frame. This frame is not initially visible. The method
     *         {@code Component.setVisible(true)} must be invoked in order to show the frame.
     */
    public static Component toFrame(Component owner, final JComponent panel,
            final String title, final WindowListener listener)
    {
        while (owner != null) {
            if (owner == panel) {
                throw new IllegalArgumentException();
            }
            // NOTE: All 'addFooListener(...)' below ignore null argument. No need to check ourself.
            if (owner instanceof JDesktopPane) {
                final JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
                frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                frame.addInternalFrameListener(InternalWindowListener.wrap(listener));
                ((JDesktopPane) owner).add(frame);
                frame.getContentPane().add(panel);
                frame.pack();
                return frame;
            }
            if (owner instanceof Frame) {
                final JDialog dialog = new JDialog((Frame) owner, title);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.addWindowListener(listener);
                dialog.getContentPane().add(panel);
                dialog.pack();
                return dialog;
            }
            if (owner instanceof Dialog) {
                final JDialog dialog = new JDialog((Dialog) owner, title);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.addWindowListener(listener);
                dialog.getContentPane().add(panel);
                dialog.pack();
                return dialog;
            }
            owner = owner.getParent();
        }
        /*
         * Adds the panel as a standalone window.
         * This window has its own button on the task bar.
         */
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(listener);
        frame.getContentPane().add(panel);
        frame.pack();
        return frame;
    }

    /**
     * Sets the title of the parent frame or internal frame of the specified component.
     *
     * @param component The component for which to set the title.
     * @param title The title to set.
     */
    public static void setTitle(Component component, final String title) {
        while (component != null) {
            if (component instanceof JInternalFrame) {
                ((JInternalFrame) component).setTitle(title);
            }
            if (component instanceof Frame) {
                ((Frame) component).setTitle(title);
                return;
            }
            if (component instanceof Dialog) {
                ((Dialog) component).setTitle(title);
                return;
            }
        }
    }

    /**
     * Brings up a "Ok/Cancel" dialog with no icon. This method can be invoked
     * from any thread and blocks until the user click on "Ok" or "Cancel".
     *
     * @param  owner  The parent component. Dialog will apears on top of this owner.
     * @param  dialog The dialog content to show.
     * @param  title  The title string for the dialog.
     * @return {@code true} if user clicked "Ok", {@code false} otherwise.
     */
    public static boolean showOptionDialog(final Component owner,
                                           final Object   dialog,
                                           final String    title)
    {
        return showOptionDialog(owner, dialog, title, null);
    }

    /**
     * Brings up a "Ok/Cancel/Reset" dialog with no icon. This method can be invoked
     * from any thread and blocks until the user click on "Ok" or "Cancel".
     *
     * @param  owner  The parent component. Dialog will apears on top of this owner.
     * @param  dialog The dialog content to show.
     * @param  title  The title string for the dialog.
     * @param  reset  Action to execute when user press "Reset", or {@code null}
     *                if there is no "Reset" button. If {@code reset} is an
     *                instance of {@link Action}, the button label will be set
     *                according the action's properties.
     * @return {@code true} if user clicked "Ok", {@code false} otherwise.
     */
    public static boolean showOptionDialog(final Component      owner,
                                           final Object        dialog,
                                           final String         title,
                                           final ActionListener reset)
    {
        /*
         * Delegates to Swing thread if this method is invoked from an other thread.
         */
        if (!EventQueue.isDispatchThread()) {
            final boolean result[] = new boolean[1];
            invokeAndWait(new Runnable() {
                @Override public void run() {
                    result[0] = showOptionDialog(owner, dialog, title, reset);
                }
            });
            return result[0];
        }
        /*
         * Constructs the buttons bar.
         */
        Object[]    options = null;
        Object initialValue = null;
        int okChoice = JOptionPane.OK_OPTION;
        if (reset != null) {
            final Vocabulary resources = Vocabulary.getResources(owner!=null ? owner.getLocale() : null);
            final JButton button;
            if (reset instanceof Action) {
                button = new JButton((Action)reset);
            } else {
                button = new JButton(resources.getString(Vocabulary.Keys.RESET));
                button.addActionListener(reset);
            }
            options = new Object[] {
                resources.getString(Vocabulary.Keys.OK),
                resources.getString(Vocabulary.Keys.CANCEL),
                button
            };
            initialValue = options[okChoice=0];
        }
        /*
         * Brings ups the dialog box.
         */
        final int choice;
        if (JOptionPane.getDesktopPaneForComponent(owner)!=null) {
            choice=JOptionPane.showInternalOptionDialog(
                    owner,                         // Composante parente
                    dialog,                        // Message
                    title,                         // Titre de la boîte de dialogue
                    JOptionPane.OK_CANCEL_OPTION,  // Boutons à placer
                    JOptionPane.PLAIN_MESSAGE,     // Type du message
                    null,                          // Icone
                    options,                       // Liste des boutons
                    initialValue);                 // Bouton par défaut
        } else {
            choice=JOptionPane.showOptionDialog(
                    owner,                         // Composante parente
                    dialog,                        // Message
                    title,                         // Titre de la boîte de dialogue
                    JOptionPane.OK_CANCEL_OPTION,  // Boutons à placer
                    JOptionPane.PLAIN_MESSAGE,     // Type du message
                    null,                          // Icone
                    options,                       // Liste des boutons
                    initialValue);                 // Bouton par défaut
        }
        return choice == okChoice;
    }

    /**
     * Brings up a message dialog with a "Ok" button. This method can be invoked
     * from any thread and blocks until the user click on "Ok".
     *
     * @param  owner   The parent component. Dialog will apears on top of this owner.
     * @param  message The dialog content to show.
     * @param  title   The title string for the dialog.
     * @param  type    The message type
     *                ({@link JOptionPane#ERROR_MESSAGE},
     *                 {@link JOptionPane#INFORMATION_MESSAGE},
     *                 {@link JOptionPane#WARNING_MESSAGE},
     *                 {@link JOptionPane#QUESTION_MESSAGE} or
     *                 {@link JOptionPane#PLAIN_MESSAGE}).
     */
    public static void showMessageDialog(final Component owner,
                                         final Object  message,
                                         final String    title,
                                         final int        type)
    {
        if (!EventQueue.isDispatchThread()) {
            invokeAndWait(new Runnable() {
                @Override public void run() {
                    showMessageDialog(owner, message, title, type);
                }
            });
            return;
        }
        if (JOptionPane.getDesktopPaneForComponent(owner)!=null) {
            JOptionPane.showInternalMessageDialog(
                    owner,     // Composante parente
                    message,   // Message
                    title,     // Titre de la boîte de dialogue
                    type);     // Type du message
        } else {
            JOptionPane.showMessageDialog(
                    owner,     // Composante parente
                    message,   // Message
                    title,     // Titre de la boîte de dialogue
                    type);     // Type du message
        }
    }

    /**
     * Brings up a confirmation dialog with "Yes/No" buttons. This method can be
     * invoked from any thread and blocks until the user click on "Yes" or "No".
     *
     * @param  owner   The parent component. Dialog will apears on top of this owner.
     * @param  message The dialog content to show.
     * @param  title   The title string for the dialog.
     * @param  type    The message type
     *                ({@link JOptionPane#ERROR_MESSAGE},
     *                 {@link JOptionPane#INFORMATION_MESSAGE},
     *                 {@link JOptionPane#WARNING_MESSAGE},
     *                 {@link JOptionPane#QUESTION_MESSAGE} or
     *                 {@link JOptionPane#PLAIN_MESSAGE}).
     * @return {@code true} if user clicked on "Yes", {@code false} otherwise.
     */
    public static boolean showConfirmDialog(final Component owner,
                                            final Object  message,
                                            final String    title,
                                            final int        type)
    {
        if (!EventQueue.isDispatchThread()) {
            final boolean result[] = new boolean[1];
            invokeAndWait(new Runnable() {
                @Override public void run() {
                    result[0] = showConfirmDialog(owner, message, title, type);
                }
            });
            return result[0];
        }
        final int choice;
        if (JOptionPane.getDesktopPaneForComponent(owner)!=null) {
            choice = JOptionPane.showInternalConfirmDialog(
                    owner,                     // Composante parente
                    message,                   // Message
                    title,                     // Titre de la boîte de dialogue
                    JOptionPane.YES_NO_OPTION, // Boutons à faire apparaître
                    type);                     // Type du message
        } else {
            choice = JOptionPane.showConfirmDialog(
                    owner,                     // Composante parente
                    message,                   // Message
                    title,                     // Titre de la boîte de dialogue
                    JOptionPane.YES_NO_OPTION, // Boutons à faire apparaître
                    type);                     // Type du message
        }
        return choice == JOptionPane.YES_OPTION;
    }

    /**
     * Setups the given table for usage as row-header. This method setups the background color to
     * the same one than the column headers.
     *
     * {@note In a previous version, we were assigning to the row headers same cell renderer than
     *        the one created by <cite>Swing</cite> for the column headers. But it produced strange
     *        effects when the L&F uses a vertical grandiant instead than a uniform color.}
     *
     * @param  table The table to setup as row headers.
     * @return The renderer which has been assigned to the table.
     */
    public static TableCellRenderer setupAsRowHeader(final JTable table) {
        final JTableHeader header = table.getTableHeader();
        Color background = header.getBackground();
        Color foreground = header.getForeground();
        if (background == null || background.equals(table.getBackground())) {
            background = SystemColor.control;
            foreground = SystemColor.controlText;
        }
        final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(background);
        renderer.setForeground(foreground);
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        final TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellRenderer(renderer);
        column.setPreferredWidth(60);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(false);
        return renderer;
    }

    /**
     * Removes the given elements from the given list. This method tries to use
     * {@link DefaultListModel#removeRange} when possible in order to group events
     * together.
     * <p>
     * <strong>Warning:</strong> This method override the given {@code indices} array.
     *
     * @param list    The list from which to remove elements.
     * @param indices The index of elements to remove.
     */
    public static void remove(final DefaultListModel list, final int[] indices) {
        // We must iterate in reverse order, because the
        // index after the removed elements will change.
        int i = indices.length;
        if (i != 0) {
            Arrays.sort(indices);
            int upper = indices[--i];
            int lower = upper;
            while (i != 0) {
                int previous = indices[--i];
                if (previous != lower - 1) {
                    if (lower == upper) {
                        list.remove(lower);
                    } else {
                        list.removeRange(lower, upper);
                    }
                    upper = previous;
                }
                lower = previous;
            }
            if (lower == upper) {
                list.remove(lower);
            } else {
                list.removeRange(lower, upper);
            }
        }
    }

    /**
     * Returns the locale for the specified component, or a default one if the component
     * is not yet part of a container hierarchy.
     *
     * @param  component The component for which to get the locale.
     * @return locale The locale from the given component.
     */
    public static Locale getLocale(final Component component) {
        if (component != null) try {
            return component.getLocale();
        } catch (IllegalComponentStateException ignore) {
            // Ignore. Will returns de default locale below.
        }
        return JComponent.getDefaultLocale();
    }

    /**
     * Causes runnable to have its run method called in the dispatch thread of
     * the event queue. This will happen after all pending events are processed.
     * The call blocks until this has happened.
     *
     * @param runnable The task to run in the dispath thread.
     */
    public static void invokeAndWait(final Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InterruptedException exception) {
                // Someone don't want to let us sleep. Go back to work.
            } catch (InvocationTargetException target) {
                final Throwable exception = target.getTargetException();
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException) exception;
                }
                if (exception instanceof Error) {
                    throw (Error) exception;
                }
                // Should not happen, since {@link Runnable#run} do not allow checked exception.
                throw new UndeclaredThrowableException(exception, exception.getLocalizedMessage());
            }
        }
    }
}
