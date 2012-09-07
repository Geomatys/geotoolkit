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
package org.geotoolkit.gui.swing;

import java.util.Objects;

import java.awt.*;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTitledSeparator;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;

import org.geotoolkit.process.ProgressController;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.util.Disposable;


/**
 * Reports progress of a lengthly operation in a window. This implementation can also formats
 * warnings. Its method can be invoked from any thread (it doesn't need to be the <cite>Swing</cite>
 * thread), which make it easier to use it from some background thread. Such background thread
 * should have a low priority in order to avoid delaying Swing repaint events.
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/ProgressWindow.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 */
@ThreadSafe
public class ProgressWindow extends ProgressController implements Disposable {
    /**
     * Initial width for the progress window, in pixels.
     */
    private static final int WIDTH = 360;

    /**
     * Initial height for the progress window, in pixels.
     * Increase this value if some component (e.g. the "Cancel" button) seems truncated.
     * The current value has been tested for Metal look and feel.
     */
    private static final int HEIGHT = 140;

    /**
     * The height of the text area containing the warning messages (if any).
     */
    private static final int WARNING_HEIGHT = 120;

    /**
     * Horizontal margin width, in pixels.
     */
    private static final int HMARGIN = 12;

    /**
     * Vertical margin height, in pixels.
     */
    private static final int VMARGIN = 9;

    /**
     * The progress window as a {@link JDialog} or a {@link JInternalFrame},
     * depending of the parent component.
     */
    private final Component window;

    /**
     * The container where to add components like the progress bar.
     */
    private final JComponent content;

    /**
     * The progress bar. Values ranges from 0 to 100.
     */
    private final JProgressBar progressBar;

    /**
     * A description of the undergoing operation. Examples: "Reading header",
     * "Reading data", <i>etc.</i>
     */
    private final JLabel description;

    /**
     * The cancel button.
     */
    private final JButton cancel;

    /**
     * Component where to display warnings.
     */
    private JComponent warningArea;

    /**
     * The row number where to insert the next warning message.
     */
    private int nextWarningRow;

    /**
     * The source of the last warning message. Used in order to avoid to repeat the source
     * for all subsequent warning messages, if the source didn't changed.
     */
    private String lastSource;

    /**
     * Creates a window for reporting progress. The window will not appears immediately.
     * It will appears only when the {@link #started} method will be invoked.
     *
     * @param parent The parent component, or {@code null} if none.
     */
    public ProgressWindow(final Component parent) {
        /*
         * Creates the window containing the components.
         */
        Dimension parentSize;
        final Vocabulary  resources = Vocabulary.getResources(parent!=null ? parent.getLocale() : null);
        final String title = resources.getString(Vocabulary.Keys.PROGRESSION);
        final JDesktopPane desktop = JOptionPane.getDesktopPaneForComponent(parent);
        if (desktop != null) {
            final JInternalFrame frame;
            frame      = new JInternalFrame(title);
            window     = frame;
            content    = new JPanel(); // For having an opaque background.
            parentSize = desktop.getSize();
            frame.setContentPane(content);
            frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            desktop.add(frame, JLayeredPane.PALETTE_LAYER);
        } else {
            final Frame frame;
            final JDialog dialog;
            frame      = JOptionPane.getFrameForComponent(parent);
            dialog     = new JDialog(frame, title);
            window     = dialog;
            content    = (JComponent) dialog.getContentPane();
            parentSize = frame.getSize();
            if (parentSize.width == 0 || parentSize.height == 0) {
                parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            }
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setResizable(false);
            dialog.setModal(true);
        }
        window.setBounds((parentSize.width-WIDTH)/2, (parentSize.height-HEIGHT)/2, WIDTH, HEIGHT);
        /*
         * Creates the label that is going to display the undergoing operation.
         * This label is initially empty.
         */
        description = new JLabel();
        description.setHorizontalAlignment(JLabel.CENTER);
        /*
         * Creates the progress bar.
         */
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createCompoundBorder(
                              BorderFactory.createEmptyBorder(6,9,6,9),
                              progressBar.getBorder()));
        /*
         * Creates the cancel button.
         */
        cancel = new JButton(resources.getString(Vocabulary.Keys.CANCEL));
        cancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        final Box cancelBox = Box.createHorizontalBox();
        cancelBox.add(Box.createGlue());
        cancelBox.add(cancel);
        cancelBox.add(Box.createGlue());
        cancelBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        /*
         * Layout the elements inside the window. An empty border is created in
         * order to put some space between the window content and the window border.
         */
        final JPanel panel = new JPanel(new GridLayout(2,1));
        panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(VMARGIN, HMARGIN, VMARGIN, HMARGIN),
                        BorderFactory.createEtchedBorder()));
        panel.add(description);
        panel.add(progressBar);
        content.setLayout(new BorderLayout());
        content.add(panel,     BorderLayout.NORTH);
        content.add(cancelBox, BorderLayout.SOUTH);
    }

    /**
     * Returns a localized string for the specified key.
     */
    private String getString(final int key) {
        return Vocabulary.getResources(window.getLocale()).getString(key);
    }

    /**
     * Sets the window title. A {@code null} value resets the default title.
     *
     * @param name The new window title.
     */
    public void setTitle(String name) {
        if (name == null) {
            name = getString(Vocabulary.Keys.PROGRESSION);
        }
        set(Caller.TITLE, name);
    }

    /**
     * Returns the window title. The default title is "Progress" localized in current locale.
     *
     * @return The current window title.
     */
    public String getTitle() {
        return (String) get(Caller.TITLE);
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.3
     */
    @Override
    public void setTask(CharSequence task) {
        super.setTask(task);
        if (task instanceof InternationalString) {
            task = ((InternationalString) task).toString(getLocale());
        }
        set(Caller.LABEL, task);
    }

    /**
     * Notifies that the operation begins. This method display the windows if it was
     * not already visible.
     */
    @Override
    public void started() {
        call(Caller.STARTED);
    }

    /**
     * Notifies that the operation is suspended. This method sets the progress bar in
     * an {@linkplain JProgressBar#setIndeterminate(boolean) indeterminated} state.
     */
    @Override
    public void paused() {
        call(Caller.PAUSED);
    }

    /**
     * Notifies that the operation has been resumed. This method stops the progress bar
     * {@linkplain JProgressBar#setIndeterminate(boolean) indeterminated} state.
     */
    @Override
    public void resumed() {
        call(Caller.RESUMED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProgress(final float percent) {
        super.setProgress(percent);
        final int p = Math.max(0, Math.min(100, (int) percent));
        set(Caller.PROGRESS, Integer.valueOf(p));
    }

    /**
     * Displays a warning message under the progress bar. The text area for warning messages
     * will appears only the first time this method is invoked.
     *
     * @param source
     *          Name of the warning source, or {@code null} if none. This is typically the
     *          filename in process of being parsed or the URL of the data being processed.
     * @param location
     *          Text to write on the left side of the warning message, or {@code null} if none.
     *          This is typically the line number where the error occurred in the {@code source}
     *          file or the feature ID of the feature that produced the message.
     * @param warning
     *          The warning message.
     */
    @Override
    public synchronized void warningOccurred(final String source, String location, final String warning) {
        set(Caller.WARNING, new String[] {source, location, warning});
    }

    /**
     * Displays an exception stack trace.
     *
     * @param exception The exception to report.
     */
    @Override
    public void exceptionOccurred(final Throwable exception) {
        ExceptionMonitor.show(window, exception);
    }

    /**
     * Notifies that the operation has finished. The window will disappears, except
     * if it contains warning or exception stack traces.
     */
    @Override
    public void completed() {
        call(Caller.COMPLETE);
    }

    /**
     * Releases any resource holds by this window. Invoking this method destroy the window.
     */
    @Override
    public void dispose() {
        call(Caller.DISPOSE);
    }

    /**
     * Returns the string {@code margin} without the parenthesis (if any).
     */
    private static String trim(String margin) {
        margin = margin.trim();
        int lower = 0;
        int upper = margin.length();
        while (lower<upper && margin.charAt(lower+0) == '(') lower++;
        while (lower<upper && margin.charAt(upper-1) == ')') upper--;
        return margin.substring(lower, upper);
    }

    /**
     * Queries one of the components in the progress window. This method
     * doesn't need to be invoked from the <cite>Swing</cite> thread.
     *
     * @param  task The desired value as one of the {@link Caller#TITLE}
     *              or {@link Caller#LABEL} constants.
     * @return The value.
     */
    private Object get(final int task) {
        final Caller caller = new Caller(-task);
        SwingUtilities.invokeAndWait(caller);
        return caller.value;
    }

    /**
     * Sets the state of one of the components in the progress window.
     * This method doesn't need to be invoked from the <cite>Swing</cite> thread.
     *
     * @param  task  The value to change as one of the {@link Caller#TITLE}
     *               or {@link Caller#LABEL} constants.
     * @param  value The new value.
     */
    private void set(final int task, final Object value) {
        final Caller caller = new Caller(task);
        caller.value = value;
        EventQueue.invokeLater(caller);
    }

    /**
     * Invokes a <cite>Swing</cite> method without arguments.
     *
     * @param task The method to invoke: {@link Caller#STARTED} or {@link Caller#DISPOSE}.
     */
    private void call(final int task) {
        EventQueue.invokeLater(new Caller(task));
    }

    /**
     * Task to run in the <cite>Swing</cite> thread. Tasks are identified by a numeric
     * constant. The {@code get} operations have negative identifiers and are executed
     * by the {@link EventQueue#invokeAndWait} method. The {@code set} operations have
     * positive identifiers and are executed by the {@link EventQueue#invokeLater} method.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    private class Caller implements Runnable {
        /** For getting or setting the window title. */
        static final int TITLE = 1;

        /** For getting or setting the progress label. */
        static final int LABEL = 2;

        /** For getting or setting the progress bar value. */
        static final int PROGRESS = 3;

        /** For adding a warning message. */
        static final int WARNING = 4;

        /** Notify that an action started. */
        static final int STARTED = 5;

        /** Notify that the process is paused. */
        static final int PAUSED = 6;

        /** Notify that the process is resumed. */
        static final int RESUMED = 7;

        /** Notify that an action is completed. */
        static final int COMPLETE = 8;

        /** Notify that the window can be disposed. */
        static final int DISPOSE = 9;

        /**
         * The task to execute, as one of the {@link #TITLE}, {@link #LABEL}, <i>etc.</i>
         * constants or their negative counterpart.
         */
        private final int task;

        /**
         * The value to get (negative value {@link #task}) or set (positive value {@link #task}).
         */
        public Object value;

        /**
         * Creates an action. {@code task} must be one of {@link #TITLE}, {@link #LABEL}
         * <i>etc.</i> constants or their negative counterpart.
         */
        public Caller(final int task) {
            this.task = task;
        }

        /**
         * Run the task.
         */
        @Override
        public void run() {
            final BoundedRangeModel model = progressBar.getModel();
            switch (task) {
                case -LABEL: {
                    value = description.getText();
                    return;
                }
                case +LABEL: {
                    description.setText(value.toString());
                    return;
                }
                case PROGRESS: {
                    model.setValue(((Integer) value).intValue());
                    progressBar.setIndeterminate(false);
                    return;
                }
                case STARTED: {
                    model.setRangeProperties(0, 1, 0, 100, false);
                    if (window instanceof Window) {
                        ((Window) window).setLocationRelativeTo(window.getParent());
                    }
                    window.setVisible(true);
                    break; // Need further action below.
                }
                case PAUSED: {
                    progressBar.setIndeterminate(true);
                    return;
                }
                case RESUMED: {
                    progressBar.setIndeterminate(false);
                    return;
                }
                case COMPLETE: {
                    progressBar.setIndeterminate(false);
                    model.setRangeProperties(100, 1, 0, 100, false);
                    window.setVisible(warningArea != null);
                    cancel.setEnabled(false);
                    break; // Need further action below.
                }
            }
            /*
             * Some of the tasks above requires an action on the window, which may be a JDialog or
             * a JInternalFrame. We need to determine the window type before to apply the action.
             */
            synchronized (ProgressWindow.this) {
                if (window instanceof JDialog) {
                    final JDialog window = (JDialog) ProgressWindow.this.window;
                    switch (task) {
                        case -TITLE: {
                            value = window.getTitle();
                            return;
                        }
                        case +TITLE: {
                            window.setTitle((String) value);
                            return;
                        }
                        case STARTED: {
                            window.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            return;
                        }
                        case COMPLETE: {
                            window.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                            return;
                        }
                        case DISPOSE: {
                            window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            if (warningArea==null || !window.isVisible()) {
                                window.dispose();
                            }
                            return;
                        }
                    }
                } else {
                    final JInternalFrame window = (JInternalFrame) ProgressWindow.this.window;
                    switch (task) {
                        case -TITLE: {
                            value = window.getTitle();
                            return;
                        }
                        case +TITLE: {
                            window.setTitle((String) value);
                            return;
                        }
                        case STARTED: {
                            window.setClosable(false);
                            return;
                        }
                        case COMPLETE: {
                            window.setClosable(true);
                            return;
                        }
                        case DISPOSE: {
                            window.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                            if (warningArea==null || !window.isVisible()) {
                                window.dispose();
                            }
                            return;
                        }
                    }
                }
                if (task != WARNING) {
                    throw new AssertionError(task); // Should never happen.
                }
                /*
                 * If the task to execute is not one of the above, we will assume that this
                 * is the WARNING task. If this is the first time we run this task, creates
                 * the panel which will contains the warnings.
                 */
                JComponent warningArea = ProgressWindow.this.warningArea;
                if (warningArea == null) {
                    warningArea = new JPanel(new GridBagLayout());
                    final JScrollPane scroll = new JScrollPane(warningArea);
                    final JPanel namedArea = new JPanel(new BorderLayout());
                    ProgressWindow.this.warningArea = warningArea;
                    namedArea.setBorder(BorderFactory.createEmptyBorder(0, HMARGIN, VMARGIN, HMARGIN));
                    namedArea.add(new JLabel(getString(Vocabulary.Keys.WARNING)), BorderLayout.NORTH);
                    namedArea.add(scroll, BorderLayout.CENTER);
                    content.add(namedArea, BorderLayout.CENTER);
                    if (window instanceof JDialog) {
                        final JDialog window = (JDialog) ProgressWindow.this.window;
                        window.setResizable(true);
                    } else {
                        final JInternalFrame window = (JInternalFrame) ProgressWindow.this.window;
                        window.setResizable(true);
                    }
                    window.setSize(WIDTH, HEIGHT+WARNING_HEIGHT);
                    window.setVisible(true); // Seems required in order to force relayout.
                }
                /*
                 * Now formats the warning message as 3 new labels.
                 */
                final GridBagConstraints c = new GridBagConstraints();
                c.weightx = 1;
                c.gridx   = 0;
                c.gridy   = nextWarningRow;
                c.fill    = GridBagConstraints.HORIZONTAL;
                c.insets.top = 3;
                final String[] values = (String[]) value;
                String source = values[0];
                if (!Objects.equals(source, lastSource)) {
                    lastSource = source;
                    if (source == null) {
                        source = getString(Vocabulary.Keys.UNTITLED);
                    }
                    c.gridwidth = 2;
                    c.insets.top += VMARGIN;
                    JXTitledSeparator title = new JXTitledSeparator(source);
                    title.setFont(Font.decode("Dialog-bolditalic-13"));
                    warningArea.add(title, c);
                    c.insets.top = 3;
                }
                c.gridy++;
                c.gridwidth = 1;
                String location = values[1];
                if (location != null) {
                    location = trim(location);
                    if (!location.isEmpty()) {
                        c.weightx = 0;
                        warningArea.add(new JLabel('(' + location + ')'), c);
                        c.weightx = 1;
                    }
                }
                c.gridx = 1;
                c.insets.left = HMARGIN;
                final JXLabel label = new JXLabel(values[2]);
                label.setLineWrap(true);
                warningArea.add(label, c);
                warningArea.getParent().validate(); // Validates the JScrollPane.
                nextWarningRow += 2;
            }
        }
    }
}
