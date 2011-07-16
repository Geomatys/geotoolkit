/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.event;

import java.util.Locale;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;

import org.geotoolkit.util.Localized;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Monitors the progress of some lengthly operation, and allows cancellation.
 * This abstract class makes no assumption about the output device. Additionally,
 * this class provides support for non-fatal warning and exception reports.
 * <p>
 * All implementations should be multi-thread safe, even the ones that provide
 * feedback to a user interface thread.
 * <p>
 * Usage example:
 *
 * {@preformat java
 *     float scale = 100f / maximumCount;
 *     controller.started();
 *     for (int counter=0; counter<maximumCount; counter++) {
 *         if (controller.isCanceled()) {
 *             break;
 *         }
 *         controller.progress(scale * counter);
 *         try {
 *             // Do some work...
 *         } catch (NonFatalException e) {
 *             controller.exceptionOccurred(e);
 *         }
 *     }
 *     controller.complete();
 * }
 *
 * {@note This class is defined in the Swing package because it is used mostly in Swing applications.
 * However this location should be understood in a loose sense since some implementations exist also
 * in the "headless" package. This is a similar situation to the Swing tree models which can actually
 * be used as a general purpose tree structure.}
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnet (Refractions Research)
 * @version 3.19
 *
 * @since 3.19 (derived from 2.0)
 * @module
 */
@ThreadSafe
public abstract class ProgressController implements Localized, org.opengis.util.ProgressListener {
    /**
     * The language to use for formatting messages.
     */
    private final Locale locale;

    /**
     * Name of the lengthly operation.
     */
    private volatile InternationalString task;

    /**
     * The current progress percentage, as a value between 0 and 100.
     */
    private volatile float progress;

    /**
     * {@code true} if the action has been canceled.
     */
    private volatile boolean canceled;

    /**
     * Constructs a progress controller which will format messages in the
     * {@linkplain Locale#getDefault() system default locale}.
     */
    protected ProgressController() {
        locale = Locale.getDefault();
    }

    /**
     * Returns the locale used for formatting messages. The default value is the
     * {@linkplain Locale#getDefault() system default} locale.
     *
     * @return The locale.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the description of the current task being performed, or {@code null} if none.
     * It is assumed that if the task is {@code null} applications may simply report that the
     * process is "<cite>in progress</cite>" or "<cite>working</cite>" as represented in the
     * {@linkplain #getLocale() current locale}.
     *
     * @return Description of the task being performed, or {@code null} if none.
     */
    public InternationalString getTask() {
        return task;
    }

    /**
     * Sets the description of the current task being performed. This method is usually invoked
     * before any progress begins. However, it is legal to invoke this method at any time during
     * the operation, in which case the task label is updated without any change to the percentage
     * accomplished.
     *
     * @param task Description of the task being performed as a {@link String} or
     *        {@link InternationalString}, or {@code null} if none.
     */
    public void setTask(CharSequence task) {
        if (task != null && !(task instanceof InternationalString)) {
            task = new SimpleInternationalString(task.toString());
        }
        this.task = (InternationalString) task;
    }

    /**
     * @deprecated Replaced by {@link #setTask(CharSequence)}.
     */
    @Override
    @Deprecated
    public final void setTask(final InternationalString task) {
        setTask((CharSequence) task);
    }

    /**
     * @deprecated Replaced by {@code getTask().toString()}.
     */
    @Override
    @Deprecated
    public final String getDescription() {
        final InternationalString task = getTask();
        return (task != null) ? task.toString() : null;
    }

    /**
     * @deprecated Replaced by {@link #setTask(CharSequence)}.
     */
    @Override
    @Deprecated
    public final void setDescription(final String description) {
        setTask(description);
    }

    /**
     * Notifies this controller that the operation begins.
     */
    public abstract void started();

    /**
     * Returns the current progress as a percent completed.
     *
     * @return Percent completed between 0 and 100 inclusive.
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Notifies this controller of progress in the lengthly operation. Progresses are reported
     * as a value between 0 and 100 inclusive. Values out of bounds will be clamped.
     *
     * @param percent The progress as a value between 0 and 100 inclusive.
     */
    public void setProgress(float percent) {
        if (percent < 0  ) percent = 0;
        if (percent > 100) percent = 100;
        progress = percent;
    }

    /**
     * @deprecated Renamed {@link #setProgress(float)}.
     */
    @Override
    @Deprecated
    public final void progress(float percent) {
        setProgress(percent);
    }

    /**
     * Notifies this controller that the operation has finished. The progress indicator will
     * shows 100% or disappears, at implementor choice. If warning messages were pending,
     * they will be displayed now.
     */
    public abstract void complete();

    /**
     * Indicates that this job should be canceled.
     */
    public void cancel() {
        canceled = true;
    }

    /**
     * @deprecated Replaced by {@link #cancel()}.
     *
     * @since 2.3
     */
    @Override
    @Deprecated
    public final void setCanceled(final boolean cancel) {
        if (cancel) cancel();
    }

    /**
     * Returns {@code true} if this job is canceled.
     *
     * @return {@code true} if this job is canceled.
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Reports a warning. This warning may be {@linkplain java.util.logger.Logger logged}, printed
     * to the {@linkplain System#err standard error stream}, appears in a windows or be ignored,
     * at implementor choice.
     *
     * @param source
     *          Name of the warning source, or {@code null} if none. This is typically the
     *          filename in process of being parsed or the URL of the data being processed
     * @param location
     *          Text to write on the left side of the warning message, or {@code null} if none.
     *          This is typically the line number where the error occurred in the {@code source}
     *          file or the feature ID of the feature that produced the message
     * @param warning
     *          The warning message.
     */
    public abstract void warningOccurred(String source, String location, String warning);

    /**
     * Reports an exception. This method may prints the stack trace to the {@linkplain System#err
     * standard error stream} or display it in a dialog box, at implementor choice.
     *
     * @param exception The exception to report.
     */
    public abstract void exceptionOccurred(Throwable exception);
}
