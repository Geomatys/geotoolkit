/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.swing;

import java.awt.Dimension;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.AbstractButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.EventQueue;

import org.jdesktop.swingx.JXLabel;

import org.geotoolkit.util.Exceptions;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;


/**
 * Class in charge of displaying any exception messages and eventually their traces.
 * The message will appear in a dialog box or in an internal window, depending on the
 * parent. <strong>Note:</strong> All methods in this class must be called in the
 * same thread as the <cite>Swing</cite> thread.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see Exceptions
 * @see org.jdesktop.swingx.JXErrorPane
 *
 * @since 2.0
 * @module
 */
@SuppressWarnings("serial")
public final class ExceptionMonitor extends JOptionPane implements ActionListener {
    /**
     * Width (in pixels) of the dialog box when it also displays the trace.
     */
    private static final int WIDTH = 600;

    /**
     * Height (in pixels) of the dialog box when it also displays the trace.
     */
    private static final int HEIGHT = 400;

    /**
     * Displayed dialog box.  It will be a {@link JDialog} object or a
     * {@link JInternalFrame} object.
     */
    private final Component dialog;

    /**
     * Exception to display in the dialog box. The method {@link Throwable#getLocalizedMessage}
     * will be called to obtain the message to display.
     */
    private final Throwable exception;

    /**
     * Box which will contain the "message" part of the constructed dialog box.  This box
     * will be expanded if the user asks to see the exception trace.  It will arrange the
     * components using {@link BorderLayout}.
     */
    private final Container message;

    /**
     * Component displaying the exception trace. Initially, this component will be null.
     * It will only be created if the trace is requested by the user.
     */
    private Container trace;

    /**
     * Indicates whether the trace is currently visible. This field value
     * will be inverted each time the user presses the button "trace".
     */
    private boolean traceVisible;

    /**
     * Button which makes the trace appear or disappear.
     */
    private final AbstractButton traceButton;

    /**
     * Initial size of the dialog box {@link #dialog}. This information will be used to
     * return the box to its initial size when the trace disappears.
     */
    private final Dimension initialSize;

    /**
     * Resources in the user's language.
     */
    private final Vocabulary resources;

    /**
     * Constructs a pane which will display the specified error message.
     *
     * @param owner     Parent Component of the dialog box to be created.
     * @param exception Exception we want to report.
     * @param message   Message to display.
     * @param buttons   Buttons to place under the message.  These buttons
     *                  should be in the order "Debug", "Close".
     * @param resources Resources in the user's language.
     */
    private ExceptionMonitor(final Component owner,   final Throwable exception,
                             final Container message, final AbstractButton[] buttons,
                             final Vocabulary resources)
    {
        super(message, ERROR_MESSAGE, OK_CANCEL_OPTION, null, buttons);
        this.exception   = exception;
        this.message     = message;
        this.resources   = resources;
        this.traceButton = buttons[0];
        buttons[0].addActionListener(this);
        buttons[1].addActionListener(this);
        /*
         * Constructs the dialog box.  Automatically detects if we can use InternalFrame or if
         * we should be happy with JDialog. The exception trace will not be written immediately.
         */
        final String classname = Classes.getShortClassName(exception);
        final String title = resources.getString(Vocabulary.Keys.Error_1, classname);
        final JDesktopPane desktop = getDesktopPaneForComponent(owner);
        if (desktop != null) {
            final JInternalFrame dialog = createInternalFrame(desktop, title);
            desktop.setLayer(dialog, JDesktopPane.MODAL_LAYER.intValue());
            dialog.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            this.dialog = dialog;
        } else {
            final JDialog dialog = createDialog(owner, title);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            this.dialog = dialog;
        }
        initialSize = dialog.getSize();
    }

    /**
     * Constructs and displays a dialog box which informs the user that an exception has
     * been produced. This method should be called in the same thread as the Swing thread.
     */
    private static void showUnsafe(final Component owner, final Throwable exception, String message) {
        final Vocabulary resources = Vocabulary.getResources((owner != null) ? owner.getLocale() : null);
        if (message == null) {
            message = exception.getLocalizedMessage();
            if (message == null) {
                final String classname = Classes.getShortClassName(exception);
                message = resources.getString(Vocabulary.Keys.NoDetails_1, classname);
            }
        }
        final JXLabel textArea = new JXLabel(message);
        textArea.setLineWrap(true);
        textArea.setMaxLineSpan(WIDTH);
        final JComponent messageBox = new JPanel(new BorderLayout());
        messageBox.add(textArea, BorderLayout.NORTH);
        final ExceptionMonitor pane = new ExceptionMonitor(owner, exception, messageBox, new AbstractButton[] {
                new JButton(resources.getString(Vocabulary.Keys.Debug)),
                new JButton(resources.getString(Vocabulary.Keys.Close))
        }, resources);
        pane.dialog.setVisible(true);
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param owner Component in which the exception is produced, or {@code null} if unknown.
     * @param exception Exception which has been thrown and is to be reported to the user.
     * @param message Message to display. If this parameter is null, then
     *        {@link Exception#getLocalizedMessage} will be called to obtain the message.
     */
    public static void show(final Component owner, final Throwable exception, final String message) {
        if (EventQueue.isDispatchThread()) {
            showUnsafe(owner, exception, message);
        } else {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override public void run() {
                    showUnsafe(owner, exception, message);
                }
            });
        }
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param owner Component in which the exception is produced, or {@code null} if unknown.
     * @param exception Exception which has been thrown and is to be reported to the user.
     */
    public static void show(final Component owner, final Throwable exception) {
        show(owner, exception, null);
    }

    /**
     * Displays the exception trace below the message. This method is called automatically
     * when the dialog box "Debug" button is pressed. If the exception trace still hasn't
     * been written, this method will construct the necessary components.
     *
     * @param event The event.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource() != traceButton) {
            dispose();
            return;
        }
        /*
         * Constructs the exception trace if it hasn't already been constructed.
         */
        if (trace == null) {
            JComponent traceComponent = null;
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                final JTextArea text = new JTextArea();
                text.setTabSize(4);
                text.setText(Exceptions.formatStackTrace(cause));
                text.setEditable(false);
                text.setCaretPosition(0);
                final JScrollPane scroll = new JScrollPane(text);
                if (traceComponent != null) {
                    if (!(traceComponent instanceof JTabbedPane)) {
                        traceComponent.setOpaque(false);
                        String classname = Classes.getShortClassName(exception);
                        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
                        tabs.addTab(classname, traceComponent);
                        traceComponent = tabs;
                    }
                    String classname = Classes.getShortClassName(cause);
                    ((JTabbedPane) traceComponent).addTab(classname, scroll);
                } else {
                    traceComponent = scroll;
                }
            }
            if (traceComponent == null) {
                // Should not happen
                return;
            }
            trace = Box.createVerticalBox();
            trace.add(Box.createVerticalStrut(12));
            trace.add(traceComponent);
        }
        /*
         * Inserts or hides the exception trace.  Even if the trace is
         * hidden, it will not be destroyed if the user would like to
         * redisplay it.
         */
        traceButton.setText(resources.getString(traceVisible ?
                Vocabulary.Keys.Debug : Vocabulary.Keys.Hide));
        traceVisible = !traceVisible;
        if (dialog instanceof Dialog) {
            ((Dialog) dialog).setResizable(traceVisible);
        } else {
            ((JInternalFrame) dialog).setResizable(traceVisible);
        }
        int dx = dialog.getWidth();
        int dy = dialog.getHeight();
        if (traceVisible) {
            message.add(trace, BorderLayout.CENTER);
            dialog.setSize(WIDTH, HEIGHT);
        } else {
            message.remove(trace);
            dialog.setSize(initialSize);
        }
        dx -= dialog.getWidth();
        dy -= dialog.getHeight();
        dialog.setLocation(Math.max(0, dialog.getX() + dx/2),
                           Math.max(0, dialog.getY() + dy/2));
        dialog.validate();
    }

    /**
     * Frees up the resources used by this dialog box. This method is called when the
     * user closes the dialog box which reported the exception.
     */
    private void dispose() {
        if (dialog instanceof Window) {
            ((Window) dialog).dispose();
        } else {
            ((JInternalFrame) dialog).dispose();
        }
    }
}
